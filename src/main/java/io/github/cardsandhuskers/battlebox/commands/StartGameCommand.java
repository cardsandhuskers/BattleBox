package io.github.cardsandhuskers.battlebox.commands;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.handlers.RoundStartHandler;
import io.github.cardsandhuskers.battlebox.listeners.*;
import io.github.cardsandhuskers.battlebox.objects.Countdown;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static io.github.cardsandhuskers.battlebox.BattleBox.handler;
import static io.github.cardsandhuskers.battlebox.BattleBox.round;
import static org.bukkit.Bukkit.getServer;

public class StartGameCommand implements CommandExecutor {
    Plugin plugin;
    PlayerPointsAPI ppAPI;
    RoundStartHandler roundStartHandler;
    public static int timeVar = 0;
    public static String timerStatus = "Game Starts in";
    public StartGameCommand(Plugin plugin, PlayerPointsAPI ppAPI) {
        this.plugin = plugin;
        this.ppAPI = ppAPI;
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
                    roundStartHandler = new RoundStartHandler((BattleBox) plugin);
                    startPregameCountdown();

                    player.sendMessage("Command Received");
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
                roundStartHandler = new RoundStartHandler((BattleBox) plugin);
                startPregameCountdown();
            }
        }



        return false;
    }

    /**
     * Starts the countdown before the first round starts
     */
    public void startPregameCountdown() {
        Countdown timer = new Countdown((JavaPlugin)plugin,
                10,
                //Timer Start
                () -> {
                    timerStatus = "Game Starts in";
                    Location location = plugin.getConfig().getLocation("WorldSpawn");
                    for(Player p: Bukkit.getOnlinePlayers()) {
                        p.teleport(location);
                    }
                    getServer().getPluginManager().registerEvents(new PregamePlayerAttackListener(), plugin);
                    Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Battlebox is Starting Soon. Get Ready!");

                    for(Player p:Bukkit.getOnlinePlayers()) {
                        p.setGameMode(GameMode.ADVENTURE);
                    }

                },

                //Timer End
                () -> {
                    HandlerList.unregisterAll(plugin);
                    //Register EventListeners
                    getServer().getPluginManager().registerEvents(new PlayerAttackListener(ppAPI), plugin);
                    getServer().getPluginManager().registerEvents(new BlockBreakListener(roundStartHandler), plugin);
                    getServer().getPluginManager().registerEvents(new BlockPlaceListener(roundStartHandler, plugin, ppAPI), plugin);
                    getServer().getPluginManager().registerEvents(new ArrowShootListener(), plugin);
                    getServer().getPluginManager().registerEvents(new ArrowStopListener(), plugin);
                    getServer().getPluginManager().registerEvents(new ButtonPressListener(roundStartHandler), plugin);

                    roundStartHandler.startRound();

                },

                //Each Second
                (t) -> {
                    if(t.getSecondsLeft() == 15 || t.getSecondsLeft() == 10 || t.getSecondsLeft() <= 5) {
                        Bukkit.broadcastMessage(ChatColor.RED + "There are " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + t.getSecondsLeft() + ChatColor.RESET + ChatColor.RED + " seconds until BattleBox Starts");
                    }
                    timeVar = t.getSecondsLeft();
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        timer.scheduleTimer();
    }

}
