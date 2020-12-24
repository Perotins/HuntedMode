package me.perotin.huntedmode.commands;

import me.perotin.huntedmode.HuntedMode;
import me.perotin.huntedmode.files.FileType;
import me.perotin.huntedmode.files.HuntedFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* Created by Perotin on 12/23/20 */


/**
 * Command ran by player wanting to enter or exit the HuntedMode
 */
public class HuntedCommand implements CommandExecutor {

    private HuntedMode plugin;


    public HuntedCommand(HuntedMode huntedMode) {

        this.plugin = huntedMode;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        HuntedFile messages = new HuntedFile(FileType.MESSAGES);

        if (!sender.hasPermission("huntedmode.use")){
            sender.sendMessage(messages.getString("no-permission"));
            return true;
        }
        if (sender instanceof Player) {
            Player huntedPlayer = (Player) sender;

            if (args.length == 0) {
                if (!HuntedMode.getHuntedModePlayers().contains(huntedPlayer.getUniqueId())) {
                    // add them to it
                    HuntedMode.addHuntedPlayer(huntedPlayer.getUniqueId());
                    messages.sendMessage(huntedPlayer, "add-player");
                } else {
                    // remove from Hunted players, check if in combat first
                    if (plugin.isCombatHooked() && plugin.isInCombat(huntedPlayer)) {
                        messages.sendMessage(huntedPlayer, "cannot-leave-hunted");
                        return true;
                    }
                    HuntedMode.removeHuntedPlayer(huntedPlayer.getUniqueId());
                    messages.sendMessage(huntedPlayer, "remove-player");


                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    if (!plugin.getNamesOfHunted().isEmpty()) {
                        messages.sendMessage(huntedPlayer, "online-hunted");

                        for (String name : plugin.getNamesOfHunted()) {
                            huntedPlayer.sendMessage(ChatColor.GREEN + " - " + name);
                        }
                    } else {
                        // none online
                        messages.sendMessage(huntedPlayer, "none-online");
                        return true;
                    }
                } else {
                    String name = args[0];
                    OfflinePlayer lookup = Bukkit.getOfflinePlayer(name);
                    if (HuntedMode.getHuntedModePlayers().contains(lookup.getUniqueId())) {
                        // they are hunted
                        messages.sendMessage(huntedPlayer, "player-is-hunted");
                    } else {
                        messages.sendMessage(huntedPlayer, "player-not-hunted");
                    }

                }
            } else {
                messages.sendMessage(huntedPlayer, "invalid-args");
            }

            return true;
        }
        return true;
    }
}
