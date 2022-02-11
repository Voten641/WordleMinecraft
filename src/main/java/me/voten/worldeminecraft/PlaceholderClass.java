package me.voten.worldeminecraft;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

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
                if(Main.players.contains(player.getPlayer()))return "true";
                else return "false";
            }
            else{
                return "§cPlayer is not Online";
            }
        }
        if(params.equalsIgnoreCase("TodayWon")){
            if(UserClass.getByPlayer(player.getPlayer()) != null){
                return String.valueOf(UserClass.getByPlayer(player.getPlayer()).isTodayWon());
            }
        }
        return null;
    }

    public Integer getInt(String s){
        if(StringUtils.isNumeric(s)){
            return Integer.parseInt(s);
        }
        return null;
    }
}
