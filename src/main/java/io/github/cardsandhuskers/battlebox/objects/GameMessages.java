package io.github.cardsandhuskers.battlebox.objects;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.teams.objects.Team;
import io.github.cardsandhuskers.teams.objects.TempPointsHolder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static io.github.cardsandhuskers.battlebox.BattleBox.multiplier;
import static io.github.cardsandhuskers.battlebox.BattleBox.winningTeamsList;
import static io.github.cardsandhuskers.teams.Teams.handler;

public class GameMessages {

    /**
     *
     * @return String to announce for game rules
     */
    public static String gameDescription() {
        String GAME_DESCRIPTION =
                ChatColor.STRIKETHROUGH + "----------------------------------------\n" + ChatColor.RESET +
                StringUtils.center(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Battle Box", 30) +
                ChatColor.BLUE + "" + ChatColor.BOLD + "\nHow To Play:" + ChatColor.RESET +
                "\nYou will be placed in an arena against the other teams in a round robin format." +
                "\nYou must replace the wool in the center with your color wool to win the round." +
                "\nKill your opponents, or don't, it's up to you! All that matters is that you complete the wool." +
                ChatColor.STRIKETHROUGH + "\n----------------------------------------";
        return GAME_DESCRIPTION;
    }

    public static String kitsDescription() {
        String KITS_DESCRIPTION =
                ChatColor.STRIKETHROUGH + "----------------------------------------" +
                ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "\nThis Game Has the Following Kits:" + ChatColor.RESET +
                "\nThe " + ChatColor.LIGHT_PURPLE + "marksman" + ChatColor.RESET + " kit adds a multishot 3 crossbow to your arsenal! A great way to provide covering fire." +
                "\nThe " + ChatColor.DARK_AQUA + "potion master" + ChatColor.RESET + " kit gives you 2 harming, 2 healing, and 1 lingering harming potion! Use them wisely." +
                "\nThe " + ChatColor.BLUE + "swordsman" + ChatColor.RESET + " kit gives you an iron sword! You can do big damage with your sword." +
                "\nThe " + ChatColor.RED + "armorer" + ChatColor.RESET + " kit adds protection 3 leather chestplate and leggings, with knockback resistance! Use it to tank damage." +
                ChatColor.STRIKETHROUGH + "----------------------------------------";
        return KITS_DESCRIPTION;
    }

    /**
     *
     * @param plugin
     * @return String to announce for points
     */
    public static String pointsDescription(BattleBox plugin) {
        String POINTS_DESCRIPTION =
                ChatColor.STRIKETHROUGH + "----------------------------------------" +
                ChatColor.GOLD + "" + ChatColor.BOLD + "\nHow is the game Scored:" +
                "\nFor winning: " + ChatColor.GOLD + (int)(plugin.getConfig().getInt("roundWinPoints") * multiplier) + ChatColor.RESET + " points per team (" +
                        ChatColor.GOLD + (int)(plugin.getConfig().getInt("roundWinPoints") * multiplier/ BattleBox.handler.getTeams().get(0).getSize()) + ChatColor.RESET + " points per player" +
                "\nFor a Kill, the killer gets: " + ChatColor.GOLD + (int)(plugin.getConfig().getInt("killPoints") * multiplier) + ChatColor.RESET + " points" +
                ChatColor.STRIKETHROUGH + "----------------------------------------";
        return POINTS_DESCRIPTION;
    }


    /**
     * Announces the top 5 earning players in the game
     */
    public static void announceTopPlayers() {
        ArrayList<TempPointsHolder> tempPointsList = new ArrayList<>();
        for(Team team: handler.getTeams()) {
            for(Player p:team.getOnlinePlayers()) {
                tempPointsList.add(team.getPlayerTempPoints(p));
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
        }

        Collections.sort(tempPointsList, Comparator.comparing(TempPointsHolder::getPoints));
        Collections.reverse(tempPointsList);

        int max;
        if(tempPointsList.size() >= 5) {
            max = 4;
        } else {
            max = tempPointsList.size() - 1;
        }

        Bukkit.broadcastMessage("\n" + ChatColor.RED + "" + ChatColor.BOLD + "Top 5 Players:");
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "------------------------------");
        int number = 1;
        for(int i = 0; i <= max; i++) {
            TempPointsHolder h = tempPointsList.get(i);
            Bukkit.broadcastMessage(number + ". " + handler.getPlayerTeam(h.getPlayer()).color + h.getPlayer().getName() + ChatColor.RESET + "    Points: " +  h.getPoints());
            number++;
        }
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "------------------------------");
    }

