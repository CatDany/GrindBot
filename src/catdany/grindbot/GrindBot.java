package catdany.grindbot;

import java.util.ArrayList;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.output.OutputIRC;
import org.pircbotx.output.OutputRaw;

import catdany.grindbot.grind.Database;
import catdany.grindbot.log.Log;
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
	
	@Override
	public void run()
	{
		int passiveGainBank = Integer.parseInt(Settings.PASSIVE_GAIN_BANK);
		long passiveGainBankCooldown = Integer.parseInt(Settings.PASSIVE_GAIN_BANK_COOLDOWN) * 1000;
		while (true)
		{
			if (Misc.time() % passiveGainBankCooldown == 0)
			{
				//ImmutableSortedSet<User> users = getUserChannelDao().getUsers(getUserChannelDao().getChannel("#" + Settings.CHANNEL));
				ArrayList<String> users = Main.botHandler.users;
				for (String user : users)
				{
					int bankAmount = Database.getBankStorage(user);
					Database.setBankStorage(user, bankAmount + passiveGainBank);
					Log.log("Tick of user <%s>. Bank status updated to <%s>", user, Database.getBankStorage(user));
				}
			}
		}
	}
}
