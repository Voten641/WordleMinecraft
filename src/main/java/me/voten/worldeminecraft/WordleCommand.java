package me.voten.worldeminecraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class WordleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if(args.length > 0){
            if(p.hasPermission("wordle.admin")){
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
                    return false;
                }
                if(args[0].equalsIgnoreCase("getword")){
                    p.sendMessage("§e"+Main.words.toString().substring(1,Main.words.toString().length()-1));
                    return false;
                }
            }
            if(args[0].equalsIgnoreCase("setlang") && args[1] != null){
                if (!p.hasPermission("wordle.changelanguage"))return false;
                if(Arrays.asList("english", "korean", "spanish", "french", "polish").contains(args[1])){
                    p.sendMessage("lang changed");
                    UserClass.getByUUID(p.getUniqueId()).setLang(args[1]);
                    return true;
                }else{
                    p.sendMessage("§cAvailiable langs: english, korean, spanish, french, polish");
                    return true;
                }
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
                MessageListener.setSB(p);
                p.sendMessage(Main.getPlugin(Main.class).getConfig().getString("startGame").replace('&', '§'));
            }
        }

        return false;
    }
}
