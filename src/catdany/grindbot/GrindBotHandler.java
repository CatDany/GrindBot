package catdany.grindbot;

import java.util.ArrayList;

import org.apache.commons.codec.Charsets;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;

import catdany.grindbot.grind.CollectionDB;
import catdany.grindbot.grind.Database;
import catdany.grindbot.grind.Giveaway;
import catdany.grindbot.grind.Mission;
import catdany.grindbot.log.Log;
import catdany.grindbot.utils.Helper;
import catdany.grindbot.utils.Misc;

public class GrindBotHandler implements Listener<GrindBot>
{
	boolean active = false;
	
	ArrayList<String> users = new ArrayList<String>();
	
	@Override
	public void onEvent(Event<GrindBot> e) throws Exception
	{
		try
		{
			if (e instanceof MessageEvent)
			{
				MessageEvent<GrindBot> me = (MessageEvent<GrindBot>)e;
				onMessage(me);
			}
			else if (e instanceof JoinEvent)
			{
				JoinEvent<GrindBot> je = (JoinEvent<GrindBot>)e;
				onJoin(je);
			}
			else if (e instanceof PartEvent)
			{
				PartEvent<GrindBot> pe = (PartEvent<GrindBot>)e;
				onPart(pe);
			}
		}
		catch (Throwable t)
		{
			Log.logStackTrace(t, "An exception was caught while trying to process an event (%s)", e.getClass().getName());
		}
	}
	
	private void onMessage(MessageEvent<GrindBot> me) throws Exception
	{
		if (me.getChannel().getName().equals("#" + Settings.CHANNEL))
		{
			String msg = me.getMessage();
			String user = me.getUser().getNick();
			Log.log("[CHAT] %s: %s", user, msg);
			if (!users.contains(user))
			{
				users.add(user);
				Log.log("User joined [msg-join]: %s", user);
			}
			if (!active && msg.startsWith("$run") && user.equals(Settings.CHANNEL))
			{
				active = true;
				new Thread(Main.bot, "GrindBot-Tick").start();
				Helper.chat(Localization.get(Localization.BOT_JOINED));
				Log.log("Bot has been activated by %s", user);
			}
			if (active)
			{
				// Bank Storage
				if (msg.equals("$"))
				{
					int amount = Database.getBankStorage(user);
					Helper.chatLocal(Localization.YOUR_BANK_STATUS, user, amount);
					Log.log("%s checked his bank status (%s)", user, amount);
				}
				// Enter a mission
				else if (msg.equals("$m") && Mission.currentMission != null)
				{
					if (Database.withdraw(user, Mission.currentMission.getCost()))
					{
						if (!Mission.currentMission.entries.contains(user))
						{
							Mission.currentMission.entries.add(user);
							Log.log("%s joined a mission party.", user);
						}
					}
					else
					{
						Log.log("%s couldn't join a mission party, because he didn't have enough money. Status: <%s>. Required: <%s>", user, Database.getBankStorage(user), Mission.currentMission.getCost());
					}
				}
				// Enter a giveaway
				else if (msg.startsWith("$g") && Giveaway.currentGiveaway != null)
				{
					try
					{
						Giveaway.currentGiveaway.addTickets(user, Integer.parseInt(msg.trim().substring(3)));
					}
					catch (NumberFormatException t) {}
				}
				// List top
				else if (msg.equals("$top"))
				{
					Helper.listTop();
				}
				// Bot info
				else if (msg.equals("$info"))
				{
					Helper.chatLocal(Localization.BOT_INFO);
				}
				// Collection Overview
				else if (msg.equals("$v"))
				{
					Helper.chatLocal(Localization.COLLECTION_OVERVIEW, user, user);
				}
				// Buy collection box
				else if (msg.equals("$" + Settings.COLLECTION_NAME))
				{
					long time = Misc.time();
					if (CollectionDB.lastCollectionBoxPurchase + 10000 < time && Database.withdraw(user, Integer.parseInt(Settings.COLLECTION_BOX_COST)))
					{
						CollectionDB.lastCollectionBoxPurchase = time;
						new Thread("CollectionBoxAnnouncement-" + Math.random())
						{
							public void run()
							{
								Helper.chatLocal(Localization.COLLECTION_BOX_PURCHASE, user);
								Misc.sleep(3500);
								CollectionDB.openBox(user);
							}
						}.start();
					}
				}
			}
		}
	}
	
	/* Not working with Twitch IRC
	@Deprecated
	private void onUserList(UserListEvent<GrindBot> ule) throws Exception
	{
		users.clear();
		for (User i : ule.getUsers())
		{
			users.add(i.getNick());
		}
		Log.log("User list received <%s>: %s", users.size(), Misc.arrayToString(", ", users.toArray()));
	}
	*/
	
	private void onJoin(JoinEvent<GrindBot> je) throws Exception
	{
		if (je.getChannel().getName().equals("#" + Settings.CHANNEL))
		{
			String user = je.getUser().getNick();
			if (!users.contains(user))
			{
				users.add(user);
			}
			Log.log("User joined: %s", user);
		}
	}
	
	private void onPart(PartEvent<GrindBot> pe) throws Exception
	{
		if (pe.getChannel().getName().equals("#" + Settings.CHANNEL))
		{
			String user = pe.getUser().getNick();
			if (users.contains(user))
			{
				users.remove(user);
			}
			Log.log("User quit: %s", user);
		}
	}
	
	GrindBot init()
	{
		Settings.reload();
		Localization.reload();
		Database.load();
		CollectionDB.loadCollection();
		CollectionDB.load();
		Mission.init();
		
		Builder<GrindBot> builder = new Builder<GrindBot>();
		builder
			.setName(Settings.NAME)
			.setLogin(Settings.NAME)
			.setCapEnabled(false)
			.addListener(this)
			.setServerHostname("irc.twitch.tv")
			.addAutoJoinChannel("#" + Settings.CHANNEL)
			.setVersion(Settings.NAME)
			.setServerPort(6667)
			.setServerPassword(Settings.OAUTH_TOKEN)
			.setEncoding(Charsets.UTF_8);
		final GrindBot bot = new GrindBot(builder.buildConfiguration());
		
		Log.log("Logging in...");
		Log.log("Type $run in chat as a broadcaster to activate a bot and finish the logging process.");
		
		new Thread("Bot-StartBot-Thread") {
			public void run()
			{
				try
				{
					bot.startBot();
				}
				catch (Throwable t)
				{
					t.printStackTrace();
				}
			}
		}.start();
		
		while (!bot.isConnected()) {}
		
		Log.log("Requesting capabilities: membership, commands...");
		bot.raw("CAP REQ :twitch.tv/membership");
		bot.raw("CAP REQ :twitch.tv/commands");
		
		return bot;
	}
}
