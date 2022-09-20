package io.github.cardsandhuskers.teams.objects;

import io.github.cardsandhuskers.teams.Teams;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

import static io.github.cardsandhuskers.teams.Teams.ppAPI;

public class Team {
    private ArrayList<Player> playerList;
    private String name;
    public String color;
    private boolean ready = false;

    /**
     * Constructor, creates the team Object
     * @param teamName
     */
    public Team(String teamName) {
        //this.color = assignColor();
        name = teamName;
        playerList = new ArrayList<Player>();
    }

    /**
     * Adds the specified player to the team
     * @param player
     */
    public void addPlayer(Player player) {
        playerList.add(player);
    }

    /**
     * Removes the specified player from the team
     * @param player
     */
    public void removePlayer(Player player) {
        playerList.remove(player);
    }

    /**
     * gets the list of players on the team
     * @return ArrayList of Players
     */
    public ArrayList<Player> getPlayers() {
        //Creates a deep copy and returns it so that no one can mess with the list
        ArrayList<Player> returnableList = new ArrayList<>();
        for(Player p: playerList) {
            returnableList.add(p);
        }

        return returnableList;
    }

    /**
     * gets player at specified index
     * @param index
     * @return Player
     */
    public Player getPlayer(int index) {

        if(index < playerList.size()) {
            return playerList.get(index);
        } else {
            return null;
        }

    }

    public int getPoints() {
        int points = 0;
        for (Player p:playerList) {
            points += ppAPI.look(p.getUniqueId());
        }
        return points;
    }

    //gets name of team
    public String getTeamName() {
        return name;
    }

    /**
     * returns the wool material representing the team's color
     * @return Material
     */
    public Material getWoolColor() {
        switch(color) {
            case "§2": return Material.GREEN_WOOL;
            case "§3": return Material.CYAN_WOOL;
            case "§5": return Material.PURPLE_WOOL;
            case "§6": return Material.ORANGE_WOOL;
            case "§7": return Material.LIGHT_GRAY_WOOL;
            case "§8": return Material.GRAY_WOOL;
            case "§9": return Material.BLUE_WOOL;
            case "§a": return Material.LIME_WOOL;
            case "§b": return Material.LIGHT_BLUE_WOOL;
            case "§c": return Material.RED_WOOL;
            case "§d": return Material.MAGENTA_WOOL;
            case "§e": return Material.YELLOW_WOOL;
            default: return Material.WHITE_WOOL;
        }
    }

    /**
     * Converts color format from the § to the & format
     * @return String color
     */
    public String getConfigColor() {
        String temp = "&";
        temp += color.substring(1);
        return temp;
        /*
        switch(color) {
            case "§2": return "&2";
            case "§3": return "&3";
            case "§5": return "&5";
            case "§6": return "&6";
            case "§7": return "&7";
            case "§8": return "&8";
            case "§9": return "&9";
            case "§a": return "&a";
            case "§b": return "&b";
            case "§c": return "&c";
            case "§d": return "&d";
            case "§e": return "&e";
            default: return "&f";
        }

         */
    }

    /**
     * Takes in the list of colors and assigns a random one to the team
     * @param colors
     * @return String of color
     */
    public String assignColor(ArrayList<String> colors) {
        if(!(colors.isEmpty())) {
            String tempColor;

            Random r = new Random();
            int number = r.nextInt(colors.size());

            tempColor = colors.get(number);
            color = tempColor;
            return tempColor;
        } else {
            color = "§f";
            return "§f";
        }

    }

    /**
     * Gets the number of players on the team
     * @return int of team size
     */
    public int getSize() {
        if (playerList.isEmpty()) {
            return 0;
        } else {
            int size = 0;
            for (Player p : playerList) {
                size++;
            }
            return size;
        }
    }

    /**
     * Converts the name of the team and its players to a string
     * @return string
     */
    @Override
    public String toString() {
        String s = "";
        for(int i = 0; i < playerList.size(); i++) {
            s += playerList.get(i) + " ";
        }
        return name + ": " + s;
    }

    /**
     * Toggles whether the team is ready
     */
    public void toggleReady() {
        if(ready == false) {
            ready = true;
        } else {
            ready = false;
        }
    }

    /**
     * returns whether the team is ready
     * @return boolean
     */
    public boolean isReady() {
        return ready;
    }
}
