package io.github.cardsandhuskers.battlebox.listeners;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.handlers.RoundStartHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class BlockBreakListener implements Listener {
    ArrayList<Block> centerBlockList;
    RoundStartHandler roundStartHandler;
    public BlockBreakListener(RoundStartHandler roundStartHandler) {
        centerBlockList = roundStartHandler.getCenterBlockList();
        this.roundStartHandler = roundStartHandler;
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
        if(isValid(e.getBlock().getType())) {
            //check if arena has been completed, if it has, cancel the event
            boolean isValidArena = true;
            Block block = getCenter(e.getBlock());
            for (Block b: BattleBox.completedArenasList) {
                if(b.equals(block)) {
                    isValidArena = false;
                }
            }
            if(isValidArena && roundStartHandler.getInGameTimer() > 0) {
                Block block2 = e.getBlock();
                block2.setType(Material.AIR);
            }
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
                        return tempBlock;
                    }
                }

            }
        }
        return null;
    }

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
            case PINK_WOOL:
            case YELLOW_WOOL:
            case WHITE_WOOL: return true;
            default: return false;
        }
    }
}
