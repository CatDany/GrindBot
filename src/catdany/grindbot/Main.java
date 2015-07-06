package catdany.grindbot;

import catdany.grindbot.log.Log;

public class Main
{
	public static Console console;
	public static GrindBotHandler botHandler;
	public static GrindBot bot;
	
	public static void main(String[] args)
	{
		Log.init();
		Log.log("This is a first line of the log, just to test things out. Have a nice day!");
		ShutdownHandler.init();
		
		console = new Console();
		
		botHandler = new GrindBotHandler();
		bot = botHandler.init();
	}
}