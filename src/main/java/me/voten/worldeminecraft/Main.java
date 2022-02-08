package me.voten.worldeminecraft;

import me.voten.worldeminecraft.listeners.MessageListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

public final class Main extends JavaPlugin {

    public static ArrayList<Player> players = new ArrayList<Player>();
    public static String word = null;
    public static ArrayList<String> listOfLines = new ArrayList<>();
    private static LocalDate day;

    @Override
    public void onEnable() {
        day = LocalDate.now();
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        getServer().getPluginManager().registerEvents(new MessageListener(), this);
        getCommand("wordle").setExecutor(new WordleCommand());
        InputStream in = getClass().getResourceAsStream("/WordList/" + config.getString("language") + ".txt");
        try {
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));
            String line = bufReader.readLine();
            while(line != null){
                listOfLines.add(line);
                line = bufReader.readLine();
            }
            bufReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        int index = random.nextInt(listOfLines.size());
        word = listOfLines.get(index);
        for(Player p : Bukkit.getOnlinePlayers()){
            new UserClass(p);
        }
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                LocalDate newDate = LocalDate.now();
                if(newDate.getDayOfMonth() != day.getDayOfMonth()){
                    Random random = new Random();
                    int index = random.nextInt(listOfLines.size());
                    word = listOfLines.get(index);
                    day = LocalDate.now();
                }
            }
        }, 0, 20*60*5);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
