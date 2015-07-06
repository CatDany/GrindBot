package catdany.grindbot;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.output.OutputIRC;

public class GrindBot extends PircBotX
{
	public final OutputIRC irc;
	
	public GrindBot(Configuration<GrindBot> config)
	{
		super(config);
		
		this.irc = new OutputIRC(this);
	}
	
	@Override
	public OutputIRC sendIRC()
	{
		return irc;
	}
	
	public void chat(String input)
	{
		irc.message("#" + Settings.CHANNEL, input);
	}
	
	public void priv(String user, String input)
	{
		irc.message("#" + Settings.CHANNEL, String.format(".w %s %s", user, input));
	}
}
