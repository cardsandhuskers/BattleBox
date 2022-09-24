package io.github.cardsandhuskers.battlebox.listeners;

import io.github.cardsandhuskers.battlebox.handlers.RoundStartHandler;
import io.github.cardsandhuskers.battlebox.objects.TeamKits;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

import static io.github.cardsandhuskers.battlebox.BattleBox.handler;

public class ButtonPressListener implements Listener {
    private RoundStartHandler roundStartHandler;

    public ButtonPressListener(RoundStartHandler roundStartHandler) {
        this.roundStartHandler = roundStartHandler;
    }

    @EventHandler
    public void onButtonPress(PlayerInteractEvent e) {
        if(e.getClickedBlock() != null) {
            Team t = handler.getPlayerTeam(e.getPlayer());
            Player p = e.getPlayer();
            ArrayList<TeamKits> teamKitsList = roundStartHandler.getTeamKitsList();

            if(e.getClickedBlock().getType() == Material.STONE_BUTTON) {

                Location buttonLoc = e.getClickedBlock().getLocation();
                //if block behind -x side A, +x is side B
                Location tempLoc = new Location(buttonLoc.getWorld(), buttonLoc.getX() + 1, buttonLoc.getY(), buttonLoc.getZ());

                Location backBlock;
                int counter = 0;
                int side;


                //side 1, +x is air
                if(tempLoc.getBlock().getType() == Material.AIR) {
                    side = 1;
                    //set X of backBlock location to clickedButton location -1
                    backBlock = new Location(buttonLoc.getWorld(), buttonLoc.getX() - 1, buttonLoc.getY(), buttonLoc.getZ());

                    //subtract z for side 1
                    buttonLoc.setZ(buttonLoc.getZ() - 1);

                    while(buttonLoc.getBlock().getType() == Material.STONE_BUTTON) {
                        buttonLoc.setZ(buttonLoc.getZ() - 1);
                        counter++;
                    }
                    //set initial button loc to first button in list
                    buttonLoc.setZ(buttonLoc.getZ() + 1);


                }
                //side 2, -x is air
                else {
                    side = 2;
                    //set X of backBlock location to clickedButton location + 1
                    backBlock = new Location(buttonLoc.getWorld(), buttonLoc.getX() + 1, buttonLoc.getY(), buttonLoc.getZ());

                    //add z for side 2
                    buttonLoc.setZ(buttonLoc.getZ() + 1);

                    while(buttonLoc.getBlock().getType() == Material.STONE_BUTTON) {
                        buttonLoc.setZ(buttonLoc.getZ() + 1);
                        counter++;
                    }
                    //set initial button loc to first button in list
                    buttonLoc.setZ(buttonLoc.getZ() - 1);
                }




                for(TeamKits k: teamKitsList) {
                    if (k.getTeam().equals(t)) {

                        if(counter == 3) {
                            //if kit has not been selected
                            if(k.selectMarksman(p)) {
                                //update blocks nearby
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                                p.sendMessage("Marksman Kit selected");
                            //if kit has already been selected
                            } else {
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1f, 1f);
                                p.sendMessage("Marksman Kit has already been selected!");
                            }
                        }
                        if(counter == 2) {
                            //if kit has not been selected
                            if(k.selectPotionMaster(p)) {
                                //update blocks nearby
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                                p.sendMessage("Potion Master Kit selected");
                            //if kit has already been selected
                            } else {
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1f, 1f);
                                p.sendMessage("Potion Master Kit has already been selected!");
                            }
                        }
                        if(counter == 1) {
                            //if kit has not been selected
                            if(k.selectSwordsman(p)) {
                                //update blocks nearby
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                                p.sendMessage("Swordsman Kit selected");
                            //if kit has already been selected
                            } else {
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1f, 1f);
                                p.sendMessage("Swordsman Kit has already been selected!");
                            }
                        }
                        if(counter == 0) {
                            //if kit has not been selected
                            if(k.selectArmorer(p)) {
                                //update blocks nearby
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                                p.sendMessage("Armorer Kit selected");
                                //if kit has already been selected
                            } else {
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1f, 1f);
                                p.sendMessage("Armorer Kit has already been selected!");
                            }
                        }
                        k.updateBlocks(counter, backBlock, side);
                    }
                }
            }
        }
    }
}
