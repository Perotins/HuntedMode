package me.perotin.huntedmode.files;

import me.perotin.huntedmode.HuntedMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;

/* Created by Perotin on 8/14/19 */
public class HuntedFile {

    private final FileType type;
    private File file;
    private FileConfiguration configuration;

    public HuntedFile(FileType type){
        this.type = type;
        switch(type){
            case PLAYERS:
                file = new File(HuntedMode.getInstance().getDataFolder(), "players.yml");
                configuration = YamlConfiguration.loadConfiguration(file);
                break;

            case MESSAGES:
                file = new File(HuntedMode.getInstance().getDataFolder(), "messages.yml");
                configuration = YamlConfiguration.loadConfiguration(file);
                break;
        }
    }
    public void save() {
        try {
            configuration.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }

    // some generic methods to speed up the process
    public boolean getBool(String path){
        return getConfiguration().getBoolean(path);
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public Object get(String path) {
        return configuration.get(path);
    }

    public void set(String path, Object value) {
        configuration.set(path, value);
    }

    public void sendMessage (Player player, String path) {
        player.sendMessage(getString(path));
    }

    public String getString(String path) {
        if(configuration.getString(path) == null){
            Bukkit.getLogger().severe("Path " + path + " is null!");
            Bukkit.getLogger().severe(file.getAbsolutePath() + " is the file that this occurred!");
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', configuration.getString(path));
    }

    /**
     * loads all files with defaults
     */
    public void load() {

        File lang = null;
        InputStream defLangStream = null;

        switch (type) {
            case PLAYERS:
                lang = new File(HuntedMode.getInstance().getDataFolder(), "players.yml");
                defLangStream = HuntedMode.getInstance().getResource("players.yml");
                break;

            case MESSAGES:
                lang = new File(HuntedMode.getInstance().getDataFolder(), "messages.yml");
                defLangStream = HuntedMode.getInstance().getResource("messages.yml");
                break;


        }
        OutputStream out = null;
        if (!lang.exists()) {
            try {
                HuntedMode.getInstance().getDataFolder().mkdir();
                lang.createNewFile();
                if (defLangStream != null) {
                    out = new FileOutputStream(lang);
                    int read;
                    byte[] bytes = new byte[1024];

                    while ((read = defLangStream.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace(); // So they notice
                Bukkit.getLogger().severe("[HuntedMode] Couldn't create " + type.toString().toLowerCase() + " file.");
                Bukkit.getLogger().severe("[HuntedMode] This is a fatal error. Now disabling");
                HuntedMode.getInstance().getPluginLoader().disablePlugin(HuntedMode.getInstance()); // Without
                // it
                // loaded,
                // we
                // can't
                // send
                // them
                // messages
            } finally {
                if (defLangStream != null) {
                    try {
                        defLangStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public File getFile(){
        return this.file;
    }
    public static void loadFiles(){

        if (!new File( HuntedMode.getInstance().getDataFolder(), "messages.yml").exists()) {
            HuntedMode.getInstance().saveResource("messages.yml", false);
        }  if (!new File( HuntedMode.getInstance().getDataFolder(), "players.yml").exists()) {
            HuntedMode.getInstance().saveResource("players.yml", false);
        }
    }






}