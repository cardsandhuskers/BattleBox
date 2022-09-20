package io.github.cardsandhuskers.teams.objects;

import com.mojang.authlib.GameProfile;
import io.github.cardsandhuskers.teams.Teams;
import io.github.cardsandhuskers.teams.handlers.TablistHandler;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import io.github.cardsandhuskers.teams.handlers.TeamHandler;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.UUID;

import static io.github.cardsandhuskers.teams.Teams.teamListArray;

public class Menu {
    private TeamHandler handler = Teams.handler;
    private Inventory inv;
    public Player player;
    TablistHandler tablistHandler;

    public Menu(Player p) {
        player = p;
        tablistHandler = new TablistHandler();

    }
    public void generateMenu(Player p) {
        inv = Bukkit.createInventory(p, 27, ChatColor.AQUA + "Team Menu");


        populateTeams();

        //Create and Leave team
        ItemStack createTeam = new ItemStack(Material.NAME_TAG, 1);
        ItemMeta createTeamMeta = createTeam.getItemMeta();
        createTeamMeta.setDisplayName(ChatColor.WHITE + "Create Team");
        createTeam.setItemMeta(createTeamMeta);

        ItemStack leaveTeam = new ItemStack(Material.BARRIER, 1);
        ItemMeta leaveTeamMeta = createTeam.getItemMeta();
        leaveTeamMeta.setDisplayName(ChatColor.WHITE + "Leave Team");
        leaveTeam.setItemMeta(leaveTeamMeta);

        ItemStack readyItem = new ItemStack(Material.RED_BANNER, 1);
        ItemMeta readyItemMeta = readyItem.getItemMeta();
        readyItemMeta.setDisplayName(ChatColor.WHITE + "Ready Up");
        readyItem.setItemMeta(readyItemMeta);


        inv.setItem(20, createTeam);
        inv.setItem(22, leaveTeam);
        inv.setItem(24, readyItem);


        p.openInventory(inv);
    }

    public void populateTeams() {
        boolean isReady;
        if(handler.getPlayerTeam(player) != null) {
            isReady = handler.getPlayerTeam(player).isReady();
        } else {
            isReady = false;
        }

        if(isReady) {
            ItemStack readyItem = new ItemStack(Material.LIME_BANNER, 1);
            ItemMeta readyItemMeta = readyItem.getItemMeta();
            readyItemMeta.setDisplayName(ChatColor.GREEN + "Ready!");
            readyItem.setItemMeta(readyItemMeta);
            inv.setItem(24, readyItem);
        } else {
            ItemStack readyItem = new ItemStack(Material.RED_BANNER, 1);
            ItemMeta readyItemMeta = readyItem.getItemMeta();
            readyItemMeta.setDisplayName(ChatColor.RED + "Ready Up!");
            readyItem.setItemMeta(readyItemMeta);
            inv.setItem(24, readyItem);
        }




        //empties out the teams portion of the menu
        for(int i = 0; i < 18; i++) {
            inv.setItem(i, null);
        }
        //checks if any teams exist (prevents null pointer)
        if(handler.getNumTeams() == 0) {

        } else {
            //for each team add the item to the menu
            int max;
            if(handler.getNumTeams() <= 18) {
                max = handler.getNumTeams();
            } else {
                max = 18;
            }

            ArrayList<ItemStack> teamStack = new ArrayList<>();

            for(int i = 0; i < max; i++) {
                //if team is empty, just add 1 (This should not happen, exists as a fallback)
                if(handler.getTeam(i).getPlayers().isEmpty()) {
                    teamStack.add(new ItemStack(handler.getTeam(i).getWoolColor(), 1));
                } else {
                    teamStack.add(new ItemStack(handler.getTeam(i).getWoolColor(), handler.getTeam(i).getSize()));
                }

                ItemMeta teamMeta = teamStack.get(i).getItemMeta();

                if(handler.getTeam(i).isReady()) {
                    teamStack.get(i).addUnsafeEnchantment(Enchantment.LURE, 1);
                    teamMeta = teamStack.get(i).getItemMeta();
                    teamMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                teamMeta.setDisplayName(handler.getTeam(i).color + handler.getTeam(i).getTeamName());

                ArrayList<String> teamLore = new ArrayList<>();
                teamLore.clear();
                //sets lore to empty for empty team (should not ever happen)
                if(handler.getTeam(i).getPlayers().isEmpty()) {
                    teamLore.add("EMPTY");
                } else {
                    //loops through members of the team to add to the lore
                    for(int j = 0; j < handler.getTeam(i).getSize(); j++) {
                        teamLore.add(ChatColor.WHITE + handler.getTeam(i).getPlayer(j).getName());
                    }
                }

                teamMeta.setLore(teamLore);
                teamStack.get(i).setItemMeta(teamMeta);
                inv.setItem(i, teamStack.get(i));

            }
            //tablistHandler.buildTabList(player);
        }
    }

}
