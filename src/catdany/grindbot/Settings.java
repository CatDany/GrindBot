package catdany.grindbot;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import catdany.grindbot.log.Log;

import com.google.common.base.Charsets;

public class Settings
{
	public static final Charset CHARSET = Charsets.UTF_8;
	
	public static String CHANNEL;
	public static String NAME;
	public static String OAUTH_TOKEN;
	public static String LOCALE;
	
	public static String PASSIVE_GAIN_BANK;
	public static String PASSIVE_GAIN_BANK_COOLDOWN;
	
	public static void reload()
	{
		Log.log("Loading settings...");
		try
		{
			File fileSettings = new File("settings.txt");
			List<String> listSettings = Files.readAllLines(fileSettings.toPath(), Charset.defaultCharset());
			for (String i : listSettings)
			{
				if (i.equals(""))
				{
					continue;
				}
				String key = i.split("=", 2)[0];
				String value = i.split("=", 2)[1];
				Field f = Settings.class.getField(key.toUpperCase());
				f.set(null, value);
			}
		}
		catch (Throwable t)
		{
			Log.logStackTrace(t, "An exception was thrown while trying to load settings from file.");
			Log.log("Unable to load settings. Shutting down.");
			System.exit(-1);
		}
		Log.log("Settings loaded.");
	}
}
