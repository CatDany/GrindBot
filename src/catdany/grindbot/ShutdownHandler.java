package catdany.grindbot;

import catdany.grindbot.grind.Database;
import catdany.grindbot.log.Log;

public class ShutdownHandler implements Runnable
{
	@Override
	public void run()
	{
		Log.log("It seems like we're shutting down. Attempting to finish...");
		Database.save();
	}
	
	public static void init()
	{
		Log.log("Attempting to register shutdown hook...");
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHandler()));
		Log.log("Shutdown hook registered successfully.");
	}
}
