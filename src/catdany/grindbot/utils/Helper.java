package catdany.grindbot.utils;

import catdany.grindbot.Localization;
import catdany.grindbot.Main;
import catdany.grindbot.Settings;
import catdany.grindbot.grind.Database;

public class Helper
{
	/**
	 * Send a message in chat
	 * @param format
	 * @param args
	 */
	public static void chat(String format, Object... args)
	{
		Main.bot.chat(String.format(format, args));
	}
	
	/**
	 * Send a message in chat. Message text is taken from localization files.
	 * @param key
	 * @param args
	 */
	public static void chatLocal(String key, Object... args)
	{
		Main.bot.chat(Localization.get(key, args));
	}
	
	/**
	 * Send a private message. WARNING: Not functional atm.
	 * @param user
	 * @param format
	 * @param args
	 */
	@Deprecated
	public static void priv(String user, String format, Object... args)
	{
		Main.bot.priv(user, String.format(format, args));
	}
	
	/**
	 * Send a private message. Message text is taken from localization files. WARNING: Not functional atm.
	 * @param user
	 * @param key
	 * @param args
	 */
	@Deprecated
	public static void privLocal(String user, String key, Object... args)
	{
		Main.bot.priv(user, Localization.get(key, args));
	}
	
	/**
	 * Send a raw line to IRC server
	 * @param format
	 * @param args
	 */
	public static void raw(String format, Object... args)
	{
		Main.bot.raw(String.format(format, args));
	}
	
	/**
	 * Replaces all '%' signs in the string with double '%' sign, so {@code format} method doesn't throw an exception 
	 */
	public static String fixString(String s)
	{
		return s.replace("%", "%%");
	}
	
	/**
	 * See {@link Helper#fixString(String) fixString()}<br>
	 * Does not modify the original array, returns a modified clone
	 * @param array
	 * @return
	 */
	public static Object[] fixArray(Object... array)
	{
		Object[] arrayClone = array.clone();
		for (int i = 0; i < array.length; i++)
		{
			arrayClone[i] = fixString(array[i].toString());
		}
		return arrayClone;
	}
	
	private static long lastTopList = 0;
	
	@SuppressWarnings("unchecked")
	public static void listTop()
	{
		long cooldown = Integer.parseInt(Settings.LIST_TOP_COOLDOWN) * 1000;
		long time = Misc.time();
		if (lastTopList + cooldown < time)
		{
			Helper.chatLocal(Localization.LIST_TOP, Settings.LIST_TOP_AMOUNT, Misc.arrayToString(", ", Database.getTopUsers(Integer.parseInt(Settings.LIST_TOP_AMOUNT))));
		}
	}
}