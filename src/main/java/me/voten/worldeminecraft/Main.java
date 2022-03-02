package me.voten.worldeminecraft;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class Main extends JavaPlugin {

    public static HashMap<String, String> words = Maps.newHashMap();
    public static HashMap<String, List<String>> allwords = Maps.newHashMap();
    private static LocalDate day;
    public static String lang = "english";
    public static HashMap<UUID, Integer> top = Maps.newHashMap();

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
        List<String> langs = Arrays.asList("english", "polish", "french", "korean", "spanish","italian");
        for(String s : langs){
            ArrayList<String> listOfLines = new ArrayList<>();
            InputStream in = getClass().getResourceAsStream("/WordList/" + s +".txt");
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
            allwords.put(s, listOfLines);
            Random random = new Random();
            int index = random.nextInt(listOfLines.size());
            words.put(s, listOfLines.get(index));
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
                LocalDate newDate = LocalDate.now();
                if(newDate.getDayOfMonth() != day.getDayOfMonth()){
                    Random random1 = new Random();
                    int index1 = random1.nextInt(listOfLines.size());
                    words.put(s, listOfLines.get(index1));
                    day = LocalDate.now();
                    for(Map.Entry<UUID, UserClass> uc : UserClass.userByUuid.entrySet()){
                        uc.getValue().newDay();
                    }
                }
            }, 0, 20*60*5);
        }
        if(!langs.contains(lang)) {
            getServer().getLogger().log(Level.WARNING, "Lang: " + lang + " doesnt exist.");
            getServer().getLogger().log(Level.WARNING, "Available langs: " + langs);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
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
        sortTop();
    }

    public static void sortTop(){
        File playerDataFolder = new File(Main.getPlugin(Main.class).getDataFolder(), "playerData");
        HashMap<UUID, Integer> unsortedtop = Maps.newHashMap();
        for(File f : playerDataFolder.listFiles()){
            FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
            unsortedtop.put(UUID.fromString(f.getName().substring(0, f.getName().indexOf('.'))), fc.getInt("wonGames"));
        }
        top = unsortedtop.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        for(int i = 0; i < top.size(); i++){
            if(i>3){
                top.remove(i);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
