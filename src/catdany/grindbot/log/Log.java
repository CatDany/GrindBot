package catdany.grindbot.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.lang3.exception.ExceptionUtils;

import catdany.grindbot.utils.Misc;

public class Log
{
	private static File logFile;
	private static PrintWriter writer;
	
	public static void init()
	{
		File logDir = new File("logs");
		if (!logDir.exists() || !logDir.isDirectory())
		{
			logDir.mkdir();
		}
		logFile = new File(logDir, Misc.formatDateFile() + ".txt");
		System.out.println(logFile.getAbsolutePath());
		if (logFile.exists() && logFile.isFile())
		{
			logFile.delete();
		}
		try
		{
			logFile.createNewFile();
			writer = new PrintWriter(new BufferedWriter(new FileWriter(logFile.getAbsolutePath(), true)));
		}
		catch (IOException t)
		{
			t.printStackTrace();
		}
	}
	
	public static boolean isInitialized()
	{
		return logFile != null && writer != null;
	}
	
	public static void log(String format, Object... args)
	{
		String str = String.format(format, args);
		String out = String.format("[%s] [%s] %s", Thread.currentThread().getName(), Misc.formatDate(), str);
		System.out.println(out);
		if (isInitialized())
		{
			writer.println(out);
			writer.flush();
		}
		else
		{
			System.err.println("[LOGGER-ERROR] File logger isn't initialized.");
		}
	}
	
	public static void logStackTrace(Throwable t, String format, Object... args)
	{
		Log.log("%s\n%s", String.format(format, args), ExceptionUtils.getStackTrace(t));
	}
}