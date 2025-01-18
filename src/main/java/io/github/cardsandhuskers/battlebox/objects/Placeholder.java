package io.github.cardsandhuskers.battlebox.objects;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.objects.stats.StatCalculator;
import io.github.cardsandhuskers.teams.handlers.TeamHandler;
import io.github.cardsandhuskers.teams.objects.Team;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static io.github.cardsandhuskers.battlebox.BattleBox.*;
import static io.github.cardsandhuskers.battlebox.commands.StartGameCommand.timeVar;

public class Placeholder extends PlaceholderExpansion {
    private final BattleBox plugin;

    public Placeholder(BattleBox plugin) {
        this.plugin = plugin;
    }


    @Override
    public String getIdentifier() {
        return "BattleBox";
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
        if(s.equalsIgnoreCase("timer")) {
            int mins = timeVar / 60;
            String seconds = String.format("%02d", timeVar - (mins * 60));
            return mins + ":" + seconds;
        }
        if(s.equalsIgnoreCase("timerstage")) {
            switch (gameState) {
                case GAME_STARTING: return "Game Starts in";
                case KIT_SELECTION: return "Kit Selection";
                case ROUND_STARTING: return "Round Starts";
                case ROUND_ACTIVE: return "Round Ends";
                case ROUND_OVER: return "Next Round";
                case GAME_OVER: return "Return to Lobby";
            }
        }
        if(s.equalsIgnoreCase("round")) {
            int currentRound;
            int totalRounds;
            if(handler.getNumTeams() %2 == 0) {
                totalRounds = handler.getNumTeams() - 1;
            } else {
                totalRounds = handler.getNumTeams();
            }
            if(round <= totalRounds) {
                currentRound = round;
            } else {
                currentRound = totalRounds;
            }
            return currentRound + "/" + (totalRounds);
        }
        if(s.equalsIgnoreCase("roundsWon")) {
            if(roundsWon.containsKey(handler.getPlayerTeam((Player) p))) {
                return roundsWon.get(handler.getPlayerTeam((Player)p)) + "";
            } else {
                return 0 + "";
            }
        }

        String[] values = s.split("_");
        //playerKills, totalKills, wins
        // lb pos
        //playerKills_1
        try {
            if (values[0].equalsIgnoreCase("playerplayKills")) {
                ArrayList<StatCalculator.SingleGameHolder> killsHolders = plugin.statCalculator.getSingleGameHolders(StatCalculator.PlayerStatsComparator.SortType.KILLS);
                if(Integer.parseInt(values[1]) > killsHolders.size()) return  "";
                StatCalculator.SingleGameHolder holder = killsHolders.get(Integer.parseInt(values[1]) - 1);

                String color = "";
                if (handler.getPlayerTeam(Bukkit.getPlayer(holder.name)) != null)
                    color = handler.getPlayerTeam(Bukkit.getPlayer(holder.name)).color;
                return color + holder.name + ChatColor.RESET + " Event " + holder.event + ": " + holder.kills;


            }
            if (values[0].equalsIgnoreCase("totalKills")) {
                ArrayList<StatCalculator.PlayerStatsHolder> killsHolders = plugin.statCalculator.getStatsHolders(StatCalculator.PlayerStatsComparator.SortType.KILLS);
                if(Integer.parseInt(values[1]) > killsHolders.size()) return  "";
                StatCalculator.PlayerStatsHolder holder = killsHolders.get(Integer.parseInt(values[1]) - 1);
                String color = "";
                if (handler.getPlayerTeam(Bukkit.getPlayer(holder.name)) != null)
                    color = handler.getPlayerTeam(Bukkit.getPlayer(holder.name)).color;
                return color + holder.name + ChatColor.RESET + ": " + holder.getTotalKills();
            }
            if (values[0].equalsIgnoreCase("wins")) {
                ArrayList<StatCalculator.PlayerStatsHolder> killsHolders = plugin.statCalculator.getStatsHolders(StatCalculator.PlayerStatsComparator.SortType.WINS);
                if(Integer.parseInt(values[1]) > killsHolders.size()) return  "";
                StatCalculator.PlayerStatsHolder holder = killsHolders.get(Integer.parseInt(values[1]) - 1);
                String color = "";
                if (handler.getPlayerTeam(Bukkit.getPlayer(holder.name)) != null)
                    color = handler.getPlayerTeam(Bukkit.getPlayer(holder.name)).color;
                return color + holder.name + ChatColor.RESET + ": " + holder.getTotalWins();
            }
            if(values[0].equalsIgnoreCase("yourKills")) {
                ArrayList<StatCalculator.PlayerStatsHolder> killsHolders = plugin.statCalculator.getStatsHolders(StatCalculator.PlayerStatsComparator.SortType.KILLS);

                int i = 1;
                StatCalculator.PlayerStatsHolder playerHolder = null;
                for(StatCalculator.PlayerStatsHolder holder: killsHolders) {
                    if(holder.name.equals(p.getName())) {
                        playerHolder = holder;
                        break;
                    }
                    i++;
                }
                if(playerHolder == null || i <= 10) return "";

                Team team = TeamHandler.getInstance().getPlayerTeam(p.getPlayer());
                String color = "";
                if(team != null) color = team.getColor();

                return i + ". " + color + "You" + ChatColor.RESET + ": " + playerHolder.getTotalKills();

            }
            if(values[0].equalsIgnoreCase("yourWins")) {
                ArrayList<StatCalculator.PlayerStatsHolder> killsHolders = plugin.statCalculator.getStatsHolders(StatCalculator.PlayerStatsComparator.SortType.WINS);

                int i = 1;
                StatCalculator.PlayerStatsHolder playerHolder = null;
                for(StatCalculator.PlayerStatsHolder holder: killsHolders) {
                    if(holder.name.equals(p.getName())) {
                        playerHolder = holder;
                        break;
                    }
                    i++;
                }
                if(playerHolder == null || i <= 10) return "";

                Team team = TeamHandler.getInstance().getPlayerTeam(p.getPlayer());
                String color = "";
                if(team != null) color = team.getColor();

                return i + ". " + color + "You" + ChatColor.RESET + ": " + playerHolder.getTotalWins();

            }



        } catch (Exception e) {
            StackTraceElement[] trace = e.getStackTrace();
            String str = "";
            for(StackTraceElement element:trace) str += element.toString() + "\n";
            plugin.getLogger().severe("Error with Placeholder!\n" + str);
        }


        return null;
    }
}
