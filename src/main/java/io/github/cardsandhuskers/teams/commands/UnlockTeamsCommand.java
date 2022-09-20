package io.github.cardsandhuskers.teams.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.cardsandhuskers.teams.Teams.teamsLocked;

public class UnlockTeamsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        if(p.isOp()) {
            teamsLocked = false;
            p.sendMessage("Teams have Been Unlocked");
        } else {
            p.sendMessage("You do not have permission to execute this command.");
        }

        return false;
    }
}
