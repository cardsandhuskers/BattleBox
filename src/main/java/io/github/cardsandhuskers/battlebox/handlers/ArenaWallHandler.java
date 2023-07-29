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
    ArrayList<Location> wallALocations, wallBLocations;
    public ArenaWallHandler(Plugin plugin) {
        this.plugin = plugin;
        wallALocations = new ArrayList<>();
        wallBLocations = new ArrayList<>();


        //make sure plugin instance is not null (error handling)
        if(plugin != null) {
            //get arenas, used to build the wool
            int counter = 1;
            while (plugin.getConfig().getLocation("ArenaWalls.Arena" + counter + "." + 1) != null && plugin.getConfig().getLocation("ArenaWalls.Arena" + counter + "." + 2) != null) {
                Location Arena1 = plugin.getConfig().getLocation("ArenaWalls.Arena" + counter + "." + 1);
                Location Arena2 = plugin.getConfig().getLocation("ArenaWalls.Arena" + counter + "." + 2);

                if(counter % 2 == 1) {
                    wallALocations.add(Arena1);
                    wallALocations.add(Arena2);
                } else {
                    wallBLocations.add(Arena1);
                    wallBLocations.add(Arena2);
                }
                counter++;
            }
        }
    }

    public void buildWalls(boolean delete) {
        //TEMPLE MAP
        for(Location l:wallALocations) {
            //X values
            for(int x = -3; x <=3; x++) {
                //Y values
                for(int y = 0; y <=3; y++) {
                    Location temp = new Location(l.getWorld(), l.getX() + x, l.getY() + y, l.getZ());
                    Block b = temp.getBlock();
                    if (delete) b.setType(Material.AIR);
                    else b.setType(Material.LIGHT_GRAY_STAINED_GLASS);
                }
            }
        }
        //DESERT MAP
        for(Location l:wallBLocations) {
            //X values
            for(int x = -2; x <=2; x++) {
                //Y values
                for(int y = 0; y <=5; y++) {
                    Location temp = new Location(l.getWorld(), l.getX() + x, l.getY() + y, l.getZ());
                    Block b = temp.getBlock();
                    if (delete) b.setType(Material.AIR);
                    else b.setType(Material.DARK_OAK_FENCE);
                }
            }
        }
    }
}
