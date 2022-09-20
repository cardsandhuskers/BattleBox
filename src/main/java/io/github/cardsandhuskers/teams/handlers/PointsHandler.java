package io.github.cardsandhuskers.teams.handlers;

import io.github.cardsandhuskers.teams.objects.Team;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

import static io.github.cardsandhuskers.teams.Teams.ppAPI;


public class PointsHandler {

    public PointsHandler() {
    }

    public int getTeamPoints(Team t) {
        int points = 0;
        for(Player p:t.getPlayers()) {
            points += ppAPI.look(p.getUniqueId());
        }
        return points;
    }

    public int getPlayerPoints(Player p) {
        return ppAPI.look(p.getUniqueId());
    }
}
