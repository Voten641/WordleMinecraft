package me.voten.worldeminecraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class WordleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if(p.hasPermission("wordle.admin")){
            if(args.length > 0){
                if(args[0].equals("resetplayerdata")){
                    for(UserClass uc : UserClass.userList){
                        uc.resetWonGames();
                    }
                    File messagesFolder = new File(Main.getPlugin(Main.class).getDataFolder(), "playerData");
                    List<File> fileList = Arrays.asList(messagesFolder.listFiles());
                    for(File f : fileList){
                        f.delete();
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    p.sendMessage("§cReset");
                }
                return false;
            }
        }
        if(Main.players.contains(p)){
            p.sendMessage(Main.getPlugin(Main.class).getConfig().getString("pauseGame").replace('&', '§'));
            Main.players.remove(p);
            return false;
        }else{
            UserClass uc = UserClass.getByPlayer(p);
            if(uc.isTodayWon()){
                p.sendMessage(Main.getPlugin(Main.class).getConfig().getString("playedToday").replace('&', '§'));
                return true;
            }else {
                Main.players.add(p);
                p.sendMessage(Main.getPlugin(Main.class).getConfig().getString("startGame").replace('&', '§'));
            }
        }

        return false;
    }
}
