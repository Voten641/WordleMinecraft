package me.voten.worldeminecraft;

import me.voten.worldeminecraft.listeners.MessageListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public final class Main extends JavaPlugin {

    public static ArrayList<Player> players = new ArrayList<Player>();
    public static String word = null;
    public static ArrayList<String> listOfLines = new ArrayList<>();

    @Override
    public void onEnable() {
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
        System.out.println(word);
        for(Player p : Bukkit.getOnlinePlayers()){
            new UserClass(p);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
