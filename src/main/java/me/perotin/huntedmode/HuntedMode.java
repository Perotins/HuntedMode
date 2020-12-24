package me.perotin.huntedmode;

/* Created by Perotin on 12/23/20 */


import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import me.perotin.huntedmode.commands.HuntedCommand;
import me.perotin.huntedmode.events.HuntedCombatEvent;
import me.perotin.huntedmode.files.FileType;
import me.perotin.huntedmode.files.HuntedFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Perotin
 * @version 12/23/20
 *
 *  Main class of HuntedMode. HuntedMode is a plugin to allow players to opt into pvp, and only fight
 *  other players also opted into pvp. Players can opt out of pvp as well.
 */
public class HuntedMode extends JavaPlugin {

    private static HuntedMode instance;

    private static List<UUID> huntedModePlayers;
    private ICombatLogX combatLogX;
    private boolean combatHooked = false;

    @Override
    public void onEnable(){
        instance = this;
        huntedModePlayers = new ArrayList<>();
        saveDefaultConfig();
        HuntedFile.loadFiles();
        getCommand("hunted").setExecutor(new HuntedCommand(this));
        Bukkit.getServer().getPluginManager().registerEvents(new HuntedCombatEvent(this), this);
        loadPlayers();
        // check for combatlog

        if (Bukkit.getPluginManager().isPluginEnabled("CombatLogX")) {
            combatLogX = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
            combatHooked = true;
        }
    }

    @Override
    public void onDisable(){
        savePlayerData();
    }

    public boolean isInCombat(Player player) {
        // Make sure to check that CombatLogX is enabled before using it for anything.
        ICombatManager combatManager = combatLogX.getCombatManager();
        return combatManager.isInCombat(player);
    }


    public boolean isCombatHooked() {
        return combatHooked;
    }

    public static HuntedMode getInstance() {
        return instance;
    }

    public static List<UUID> getHuntedModePlayers(){
        return huntedModePlayers;
    }

    public static void addHuntedPlayer(UUID uuid) {
        huntedModePlayers.add(uuid);
    }

    public static void removeHuntedPlayer(UUID uuid) {
        huntedModePlayers.remove(uuid);
    }

    private void loadPlayers() {
        HuntedFile file = new HuntedFile(FileType.PLAYERS);
        if (file.getConfiguration().contains("hunted-players")) {
            List<String> hunted = (List<String>) file.getConfiguration().getList("hunted-players");

            huntedModePlayers = hunted.stream().map(UUID::fromString).collect(Collectors.toList());
        }

    }


    public boolean isHuntedPlayer (UUID uuid) {
        return huntedModePlayers.contains(uuid);
    }
    public List<String> getNamesOfHunted() {
        List<String> names = new ArrayList<>();
        for (UUID uuid : huntedModePlayers) {
            if (Bukkit.getPlayer(uuid) != null) {
                names.add(Bukkit.getPlayer(uuid).getName());
            }

        }
        return names;
    }
    private void savePlayerData() {
        HuntedFile file = new HuntedFile(FileType.PLAYERS);
        List<String> uuidSrings = huntedModePlayers.stream().map(UUID::toString).collect(Collectors.toList());
        file.set("hunted-players", uuidSrings);
        file.save();
    }

}
