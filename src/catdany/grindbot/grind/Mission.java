package catdany.grindbot.grind;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import catdany.grindbot.Localization;
import catdany.grindbot.Settings;
import catdany.grindbot.log.Log;
import catdany.grindbot.utils.Misc;

public class Mission
{
	public static List<String> smallMissions;
	public static List<String> mediumMissions;
	public static List<String> largeMissions;
	
	public static long lastMissionTime = 0;
	public static Mission currentMission = null;
	
	public final String name;
	public final int size;
	public final ArrayList<String> entries = new ArrayList<String>();
	
	public Mission(String name, int size)
	{
		this.name = name;
		this.size = size;
	}
	
	public String[] rollWinners()
	{
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < getPeopleRequired(); i++)
		{
			String winner = entries.get(Misc.random(entries.size()));
			entries.remove(winner);
			list.add(winner);
		}
		return list.toArray(new String[0]);
	}
	
	public String getLocalizedSize()
	{
		switch (size)
		{
		case 0:
			return Localization.get(Localization.MISSION_SIZE_SMALL);
		case 1:
			return Localization.get(Localization.MISSION_SIZE_MEDIUM);
		case 2:
			return Localization.get(Localization.MISSION_SIZE_LARGE);
		}
		Log.logWithException("Size of a mission is %s, not fitting in range (0-2)", size);
		return null;
	}
	
	public int getCost()
	{
		switch (size)
		{
		case 0:
			return Integer.parseInt(Settings.MISSION_SMALL_COST);
		case 1:
			return Integer.parseInt(Settings.MISSION_MEDIUM_COST);
		case 2:
			return Integer.parseInt(Settings.MISSION_LARGE_COST);
		}
		Log.logWithException("Size of a mission is %s, not fitting in range (0-2)", size);
		return -1;
	}
	
	public int getReward()
	{
		switch (size)
		{
		case 0:
			return Integer.parseInt(Settings.MISSION_SMALL_REWARD);
		case 1:
			return Integer.parseInt(Settings.MISSION_MEDIUM_REWARD);
		case 2:
			return Integer.parseInt(Settings.MISSION_LARGE_REWARD);
		}
		Log.logWithException("Size of a mission is %s, not fitting in range (0-2)", size);
		return -1;
	}
	
	public int getPeopleRequired()
	{
		switch (size)
		{
		case 0:
			return Integer.parseInt(Settings.MISSION_SMALL_PEOPLE);
		case 1:
			return Integer.parseInt(Settings.MISSION_MEDIUM_PEOPLE);
		case 2:
			return Integer.parseInt(Settings.MISSION_LARGE_PEOPLE);
		}
		Log.logWithException("Size of a mission is %s, not fitting in range (0-2)", size);
		return -1;
	}
	
	public static Mission createMission()
	{
		int size = randomMissionSize();
		String name = randomMissionName(size);
		return new Mission(name, size);
	}
	
	/**
	 * Randomize mission size (small, medium or large) depending on {@linkplain Settings} (see {@link Settings#MISSION_SMALL_WEIGHT 1}, {@link Settings#MISSION_MEDIUM_WEIGHT 2}, {@link Settings#MISSION_LARGE_WEIGHT 3})
	 * @return 0 (small); 1 (medium); 2 (large)
	 */
	public static int randomMissionSize()
	{
		int weightSmall = Integer.parseInt(Settings.MISSION_SMALL_WEIGHT);//2
		int weightMedium = Integer.parseInt(Settings.MISSION_MEDIUM_WEIGHT);//3
		int weightLarge = Integer.parseInt(Settings.MISSION_LARGE_WEIGHT);//4
		
		int weightAll = weightSmall + weightMedium + weightLarge;
		int random = Misc.random(weightAll);
		if (random >= weightSmall + weightMedium)
			return 2;
		else if (random >= weightSmall)
			return 1;
		else
			return 0;
	}
	
	/**
	 * Get random mission name for given size
	 * @param size
	 * @return
	 */
	public static String randomMissionName(int size)
	{
		List<String> list = null;
		switch (size)
		{
		case 0:
			list = smallMissions;
			break;
		case 1:
			list = mediumMissions;
			break;
		case 2:
			list = largeMissions;
			break;
		}
		return list.get(Misc.random(list.size()));
	}
	
	public static void init()
	{
		Log.log("Loading missions...");
		try
		{
			File fileSmall = new File("missions_small.txt");
			File fileMedium = new File("missions_medium.txt");
			File fileLarge = new File("missions_large.txt");
			smallMissions = Files.readAllLines(fileSmall.toPath(), Charset.defaultCharset());
			mediumMissions = Files.readAllLines(fileMedium.toPath(), Charset.defaultCharset());
			largeMissions = Files.readAllLines(fileLarge.toPath(), Charset.defaultCharset());
		}
		catch (Throwable t)
		{
			Log.logStackTrace(t, "An exception was thrown while trying to load mission names from file.");
			Log.log("Unable to load missions. Shutting down.");
			System.exit(-1);
		}
		Log.log("Missions loaded.");
	}
}
