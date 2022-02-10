package me.voten.worldeminecraft;

import com.google.common.collect.Maps;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UserClass {

    private Player p = null;
    private HashMap<Character, Character> map = Maps.newHashMap();
    public static List<UserClass> userList = new ArrayList<>();
    private Integer attemp = 0;
    private boolean todayWon = false;
    private Integer wonGames = 0;
    private static File file;
    private static FileConfiguration config;

    public UserClass(Player pl){
        p = pl;
        List<Character> selectedaplhabet = new ArrayList<>();
        List<Character> rualphabet = Arrays.asList('А','Б','В','Г','Д','Е','Ё','Ж','З','И','Й','К','Л','М','Н',
                'О','П','Р','С','Т','У','Ф','Х','Ц','Ч','Ш','Щ','ъ','Ы','ь','Э','Ю','Я');
        List<Character> alphabet = Arrays.asList('Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K',
                'L','Z','X','C','V','B','N','M');
        switch (Main.lang){
            case "english":
            case "french":
                selectedaplhabet = alphabet;
                break;
            case "russian":
                selectedaplhabet = rualphabet;
        }
        for(Character c : selectedaplhabet){
            map.put(c, 'f');
        }
        userList.add(this);
        File messagesFolder = new File(Main.getPlugin(Main.class).getDataFolder(), "playerData");
        if(!messagesFolder.exists()) {
            messagesFolder.mkdirs();
        }
        file = new File(Main.getPlugin(Main.class).getDataFolder(), "playerData/" + p.getUniqueId()+".yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        wonGames = config.getInt("wonGames");
    }

    public Integer getWonGames(){
        return wonGames;
    }

    public void addWonGame(){
        wonGames++;
        config.set("wonGames", wonGames);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetAll(){
        attemp = 0;
        for(Map.Entry<Character, Character> m : map.entrySet()){
            map.replace(m.getKey(), 'f');
        }
        todayWon = true;
        Main.players.remove(p);
    }

    public boolean isTodayWon(){
        return todayWon;
    }

    public void resetWonGames(){
        wonGames = 0;
    }

    public Integer getAttemp(){
        return attemp;
    }
    public void addAttemp(){
        attemp++;
    }

    public Player getPlayer(){
        return p;
    }

    public static UserClass getByPlayer(Player pl){
        for(UserClass uc : userList){
            if(uc.getPlayer().equals(pl)) return uc;
        }
        return null;
    }

    public HashMap<Character, Character> getMap() {
        return map;
    }
    public Character getColor(Character c){
        return map.get(c);
    }

    public void setColor(Character key, Character newColor) {
        map.replace(key, newColor);
    }
}
