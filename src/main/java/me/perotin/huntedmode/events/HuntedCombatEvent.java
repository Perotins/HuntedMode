package me.perotin.huntedmode.events;

import me.perotin.huntedmode.HuntedMode;
import me.perotin.huntedmode.files.FileType;
import me.perotin.huntedmode.files.HuntedFile;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/* Created by Perotin on 12/24/20 */
public class HuntedCombatEvent implements Listener {

    private HuntedMode plugin;

    public HuntedCombatEvent(HuntedMode plugin) {
        this.plugin = plugin;
    }

    // Event for when player's pvp
    @EventHandler
    public void onCombat(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Entity defender = (Player) event.getEntity();
            HuntedFile messages = new HuntedFile(FileType.MESSAGES);
            if (!plugin.isHuntedPlayer(attacker.getUniqueId()) || !plugin.isHuntedPlayer(defender.getUniqueId())) {
                event.setCancelled(true);
                if (plugin.isHuntedPlayer(attacker.getUniqueId())) {
                    messages.sendMessage(attacker, "cannot-attack-nonhunted");
                } else {
                    messages.sendMessage(attacker, "cannot-attack");

                }
            }
        }

    }
}
