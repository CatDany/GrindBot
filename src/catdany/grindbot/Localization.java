package catdany.grindbot;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import catdany.grindbot.log.Log;
import catdany.grindbot.utils.Misc;

public class Localization
{
	public static final HashMap<String, String> localizationMap = new HashMap<String, String>();
	
	public static final String BOT_JOINED = "bot_joined";
	public static final String YOUR_BANK_STATUS = "your_bank_status";
	
	public static final String MISSION_STARTED = "mission_started";
	public static final String MISSION_ENDED = "mission_ended";
	public static final String MISSION_WINNERS = "mission_winners";
	public static final String MISSION_NOT_ENOUGH_PEOPLE = "mission_not_enough_people";
	public static final String MISSION_SIZE_SMALL = "mission_size_small";
	public static final String MISSION_SIZE_MEDIUM = "mission_size_medium";
	public static final String MISSION_SIZE_LARGE = "mission_size_large";
	
	public static final String GIVEAWAY_START = "giveaway_start";
	public static final String GIVEAWAY_SOON = "giveaway_soon";
	public static final String GIVEAWAY_GO = "giveaway_go";
	public static final String GIVEAWAY_END = "giveaway_end";
	
	public static final String LIST_TOP = "list_top";
	public static final String BOT_INFO = "bot_info";
	
	public static final String COLLECTION_BOX_PURCHASE = "collection_box_purchase";
	public static final String COLLECTION_BOX_OPEN = "collection_box_open";
	public static final String COLLECTION_BOX_OPEN_DUPLICATE = "collection_box_open_duplicate";
	public static final String COLLECTION_OVERVIEW = "collection_overview";
	public static final String COLLECTION_RARITY_ = "collection_rarity_";
	
	public static String get(String key, Object... args)
	{
		if (localizationMap.containsKey(key))
		{
			if (args.length == 0)
			{
				return localizationMap.get(key);
			}
			else
			{
				return String.format(localizationMap.get(key), args);
			}
		}
		else
		{
			if (args.length == 0)
			{
				return key;
			}
			else
			{
				return String.format("%s[%s]", key, Misc.arrayToString(";", args));
			}
		}
	}
	
	public static void reload()
	{
		boolean success = true;
		Log.log("Loading localization lines...");
		try
		{
			String localeKey = Settings.LOCALE;
			File file = new File(String.format("lang\\%s.lang", localeKey));
			List<String> list = Files.readAllLines(file.toPath(), Settings.CHARSET);
			for (String i : list)
			{
				String key = i.split("=", 2)[0];
				String value = i.split("=", 2)[1];
				localizationMap.put(key, value);
				Log.log("Localization line loaded. %s=%s", key, value);
			}
		}
		catch (Throwable t)
		{
			success = false;
			Log.logStackTrace(t, "An exception was caught while trying to load localization lines from file.");
		}
		if (success)
		{
			Log.log("Localization loaded.");
		}
	}
}
