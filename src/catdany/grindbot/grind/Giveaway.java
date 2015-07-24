package catdany.grindbot.grind;

import java.util.ArrayList;

import catdany.grindbot.Localization;
import catdany.grindbot.log.Log;
import catdany.grindbot.utils.Helper;
import catdany.grindbot.utils.Misc;

public class Giveaway implements Runnable
{
	public static Giveaway currentGiveaway = null;
	
	public final String name;
	public final int cost;
	public final int maxTickets;
	
	public final ArrayList<String> tickets = new ArrayList<String>();
	
	public Giveaway(String name, int cost, int maxTickets)
	{
		this.name = name;
		this.cost = cost;
		this.maxTickets = maxTickets;
	}
	
	public static void startGiveaway(Giveaway g)
	{
		Thread t = new Thread(g, "Giveaway-Thread-" + Math.random());
		t.start();
	}
	
	@Override
	public void run()
	{
		Helper.chatLocal(Localization.GIVEAWAY_START, name, cost, maxTickets);
		Misc.sleep(3000);
		Helper.chatLocal(Localization.GIVEAWAY_SOON);
		Misc.sleep(15000);
		currentGiveaway = this;
		Helper.chatLocal(Localization.GIVEAWAY_GO);
	}
	
	public void addTickets(String user, int amount)
	{
		if (!tickets.contains(user) && amount <= maxTickets && Database.withdraw(user, cost * amount))
		{
			for (int i = 0; i < amount; i++)
			{
				tickets.add(user);
			}
			Log.log("%s entered a giveaway with %s tickets.", user, amount);
		}
	}
	
	public String rollWinner()
	{
		return tickets.get(Misc.random(tickets.size()));
	}
	
	public int getTicketsCount(String user)
	{
		int count = 0;
		for (String i : tickets)
		{
			if (i.equals(user))
			{
				count++;
			}
		}
		return count;
	}
}
