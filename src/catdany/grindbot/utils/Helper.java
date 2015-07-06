package catdany.grindbot.utils;

import catdany.grindbot.Main;

public class Helper
{
	public static void chat(String format, Object... args)
	{
		Main.bot.chat(String.format(format, args));
	}
	
	public static void priv(String user, String format, Object... args)
	{
		Main.bot.priv(user, String.format(format, args));
	}
}