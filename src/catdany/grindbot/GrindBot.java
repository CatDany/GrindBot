package catdany.grindbot;

import java.util.ArrayList;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.output.OutputIRC;
import org.pircbotx.output.OutputRaw;

import catdany.grindbot.grind.Database;
import catdany.grindbot.grind.Mission;
import catdany.grindbot.log.Log;
import catdany.grindbot.utils.Helper;
import catdany.grindbot.utils.Misc;

public class GrindBot extends PircBotX implements Runnable
{
	public final OutputIRC irc;
	public final OutputRaw raw;
	
	public GrindBot(Configuration<GrindBot> config)
	{
		super(config);
		
		this.irc = new OutputIRC(this);
		this.raw = new OutputRaw(this);
	}
	
	@Override
	public OutputIRC sendIRC()
	{
		return irc;
	}
	
	public void chat(String input)
	{
		irc.message("#" + Settings.CHANNEL, input);
		Log.log("[CHAT] %s: %s", Settings.NAME, input);
	}
	
	@Deprecated
	public void priv(String user, String input)
	{
		irc.message("#" + Settings.CHANNEL, String.format(".w %s %s", user, input));
		Log.log("[PRIV] %s -> %s: %s", Settings.NAME, user, input);
	}
	
	public void raw(String input)
	{
		raw.rawLine(input);
	}
	
	private long lastTickTime = 0;
	
	@Override
	public void run()
	{
		int passiveGainBank = Integer.parseInt(Settings.PASSIVE_GAIN_BANK);
		long passiveGainBankCooldown = Integer.parseInt(Settings.PASSIVE_GAIN_BANK_COOLDOWN) * 1000;
		long missionCooldown = Integer.parseInt(Settings.MISSION_COOLDOWN) * 1000;
		long missionEntryTime = Integer.parseInt(Settings.MISSION_ENTRY_TIME) * 1000;
		while (true)
		{
			long time = Misc.time();
			if (time % passiveGainBankCooldown == 0 && lastTickTime != time)
			{
				lastTickTime = time;
				//ImmutableSortedSet<User> users = getUserChannelDao().getUsers(getUserChannelDao().getChannel("#" + Settings.CHANNEL));
				ArrayList<String> users = Main.botHandler.users;
				for (String user : users)
				{
					if (!user.equals(Settings.NAME))
					{
						int bankAmount = Database.getBankStorage(user);
						Database.setBankStorage(user, bankAmount + passiveGainBank);
						//Log.log("Tick of user <%s>. Bank status updated to <%s> (+%s)", user, Database.getBankStorage(user), passiveGainBank);
					}
				}
			}
			if (Mission.lastMissionTime + missionCooldown < time && Mission.currentMission == null)
			{
				Mission.lastMissionTime = time;
				Mission.currentMission = Mission.createMission();
				Helper.chatLocal(Localization.MISSION_STARTED, Mission.currentMission.name, Mission.currentMission.getLocalizedSize(), Mission.currentMission.getPeopleRequired(), Mission.currentMission.getCost());
			}
			if (Mission.currentMission != null && Mission.lastMissionTime + missionEntryTime < time)
			{
				if (Mission.currentMission.entries.size() < Mission.currentMission.getPeopleRequired())
				{
					Helper.chatLocal(Localization.MISSION_NOT_ENOUGH_PEOPLE, Mission.currentMission.name, Mission.currentMission.getLocalizedSize(), Mission.currentMission.entries.size(), Mission.currentMission.getPeopleRequired());
					for (String i : Mission.currentMission.entries)
					{
						Database.setBankStorage(i, Database.getBankStorage(i) + Mission.currentMission.getCost());
					}
				}
				else
				{
					String[] winners = Mission.currentMission.rollWinners();
					Helper.chatLocal(Localization.MISSION_ENDED, Mission.currentMission.name, Mission.currentMission.getLocalizedSize());
					Misc.sleep(1000);
					Helper.chatLocal(Localization.MISSION_WINNERS, Mission.currentMission.getReward(), Misc.arrayToString(", ", winners));
				}
				Mission.currentMission = null;
			}
		}
	}
}
