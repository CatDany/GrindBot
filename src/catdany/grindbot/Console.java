package catdany.grindbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang3.exception.ExceptionUtils;

import catdany.grindbot.grind.Database;
import catdany.grindbot.grind.Giveaway;
import catdany.grindbot.log.Log;
import catdany.grindbot.utils.Helper;

public class Console implements Runnable
{
	public Console()
	{
		Log.log("Initializing console listener.");
		Thread t = new Thread(this, "Console-Listener");
		t.start();
	}
	
	@Override
	public void run()
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		while (true)
		{
			try
			{
				String in = r.readLine();
				Log.log("Console input: %s", in);
				boolean a = Main.botHandler.active;
				if (in.startsWith("reload"))
				{
					Settings.reload();
					Localization.reload();
				}
				else if (in.startsWith("stop"))
				{
					System.exit(0);
				}
				else if (a && in.startsWith("setbank"))
				{
					String[] args = in.split(" ", 3);
					String user = args[1].toLowerCase();
					int amount = Integer.parseInt(args[2]);
					Database.setBankStorage(user, amount);
				}
				else if (a && in.startsWith("addbank"))
				{
					String[] args = in.split(" ", 3);
					String user = args[1].toLowerCase();
					int amount = Integer.parseInt(args[2]);
					Database.setBankStorage(user, Database.getBankStorage(user) + amount);
				}
				else if (a && in.equals("giveaway roll"))
				{
					if (Giveaway.currentGiveaway != null)
					{
						String user = Giveaway.currentGiveaway.rollWinner();
						int ticketsCount = Giveaway.currentGiveaway.getTicketsCount(user);
						Helper.chatLocal(Localization.GIVEAWAY_END, user, ticketsCount);
						Log.log("%s won a giveaway with %s tickets.", user, ticketsCount);
						Giveaway.currentGiveaway = null;
					}
				}
				else if (a && in.startsWith("giveaway"))
				{
					if (in.equals("giveaway"))
					{
						Log.log("Usage: giveaway <ticket cost> <max tickets> <giveaway name>");
					}
					else
					{
						String[] args = in.split(" ", 4);
						int cost = Integer.parseInt(args[1]);
						int maxTickets = Integer.parseInt(args[2]);
						String name = args[3];
						Giveaway.startGiveaway(new Giveaway(name, cost, maxTickets));
					}
				}
				else
				{
					Log.log("Unable to recognize the input data. Maybe the command is incorrect or doesn't exist.");
				}
			}
			catch (Throwable t)
			{
				Log.log("%s\n%s", t.getMessage(), ExceptionUtils.getStackTrace(t));
			}
		}
	}
	
}
