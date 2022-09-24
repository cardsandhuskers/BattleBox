package io.github.cardsandhuskers.battlebox.handlers;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PlayerDeathHandler {
    Player player;
    public PlayerDeathHandler(Player player) {
        this.player = player;
        player.setGameMode(GameMode.SPECTATOR);
    }
}
