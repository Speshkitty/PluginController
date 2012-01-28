package uk.co.speshkittyonline.pluginController;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginController extends JavaPlugin implements Listener
{
	private Permission permEnable = new Permission("plugins.enable"), permDisable = new Permission("plugins.disable");
	private Permission permReload = new Permission("plugins.reload"), permLoad = new Permission("plugins.load");
	private Permission permList = new Permission("plugins.list");
	private PluginManager pm;
	
	private String[] help = { //Use £ as separator between  section and help, ie list£Lists all plugins on the server 
			"help£" + ChatColor.DARK_GREEN + "/plugins help " + ChatColor.WHITE + "-" + ChatColor.GREEN + "Prints all the help.",
			"help£" + ChatColor.DARK_GREEN + "/plugins help [subcommand]" + ChatColor.WHITE + "-" + ChatColor.GREEN + "Prints command specific help.",
			"list£" + ChatColor.DARK_GREEN + "/plugins " + ChatColor.WHITE + "-" + ChatColor.GREEN + " Lists all the plugins currently loaded on the server.",
			"load£" + ChatColor.DARK_GREEN + "/plugins load " + ChatColor.WHITE + "-" + ChatColor.GREEN + " Loads a plugin by filename.",
			"enable£" + ChatColor.DARK_GREEN + "/plugins enable [plugin] " + ChatColor.WHITE + "-" + ChatColor.GREEN + " Enables a plugin.",
			"disable£" + ChatColor.DARK_GREEN + "/plugins disable [plugin] " + ChatColor.WHITE + "-" + ChatColor.GREEN + " Disables a plugin.",
			"reload£" + ChatColor.DARK_GREEN + "/plugins reload [plugin] " + ChatColor.WHITE + "-" + ChatColor.GREEN + " Reloads a plugin."
			};
	private ArrayList<String> helpValid = new ArrayList<String>();
	
	@Override
	public void onDisable()
	{
		helpValid.clear();
		Log("Plugin controller shutdown.");
	}

	@Override
	public void onEnable()
	{
		helpValid.add("help");
		helpValid.add("list");
		helpValid.add("load");
		helpValid.add("enable");
		helpValid.add("disable");
		helpValid.add("reload");
		pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		Log("Plugin controller started.");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		pm = getServer().getPluginManager();
		if(args.length == 0) //Treat as /plugins list
		{
			if(sender.hasPermission(permList))
			{
				Plugin[] array = pm.getPlugins();
				String names = "";
				int countDisabled = 0;
				for(int i=0;i<array.length;i++)
				{
					if(array[i].isEnabled()) { names = names.concat(ChatColor.GREEN + array[i].getDescription().getFullName()).concat(ChatColor.WHITE + ", "); }
					else { names = names.concat(ChatColor.RED + array[i].getDescription().getFullName()).concat(ChatColor.WHITE + ", "); countDisabled++; }
				}
				sender.sendMessage("There is " + ChatColor.GREEN + array.length + ChatColor.WHITE + " plugins on this server (" + ChatColor.RED + countDisabled + " disabled" + ChatColor.WHITE + ").");
				sender.sendMessage(names.substring(0, names.length()-2));
			}
			else { sender.sendMessage(ChatColor.RED + "You don't have permission to use that command."); }
		}
		else if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("list"))
			{
				if(sender.hasPermission(permList))
				{
					Plugin[] array = pm.getPlugins();
					String names = "";
					int countDisabled = 0;
					for(int i=0;i<array.length;i++)
					{
						if(array[i].isEnabled()) { names = names.concat(ChatColor.GREEN + array[i].getDescription().getFullName()).concat(ChatColor.WHITE + ", "); }
						else { names = names.concat(ChatColor.RED + array[i].getDescription().getFullName()).concat(ChatColor.WHITE + ", "); countDisabled++; }
					}
					sender.sendMessage("There is " + ChatColor.GREEN + array.length + ChatColor.WHITE + " plugins on this server (" + ChatColor.RED + countDisabled + " disabled" + ChatColor.WHITE + ").");
					sender.sendMessage(names.substring(0, names.length()-2));
				}
				else { sender.sendMessage(ChatColor.RED + "You don't have permission to use that command."); }
			}
			else if(args[0].equalsIgnoreCase("help"))
			{
				for(int i=0;i<help.length;i++)
				{
					String[] helpText = help[i].split("£", 2);
					if(sender.hasPermission("plugins." + helpText[0]) || helpText[0].equalsIgnoreCase("help")) { sender.sendMessage(helpText[1]); }
				}
			}
			else
			{
				boolean returned = false;
				for(int i=0;i<help.length;i++)
				{
					String[] helpText = help[i].split("£", 2);
					if(helpText[0].equalsIgnoreCase(args[0]))
					{
						sender.sendMessage(helpText[1]);
						returned = true;
					}
				}
				if(returned) { return true; }
				
				sender.sendMessage(ChatColor.RED + "Argument not recognised.");
				for(int i=0;i<help.length;i++)
				{
					String[] helpText = help[i].split("£", 2);
					if(sender.hasPermission("plugins." + helpText[0]) || helpText[0].equalsIgnoreCase("help"))
					{
						sender.sendMessage(helpText[1]);
					}
				}
			}
		}
		else if(args.length == 2)
		{
			if(args[0].equalsIgnoreCase("enable"))
			{
				if(sender.hasPermission(permEnable))
				{
					Plugin plugin = pm.getPlugin(args[1]);
					if(plugin != null)
					{
						if(plugin.isEnabled()) { sender.sendMessage(ChatColor.RED + "That plugin is already enabled!"); }
						else
						{
							pm.enablePlugin(plugin);
							sender.sendMessage(ChatColor.DARK_GREEN + plugin.getDescription().getName() + ChatColor.GREEN + " was enabled!");
						}
					}
					else { sender.sendMessage(ChatColor.RED + "The plugin " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not loaded!"); }
				}
				else { sender.sendMessage(ChatColor.RED + "You don't have permission to enable plugins."); }
			}
			else if(args[0].equalsIgnoreCase("disable"))
			{
				if(sender.hasPermission(permDisable))
				{
					Plugin plugin = pm.getPlugin(args[1]);
					if(plugin != null)
					{
						if(!plugin.isEnabled()) { sender.sendMessage(ChatColor.RED + "That plugin is already disabled!"); }
						else
						{
							pm.disablePlugin(plugin);
							sender.sendMessage(ChatColor.DARK_GREEN + plugin.getDescription().getName() + ChatColor.GREEN + " was disabled!");
						}
					}
					else { sender.sendMessage(ChatColor.RED + "The plugin " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not loaded!"); }
				}
				else { sender.sendMessage(ChatColor.RED + "You don't have permission to disable plugins."); }
			}
			else if(args[0].equalsIgnoreCase("reload"))
			{
				if(sender.hasPermission(permReload))
				{
					Plugin plugin = pm.getPlugin(args[1]);
					if(plugin != null)
					{
						if(!plugin.isEnabled())
						{
							pm.enablePlugin(plugin);
							sender.sendMessage(ChatColor.DARK_GREEN + plugin.getDescription().getName() + ChatColor.GREEN + " has been re-enabled!");
						}
						else
						{
							pm.disablePlugin(plugin);
							pm.enablePlugin(plugin);
							sender.sendMessage(ChatColor.DARK_GREEN + plugin.getDescription().getName() + ChatColor.GREEN + " was restarted!");
						}
					}
					else { sender.sendMessage(ChatColor.RED + "The plugin " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not loaded!"); }
				}
				else { sender.sendMessage(ChatColor.RED + "You don't have permission to reload plugins."); }
			}
			else if(args[0].equalsIgnoreCase("load"))
			{
				if(sender.hasPermission(permLoad))
				{
					if(pm.getPlugin(args[1]) != null) { sender.sendMessage(ChatColor.RED + "That plugin is already loaded."); }
					else
					{
						try
						{
							File newPlugin = new File(getDataFolder().getParent().concat("/" + args[1] + ".jar"));
							pm.loadPlugin(newPlugin);
							sender.sendMessage(ChatColor.GREEN + "The plugin " + ChatColor.DARK_GREEN + args[1] + ChatColor.GREEN + " was loaded!");
						}
						catch (InvalidPluginException e) { sender.sendMessage(ChatColor.RED + "That is not a valid plugin!"); }
						catch (InvalidDescriptionException e) { sender.sendMessage(ChatColor.RED + "That plugin has an invalid description!"); }
						catch (UnknownDependencyException e) { sender.sendMessage(ChatColor.RED + "That plugin is missing a dependancy!"); }
						catch (Exception e) { sender.sendMessage(ChatColor.RED + "An unspecified error was thrown!"); }
					}
				}
				else { sender.sendMessage(ChatColor.RED + "You don't have permission to load plugins."); } 
			}
			else if(args[0].equalsIgnoreCase("help"))
			{
				if(helpValid.contains(args[1]))
				{
					for(int i=0;i<help.length;i++)
					{
						String[] helpText = help[i].split("£", 2);
						if(helpText[0].equalsIgnoreCase(args[1])) { sender.sendMessage(helpText[1]); }
					}
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "Argument not recognised.");
					for(int i=0;i<help.length;i++)
					{
						String[] helpText = help[i].split("£", 2);
						if(sender.hasPermission("plugins." + helpText[0]) || helpText[0].equalsIgnoreCase("help")) { sender.sendMessage(helpText[1]); }
					}
				}
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Argument not recognised.");
				for(int i=0;i<help.length;i++)
				{
					String[] helpText = help[i].split("£", 2);
					if(sender.hasPermission("plugins." + helpText[0]) || helpText[0].equalsIgnoreCase("help")) { sender.sendMessage(helpText[1]); }
				}
			}
		}
		else if(args.length > 2)
		{
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			for(int i=0;i<help.length;i++)
			{
				String[] helpText = help[i].split("£", 2);
				if(sender.hasPermission("plugins." + helpText[0]) || helpText[0].equalsIgnoreCase("help")) { sender.sendMessage(helpText[1]); }
			}
		}
		return true;
	}
		
	@EventHandler
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
	
	private void Log(Level level, String message) { getServer().getLogger().log(level, "[PluginController] " + message); }
	private void Log(String message) { Log(Level.INFO, message); }
}
