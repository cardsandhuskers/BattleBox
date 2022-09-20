package io.github.cardsandhuskers.teams.commands;

import io.github.cardsandhuskers.teams.Teams;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.cardsandhuskers.teams.Teams.teamsLocked;

public class LockTeamsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        if(p.isOp()) {
            teamsLocked = true;
            for(Player player: Bukkit.getOnlinePlayers()) {
                player.closeInventory();
            }
            p.sendMessage("Teams have Been Locked");
        } else {
            p.sendMessage("You do not have permission to execute this command.");
        }

        return false;
    }
}
