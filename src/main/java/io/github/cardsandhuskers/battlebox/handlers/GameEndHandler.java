package io.github.cardsandhuskers.battlebox.handlers;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.commands.StartGameCommand;
import io.github.cardsandhuskers.battlebox.objects.Countdown;
import io.github.cardsandhuskers.battlebox.objects.GameMessages;
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
    private Countdown gameOverTimer;

    public GameEndHandler(BattleBox plugin) {
        this.plugin = plugin;
    }

    public void endGame() {
        gameOverTimer = new Countdown((JavaPlugin)plugin,
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
                    if(t.getSecondsLeft() == t.getTotalSeconds() - 1) GameMessages.announceTopPlayers();
                    if(t.getSecondsLeft() == t.getTotalSeconds() - 6) GameMessages.announceTeamPlayers();
                    if(t.getSecondsLeft() == t.getTotalSeconds() - 11) GameMessages.announceTeamLeaderboard();


                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        gameOverTimer.scheduleTimer();
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

    public void cancelTimers() {
        if(gameOverTimer != null) gameOverTimer.cancelTimer();
    }

}
