package catdany.grindbot.grind;

import java.io.File;
import java.util.HashMap;

import com.google.common.io.Files;

import catdany.grindbot.log.Log;
import catdany.grindbot.utils.Misc;

public class Database
{
	public static HashMap<String, Integer> bankDatabase = new HashMap<String, Integer>();
	
	public static void save()
	{
		Log.log("Saving the database...");
		try
		{
			File bankFile = new File("bank.dat");
			File bankBackupFile = new File("bank_backup.dat");
			if (bankBackupFile.exists() && bankBackupFile.isFile())
			{
				bankBackupFile.delete();
			}
			if (bankFile.exists() && bankFile.isFile())
			{
				bankFile.renameTo(bankBackupFile);
			}
			Misc.writeToFile(bankFile, Misc.serialize(bankDatabase));
			Log.log("Database saved.");
		}
		catch (Throwable t)
		{
			Log.logStackTrace(t, "An exception was caught while trying to save the database. If it's corrupted, try using a backup file.");
		}
	}
	
	public static void load()
	{
		Log.log("Loading the database...");
		try
		{
			File bankFile = new File("bank.dat");
			if (!bankFile.exists() || !bankFile.isFile())
			{
				Log.log("Skipping database load, no database file found. Probably because it's the first run.");
			}
			else
			{
				bankDatabase = Misc.deserialize(bankDatabase, Files.toByteArray(bankFile));
				Log.log("Database loaded");
			}
		}
		catch (Throwable t)
		{
			Log.logStackTrace(t, "An exception was caught while trying to load the database from file. If it's corrupted, try using a backup file.");
		}
	}
}
