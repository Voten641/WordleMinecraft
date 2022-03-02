package me.voten.worldeminecraft;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class PlaceholderClass extends PlaceholderExpansion {

    private final Main plugin;

    public PlaceholderClass(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "Wordle";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Voten641";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.3";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("wonGames")){
            File file = new File(Main.getPlugin(Main.class).getDataFolder(), "playerData/" + player.getUniqueId()+".yml");
            return String.valueOf(YamlConfiguration.loadConfiguration(file).getInt("wonGames"));
        }if(params.equalsIgnoreCase("isCurrentlyPlaying")){
            if(player.isOnline()){
                return String.valueOf(Objects.requireNonNull(UserClass.getByPlayer(player.getPlayer())).isPlaying());
            }
            else{
                return "§cPlayer is not Online";
            }
        }
        if(params.equalsIgnoreCase("TodayWon")){
            if(UserClass.getByPlayer(player.getPlayer()) != null){
                return String.valueOf(Objects.requireNonNull(UserClass.getByPlayer(player.getPlayer())).isTodayWon());
            }
        }
        if(params.toUpperCase().contains("TOP_")){
            Set<UUID> keySet = Main.top.keySet();
            List<UUID> listKeys = new ArrayList<UUID>(keySet);
            if(params.equalsIgnoreCase("top_1")){
                if(listKeys.size() > 0) return Bukkit.getOfflinePlayer(listKeys.get(0)).getName();
                else return "null";
            }else if(params.equalsIgnoreCase("top_2")){
                if(listKeys.size() > 1) return Bukkit.getOfflinePlayer(listKeys.get(1)).getName();
                else return "null";
            }else if(params.equalsIgnoreCase("top_3")){
                if(listKeys.size() > 2) return Bukkit.getOfflinePlayer(listKeys.get(2)).getName();
                else return "null";
            }
        }
        return null;
    }
}
