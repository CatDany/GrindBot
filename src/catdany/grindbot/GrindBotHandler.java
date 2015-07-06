package catdany.grindbot;

import org.apache.commons.codec.Charsets;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.MessageEvent;

import catdany.grindbot.grind.Database;
import catdany.grindbot.log.Log;
import catdany.grindbot.utils.Helper;

public class GrindBotHandler implements Listener<GrindBot>
{
	private boolean active = false;
	
	@Override
	public void onEvent(Event<GrindBot> e) throws Exception
	{
		try
		{
		if (e instanceof MessageEvent)
		{
			MessageEvent<GrindBot> me = (MessageEvent<GrindBot>)e;
			String msg = me.getMessage();
			String user = me.getUser().getNick();
			Log.log("[CHAT] %s: %s", user, msg);
			Helper.chat("%s", Localization.get("debug_response", user, msg)); // FIXME: Debug code
			if (!active && msg.startsWith("%activate") && user.equals(Settings.CHANNEL))
			{
				active = true;
				Helper.chat(Localization.get(Localization.BOT_JOINED));
				Log.log("Bot has been activated by %s", user);
			}
			if (active)
			{
				//
			}
		}
		}
		catch (Throwable t)
		{
			Log.logStackTrace(t, "An exception was caught while trying to process an event (%s)", e.getClass().getName());
		}
	}
	
	GrindBot init()
	{
		Settings.reload();
		Localization.reload();
		Database.load();
		
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
		Log.log("Type %activate in chat as a broadcaster to activate a bot and finish the logging process.");
		
		new Thread() {
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
		
		return bot;
	}
}
