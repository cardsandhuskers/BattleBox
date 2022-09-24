package io.github.cardsandhuskers.battlebox.handlers;

import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerTeleportHandler {
    Plugin plugin;
    private Team[][] matchups;
    public PlayerTeleportHandler(Plugin plugin, Team[][] matchups) {
        this.plugin = plugin;
        this.matchups = matchups;
    }

    public void teleportToKitRooms(Team[][] matchups) {
        int counter = 1;
        this.matchups = matchups;

        //while the arena has 2 spawns (second one is checked for error handling)
        while(plugin.getConfig().getLocation("ArenaKitSpawns.Arena" + counter + "." + 1) != null && plugin.getConfig().getLocation("ArenaSpawns.Arena" + counter + "." + 2) != null) {
            if(counter-1 < matchups.length) {
                //Team 1
                Location location1 = plugin.getConfig().getLocation("ArenaKitSpawns.Arena" + counter + "." + 1);
                Team team1 = matchups[counter-1][0];

                //Check if team is dummy (handles bye week if odd number of teams)
                if(team1.getTeamName().equals("DUMMYTEAM")) {

                }

                //Teleport the players
                for(Player p : team1.getOnlinePlayers()) {
                    p.teleport(location1);
                }


                //Team 2
                Location location2 = plugin.getConfig().getLocation("ArenaKitSpawns.Arena" + counter + "." + 2);
                Team team2 = matchups[counter-1][1];

                //Check if team is dummy (handles bye week if odd number of teams)
                if(team2.getTeamName().equals("DUMMYTEAM")) {

                }

                //Teleport the players
                for(Player p : team2.getOnlinePlayers()) {
                    p.teleport(location2);
                }
            }
            counter++;
        }
    }

    public void teleportToArenas() {
        int counter = 1;

        //while the arena has 2 spawns (second one is checked for error handling)
        while(plugin.getConfig().getLocation("ArenaSpawns.Arena" + counter + "." + 1) != null && plugin.getConfig().getLocation("ArenaSpawns.Arena" + counter + "." + 2) != null) {
            if(counter-1 < matchups.length) {
                //Team 1
                Location location1 = plugin.getConfig().getLocation("ArenaSpawns.Arena" + counter + "." + 1);
                Team team1 = matchups[counter-1][0];

                //Check if team is dummy (handles bye week if odd number of teams)
                if(team1.getTeamName().equals("DUMMYTEAM")) {

                }

                //Teleport the players
                for(Player p : team1.getOnlinePlayers()) {
                    p.teleport(location1);
                }


                //Team 2
                Location location2 = plugin.getConfig().getLocation("ArenaSpawns.Arena" + counter + "." + 2);
                Team team2 = matchups[counter-1][1];

                //Check if team is dummy (handles bye week if odd number of teams)
                if(team2.getTeamName().equals("DUMMYTEAM")) {

                }

                //Teleport the players
                for(Player p : team2.getOnlinePlayers()) {
                    p.teleport(location2);
                }
            }
            counter++;
        }
    }
}
