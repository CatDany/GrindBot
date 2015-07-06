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
	
	public static String get(String key, Object... args)
	{
		if (localizationMap.containsKey(key))
		{
			//
		}
		else
		{
			return String.format("%s[%s]", key, Misc.arrayToString(";", args));
		}
		if (args.length == 0)
		{
			return localizationMap.get(key);
		}
		else
		{
			return String.format(localizationMap.get(key), args);
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
