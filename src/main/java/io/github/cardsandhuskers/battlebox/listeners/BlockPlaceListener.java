package io.github.cardsandhuskers.battlebox.listeners;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.handlers.RoundStartHandler;
import io.github.cardsandhuskers.teams.objects.Team;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

import static io.github.cardsandhuskers.battlebox.BattleBox.*;

public class BlockPlaceListener implements Listener {
    ArrayList<Block> blockList;
    ArrayList<Block> centerBlockList;
    Plugin plugin;
    RoundStartHandler roundStartHandler;
    PlayerPointsAPI ppAPI;

    public BlockPlaceListener(RoundStartHandler roundStartHandler, Plugin plugin, PlayerPointsAPI ppAPI) {
        this.plugin = plugin;
        this.roundStartHandler = roundStartHandler;
        this.ppAPI = ppAPI;
        blockList = roundStartHandler.getBlockList();
        centerBlockList = roundStartHandler.getCenterBlockList();
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        //if block is valid (wool)
        if(isValid(e.getBlock().getType())) {
            boolean isValidLocation = false;
            for(Block b: blockList) {
                if(e.getBlock().equals(b)) {
                    isValidLocation = true;
                }
            }
            //if the location is not valid, cancel
            if(!isValidLocation) {
                e.setCancelled(true);
            } else {
                int completedArenas = 0;

                //for each center block
                for(Block b: centerBlockList) {
                    //get 3x3 around it
                    Material centerMat = b.getType();
                    boolean allSame = true;
                    //check if all 9 blocks are same type of wool excluding white
                    //excludes white
                    if(centerMat == Material.WHITE_WOOL) {
                        allSame = false;
                    }
                    //loop through 3x3 and check
                    for(int x = -1; x <= 1; x++) {
                        for(int z = -1; z <= 1; z++) {
                            Location l = b.getLocation();
                            l.setX(l.getX() + x);
                            l.setZ(l.getZ() + z);
                            Block tempBlock = l.getBlock();
                            //if any block is different from center, set to false
                            if(!(tempBlock.getType() == centerMat)) {
                                allSame = false;
                            }
                            if(tempBlock.getType() == Material.AIR) {
                                allSame = false;
                            }
                            if(tempBlock.getType() == Material.WHITE_WOOL) {
                                allSame = false;
                            }
                        }
                    }
                    //after loop, if allSame is true, then arena is done
                    if(allSame) {
                        //If the completed arena is the same one as the block place, give the winning team points
                        if(centerMat == e.getBlock().getType()) {
                            completedArenasList.add(getCenter(e.getBlock()));
                            String teamColor = translateColor(centerMat);
                            Team t = handler.getTeamByColor(teamColor);
                            winningTeamsList.add(t);
                            for(Player p: t.getOnlinePlayers()) {
                                ppAPI.give(p.getUniqueId(), 50);
                                p.sendMessage(ChatColor.GREEN + "Your Team Won! " + ChatColor.RESET + "[+" + 50 + "] " + ChatColor.GREEN + "Points!");
                                p.sendTitle(ChatColor.GREEN + "Your Team Won!", "", 4, 40, 4);
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                            }
                            Location loc = b.getLocation();
                            loc.setY(loc.getY()+1);
                            loc.setX(loc.getX() + .5);
                            loc.setZ(loc.getZ() + .5);
                            Firework firework = (Firework) b.getWorld().spawnEntity(loc, EntityType.FIREWORK);
                            FireworkMeta fireworkMeta = firework.getFireworkMeta();
                            fireworkMeta.addEffect(FireworkEffect.builder().withColor(handler.getPlayerTeam(e.getPlayer()).translateColor()).flicker(true).build());
                            firework.setFireworkMeta(fireworkMeta);
                            firework.detonate();
                        }
                        completedArenas++;
                    }
                }
                //after all arenas have been checked, see if all have been completed
                int numArenas;
                if(handler.getNumTeams() %2 == 0) {
                    numArenas = handler.getNumTeams()/2;
                } else {
                    numArenas = handler.getNumTeams()/2 + 1;
                }
                if(completedArenas >= numArenas) {
                    roundStartHandler.cancelInGameTimer();
                    roundStartHandler.endRound();
                }
            }
        } else {
            e.setCancelled(true);
        }
    }

    public Block getCenter(Block b) {
        for(int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Location l = b.getLocation();
                l.setX(l.getX() + x);
                l.setZ(l.getZ() + z);
                Block tempBlock = l.getBlock();
                for(Block block:centerBlockList) {
                    //if tempBlock is equal to a center block return it
                    if(tempBlock.equals(block)) {
                        System.out.println("EQUAL");
                        return tempBlock;
                    }
                }
            }
        }
        return null;
    }

    /**
     *Checks if the material passed is valid
     * @return Material
     */
    public boolean isValid(Material material) {
        switch (material) {
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
            case YELLOW_WOOL:
            case WHITE_WOOL: return true;
            default: return false;
        }
    }

    /**
     * Gets the string version of the color of the wool type passed
     * @param material
     * @return String of color
     */
    public String translateColor(Material material) {
        switch (material) {
            case GREEN_WOOL: return "§2";
            case CYAN_WOOL: return "§3";
            case PURPLE_WOOL: return "§5";
            case ORANGE_WOOL: return "§6";
            case LIGHT_GRAY_WOOL: return "§7";
            case GRAY_WOOL: return "§8";
            case BLUE_WOOL: return "§9";
            case LIME_WOOL: return "§a";
            case LIGHT_BLUE_WOOL: return "§b";
            case RED_WOOL: return "§c";
            case MAGENTA_WOOL: return "§d";
            case YELLOW_WOOL: return "§e";
            case WHITE_WOOL: return "§f";
            default: return null;
        }
    }

}
