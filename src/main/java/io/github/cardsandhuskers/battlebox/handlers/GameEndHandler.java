package io.github.cardsandhuskers.battlebox.handlers;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.BattleBox.GameState;
import io.github.cardsandhuskers.battlebox.commands.StartGameCommand;
import io.github.cardsandhuskers.battlebox.objects.Countdown;
import io.github.cardsandhuskers.battlebox.objects.GameMessages;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import static io.github.cardsandhuskers.battlebox.BattleBox.*;

import io.github.cardsandhuskers.battlebox.objects.stats.Stats;

public class GameEndHandler {
    private BattleBox plugin;
    private Countdown gameOverTimer;
    private Stats killStats;
    private Stats winStats;

    public GameEndHandler(BattleBox plugin, Stats killStats, Stats winStats) {
        this.plugin = plugin;
        this.killStats = killStats;
        this.winStats = winStats;
    }

    public void endGame() {
        gameOverTimer = new Countdown((JavaPlugin)plugin,
            plugin.getConfig().getInt("GameEndTime"),
                //Timer Start
                () -> {
                    gameState = GameState.GAME_OVER;
                    Bukkit.getScoreboardManager().getMainScoreboard().getObjective("belowNameHP").unregister();

                    int eventNum = -1;
                    try {
                        eventNum = Bukkit.getPluginManager().getPlugin("LobbyPlugin")
                            .getConfig().getInt("eventNum");
                    }   catch (Exception e) 
                        {eventNum = 1;}

                    String fileName = "battleBoxKillStats" + Integer.toString(eventNum);
                    killStats.writeToFile(plugin.getDataFolder().toPath().toString(), fileName);

                    fileName = "battleBoxWinStats" + Integer.toString(eventNum);
                    winStats.writeToFile(plugin.getDataFolder().toPath().toString(), fileName);

                },

                //Timer End
                () -> {
                    /*try {
                        //saveRecords();
                    } catch (IOException e) {
                        StackTraceElement[] trace = e.getStackTrace();
                        String str = "";
                        for(StackTraceElement element:trace) str += element.toString() + "\n";
                        plugin.getLogger().severe("ERROR Calculating Stats!\n" + str);
                    }*/

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

    /*public void saveRecords() throws IOException {

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
            if(TeamHandler.getInstance().getPlayerTeam(p) == null) continue;

            Team t = TeamHandler.getInstance().getPlayerTeam(p);
            int rounds;
            rounds = roundsWon.getOrDefault(t, 0);

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

    }*/

    public void cancelTimers() {
        if(gameOverTimer != null) gameOverTimer.cancelTimer();
    }

}
