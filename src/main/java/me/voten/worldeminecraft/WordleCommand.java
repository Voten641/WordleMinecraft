package me.voten.worldeminecraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class WordleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if(p.hasPermission("wordle.admin")){
            if(args.length > 0){
                if(args[0].equalsIgnoreCase("resetplayerdata")){
                    for(Map.Entry<UUID, UserClass> uc : UserClass.userByUuid.entrySet()){
                        uc.getValue().resetWonGames();
                    }
                    File messagesFolder = new File(Main.getPlugin(Main.class).getDataFolder(), "playerData");
                    File[] fileList = messagesFolder.listFiles();
                    assert fileList != null;
                    for(File f : fileList){
                        if(f.delete()) Main.getPlugin(Main.class).getLogger().log(Level.WARNING, "PlayerData File delete failed.");
                        try {
                            if(f.createNewFile()) Main.getPlugin(Main.class).getLogger().log(Level.WARNING, "PlayerData File creation failed.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    p.sendMessage("§cReset");
                }
                if(args[0].equalsIgnoreCase("getword")){
                    p.sendMessage("§e"+Main.word);
                }
                return false;
            }
        }
        UserClass uc = UserClass.getByPlayer(p);
        assert uc != null;
        if(uc.isPlaying()){
            p.sendMessage(Main.getPlugin(Main.class).getConfig().getString("pauseGame").replace('&', '§'));
            uc.setPlaying(false);
            return false;
        }else{
            if(uc.isTodayWon()){
                p.sendMessage(Main.getPlugin(Main.class).getConfig().getString("playedToday").replace('&', '§'));
                return true;
            }else {
                uc.setPlaying(true);
                p.sendMessage(Main.getPlugin(Main.class).getConfig().getString("startGame").replace('&', '§'));
            }
        }

        return false;
    }
}
