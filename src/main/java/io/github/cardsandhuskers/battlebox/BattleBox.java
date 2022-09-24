package io.github.cardsandhuskers.battlebox;

import io.github.cardsandhuskers.battlebox.commands.*;
import io.github.cardsandhuskers.battlebox.objects.Placeholder;
import io.github.cardsandhuskers.teams.Teams;
import io.github.cardsandhuskers.teams.handlers.TeamHandler;
import io.github.cardsandhuskers.teams.objects.Team;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;


public final class BattleBox extends JavaPlugin {
    private PlayerPointsAPI ppAPI;
    public static TeamHandler handler;
    public static int round;
    public static ArrayList<Block> completedArenasList = new ArrayList<>();
    public static ArrayList<Team> winningTeamsList = new ArrayList<>();


    @Override
    public void onEnable() {
        // Plugin startup logic
        //APIs
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            this.ppAPI = PlayerPoints.getInstance().getAPI();
        } else {
            System.out.println("Could not find PlayerPointsAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        /*
        // When you want to access the API, check if the instance is null
        if (this.ppAPI != null) {
            // Do stuff with the API here

        }

         */

        //Placeholder API validation
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            /*
             * We register the EventListener here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             */
            new Placeholder(this).register();

        } else {
            /*
             * We inform about the fact that PlaceholderAPI isn't installed and then
             * disable this plugin to prevent issues.
             */
            System.out.println("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        handler = Teams.handler;



        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //Register Commands
        getCommand("startBattleBox").setExecutor(new StartGameCommand(this, ppAPI));
        getCommand("setBattleboxSpawn").setExecutor(new SetWorldSpawnCommand(this));
        getCommand("setBattleboxArena").setExecutor(new SetArenaCommand(this));
        getCommand("setBattleboxArenaSpawn").setExecutor(new SetArenaSpawnCommand(this));
        getCommand("setBattleboxKitSpawn").setExecutor(new SetArenaKitSelectionCommand(this));

        //Events Are registered in the StartGameCommand


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}