    /**
     * Announces the leaderboard for players on your team based on points earned in the game
     */
    public static void announceTeamPlayers() {
        for (Team team : handler.getTeams()) {
            ArrayList<TempPointsHolder> tempPointsList = new ArrayList<>();
            for (Player p : team.getOnlinePlayers()) {
                if (team.getPlayerTempPoints(p) != null) {
                    tempPointsList.add(team.getPlayerTempPoints(p));
                }
            }
            Collections.sort(tempPointsList, Comparator.comparing(TempPointsHolder::getPoints));
            Collections.reverse(tempPointsList);

            for (Player p : team.getOnlinePlayers()) {
                p.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Your Team Standings:");
                p.sendMessage(ChatColor.DARK_BLUE + "------------------------------");
                int number = 1;
                for (TempPointsHolder h : tempPointsList) {
                    p.sendMessage(number + ". " + handler.getPlayerTeam(p).color + h.getPlayer().getName() + ChatColor.RESET + "    Points: " + h.getPoints());
                    number++;
                }
                p.sendMessage(ChatColor.DARK_BLUE + "------------------------------\n");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
        }
    }

    /**
     * Announces the leaderboard of teams based on points earned in the game
     */
    public static void announceTeamLeaderboard() {
        ArrayList<Team> teamList = handler.getTeams();
        Collections.sort(teamList, Comparator.comparing(Team::getTempPoints));
        Collections.reverse(teamList);

        Bukkit.broadcastMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Team Leaderboard:");
        Bukkit.broadcastMessage(ChatColor.GREEN + "------------------------------");
        int counter = 1;
        for(Team team:teamList) {
            Bukkit.broadcastMessage(counter + ". " + team.color + ChatColor.BOLD +  team.getTeamName() + ChatColor.RESET + " Points: " + team.getTempPoints());
            counter++;
        }
        Bukkit.broadcastMessage(ChatColor.GREEN + "------------------------------");
        for(Player p: Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
    }

    public static String announceRoundResult(Team[][] matchups) {
        String msg = "\nRound Summary: \n";
        for(int i = 0; i <matchups.length; i++) {
            boolean gameFound = false;
            for(Team t: winningTeamsList) {
                if(matchups[i][0].equals(t)) {
                    msg += StringUtils.center(matchups[i][0].color + ChatColor.BOLD + matchups[i][0].getTeamName() + ChatColor.RESET + "  vs.  " + ChatColor.DARK_GRAY + matchups[i][1].getTeamName(), 45) + "\n";
                    gameFound = true;
                } else if(matchups[i][1].equals(t)){
                    msg += StringUtils.center(ChatColor.DARK_GRAY + matchups[i][0].getTeamName() + ChatColor.RESET + "  vs.  " + matchups[i][1].color + ChatColor.BOLD + matchups[i][1].getTeamName(), 45) + "\n";
                    gameFound = true;
                }
            }
            if(!gameFound) {
                msg += StringUtils.center(ChatColor.DARK_GRAY + matchups[i][0].getTeamName() + ChatColor.RESET + "  vs.  " + ChatColor.DARK_GRAY + matchups[i][1].getTeamName(), 45) + "\n";
            }
        }
        return msg;
    }

}
