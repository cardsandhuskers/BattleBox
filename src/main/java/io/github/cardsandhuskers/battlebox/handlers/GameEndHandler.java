package io.github.cardsandhuskers.battlebox.handlers;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.commands.StartGameCommand;
import io.github.cardsandhuskers.battlebox.objects.Countdown;
import io.github.cardsandhuskers.teams.objects.Team;
import io.github.cardsandhuskers.teams.objects.TempPointsHolder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static io.github.cardsandhuskers.battlebox.BattleBox.*;
import static io.github.cardsandhuskers.teams.Teams.handler;

public class GameEndHandler {
    private BattleBox plugin;

    public GameEndHandler(BattleBox plugin) {
        this.plugin = plugin;
    }

    public void endGame() {
        Countdown timer = new Countdown((JavaPlugin)plugin,
                plugin.getConfig().getInt("GameEndTime"),
                //Timer Start
                () -> {
                    gameState = GameState.GAME_OVER;
                    Bukkit.getScoreboardManager().getMainScoreboard().getObjective("belowNameHP").unregister();
                },

                //Timer End
                () -> {
                    try {
                        saveRecords();
                    } catch (IOException e) {
                        StackTraceElement[] trace = e.getStackTrace();
                        String str = "";
                        for(StackTraceElement element:trace) str += element.toString() + "\n";
                        plugin.getLogger().severe("ERROR Calculating Stats!\n" + str);
                    }

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

    public void saveRecords() throws IOException {
        for(Player p:killsMap.keySet()) if(p != null) System.out.println(p.getDisplayName() + ": " + killsMap.get(p));
        System.out.println("~~~~~~~~~~~~~~~");

        FileWriter writer = new FileWriter(plugin.getDataFolder() + "/stats.csv", true);
        FileReader reader = new FileReader(plugin.getDataFolder() + "/stats.csv");

        String[] headers = {"Event", "Team", "Name", "Kills", "Wins"};

        CSVFormat.Builder builder = CSVFormat.Builder.create();
        builder.setHeader(headers);
        CSVFormat format = builder.build();

        CSVParser parser = new CSVParser(reader, format);

        if(!parser.getRecords().isEmpty()) {
            format = CSVFormat.DEFAULT;
        }

        CSVPrinter printer = new CSVPrinter(writer, format);

        int eventNum;
        try {eventNum = Bukkit.getPluginManager().getPlugin("LobbyPlugin").getConfig().getInt("eventNum");} catch (Exception e) {eventNum = 1;}
        //printer.printRecord(currentGame);
        for(Player p:killsMap.keySet()) {
            if(p == null) continue;
            if(handler.getPlayerTeam(p) == null) continue;

            Team t = handler.getPlayerTeam(p);
            int rounds;
            if(roundsWon.containsKey(t)) rounds = roundsWon.get(t);
            else rounds = 0;

            printer.printRecord(eventNum, t.getTeamName(), p.getDisplayName(), killsMap.get(p), rounds);
        }
        writer.close();

        try {
            plugin.statCalculator.calculateStats();
        } catch (Exception e) {
            StackTraceElement[] trace = e.getStackTrace();
            String str = "";
            for(StackTraceElement element:trace) str += element.toString() + "\n";
            plugin.getLogger().severe("ERROR Calculating Stats!\n" + str);
        }

    }

}
