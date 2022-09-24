package io.github.cardsandhuskers.battlebox.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class GameEndHandler {
    private Plugin plugin;

    public GameEndHandler(Plugin plugin) {
        this.plugin = plugin;
    }
    public void endGame() {
        //Unregisters the handlers to clean them up
        HandlerList.unregisterAll(plugin);

        Bukkit.broadcastMessage("Game has ended!");
        //print results


        //tp everyone to lobby and kill handlers after countdown
        for (Player p: Bukkit.getOnlinePlayers()) {
            //p.teleport("world", )
        }

    }
}
