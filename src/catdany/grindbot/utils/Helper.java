package catdany.grindbot.utils;

import java.io.InputStream;
import java.net.URL;

import catdany.grindbot.Localization;
import catdany.grindbot.Main;
import catdany.grindbot.Settings;
import catdany.grindbot.grind.Database;
import catdany.grindbot.log.Log;

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
	 * Send a private message.
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
	 * Send a private message. Message text is taken from localization files.
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
	
	public static void listTop()
	{
		long cooldown = Integer.parseInt(Settings.LIST_TOP_COOLDOWN) * 1000;
		long time = Misc.time();
		if (lastTopList + cooldown < time)
		{
			Helper.chatLocal(Localization.LIST_TOP, Settings.LIST_TOP_AMOUNT, Misc.arrayToStringNum(" | ", Database.getTopUsers(Integer.parseInt(Settings.LIST_TOP_AMOUNT)).toArray(new String[0])));
		}
	}
	
	/**
	 * Parse a binary string (e.g. "01010") to a boolean array<br>
	 * "01010" => {false, true, false, true, false}<br>
	 * An opposite of {@link Helper#toBinaryString(boolean[])}
	 * @param str
	 * @return
	 */
	public static boolean[] parseBinaryString(String str)
	{
		char[] chars = str.toCharArray();
		boolean[] bits = new boolean[chars.length];
		for (int i = 0; i < chars.length; i++)
		{
			bits[i] = chars[i] == '1';
		}
		return bits;
	}
	
	/**
	 * Convert a boolean array into a binary string (e.g. "01010")<br>
	 * {false, true, false} => "010"<br>
	 * An opposite of {@link Helper#parseBinaryString(String)}
	 * @param bits
	 * @return
	 */
	public static String toBinaryString(boolean[] bits)
	{
		char[] chars = new char[bits.length];
		for (int i = 0; i < bits.length; i++)
		{
			chars[i] = bits[i] ? '1' : '0';
		}
		return new String(chars);
	}
	
	public static void syncCollection(String user, String value)
	{
		try
		{
			URL url = new URL(String.format(Settings.COLLECTION_SYNC_URL, user, value));
			InputStream is = url.openStream();
			is.close();
			Log.log("Successfully synced collection with remote server.");
		}
		catch (Throwable t)
		{
			Log.logStackTrace(t, "Unable to sync collection for user %s (value: %s)", user, value);
		}
	}
}