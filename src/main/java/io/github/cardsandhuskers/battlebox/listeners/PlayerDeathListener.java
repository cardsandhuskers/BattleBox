package io.github.cardsandhuskers.battlebox.listeners;

import io.github.cardsandhuskers.battlebox.objects.StoredAttacker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

import static io.github.cardsandhuskers.battlebox.BattleBox.*;

public class PlayerDeathListener implements Listener {
    HashMap<Player, Location> playerLocationMap;
    Plugin plugin;
    public PlayerDeathListener(Plugin plugin, HashMap playerLocationMap) {
        this.playerLocationMap = playerLocationMap;
        this.plugin = plugin;
    }

    /**
     * Player Death Event
     * @param e
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player attacked = (Player)e.getEntity();
        e.setDeathMessage("");
        playerLocationMap.put(attacked, attacked.getLocation());

        EntityDamageEvent cause = e.getEntity().getLastDamageCause();

        //If player is killed by other player
        if(cause.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if(e.getEntity().getKiller().getType() == EntityType.PLAYER) {
                Player attacker = e.getEntity().getKiller();

                int numPoints = plugin.getConfig().getInt("killPoints");
                handler.getPlayerTeam(attacker).addTempPoints(attacker, (numPoints * multiplier));
                for(Player p: handler.getPlayerTeam(attacker).getOnlinePlayers()) {
                    if(p.equals(attacker)) {
                        p.sendMessage("[+" + numPoints * multiplier + " points] " + handler.getPlayerTeam(attacked).color + attacked.getName() + ChatColor.RESET + " was killed by " + handler.getPlayerTeam(attacker).color + attacker.getName());
                    } else {
                        p.sendMessage(handler.getPlayerTeam(attacked).color + attacked.getName() + ChatColor.RESET + " was killed by " + handler.getPlayerTeam(attacker).color + attacker.getName());
                    }
                }
                for(Player p: handler.getPlayerTeam(attacked).getOnlinePlayers()) {
                    p.sendMessage(handler.getPlayerTeam(attacked).color + attacked.getName() + ChatColor.RESET + " was killed by " + handler.getPlayerTeam(attacker).color + attacker.getName());
                }
                attacker.playSound(attacker.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1f, 2f);
                attacker.sendTitle("Killed " + handler.getPlayerTeam(attacked).color + attacked.getName(), "", 2, 16, 2);
            }
        //If Player dies another way(probably lava)
        } else {
            boolean isAttacked = false;
            //look through stored attackers, these are objects that hold an attacker for each player if they've been attacked
            //This should eventually be replaced by a HashMap
            for(StoredAttacker s:storedAttackers) {
                if(s.getAttacked().equals(attacked)) {
                    isAttacked = true;
                    Player attacker = s.getAttacker();
                    int numPoints = plugin.getConfig().getInt("killPoints");
                    handler.getPlayerTeam(attacker).addTempPoints(attacker, (numPoints * multiplier));
                    //send kill message to attacker and their team
                    for(Player player: handler.getPlayerTeam(attacker).getOnlinePlayers()) {
                        if(player.equals(attacker)) {
                            player.sendMessage("[+" + numPoints * multiplier + " points] " + handler.getPlayerTeam(attacked).color + attacked.getName() + ChatColor.RESET + " was killed by " + handler.getPlayerTeam(attacker).color + attacker.getName());
                        } else {
                            player.sendMessage(handler.getPlayerTeam(attacked).color + attacked.getName() + ChatColor.RESET + " was killed by " + handler.getPlayerTeam(attacker).color + attacker.getName());
                        }
                    }
                    //send messages to attacked and their team (message is identical to attacker team sans attacker)
                    for(Player player: handler.getPlayerTeam(attacked).getOnlinePlayers()) {
                        player.sendMessage(handler.getPlayerTeam(attacked).color + attacked.getName() + ChatColor.RESET + " was killed by " + handler.getPlayerTeam(attacker).color + attacker.getName());
                    }
                    attacker.playSound(attacker.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1f, 2f);
                    attacker.sendTitle("Killed " + handler.getPlayerTeam(attacked).color + attacked.getName(), "", 2, 16, 2);
                }
            }
            if(!isAttacked) {
                for(Player player: handler.getPlayerTeam(attacked).getOnlinePlayers()) {
                    player.sendMessage(handler.getPlayerTeam(attacked).color + attacked.getName() + ChatColor.RESET + " died.");
                }
            }
        }
    }
}
