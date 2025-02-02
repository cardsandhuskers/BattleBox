package io.github.cardsandhuskers.battlebox.commands;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.handlers.ArenaWallHandler;
import io.github.cardsandhuskers.battlebox.handlers.RoundStartHandler;
import io.github.cardsandhuskers.battlebox.listeners.*;
import io.github.cardsandhuskers.battlebox.objects.Countdown;
import io.github.cardsandhuskers.battlebox.objects.GameMessages;
import io.github.cardsandhuskers.teams.objects.Team;
import io.github.cardsandhuskers.battlebox.objects.stats.Stats;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static io.github.cardsandhuskers.battlebox.BattleBox.*;
import static org.bukkit.Bukkit.getServer;

public class StartGameCommand implements CommandExecutor {
    Plugin plugin;
    RoundStartHandler roundStartHandler;
    private Countdown pregameTimer;
    public static int timeVar = 0;
    ArenaWallHandler wallHandler;
    Stats killStats;


    public StartGameCommand(Plugin plugin) {
        this.plugin = plugin;
        this.killStats = new Stats("round,killer,killerTeam,prey,preyTeam,time");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        round = 1;
        if(sender instanceof  Player player) {
            //Check if player is operator to prevent non ops from running it
            if(player.isOp()) {
                //make sure there are teams
                if (handler.getNumTeams() == 0) {
                    player.sendMessage(ChatColor.RED + "ERROR: There are no Teams");
                } else {
                    wallHandler = new ArenaWallHandler(plugin);
                    roundStartHandler = new RoundStartHandler((BattleBox) plugin, wallHandler,killStats);

                    startPregameCountdown();

                    player.sendMessage("Command Received");
                    try {
                        if(args.length > 0) {
                            multiplier = Float.parseFloat(args[0]);
                        }
                    } catch(Exception e) {
                        player.sendMessage(ChatColor.RED + "ERROR: MULTIPLIER MUST BE A FLOAT");
                    }


                }
            } else {
                player.sendMessage(ChatColor.RED + "You do not have Permission to do this");
            }
        //make sure there are teams
        } else {
            if(handler.getNumTeams() == 0) {
                for(Player p:Bukkit.getOnlinePlayers()) {
                    if(p.isOp()) {
                        p.sendMessage(ChatColor.RED + "ERROR: No Teams");
                    }
                }
            } else {
                wallHandler = new ArenaWallHandler(plugin);
                roundStartHandler = new RoundStartHandler((BattleBox) plugin, wallHandler, killStats);
                startPregameCountdown();

                try {
                    if(args.length > 0) {
                        multiplier = Float.parseFloat(args[0]);
                    }
                } catch(Exception e) {
                    System.out.println(ChatColor.RED + "ERROR: MULTIPLIER MUST BE A FLOAT");
                }
            }
        }

        return false;
    }

    /**
     * Starts the countdown before the first round starts
     */
    public void startPregameCountdown() {
        int totalSeconds = plugin.getConfig().getInt("PregameTime");
        pregameTimer = new Countdown((JavaPlugin)plugin,
                //should be 80
                totalSeconds,
                //Timer Start
                () -> {
                    gameState = GameState.GAME_STARTING;
                    Location location = plugin.getConfig().getLocation("WorldSpawn");
                    for(Player p: Bukkit.getOnlinePlayers()) {
                        p.teleport(location);
                    }
                    killsMap = new HashMap<>();
                    roundsWon = new HashMap<>();
                    getServer().getPluginManager().registerEvents(new PregamePlayerAttackListener(), plugin);
                    getServer().getPluginManager().registerEvents(new PlayerJoinListener(plugin), plugin);
                    getServer().getPluginManager().registerEvents(new PlayerDamageListener(), plugin);

                    //Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Battlebox is Starting Soon. Get Ready!");
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()-> {
                        for(Player p:Bukkit.getOnlinePlayers()) {
                            if(handler.getPlayerTeam(p) != null) {
                                p.setGameMode(GameMode.ADVENTURE);
                            } else {
                                p.setGameMode(GameMode.SPECTATOR);
                            }
                        }
                    }, 5L);
                    Location spawn = plugin.getConfig().getLocation("WorldSpawn");
                    spawn.getWorld().setSpawnLocation(spawn);



                    for(Team t: handler.getTeams()) {
                        t.resetTempPoints();
                        for(Player p:t.getOnlinePlayers()) {
                            killsMap.put(p, 0);
                        }
                    }
                },

                //Timer End
                () -> {
                    HandlerList.unregisterAll(plugin);
                    //Register EventListeners
                    getServer().getPluginManager().registerEvents(new PlayerAttackListener(), plugin);
                    getServer().getPluginManager().registerEvents(new PlayerDamageListener(), plugin);
                    getServer().getPluginManager().registerEvents(new BlockBreakListener(roundStartHandler), plugin);
                    getServer().getPluginManager().registerEvents(new BlockPlaceListener(roundStartHandler, plugin), plugin);
                    //getServer().getPluginManager().registerEvents(new ArrowShootListener(), plugin);
                    //only kills arrow entities when they hit, I don't want this to happen
                    //getServer().getPluginManager().registerEvents(new ArrowStopListener(), plugin);
                    getServer().getPluginManager().registerEvents(new ButtonPressListener(roundStartHandler), plugin);
                    getServer().getPluginManager().registerEvents(new PlayerJoinListener(plugin), plugin);
                    getServer().getPluginManager().registerEvents(new PlayerMoveListener(), plugin);
                    getServer().getPluginManager().registerEvents(new PlayerThrowListener(), plugin);

                    HashMap<Player, Location> playerLocationMap = new HashMap<>();
                    getServer().getPluginManager().registerEvents(new PlayerDeathListener(plugin, playerLocationMap,killStats), plugin);
                    getServer().getPluginManager().registerEvents(new PlayerRespawnListener(plugin, playerLocationMap), plugin);


                    roundStartHandler.startRound();
                    Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                    if(scoreboard.getObjective("belowNameHP") != null) scoreboard.getObjective("belowNameHP").unregister();
                    Objective belowNameHP = scoreboard.registerNewObjective("belowNameHP", Criteria.HEALTH, ChatColor.DARK_RED + "❤");
                    belowNameHP.setDisplaySlot(DisplaySlot.BELOW_NAME);
                    for(Player p:Bukkit.getOnlinePlayers()) {
                        p.sendHealthUpdate();
                    }

                },

                //Each Second
                (t) -> {
                    if(t.getSecondsLeft() == totalSeconds - 2) Bukkit.broadcastMessage(GameMessages.gameDescription());
                    if(t.getSecondsLeft() == totalSeconds - 12) Bukkit.broadcastMessage(GameMessages.kitsDescription());
                    if(t.getSecondsLeft() == totalSeconds - 22) Bukkit.broadcastMessage(GameMessages.pointsDescription((BattleBox) plugin));

                    if(t.getSecondsLeft() == 15 || t.getSecondsLeft() == 10 || t.getSecondsLeft() == 5) {
                        for(Player p:Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }
                        Bukkit.broadcastMessage(ChatColor.RED + "There are " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + t.getSecondsLeft() + ChatColor.RESET + ChatColor.RED + " seconds until BattleBox Starts");
                    }
                    timeVar = t.getSecondsLeft();
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        pregameTimer.scheduleTimer();

    }
    public boolean cancelTimers() {
        if(pregameTimer != null) {
            pregameTimer.cancelTimer();
            roundStartHandler.cancelTimers();
            return true;
        } else {
            return false;
        }
    }

}
