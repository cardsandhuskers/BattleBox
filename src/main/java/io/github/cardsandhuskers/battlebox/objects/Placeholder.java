package io.github.cardsandhuskers.battlebox.objects;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.commands.StartGameCommand;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import static io.github.cardsandhuskers.battlebox.BattleBox.handler;
import static io.github.cardsandhuskers.battlebox.BattleBox.round;

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
            return String.valueOf(StartGameCommand.timeVar);
        }
        if(s.equalsIgnoreCase("timerstage")) {
            return StartGameCommand.timerStatus;
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
        return null;
    }
}
