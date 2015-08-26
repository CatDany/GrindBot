package catdany.grindbot.grind;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import catdany.grindbot.log.Log;
import catdany.grindbot.utils.Misc;

import com.google.common.io.Files;

public class Database
{
	private static HashMap<String, Integer> bankDatabase = new HashMap<String, Integer>();
	
	public static int getBankStorage(String user)
	{
		if (bankDatabase.containsKey(user))
		{
			return bankDatabase.get(user);
		}
		else
		{
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getTopUsers(int amount)
	{
		ArrayList<String> list = new ArrayList<String>();
		List<Entry<String, Integer>> top = Misc.getTop((HashMap<String, Integer>)bankDatabase.clone(), amount);
		for (Entry<String, Integer> i : top)
		{
			list.add(i.getKey() + " [" + i.getValue() + "]");
		}
		return list;
	}
	
	public static void setBankStorage(String user, int amount)
	{
		bankDatabase.put(user, amount);
	}
	
	public static boolean withdraw(String user, int amount)
	{
		int status = getBankStorage(user);
		if (status < amount)
		{
			return false;
		}
		else
		{
			setBankStorage(user, status - amount);
			return true;
		}
	}
	
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
				Log.log("Database loaded.");
			}
		}
		catch (Throwable t)
		{
			Log.logStackTrace(t, "An exception was caught while trying to load the database from file. If it's corrupted, try using a backup file.");
		}
	}
}
