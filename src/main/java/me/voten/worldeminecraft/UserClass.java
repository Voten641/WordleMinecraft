package me.voten.worldeminecraft;

import com.google.common.collect.Maps;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class UserClass {

    private final OfflinePlayer p;
    private boolean curPlaying = false;
    private final HashMap<Character, Character> map = Maps.newHashMap();
    public static Map<UUID, UserClass> userByUuid = Maps.newHashMap();
    public static Map<String, UserClass> userByName = Maps.newHashMap();
    public static Map<OfflinePlayer, UserClass> userByPlayer = Maps.newHashMap();
    private Integer attempt = 0;
    private boolean todayWon = false;
    private Integer wonGames;
    private static File file;
    private static FileConfiguration config;
    private String lang = Main.lang;

    public UserClass(Player pl){
        p = pl;
        userByName.put(p.getName(), this);
        userByUuid.put(p.getUniqueId(), this);
        userByPlayer.put(p, this);
        file = new File(Main.getPlugin(Main.class).getDataFolder(), "playerData/" + p.getUniqueId()+".yml");
        if(!file.exists()){
            try {
                if(!file.createNewFile()) Main.getPlugin(Main.class).getLogger().log(Level.WARNING, "PlayerData File creation failed.");
                else {
                    config = YamlConfiguration.loadConfiguration(file);
                    config.set("wonGames", Main.lang);
                    config.set("language", Main.lang);
                    config.save(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        wonGames = config.getInt("wonGames");
        if(config.contains("language")) lang = config.getString("language");
        setupLang();
    }

    public void setupLang(){
        map.clear();
        List<Character> universalaplhabet = Arrays.asList('Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K',
                'L','Z','X','C','V','B','N','M');
        List<Character> rualphabet = Arrays.asList('А','Б','В','Г','Д','Е','Ё','Ж','З','И','Й','К','Л','М','Н',
                'О','П','Р','С','Т','У','Ф','Х','Ц','Ч','Ш','Щ','ъ','Ы','ь','Э','Ю','Я');
        List<Character> fralphabet = Arrays.asList('Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K',
                'L','Z','X','C','V','B','N','M','À','Â','Æ','Ç','É','È','Ê','Ë','Î','Ï','Ô','Œ','Ù','Û','Ü','Ÿ');
        List<Character> plalphabet = Arrays.asList('Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K',
                'L','Z','X','C','V','B','N','M','Ą','Ć','Ę','Ł','Ń','Ó','Ś','Ż','Ź');
        List<Character> spalphabet = Arrays.asList('Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K',
                'L','Z','X','C','V','B','N','M','Ñ','ú','ü','ó','í','é','á');
        List<Character> koalphabet = Arrays.asList('ㅂ','ㅈ','ㄷ','ㄱ','ㅅ','ㅛ','ㅕ','ㅑ','ㅐ','ㅔ','ㅁ','ㄴ','ㅇ','ㄹ'
                ,'ㅎ','ㅗ','ㅓ','ㅏ','ㅣ','ㅋ','ㅌ','ㅊ','ㅍ','ㅠ','ㅜ','ㅡ');
        switch (lang){
            case "french":
                universalaplhabet = fralphabet;
                break;
            case "russian":
                universalaplhabet = rualphabet;
                break;
            case "polish":
                universalaplhabet = plalphabet;
                break;
            case "spanish":
                universalaplhabet = spalphabet;
                break;
            case "korean":
                universalaplhabet = koalphabet;
                break;
        }
        universalaplhabet = universalaplhabet.stream().distinct().collect(Collectors.toList());
        for(Character c : universalaplhabet){
            map.put(Character.toUpperCase(c), 'f');
        }
    }

    public boolean isPlaying(){
        return curPlaying;
    }
    public void setPlaying(boolean b){
        curPlaying = b;
    }

    public String getLang(){
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
        config.set("language", this.lang);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupLang();
        MessageListener.setSB(p.getPlayer());
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
        attempt = 0;
        for(Map.Entry<Character, Character> m : map.entrySet()){
            map.replace(m.getKey(), 'f');
        }
        todayWon = true;
        curPlaying = false;
    }

    public boolean isTodayWon(){
        return todayWon;
    }

    public void resetWonGames(){
        wonGames = 0;
    }

    public Integer getAttempt(){
        return attempt;
    }
    public void addAttempt(){
        attempt++;
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
