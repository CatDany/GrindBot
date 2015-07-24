package catdany.grindbot.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Random;

import catdany.grindbot.log.Log;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class Misc
{
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	public static final Random random = new Random();
	
	/**
	 * Get current time in millis<br>
	 * Shorter version of {@linkplain System#currentTimeMillis()}
	 * @return
	 */
	public static long time()
	{
		return System.currentTimeMillis();
	}
	
	/**
	 * Get formatted date<br>
	 * Format is {@linkplain Misc#dateFormat} (yyyy/MM/dd HH:mm:ss)
	 * @return
	 */
	public static String formatDate()
	{
		return dateFormat.format(new Date(time()));
	}
	
	/**
	 * Get formatted date for file<br>
	 * Format is {@linkplain Misc#fileDateFormat} (yyyy-MM-dd HH-mm-ss)
	 * @return
	 */
	public static String formatDateFile()
	{
		return fileDateFormat.format(new Date(time()));
	}
	
	/**
	 * Get random integer from <code>0</code> to <code>bound-1</code>
	 * @param bound
	 * @return
	 */
	public static int random(int bound)
	{
		return random.nextInt(bound);
	}
	
	//XXX: Check for ClassCastException https://stackoverflow.com/questions/12462079
	@SuppressWarnings("unchecked")
	public static <T>String arrayToString(String separator, T... array)
	{
		String str = "";
		for (T i : array)
		{
			str += i.toString() + separator;
		}
		str = str.substring(0, str.length() - separator.length());
		return str;
	}
	
	@SuppressWarnings("unchecked")
	public static <T>String arrayToStringNum(String separator, T... array)
	{
		String str = "";
		int count = 0;
		for (T i : array)
		{
			count++;
			str += count + ". " + i.toString() + separator;
		}
		str = str.substring(0, str.length() - separator.length());
		return str;
	}
	
	public static void writeToFile(File file, byte[] bytes) throws IOException
	{
		Files.write(bytes, file);
	}
	
	public static byte[] serialize(Serializable o) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(o);
		byte[] bytes = bos.toByteArray();
		oos.close();
		bos.close();
		return bytes;
	}
	
	@SuppressWarnings("unchecked")
	public static <T>T deserialize(T type, byte[] bytes) throws IOException, ClassNotFoundException, ClassCastException
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Object o = ois.readObject();
		bis.close();
		ois.close();
		return (T)o;
	}
	
	public static void sleep(long time)
	{
		try
		{
			Thread.sleep(time);
		}
		catch (InterruptedException t)
		{
			Log.logStackTrace(t, "OMG, I can't even ResidentSleeper right now! What the heck!?");
		}
	}
	
	public static <K, V extends Comparable<? super V>> List<Entry<K, V>> getTop(Map<K, V> map, int n)
	{
		Comparator<? super Entry<K, V>> comparator = new Comparator<Entry<K, V>>()
		{
			@Override
			public int compare(Entry<K, V> e0, Entry<K, V> e1)
			{
				V v0 = e0.getValue();
				V v1 = e1.getValue();
				return v0.compareTo(v1);
			}
		};
		
		PriorityQueue<Entry<K, V>> highest = 
		new PriorityQueue<Entry<K,V>>(n, comparator);
		
		for (Entry<K, V> entry : map.entrySet())
		{
			highest.offer(entry);
			while (highest.size() > n)
			{
				highest.poll();
			}
		}
		
		ArrayList<Entry<K, V>> result = new ArrayList<Map.Entry<K,V>>();
		while (highest.size() > 0)
		{
			result.add(highest.poll());
		}
		return Lists.reverse(result);
	}
}