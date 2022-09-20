package io.github.cardsandhuskers.teams.listeners;

import io.github.cardsandhuskers.teams.Teams;
import io.github.cardsandhuskers.teams.handlers.TeamHandler;
import io.github.cardsandhuskers.teams.objects.Menu;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import static io.github.cardsandhuskers.teams.Teams.menuList;


public class TeamMenuListener implements Listener {
    TeamHandler handler = Teams.handler;
    Plugin plugin;


    public TeamMenuListener(Plugin p) {
        plugin = p;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        String invName = e.getView().getTitle();

        if (invName.equalsIgnoreCase(ChatColor.AQUA + "Team Menu")) {
            HumanEntity p = e.getWhoClicked();
            Player player = (Player) p;
            if (e.getCurrentItem() == null) {
                return;
            }

            if (isValid(e.getCurrentItem().getType())) {
                String s = e.getCurrentItem().getItemMeta().getDisplayName();
                s = ChatColor.stripColor(s);
                boolean result = handler.addPlayer(player, s);
                if (result == false) {
                    p.sendMessage(ChatColor.RED + "ERROR: You are already on that team");
                }
            }

            if (e.getCurrentItem().getType() == Material.NAME_TAG) {
                new AnvilGUI.Builder()
                        .onClose(player1 -> {                                               //called when the inventory is closing
                        })
                        .onComplete((player1, text) -> {                                    //called when the inventory output slot is clicked
                            String teamName = text.trim();
                            boolean result = handler.createTeam(teamName);

                            if (result == true) {
                                handler.addPlayer(player, teamName);
                                player.sendMessage(handler.getTeam(teamName).color + "Team " + teamName + " created successfully!");

                            } else {
                                player.sendMessage("Could not Create Team");
                            }
                            return AnvilGUI.Response.close();
                        })
                        .text(" ")                              //sets the text the GUI should start with
                        .itemLeft(new ItemStack(Material.PAPER))                      //use a custom item for the first slot
                        .title("Enter Team Name:")                                       //set the title of the GUI (only works in 1.14+)
                        .plugin(plugin)                                          //set the plugin instance
                        .open(player);                                                   //opens the GUI for the player provided
            }

                if (e.getCurrentItem().getType() == Material.BARRIER) {
                    if (!(handler.getPlayerTeam(player) == null)) {
                        handler.removePlayer(player, handler.getPlayerTeam(player));
                    } else {
                        p.sendMessage(ChatColor.RED + "ERROR: You are not on a team");
                    }
                }

                if (e.getSlot() == 24) {
                    if (handler.getPlayerTeam(player) == null) {

                    } else {
                        handler.getPlayerTeam(player).toggleReady();
                    }


                }

                for (Menu m : menuList) {
                    m.populateTeams();
                }
                e.setCancelled(true);
        }
    }
    public boolean isValid(Material m) {
        switch (m) {
            case GREEN_WOOL:
            case LIGHT_BLUE_WOOL:
            case CYAN_WOOL:
            case PURPLE_WOOL:
            case ORANGE_WOOL:
            case LIGHT_GRAY_WOOL:
            case GRAY_WOOL:
            case BLUE_WOOL:
            case LIME_WOOL:
            case RED_WOOL:
            case MAGENTA_WOOL:
            case YELLOW_WOOL: return true;
            default: return false;
        }
    }


}
