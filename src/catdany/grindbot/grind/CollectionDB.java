package catdany.grindbot.grind;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import catdany.grindbot.Localization;
import catdany.grindbot.log.Log;
import catdany.grindbot.utils.Helper;
import catdany.grindbot.utils.Misc;

public class CollectionDB
{
	private static HashMap<String, String> collectionDatabase = new HashMap<String, String>();
	public static final ArrayList<CollectionItem> collectionList = new ArrayList<CollectionItem>();
	
	public static long lastCollectionBoxPurchase = 0;
	
	public static CollectionRarity getRandomRarity()
	{
		int weightAll = CollectionRarity.COMMON.weight + CollectionRarity.UNCOMMON.weight + CollectionRarity.RARE.weight + CollectionRarity.EPIC.weight + CollectionRarity.LEGENDARY.weight;
		int random = Misc.random(weightAll);
		if (random >= CollectionRarity.COMMON.weight + CollectionRarity.UNCOMMON.weight + CollectionRarity.RARE.weight + CollectionRarity.EPIC.weight)
			return CollectionRarity.LEGENDARY;
		else if (random >= CollectionRarity.COMMON.weight + CollectionRarity.UNCOMMON.weight + CollectionRarity.RARE.weight)
			return CollectionRarity.EPIC;
		else if (random >= CollectionRarity.COMMON.weight + CollectionRarity.UNCOMMON.weight)
			return CollectionRarity.RARE;
		else if (random >= CollectionRarity.COMMON.weight)
			return CollectionRarity.UNCOMMON;
		else
			return CollectionRarity.COMMON;
	}
	
	public static CollectionItem getRandomItem(CollectionRarity rarity)
	{
		ArrayList<CollectionItem> pool = new ArrayList<CollectionItem>();
		for (CollectionItem i : collectionList)
		{
			if (i.rarity == rarity)
			{
				pool.add(i);
			}
		}
		return pool.get(Misc.random(pool.size()));
	}
	
	public static CollectionItem openBox(String user)
	{
		CollectionItem item = getRandomItem(getRandomRarity());
		boolean isNew = addToCollection(user, item.id);
		Log.log("%s opened a box with a collectible item and won %s (%s)", user, item.name, item.rarity);
		Helper.chatLocal(isNew ? Localization.COLLECTION_BOX_OPEN : Localization.COLLECTION_BOX_OPEN_DUPLICATE, user, item.name, Localization.get(Localization.COLLECTION_RARITY_ + item.rarity));
		return item;
	}
	
	public static boolean[] getCollection(String user)
	{
		if (collectionDatabase.containsKey(user))
		{
			return Helper.parseBinaryString(collectionDatabase.get(user));
		}
		else
		{
			return Helper.parseBinaryString(getStarterCollection());
		}
	}
	
	public static String getStarterCollection()
	{
		int all = collectionList.size();
		char[] chars = new char[all];
		for (int i = 0; i < all; i++)
		{
			chars[i] = '0';
		}
		return new String(chars);
	}
	
	public static boolean addToCollection(String user, int id)
	{
		boolean[] bits = getCollection(user);
		if (bits[id])
		{
			return false;
		}
		else
		{
			bits[id] = true;
			String value = Helper.toBinaryString(bits);
			collectionDatabase.put(user, value);
			Helper.syncCollection(user, value);
			return true;
		}
	}
	
	/**
	 * Get {@link CollectionRarity} from String (common, uncommon, rare, epic, legendary)
	 * @return
	 */
	public static CollectionRarity getRarity(String str)
	{
		switch (str)
		{
		case "common":
			return CollectionRarity.COMMON;
		case "uncommon":
			return CollectionRarity.UNCOMMON;
		case "rare":
			return CollectionRarity.RARE;
		case "epic":
			return CollectionRarity.EPIC;
		case "legendary":
			return CollectionRarity.LEGENDARY;
		}
		Log.log("String %s does not represent a rarity. Returning %s", str, CollectionRarity.COMMON);
		return CollectionRarity.COMMON;
	}
	
	public static void save()
	{
		Log.log("Saving the collection database...");
		try
		{
			File collectionFile = new File("collection.dat");
			File collectionBackupFile = new File("collection_backup.dat");
			if (collectionBackupFile.exists() && collectionBackupFile.isFile())
			{
				collectionBackupFile.delete();
			}
			if (collectionFile.exists() && collectionFile.isFile())
			{
				collectionFile.renameTo(collectionBackupFile);
			}
			Misc.writeToFile(collectionFile, Misc.serialize(collectionDatabase));
			Log.log("Collection database saved.");
		}
		catch (Throwable t)
		{
			Log.logStackTrace(t, "An exception was caught while trying to save the collection database. If it's corrupted, try using a backup file.");
		}
	}
	
	public static void load()
	{
		Log.log("Loading the collection database...");
		try
		{
			File collectionFile = new File("collection.dat");
			if (!collectionFile.exists() || !collectionFile.isFile())
			{
				Log.log("Skipping collection database load, no database file found. Probably because it's the first run.");
			}
			else
			{
				collectionFile = Misc.deserialize(collectionFile, com.google.common.io.Files.toByteArray(collectionFile));
				Log.log("Collection database loaded.");
			}
		}
		catch (Throwable t)
		{
			Log.logStackTrace(t, "An exception was caught while trying to load the collection database from file. If it's corrupted, try using a backup file.");
		}
	}
	
	public static void loadCollection()
	{
		Log.log("Loading collection lines...");
		try
		{
			File file = new File("collection.txt");
			List<String> lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
			for (String i : lines)
			{
				String[] split = i.split(" ", 2);
				CollectionItem item = new CollectionItem(collectionList.size(), split[1], getRarity(split[0]));
				collectionList.add(item);
				Log.log("Added a collectible item to the list: %s (%s)", item.name, item.rarity);
			}
		}
		catch (Throwable t)
		{
			Log.logStackTrace(t, "An exception was caught while trying to load the collection list.");
		}
	}
	
	public static class CollectionItem
	{
		public final int id;
		public final CollectionRarity rarity;
		public final String name;
		
		public CollectionItem(int id, String name, CollectionRarity rarity)
		{
			this.id = id;
			this.name = name;
			this.rarity = rarity;
		}
	}
	
	public static enum CollectionRarity
	{
		COMMON(45),
		UNCOMMON(26),
		RARE(20),
		EPIC(8),
		LEGENDARY(1);
		
		public final int weight;
		
		private CollectionRarity(int weight)
		{
			this.weight = weight;
		}
		
		@Override
		public String toString()
		{
			return super.toString().toLowerCase();
		}
	}
}