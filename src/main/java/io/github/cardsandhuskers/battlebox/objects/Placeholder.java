package io.github.cardsandhuskers.battlebox.objects;

import io.github.cardsandhuskers.battlebox.BattleBox;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
        return null;
    }
}
