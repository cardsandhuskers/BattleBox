package io.github.cardsandhuskers.teams.listeners;

import io.github.cardsandhuskers.teams.handlers.TablistHandler;
import io.github.cardsandhuskers.teams.objects.Menu;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;

import static io.github.cardsandhuskers.teams.Teams.menuList;


public class InventoryCloseListener implements Listener {
    private TablistHandler tablistHandler;

    public InventoryCloseListener() {
        tablistHandler = new TablistHandler();
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent e) {
        HumanEntity p = e.getPlayer();
        Player player = (Player)p;
        ArrayList<Menu> tempMenuList = new ArrayList<>();


        for (Menu m : menuList) {
            m.populateTeams();
        }
        //tablistHandler.buildTabList(player);

        for(Menu m: menuList) {
            if(m.player.equals(player)) {
                tempMenuList.add(m);

            }
        }
        for(Menu m:tempMenuList) {
            menuList.remove(m);
        }


    }
}
