package io.github.cardsandhuskers.battlebox.listeners;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.objects.StoredAttacker;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static io.github.cardsandhuskers.battlebox.BattleBox.*;
import static io.github.cardsandhuskers.battlebox.commands.StartGameCommand.timerStatus;


public class PlayerAttackListener implements Listener {
    private BattleBox plugin = (BattleBox) Bukkit.getPluginManager().getPlugin("BattleBox");
    public PlayerAttackListener() {

    }

    /**
     * Determines the source and target of attack and gets the players
     * Add any conditional handling to the damage method
     * @param e
     */
    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent e) {
        Player attacker;
        Player attacked;

        if(e.getEntity().getType() == EntityType.PLAYER) {
            attacked = (Player) e.getEntity();
            //if attack is from arrow
            if(e.getDamager().getType() == EntityType.ARROW) {
                Arrow arrow = (Arrow) e.getDamager();
                attacker = (Player) arrow.getShooter();
                damage(attacker, attacked, e);

            //if attack is from player
            } else if(e.getDamager().getType() == EntityType.PLAYER) {
                attacker = (Player) e.getDamager();
                damage(attacker, attacked, e);
            } else if(e.getDamager().getType() == EntityType.SPLASH_POTION) {
                ThrownPotion potion = (ThrownPotion) e.getDamager();
                attacker = (Player) potion.getShooter();
                damage(attacker, attacked, e);
            }else {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }

    /**
     * Handles the conditions for pvp damage and determines whether to cancel it
     * @param attacker
     * @param attacked
     * @param e
     */
    public void damage(Player attacker, Player attacked, EntityDamageByEntityEvent e) {
        if(!timerStatus.equals("Round ends in")) e.setCancelled(true);
        //handle error if someone's team is null
        if (!(handler.getPlayerTeam(attacker) == null || handler.getPlayerTeam(attacked) == null)) {
            if (handler.getPlayerTeam(attacker).equals(handler.getPlayerTeam(attacked))) {
                e.setCancelled(true);
            } else {
                //if attack is from an arena that is done, cancel the event
                for(Team t:winningTeamsList) {
                    if(handler.getPlayerTeam(attacker).equals(t) || handler.getPlayerTeam(attacked).equals(t)) {
                        e.setCancelled(true);
                    }
                }

                boolean found = false;
                for(StoredAttacker s: storedAttackers) {
                    if(s.getAttacked().equals(attacked)) {
                        found = true;
                        s.setAttacker(attacker);
                    }
                }
                if(!found) {
                    StoredAttacker storedAttacker = new StoredAttacker(attacker, attacked);
                    storedAttackers.add(storedAttacker);
                }
            }
        }
    }
}
