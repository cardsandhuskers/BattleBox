package io.github.cardsandhuskers.teams.objects;

import io.github.cardsandhuskers.teams.Teams;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static io.github.cardsandhuskers.teams.Teams.handler;

public class Placeholder extends PlaceholderExpansion {
    private final Teams plugin;

    public Placeholder(Teams plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getIdentifier() {
        return "Teams";
    }
    @Override
    public String getAuthor() {
        return "cardsandhuskers";
    }
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    @Override
    public boolean persist() {
        return true;
    }


    @Override
    public String onRequest(OfflinePlayer p, String s) {

        if(s.equalsIgnoreCase("teamPoints")) {
            if(handler.getPlayerTeam((Player) p)!= null) {
                return "" + handler.getPlayerTeam((Player) p).getPoints();
            } else {
                return "";
            }
        }
        if(s.equalsIgnoreCase("team")) {
            if(handler.getPlayerTeam((Player) p) != null) {
                return handler.getPlayerTeam((Player)p).getTeamName();
            } else {
                return "No Team";
            }

        }
        if(s.equalsIgnoreCase("color")) {
            if(handler.getPlayerTeam((Player) p) != null) {
                return handler.getPlayerTeam((Player)p).getConfigColor();
            } else {
                return "&f";
            }
        }
        return null;
    }
}
