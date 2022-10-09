package io.github.cardsandhuskers.battlebox.objects;

import org.bukkit.entity.Player;

public class StoredAttacker {
    private Player attacked;
    private Player attacker;

    /**
     * Constructor to build a new storedAttacker
     * @param attacker
     * @param attacked
     */
    public StoredAttacker(Player attacker, Player attacked) {
        this.attacker = attacker;
        this.attacked = attacked;
    }
    public void setAttacker(Player p) {
        attacker = p;
    }


    public Player getAttacked() {
        return attacked;
    }
    public Player getAttacker() {
        return attacker;
    }
}
