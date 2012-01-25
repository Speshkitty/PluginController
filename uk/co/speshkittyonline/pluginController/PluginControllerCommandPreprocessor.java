package uk.co.speshkittyonline.pluginController;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

public class PluginControllerCommandPreprocessor extends PlayerListener
{

	public PluginControllerCommandPreprocessor(PluginController pluginController) { }
	
	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if(event.isCancelled()) { return; }
		String[] args = event.getMessage().split(" ");

	    if(args[0].equalsIgnoreCase("/plugins"))
	    {
	    	String message = "";
	    	for(int i=1;i<args.length;i++) { message = message.concat(args[i]).concat(" "); }
	    	
	    	event.setCancelled(true);
	    	event.getPlayer().chat("/plugin " + message.trim());
	    }
	}
}
