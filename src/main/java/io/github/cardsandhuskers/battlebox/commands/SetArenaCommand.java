package io.github.cardsandhuskers.battlebox.commands;

import io.github.cardsandhuskers.battlebox.BattleBox;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetArenaCommand implements CommandExecutor {
    private BattleBox plugin;
    public SetArenaCommand(BattleBox plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //This command is for setting the center of the arena, where the wool is
        if(sender instanceof Player p) {
            if(p.isOp()) {
                try {
                    int arenaIndex = Integer.parseInt(args[0]);
                    Location location = p.getLocation();
                    location.setY(location.getY() - 1);
                    plugin.getConfig().set("Arenas." + "Arena" + arenaIndex, location);
                    plugin.saveConfig();
                    p.sendMessage("Location set at:\nWorld: " + location.getWorld() + "\nX: " + location.getX() + " Y: " + location.getY() + " Z: " + location.getZ());

                } catch (Exception e) {
                    p.sendMessage(ChatColor.RED + "ERROR: Command must have 1 integer to represent arena number");
                }
            } else {
                p.sendMessage(ChatColor.RED + "You do not have Permission to do this");
            }


        } else {
            System.out.println("ERROR: cannot run from console.");
        }



        return true;
    }
}
