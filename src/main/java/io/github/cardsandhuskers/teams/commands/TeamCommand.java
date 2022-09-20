package io.github.cardsandhuskers.teams.commands;

import io.github.cardsandhuskers.teams.handlers.TeamHandler;
import io.github.cardsandhuskers.teams.objects.Menu;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.cardsandhuskers.teams.Teams.menuList;
import static io.github.cardsandhuskers.teams.Teams.teamsLocked;

public class TeamCommand implements CommandExecutor {
    TeamHandler handler;

    public TeamCommand(TeamHandler h, PlayerPointsAPI ppAPI) {
        handler = h;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (teamsLocked == false) {
            Menu m = new Menu(p);
            m.generateMenu(p);
            menuList.add(m);
        } else {
            p.sendMessage("Teams are locked. You are no longer allowed to make changes.");
        }





        return false;
    }
}
