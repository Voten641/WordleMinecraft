package me.voten.worldeminecraft;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;

import java.util.*;

public class MessageListener implements Listener {

    @EventHandler
    public void onMessage(PlayerChatEvent e){
        UserClass uc = UserClass.getByPlayer(e.getPlayer());
        assert uc != null;
        if(!uc.isPlaying()){
            if(e.getMessage().toUpperCase().contains(Main.words.get(uc.getLang()).toUpperCase())){
                if (uc.isTodayWon()) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(Main.getPlugin(Main.class).getConfig().getString("cancelWord").replace('&', '§'));
                }
              }
            return;
        }
        e.setCancelled(true);
        String message = e.getMessage().toUpperCase();
        if(!Main.allwords.get(uc.getLang()).contains(message)){
            e.getPlayer().sendMessage(Main.getPlugin(Main.class).getConfig().getString("wrongWord").replace('&', '§'));
            return;
        }
        StringBuilder end = new StringBuilder();
        int numberofgood = 0;
        for (int i = 0; i < 5; i++){
            Character c = message.charAt(i);
            if(Main.words.get(uc.getLang()).contains(""+c)){
                if(Main.words.get(uc.getLang()).charAt(i) == c) {
                    uc.setColor(c, 'a');
                    numberofgood++;
                }
                else uc.setColor(c, 'e');
            }else uc.setColor(c, '8');
            end.append("§").append(uc.getColor(c)).append("§l[").append(c).append("]");
        }
        e.getPlayer().sendMessage(end.toString());
        uc.addAttempt();
        if(numberofgood == 5){
            uc.addWonGame();
            e.getPlayer().sendMessage(Main.getPlugin(Main.class).getConfig().getString("winMessage").replace('&', '§').replace("%attempt", uc.getAttempt()+""));
            uc.resetAll();
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()), 20L*5);
            if(Main.getPlugin(Main.class).getConfig().getBoolean("giveReward")) giveRewards(e.getPlayer());
        }
        if(uc.getAttempt() == 5){
            e.getPlayer().sendMessage(Main.getPlugin(Main.class).getConfig().getString("loseMessage").replace('&', '§'));
            uc.resetAll();
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()), 20L*5);
        }

    }

    public static void setSB(Player p){
        ScoreboardManager m = Bukkit.getScoreboardManager();
        Scoreboard b = m.getNewScoreboard();

        Objective o = b.registerNewObjective("Gold", "");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(Main.getPlugin(Main.class).getConfig().getString("scoreboardTitle").replace('&', '§'));
        List<String> ssc = new ArrayList<>();
        StringBuilder ssct = new StringBuilder();
        UserClass uc = UserClass.getByUUID(p.getUniqueId());
        int count = 0;
        for (Map.Entry<Character,Character> ma : uc.getMap().entrySet()){
            if(count<7){
                ssct.append("§").append(ma.getValue()).append("[").append(ma.getKey()).append("]");
                count++;
            }else{
                ssct.append("§").append(ma.getValue()).append("[").append(ma.getKey()).append("]");
                ssc.add(ssct.toString());
                count = 0;
                ssct = new StringBuilder();
            }
        }
        ssc.add(ssct.toString());
        List<Score> sc = new ArrayList<>();
        for(String s : ssc){
            sc.add(o.getScore(s));
        }
        for( int i = 0; i < sc.size(); i++){
            sc.get(i).setScore(sc.size()-i);
        }

        p.setScoreboard(b);
    }

    public void giveRewards(Player p) {
        ConfigurationSection items = Main.getPlugin(Main.class).getConfig().getConfigurationSection("reward");
        ItemStack it = XMaterial.valueOf(items.getConfigurationSection("ITEM").getString("material").toUpperCase()).parseItem();
        assert it != null;
        it.setAmount(Integer.parseInt(items.getConfigurationSection("ITEM").getString("ammount")));
        p.getInventory().addItem(it);
        String command = items.getConfigurationSection("COMMAND").getString("command");
        command = command.replace("%player", p.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(!UserClass.userByUuid.containsKey(e.getPlayer().getUniqueId()))
        new UserClass(e.getPlayer());
    }
}
