package me.voten.worldeminecraft;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UserClass {

    private OfflinePlayer p;
    private HashMap<Character, Character> map = Maps.newHashMap();
    public static Map<UUID, UserClass> userByUuid = Maps.newHashMap();
    public static Map<String, UserClass> userByName = Maps.newHashMap();
    public static Map<OfflinePlayer, UserClass> userByPlayer = Maps.newHashMap();
    private Integer attemp = 0;
    private boolean todayWon = false;
    private Integer wonGames = 0;
    private static File file;
    private static FileConfiguration config;

    public UserClass(Player pl){
        p = pl;
        List<Character> universalaplhabet = Arrays.asList('Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K',
                'L','Z','X','C','V','B','N','M');
        List<Character> rualphabet = Arrays.asList('А','Б','В','Г','Д','Е','Ё','Ж','З','И','Й','К','Л','М','Н',
                'О','П','Р','С','Т','У','Ф','Х','Ц','Ч','Ш','Щ','ъ','Ы','ь','Э','Ю','Я');
        List<Character> fralphabet = Arrays.asList('À','Â','Æ','Ç','É','È','Ê','Ë','Î','Ï','Ô','Œ','Ù','Û','Ü','Ÿ');
        List<Character> plalphabet = Arrays.asList('Ą','Ć','Ę','Ł','Ń','Ó','Ś','Ż','Ź');
        List<Character> spalphabet = Arrays.asList('Ñ','ú','ü','ó','í','é','á');
        List<Character> koalphabet = Arrays.asList('ㅂ','ㅈ','ㄷ','ㄱ','ㅅ','ㅛ','ㅕ','ㅑ','ㅐ','ㅔ','ㅁ','ㄴ','ㅇ','ㄹ'
                ,'ㅎ','ㅗ','ㅓ','ㅏ','ㅣ','ㅋ','ㅌ','ㅊ','ㅍ','ㅠ','ㅜ','ㅡ');
        switch (Main.lang){
            case "french":
                universalaplhabet.addAll(fralphabet);
                break;
            case "russian":
                universalaplhabet.addAll(rualphabet);
                break;
            case "polish":
                universalaplhabet.addAll(plalphabet);
                break;
            case "spanish":
                universalaplhabet.addAll(spalphabet);
                break;
            case "korean":
                universalaplhabet.clear();
                universalaplhabet.addAll(koalphabet);
                break;
        }
        universalaplhabet = universalaplhabet.stream().distinct().collect(Collectors.toList());
        for(Character c : universalaplhabet){
            map.put(Character.toUpperCase(c), 'f');
        }
        userByName.put(p.getName(), this);
        userByUuid.put(p.getUniqueId(), this);
        userByPlayer.put(p, this);
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

    public OfflinePlayer getPlayer(){
        return p;
    }

    public void newDay(){
        todayWon = false;
    }

    public static UserClass getByPlayer(OfflinePlayer pl){
        if(userByPlayer.containsKey(pl)) return userByPlayer.get(pl);
        return null;
    }
    public static UserClass getByUUID(UUID uuid){
        if(userByUuid.containsKey(uuid)) return userByUuid.get(uuid);
        return null;
    }
    public static UserClass getByName(String name){
        if(userByName.containsKey(name)) return userByName.get(name);
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
