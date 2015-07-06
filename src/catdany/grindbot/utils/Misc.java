package catdany.grindbot.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.google.common.io.Files;

public class Misc
{
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	public static final Random random = new Random();
	
	public static long time()
	{
		return System.currentTimeMillis();
	}
	
	public static String formatDate()
	{
		return dateFormat.format(new Date(time()));
	}
	
	public static String formatDateFile()
	{
		return fileDateFormat.format(new Date(time()));
	}
	
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
}
