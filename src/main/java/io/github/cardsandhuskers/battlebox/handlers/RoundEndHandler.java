package io.github.cardsandhuskers.battlebox.handlers;

import io.github.cardsandhuskers.battlebox.commands.StartGameCommand;
import io.github.cardsandhuskers.battlebox.objects.Countdown;
import io.github.cardsandhuskers.teams.objects.Team;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

import static io.github.cardsandhuskers.battlebox.BattleBox.*;

public class RoundEndHandler {
    private Plugin plugin;
    private RoundStartHandler roundStartHandler;
    private Countdown roundOverTimer;
    public RoundEndHandler(Plugin plugin, RoundStartHandler roundStartHandler) {
        this.plugin = plugin;
        this.roundStartHandler = roundStartHandler;
    }

    public void cancelTimers() {
        if(roundOverTimer != null) roundOverTimer.cancelTimer();
    }
    public void endRound() {

        Team[][] matchups = roundStartHandler.getMatchups();
        Bukkit.broadcastMessage("\nRound Summary: ");
        for(int i = 0; i <matchups.length; i++) {
            boolean gameFound = false;
            for(Team t: winningTeamsList) {
                if(matchups[i][0].equals(t)) {
                    Bukkit.broadcastMessage(StringUtils.center(matchups[i][0].color + ChatColor.BOLD + matchups[i][0].getTeamName() + ChatColor.RESET + "  vs.  " + ChatColor.DARK_GRAY + matchups[i][1].getTeamName(), 45));
                    gameFound = true;
                } else if(matchups[i][1].equals(t)){
                    Bukkit.broadcastMessage(StringUtils.center(ChatColor.DARK_GRAY + matchups[i][0].getTeamName() + ChatColor.RESET + "  vs.  " + matchups[i][1].color + ChatColor.BOLD + matchups[i][1].getTeamName(), 45));
                    gameFound = true;
                }
            }
            if(!gameFound) {
                Bukkit.broadcastMessage(StringUtils.center(matchups[i][0].color + ChatColor.BOLD + matchups[i][0].getTeamName() + ChatColor.RESET + "  vs.  " + matchups[i][1].color + ChatColor.BOLD + matchups[i][1].getTeamName(), 45));
            }
        }
        Bukkit.broadcastMessage("\n\n");

        for(Team t: handler.getTeams()) {
            winningTeamsList.add(t);
        }


        roundOverTimer();
    }

    /**
     * Timer to put a pause between rounds
     */
    public void roundOverTimer() {
        roundOverTimer = new Countdown((JavaPlugin)plugin,
                5,
                //Timer Start
                () -> {
                    gameState = GameState.ROUND_OVER;
                },

                //Timer End
                () -> {
                    StartGameCommand.timeVar = 0;
                    Bukkit.broadcastMessage(ChatColor.RED + "Round " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + (round) + ChatColor.RESET + ChatColor.RED + " has Ended!");
                    round++;
                    roundStartHandler.startRound();
                    winningTeamsList.clear();
                    Collection<Entity> entityList = plugin.getConfig().getLocation("WorldSpawn").getWorld().getEntities();
                    for (Entity e : entityList) {
                        if (e.getType() != EntityType.PLAYER && e.getType() != EntityType.ITEM_FRAME) {
                            e.remove();
                        }
                    }
                },

                //Each Second
                (t) -> {
                    StartGameCommand.timeVar = t.getSecondsLeft();

                    if(t.getSecondsLeft() <= 5) {
                        //Bukkit.broadcastMessage(ChatColor.RED + "Round " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + round+1 + ChatColor.RESET + ChatColor.RED + " is Starting soon " + t.getSecondsLeft() + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + ChatColor.RESET + ChatColor.RED + " Seconds!");
                    }
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        roundOverTimer.scheduleTimer();
    }
}
