package catdany.grindbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.exception.ExceptionUtils;

import catdany.grindbot.grind.Database;
import catdany.grindbot.log.Log;

public class Console implements Runnable
{
	public Console()
	{
		Log.log("Initializing console listener.");
		Thread t = new Thread(this, "Console-Listener");
		t.start();
	}
	
	@Override
	public void run()
	{
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		while (true)
		{
			try
			{
				String in = r.readLine();
				Log.log("Console input: %s", in);
				if (in.startsWith("reload"))
				{
					Settings.reload();
					Localization.reload();
				}
				else if (in.startsWith("stop"))
				{
					System.exit(0);
				}
				else if (in.startsWith("setbank"))
				{
					String[] args = in.split(" ", 3);
					String user = args[1].toLowerCase();
					int amount = Integer.parseInt(args[2]);
					Database.setBankStorage(user, amount);
				}
				else if (in.startsWith("addbank"))
				{
					String[] args = in.split(" ", 3);
					String user = args[1].toLowerCase();
					int amount = Integer.parseInt(args[2]);
					Database.setBankStorage(user, Database.getBankStorage(user) + amount);
				}
				else
				{
					Log.log("Unable to recognize the input data. Maybe the command is incorrect or doesn't exist.");
				}
			}
			catch (IOException t)
			{
				Log.log("%s\n%s", t.getMessage(), ExceptionUtils.getStackTrace(t));
			}
		}
	}
	
}
