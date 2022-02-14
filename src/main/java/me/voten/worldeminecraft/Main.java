package me.voten.worldeminecraft;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    public static String word = null;
    public static ArrayList<String> listOfLines = new ArrayList<>();
    private static LocalDate day;
    public static String lang = "english";

    @Override
    public void onEnable() {
        day = LocalDate.now();
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        saveConfig();
        FileConfiguration config = this.getConfig();
        getServer().getPluginManager().registerEvents(new MessageListener(), this);
        getCommand("wordle").setExecutor(new WordleCommand());
        lang = config.getString("language");
        List<String> langs = Arrays.asList("english", "polish", "french", "korean","russian", "spanish");
        if(!langs.contains(lang)) {
            getServer().getLogger().log(Level.WARNING, "Lang: " + lang + " doesnt exist.");
            getServer().getLogger().log(Level.WARNING, "Available langs: " + langs);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        InputStream in = getClass().getResourceAsStream("/WordList/" + lang +".txt");
        try {
            assert in != null;
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = bufReader.readLine();
            while(line != null){
                listOfLines.add(line.toUpperCase());
                line = bufReader.readLine();
            }
            bufReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        int index = random.nextInt(listOfLines.size());
        word = listOfLines.get(index);
        File playerDataFolder = new File(Main.getPlugin(Main.class).getDataFolder(), "playerData");
        if(!playerDataFolder.exists()) {
            if(playerDataFolder.mkdirs()){
                getLogger().log(Level.WARNING, "PlayerData Folder creation failed.");
            }
        }
        for(Player p : Bukkit.getOnlinePlayers()){
            new UserClass(p);
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new PlaceholderClass(this).register();
        }
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            LocalDate newDate = LocalDate.now();
            if(newDate.getDayOfMonth() != day.getDayOfMonth()){
                Random random1 = new Random();
                int index1 = random1.nextInt(listOfLines.size());
                word = listOfLines.get(index1);
                day = LocalDate.now();
                for(Map.Entry<UUID, UserClass> uc : UserClass.userByUuid.entrySet()){
                    uc.getValue().newDay();
                }
            }
        }, 0, 20*60*5);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
