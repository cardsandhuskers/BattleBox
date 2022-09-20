package io.github.cardsandhuskers.teams;

import io.github.cardsandhuskers.teams.commands.LockTeamsCommand;
import io.github.cardsandhuskers.teams.commands.TeamCommand;
import io.github.cardsandhuskers.teams.commands.UnlockTeamsCommand;
import io.github.cardsandhuskers.teams.handlers.TeamHandler;
import io.github.cardsandhuskers.teams.listeners.InventoryCloseListener;
import io.github.cardsandhuskers.teams.listeners.TeamMenuListener;
import io.github.cardsandhuskers.teams.objects.Menu;
import io.github.cardsandhuskers.teams.objects.Placeholder;
import net.minecraft.server.level.ServerPlayer;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLOutput;
import java.util.ArrayList;

public final class Teams extends JavaPlugin {
    public static TeamHandler handler;

    public static boolean teamsLocked = false;
    public static ArrayList<Menu> menuList;

    public static ArrayList<ServerPlayer> teamListArray = new ArrayList<>();

    public static PlayerPointsAPI ppAPI;
    //public Menu m;
    @Override
    public void onEnable() {
        // Plugin startup logic

        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            this.ppAPI = PlayerPoints.getInstance().getAPI();
        } else {
            System.out.println("PLAYER POINTS API IS NULL");
            Bukkit.getPluginManager().disablePlugin(this);
        }

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

        handler = new TeamHandler();
        getCommand("teamMenu").setExecutor(new TeamCommand(handler, ppAPI));
        getCommand("lockTeams").setExecutor(new LockTeamsCommand());
        getCommand("unlockTeams").setExecutor(new UnlockTeamsCommand());
        getServer().getPluginManager().registerEvents(new TeamMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(), this);

        menuList = new ArrayList<>();



        // When you want to access the API, check if the instance is null
        if (this.ppAPI != null) {
            // Do stuff with the API here

        } else {
            Bukkit.getPluginManager().disablePlugin(this);
        }


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
