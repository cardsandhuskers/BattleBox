package io.github.cardsandhuskers.battlebox.handlers;

import io.github.cardsandhuskers.battlebox.commands.StartGameCommand;
import io.github.cardsandhuskers.battlebox.objects.Countdown;
import io.github.cardsandhuskers.teams.objects.Team;
import io.github.cardsandhuskers.teams.objects.TempPointsHolder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
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
                plugin.getConfig().getInt("GameEndTime"),
                //Timer Start
                () -> {
                    StartGameCommand.timerStatus = "Return to Lobby";
                    roundsWon.clear();
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

                    if(t.getSecondsLeft() == t.getTotalSeconds() - 3)
                        for(Team team:handler.getTeams()) {
                            ArrayList<TempPointsHolder> tempPointsList = new ArrayList<>();
                            for(OfflinePlayer p:team.getPlayers()) {
                                tempPointsList.add(team.getPlayerTempPoints(p));
                            }
                            Collections.sort(tempPointsList, Comparator.comparing(TempPointsHolder::getPoints));
                            Collections.reverse(tempPointsList);


                            for(Player p:team.getOnlinePlayers()) {
                                p.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Your Team Standings:");
                                p.sendMessage(ChatColor.DARK_BLUE + "------------------------------");
                                int number = 1;
                                for(TempPointsHolder h:tempPointsList) {
                                    p.sendMessage(number + ". " + handler.getPlayerTeam(p).color + h.getPlayer().getName() + ChatColor.RESET + "    Points: " + (int)h.getPoints());
                                    number++;
                                }
                                p.sendMessage(ChatColor.DARK_BLUE + "------------------------------\n");
                            }
                        }

                    if(t.getSecondsLeft() == t.getTotalSeconds() - 8) {
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
                            Bukkit.broadcastMessage(number + ". " + handler.getPlayerTeam(h.getPlayer()).color + h.getPlayer().getName() + ChatColor.RESET + "    Points: " +  (int)h.getPoints());
                            number++;
                        }
                        Bukkit.broadcastMessage(ChatColor.DARK_RED + "------------------------------");
                    }
                    if(t.getSecondsLeft() == t.getTotalSeconds() - 13) {
                        ArrayList<Team> teamList = handler.getTempPointsSortedList();

                        Bukkit.broadcastMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Team Performance:");
                        Bukkit.broadcastMessage(ChatColor.GREEN + "------------------------------");
                        int counter = 1;
                        for (Team team : teamList) {
                            Bukkit.broadcastMessage(counter + ". " + team.color + ChatColor.BOLD + team.getTeamName() + ChatColor.RESET + " Points: " + (int)team.getTempPoints());
                            counter++;
                        }
                        Bukkit.broadcastMessage(ChatColor.GREEN + "------------------------------");
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }
                    }


                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        timer.scheduleTimer();
    }

}
