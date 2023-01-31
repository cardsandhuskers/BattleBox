package io.github.cardsandhuskers.battlebox.handlers;

import io.github.cardsandhuskers.battlebox.commands.StartGameCommand;
import io.github.cardsandhuskers.battlebox.objects.Countdown;
import io.github.cardsandhuskers.teams.objects.Team;
import io.github.cardsandhuskers.teams.objects.TempPointsHolder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static io.github.cardsandhuskers.battlebox.BattleBox.*;

public class GameEndHandler {
    private Plugin plugin;

    public GameEndHandler(Plugin plugin) {
        this.plugin = plugin;
    }
    public void endGame() {

        Countdown timer = new Countdown((JavaPlugin)plugin,
                4,
                //Timer Start
                () -> {
                    for(Team t:handler.getTeams()) {
                        ArrayList<TempPointsHolder> tempPointsList = new ArrayList<>();
                        for(OfflinePlayer p:t.getPlayers()) {
                            tempPointsList.add(t.getPlayerTempPoints(p));
                        }
                        Collections.sort(tempPointsList, Comparator.comparing(TempPointsHolder::getPoints));
                        Collections.reverse(tempPointsList);


                        for(Player p:t.getOnlinePlayers()) {
                            p.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Your Team Standings:");
                            p.sendMessage(ChatColor.DARK_BLUE + "------------------------------");
                            int number = 1;
                            for(TempPointsHolder h:tempPointsList) {
                                p.sendMessage(number + ". " + handler.getPlayerTeam(p).color + h.getPlayer().getName() + ChatColor.RESET + "    Points: " + h.getPoints());
                                number++;
                            }
                            p.sendMessage(ChatColor.DARK_BLUE + "------------------------------\n");
                        }
                    }
                    roundsWon.clear();
                },

                //Timer End
                () -> {

                },

                //Each Second
                (t) -> {
                    if(t.getSecondsLeft() == 2) {
                        ArrayList<TempPointsHolder> tempPointsList = new ArrayList<>();
                        for(Team team: handler.getTeams()) {
                            for(Player p:team.getOnlinePlayers()) {
                                tempPointsList.add(team.getPlayerTempPoints(p));
                            }
                        }
                        Collections.sort(tempPointsList, Comparator.comparing(TempPointsHolder::getPoints));
                        Collections.reverse(tempPointsList);

                        int max;
                        if(tempPointsList.size() >= 5) {
                            max = 4;
                        } else {
                            max = tempPointsList.size() - 1;
                        }

                        Bukkit.broadcastMessage("\n" + ChatColor.RED + "" + ChatColor.BOLD + "Top 5 Players:");
                        Bukkit.broadcastMessage(ChatColor.DARK_RED + "------------------------------");
                        int number = 1;
                        for(int i = 0; i <= max; i++) {
                            TempPointsHolder h = tempPointsList.get(i);
                            Bukkit.broadcastMessage(number + ". " + handler.getPlayerTeam(h.getPlayer()).color + h.getPlayer().getName() + ChatColor.RESET + "    Points: " +  h.getPoints());
                            number++;
                        }
                        Bukkit.broadcastMessage(ChatColor.DARK_RED + "------------------------------");
                    }
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        timer.scheduleTimer();


        //print results




        gameEndTimer();

    }

    public void gameEndTimer() {
        Countdown timer = new Countdown((JavaPlugin)plugin,
                plugin.getConfig().getInt("GameEndTime"),
                //Timer Start
                () -> {
                    StartGameCommand.timerStatus = "Return to Lobby";
                },

                //Timer End
                () -> {
                    //tp everyone to lobby
                    Location location = plugin.getConfig().getLocation("Lobby");
                    for (Player p: Bukkit.getOnlinePlayers()) {
                        p.teleport(location);
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "startRound");
                    //Unregisters the handlers to clean them up
                    HandlerList.unregisterAll(plugin);
                },

                //Each Second
                (t) -> {
                    StartGameCommand.timeVar = t.getSecondsLeft();
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        timer.scheduleTimer();
    }



}
