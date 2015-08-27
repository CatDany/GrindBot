package catdany.grindbot;

import catdany.grindbot.log.Log;

public class Main
{
	public static final String VERSION = "0.7-BRANCH-COLLECTION Internal Testing Build from 2015-08-27";
	
	public static Console console;
	public static GrindBotHandler botHandler;
	public static GrindBot bot;
	
	public static void main(String[] args)
	{
		Log.init();
		Log.log("Welcome to GrindBot. Current version is: %s", VERSION);
		ShutdownHandler.init();
		
		console = new Console();
		
		botHandler = new GrindBotHandler();
		bot = botHandler.init();
	}
}