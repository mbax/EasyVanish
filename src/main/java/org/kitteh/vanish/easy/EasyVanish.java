package org.kitteh.vanish.easy;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class EasyVanish extends JavaPlugin implements Listener {
    private final HashSet<String> vanished = new HashSet<String>();
    private static final String VANISH_PERM = "vanish.vanish";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Can't vanish if not a player!");
                return true;
            }
            final boolean vanishing = !this.vanished.contains(sender.getName());
            if (!vanishing) {
                this.vanished.remove(sender.getName());
            } else {
                this.vanished.add(sender.getName());
            }
            final Player player = (Player) sender;
            for (final Player plr : this.getServer().getOnlinePlayers()) {
                if (vanishing && !plr.hasPermission(EasyVanish.VANISH_PERM)) {
                    plr.hidePlayer(player);
                } else if (!vanishing && !plr.canSee(player)) {
                    plr.showPlayer(player);
                }
            }
            this.getServer().broadcast(ChatColor.AQUA + player.getName() + " has " + (vanishing ? "vanished" : "unvanished"), EasyVanish.VANISH_PERM);
        } else if (args[0].equalsIgnoreCase("list")) {
            final StringBuilder list = new StringBuilder();
            list.append(ChatColor.AQUA);
            list.append("Vanished (");
            list.append(this.vanished.size());
            list.append("): ");
            for (final String name : this.vanished) {
                list.append(name);
                list.append(", ");
            }
            list.setLength(list.length() - 2);
            sender.sendMessage(list.toString());
        }
        return true;
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission(EasyVanish.VANISH_PERM) && (this.vanished.size() > 0)) {
            final Player player = event.getPlayer();
            for (final Player plr : this.getServer().getOnlinePlayers()) {
                if (this.vanished.contains(plr.getName())) {
                    player.hidePlayer(plr);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (this.vanished.contains(event.getPlayer().getName())) {
            final Player player = event.getPlayer();
            this.vanished.remove(event.getPlayer().getName());
            for (final Player plr : this.getServer().getOnlinePlayers()) {
                if ((plr != null) && !plr.canSee(event.getPlayer())) {
                    plr.showPlayer(player);
                }
            }
        }
    }
}