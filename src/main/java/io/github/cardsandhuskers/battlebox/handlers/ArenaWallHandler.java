package io.github.cardsandhuskers.battlebox.handlers;

import io.github.cardsandhuskers.battlebox.BattleBox;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

import static io.github.cardsandhuskers.battlebox.BattleBox.completedArenasList;

public class ArenaWallHandler {
    Plugin plugin;
    ArrayList<Location> wallLocations;
    public ArenaWallHandler(Plugin plugin) {
        this.plugin = plugin;
        wallLocations = new ArrayList<>();


        //make sure plugin instance is not null (error handling)
        if(plugin != null) {
            //get arenas, used to build the wool
            int counter = 1;
            while (plugin.getConfig().getLocation("ArenaWalls.Arena" + counter + "." + 1) != null && plugin.getConfig().getLocation("ArenaWalls.Arena" + counter + "." + 2) != null) {
                Location Arena1 = plugin.getConfig().getLocation("ArenaWalls.Arena" + counter + "." + 1);
                Location Arena2 = plugin.getConfig().getLocation("ArenaWalls.Arena" + counter + "." + 2);
                wallLocations.add(Arena1);
                wallLocations.add(Arena2);
                counter++;
            }
        }
    }

    public void buildWalls() {
        for(Location l:wallLocations) {
            //X values
            for(int x = -2; x <=2; x++) {
                //Y values
                for(int y = 0; y <=2; y++) {
                    Location temp = new Location(l.getWorld(), l.getX() + x, l.getY() + y, l.getZ());
                    Block b = temp.getBlock();
                    b.setType(Material.RED_STAINED_GLASS);
                }
            }
        }
    }

    public void deleteWalls() {
        for(Location l:wallLocations) {
            //X values
            for(int x = -2; x <=2; x++) {
                //Y values
                for(int y = 0; y <=2; y++) {
                    Location temp = new Location(l.getWorld(), l.getX() + x, l.getY() + y, l.getZ());
                    Block b = temp.getBlock();
                    b.setType(Material.AIR);
                }
            }
        }
    }
}
