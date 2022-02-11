package me.voten.worldeminecraft;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

public final class Main extends JavaPlugin {

    public static ArrayList<Player> players = new ArrayList<Player>();
    public static String word = null;
    public static ArrayList<String> listOfLines = new ArrayList<>();
    private static LocalDate day;
    public static String lang = "english";

    @Override
    public void onEnable() {
        day = LocalDate.now();
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        getServer().getPluginManager().registerEvents(new MessageListener(), this);
        getCommand("wordle").setExecutor(new WordleCommand());
        lang = config.getString("language");
        InputStream in = getClass().getResourceAsStream("/WordList/" + lang + ".txt");
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
        System.out.println(word);
        File messagesFolder = new File(Main.getPlugin(Main.class).getDataFolder(), "playerData");
        if(!messagesFolder.exists()) {
            messagesFolder.mkdirs();
        }
        for(Player p : Bukkit.getOnlinePlayers()){
            new UserClass(p);
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new PlaceholderClass(this).register();
        }
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                LocalDate newDate = LocalDate.now();
                if(newDate.getDayOfMonth() != day.getDayOfMonth()){
                    Random random = new Random();
                    int index = random.nextInt(listOfLines.size());
                    word = listOfLines.get(index);
                    day = LocalDate.now();
                    for(UserClass uc : UserClass.userList){
                        uc.newDay();
                    }
                }
            }
        }, 0, 20*60*5);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
