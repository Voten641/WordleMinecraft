package me.voten.worldeminecraft.listeners;

import me.voten.worldeminecraft.Main;
import me.voten.worldeminecraft.UserClass;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

import java.util.Map;

public class MessageListener implements Listener {

    @EventHandler
    public void onMessage(PlayerChatEvent e){
        if(!Main.players.contains(e.getPlayer())){
            return;
        }
        e.setCancelled(true);
        UserClass uc = UserClass.getByPlayer(e.getPlayer());
        String message = e.getMessage().toLowerCase();
        if(!Main.listOfLines.contains(message)){
            e.getPlayer().sendMessage(Main.getPlugin(Main.class).getConfig().getString("wrongWord").replace('&', '§'));
            return;
        }
        String end = "";
        int numberofgood = 0;
        for (int i = 0; i < 5; i++){
            Character c = message.charAt(i);
            if(Main.word.contains(""+c)){
                if(Main.word.charAt(i) == c) {
                    uc.setColor(c, 'a');
                    numberofgood++;
                }
                else uc.setColor(c, 'e');
            }else uc.setColor(c, '8');
            end += "§"+uc.getColor(c)+"§l["+c+"]";
        }
        ScoreboardManager m = Bukkit.getScoreboardManager();
        Scoreboard b = m.getNewScoreboard();

        Objective o = b.registerNewObjective("Gold", "");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(Main.getPlugin(Main.class).getConfig().getString("scoreboardTitle").replace('&', '§'));
        String ssc1 = "";
        String ssc2 = "";
        String ssc3 = "";
        String ssc4 = "";
        int i = 0;
        for(Map.Entry<Character, Character> ma : uc.getMap().entrySet()){
            if(i < 8){
                ssc1 += "§"+ma.getValue()+"["+ma.getKey()+"]";
                i++;
                continue;
            }if(i < 16){
                ssc2 += "§"+ma.getValue()+"["+ma.getKey()+"]";
                i++;
                continue;
            }if (i< 24){
                ssc3 += "§"+ma.getValue()+"["+ma.getKey()+"]";
                i++;
                continue;
            }else{
                ssc4 += "§"+ma.getValue()+"["+ma.getKey()+"]";
                i++;
                continue;
            }
        }
        Score sc1 = o.getScore(ssc4);
        Score sc2 = o.getScore(ssc3);
        Score sc3 = o.getScore(ssc2);
        Score sc4 = o.getScore(ssc1);
        sc1.setScore(1);
        sc2.setScore(2);
        sc3.setScore(3);
        sc4.setScore(4);

        e.getPlayer().setScoreboard(b);
        e.getPlayer().sendMessage(end);
        uc.addAttemp();
        if(numberofgood == 5){
            uc.addWonGame();
            e.getPlayer().sendMessage(Main.getPlugin(Main.class).getConfig().getString("winMessage").replace('&', '§').replace("%attemp", uc.getAttemp()+""));
            uc.resetAll();
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
                public void run() {
                    e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }
            }, 20L*5);
        }
        if(uc.getAttemp() == 5){
            e.getPlayer().sendMessage(Main.getPlugin(Main.class).getConfig().getString("loseMessage").replace('&', '§'));
            uc.resetAll();
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
                public void run() {
                    e.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }
            }, 20L*5);
            return;
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(!UserClass.userList.contains(e.getPlayer()))
        new UserClass(e.getPlayer());
    }
}
