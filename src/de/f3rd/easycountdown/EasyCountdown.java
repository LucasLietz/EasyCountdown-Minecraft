package de.f3rd.easycountdown;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Dictionary;
import java.util.Hashtable;


//main class
public class EasyCountdown extends JavaPlugin
{
    private Server _server;
    private int _seconds = 0;
    private Dictionary<Player, String> _oldexp = new Hashtable<Player, String>();
    ConsoleCommandSender ccs = this.getServer().getConsoleSender();

    @Override
    public void onEnable()
    {
        _server = getServer();

        ccs.sendMessage(ChatColor.GOLD + "[EasyCountdown] wurde erfolgreich aktiviert.");
        ccs.sendMessage("[EasyCountdown] by f3rd");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if(sender instanceof Player)
        {
            final Player player;
            player = (Player) sender;
            if(player.hasPermission("countdown.run"))
            {
                    if (cmd.getName().equalsIgnoreCase("countdown"))
                    {
                        if(args != null && args.length==1)
                        {
                            if(args[0].equalsIgnoreCase("rl"))
                            {
                                getPluginLoader().disablePlugin(this);
                                player.sendRawMessage(ChatColor.GREEN + "EasyCountdown reloaded successfully.");
                                getPluginLoader().enablePlugin(this);
                                return true;
                            }
                            try
                            {
                                _seconds = Integer.valueOf(args[0]);
                                if(_seconds<0)
                                {
                                    player.sendRawMessage(ChatColor.RED + "Gib bitte Zahlen größer 0 ein!");
                                    return false;
                                }
                            }
                            catch (Exception ex)
                            {
                                player.sendRawMessage(ChatColor.RED + "Da ist was schief gelaufen. Achte darauf, nur Zahlen als Paramenter zu übergeben!" +
                                        "\nZum Beispiel: /countdown 10");
                                ccs.sendMessage(ex.toString());
                                return false;
                            }

                            _server.broadcastMessage(ChatColor.GOLD + "Ein neuer Countdown von "+ _seconds + " Sekunden wurde von " + ChatColor.RED + player.getDisplayName() + ChatColor.GOLD + " gestartet.");

                            for (Player p : _server.getOnlinePlayers())
                            {
                                String lvl = String.valueOf(p.getLevel());
                                String username = p.getDisplayName();

                                _oldexp.put(p, lvl);
                            }

                                _server.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                                    @Override
                                    public void run() {

                                            if(_seconds>=0)
                                            {
                                                for(Player p : _server.getOnlinePlayers()) {
                                                    p.setLevel(_seconds);
                                                }

                                                if(_seconds <= 5 && _seconds > 0)
                                                {
                                                    _server.broadcastMessage(ChatColor.GOLD + "Noch " + ChatColor.RED + _seconds + ChatColor.GOLD + " Sekunden!");
                                                }
                                                if(_seconds<=3)
                                                {
                                                    for(Player p : _server.getOnlinePlayers()){
                                                    p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT,10,1);
                                                    }
                                                }
                                                else
                                                {
                                                   if(_seconds < 10)
                                                   {
                                                       for(Player p : _server.getOnlinePlayers()) {
                                                           p.playSound(p.getLocation(), Sound.CLICK, 10, 1);
                                                       }
                                                   }
                                                }
                                                if(_seconds == 0)
                                                {
                                                    _server.broadcastMessage(ChatColor.GOLD + "GO!");
                                                    for(Player p : _server.getOnlinePlayers()) {
                                                        p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 10, 1);
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                for(Player p : _server.getOnlinePlayers()) {
                                                    p.setLevel(Integer.valueOf(_oldexp.get(p)));
                                                }
                                                _server.getScheduler().cancelAllTasks();
                                            }
                                            _seconds--;

                                    }
                                }, 0, 20);

                        return true;
                    }
                    else
                    {
                        player.sendRawMessage(ChatColor.RED + "Du hast nicht genau ein Argument übergeben!" +
                                "Beispiel: /countdown 10");
                        return false;
                    }
                }
                else
                {
                    player.sendRawMessage(ChatColor.RED + "Du hast nicht genau ein Argument übergeben!" +
                            "Beispiel: /countdown 10");
                return false;
                }
            }
            else
            {
                player.sendRawMessage(ChatColor.RED + "Keine Berechtigung!");
                return false;
            }


        }
        ccs.sendRawMessage(ChatColor.GOLD + "[Countdown] " + ChatColor.RESET + "Du musst ein Spieler sein, um diesen Command ausführen zu können.");
        return false;
    }

    @Override
    public void onDisable()
    {
        ccs.sendMessage(ChatColor.RED + " [EasyCountdown] wurde deaktiviert.");
    }

}
