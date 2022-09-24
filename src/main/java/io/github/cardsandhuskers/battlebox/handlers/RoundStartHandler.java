package io.github.cardsandhuskers.battlebox.handlers;

import io.github.cardsandhuskers.battlebox.BattleBox;
import io.github.cardsandhuskers.battlebox.commands.StartGameCommand;
import io.github.cardsandhuskers.battlebox.objects.Bracket;
import io.github.cardsandhuskers.battlebox.objects.Countdown;
import io.github.cardsandhuskers.battlebox.objects.TeamKits;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import static io.github.cardsandhuskers.battlebox.BattleBox.*;

public class RoundStartHandler {
    private Team[][] matchups;
    private BattleBox plugin;
    private ArrayList<Block> blockList;
    private ArrayList<Block> centerBlockList;
    private Countdown inGameTimer;
    private PlayerTeleportHandler teleporter;
    private ArrayList<TeamKits> teamKitsList;
    Bracket bracket;
    RoundEndHandler roundEndHandler;

    public RoundStartHandler(BattleBox plugin) {
        this.plugin = plugin;
        blockList = new ArrayList<>();
        centerBlockList = new ArrayList<>();
        bracket = new Bracket();
        roundEndHandler = new RoundEndHandler(plugin, this);
        //matchups = bracket.getMatchups(handler.getTeams(), round);
        teleporter = new PlayerTeleportHandler(plugin, matchups);
        teamKitsList = new ArrayList<>();

    }

    /**
     * Checks if the round was the last one before initializing
     */
    public void startRound() {



        int totalRounds;
        if(handler.getNumTeams() %2 == 1) {
            totalRounds = handler.getNumTeams();
        } else {
            totalRounds = handler.getNumTeams() - 1;
        }
        if(round > totalRounds) {
            GameEndHandler gameEndHandler = new GameEndHandler(plugin);
            gameEndHandler.endGame();
        } else {
            matchups = bracket.getMatchups(handler.getTeams(), round);
            init();
        }
    }

    /**
     * Initializes a new round
     */
    public void init() {
        centerBlockList.clear();
        blockList.clear();


        //make sure plugin instance is not null (error handling)
        if(plugin != null) {
            completedArenasList.clear();
            //get arenas, used to build the wool
            int counter = 1;
            while(plugin.getConfig().getLocation("Arenas.Arena" + counter) != null) {
                Location center = plugin.getConfig().getLocation("Arenas.Arena" + counter);
                buildWool(center);
                counter++;
            }

            teamKitsList.clear();
            //create teamKits object for each team in the game
            for(Team t: handler.getTeams()) {
                teamKitsList.add(new TeamKits(t));
            }

            initKitTimer();


        } else {
            System.out.println("PLUGIN IS NULL");
        }

    }

    /**
     * Resets the center wool of all arenas
     * @param center
     */
    public void buildWool(Location center) {
        centerBlockList.add(center.getBlock());
        Location tempLocation = new Location(center.getWorld(), center.getX(), center.getY(), center.getZ());
        Block tempCenter;
        //loop through the 3x3 for the passed arena center block
        for(int x = -1; x <= 1; x++) {
            for(int z = -1; z <= 1; z++) {
                //set X and Z of location
                tempLocation.setX(center.getX() + x);
                tempLocation.setZ(center.getZ() + z);

                //get block at location, set it to white wool to initialize it, and add it to the blockList
                tempCenter = tempLocation.getBlock();
                tempCenter.setType(Material.WHITE_WOOL);
                blockList.add(tempCenter);

                //reset tempLocation variable
                tempLocation.setZ(center.getZ());
                tempLocation.setX(center.getX());
            }
        }
    }

    /**
     * Getter for blockList
     * @return blockList
     */
    public ArrayList<Block> getBlockList() {
        return blockList;
    }

    /**
     * Getter for CenterBlockList
     * @return centerBlockList
     */
    public ArrayList<Block> getCenterBlockList() {
        return centerBlockList;
    }

    public Team[][] getMatchups() {
        return matchups;
    }

    /**
     * 15-second pregame timer, for selecting kits
     */
    public void initKitTimer() {
        Countdown timer = new Countdown((JavaPlugin)plugin,
                15,
                //Timer Start
                () -> {
                    for(Team t: handler.getTeams()) {
                        for(int i = 0; i < matchups.length; i++) {
                            if(t.equals(matchups[i][0])) {
                                for(Player p:t.getOnlinePlayers()) {
                                    p.sendTitle("Round: " + round,matchups[i][0].color + matchups[i][0].getTeamName() + ChatColor.RESET + " vs. " + matchups[i][1].color + matchups[i][1].getTeamName() ,3,40,3);
                                }
                            } else if(t.equals(matchups[i][1])) {
                                for(Player p:t.getOnlinePlayers()) {
                                    p.sendTitle("Round: " + round, matchups[i][1].color + matchups[i][1].getTeamName() + ChatColor.RESET + " vs. " + matchups[i][0].color + matchups[i][0].getTeamName(), 3, 40, 3);
                                }
                            }
                        }
                    }
                    StartGameCommand.timerStatus = "Kit Selection";
                    teleporter.teleportToKitRooms(matchups);

                },

                //Timer End
                () -> {
                    StartGameCommand.timeVar = 0;
                    Bukkit.broadcastMessage(ChatColor.RED + "Round " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + round + ChatColor.RESET + ChatColor.RED + " is Starting!");

                    for(TeamKits k: teamKitsList) {
                        k.populateRemainingKits();
                    }


                    initGameCountdownTimer();
                },

                //Each Second
                (t) -> {
                    StartGameCommand.timeVar = t.getSecondsLeft();
                    if(t.getSecondsLeft() == 15) {
                        Bukkit.broadcastMessage(ChatColor.RED + "You have " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + t.getSecondsLeft() + ChatColor.RESET + ChatColor.RED + " seconds to select your kit!");
                    }

                    if(t.getSecondsLeft() <= 5) {
                        Bukkit.broadcastMessage(ChatColor.RED + "You have " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + t.getSecondsLeft() + ChatColor.RESET + ChatColor.RED + " seconds to select your kit!");
                    }
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        timer.scheduleTimer();
    }

    /**
     * 5-second countdown to game start
     */
    public void initGameCountdownTimer() {
        Countdown timer = new Countdown((JavaPlugin)plugin,
                5,
                //Timer Start
                () -> {
                    StartGameCommand.timerStatus = "Battle Begins in";
                    Bukkit.broadcastMessage(ChatColor.RED + "Box Opening Soon!");
                    teleporter.teleportToArenas();
                },

                //Timer End
                () -> {
                    StartGameCommand.timeVar = 0;
                    Bukkit.broadcastMessage("" + ChatColor.BOLD + ChatColor.RED + "GO!!!");
                    initInGameTimer();
                    for(Player p:Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                        p.sendTitle(handler.getPlayerTeam(p).color + "GO!", "", 2, 12, 2);
                    }
                },

                //Each Second
                (t) -> {
                    StartGameCommand.timeVar = t.getSecondsLeft();
                    Bukkit.broadcastMessage(ChatColor.RED + "Box opens in " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + t.getSecondsLeft() + ChatColor.RESET + ChatColor.RED + " Seconds!");
                    for(Player p:Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                    }

                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        timer.scheduleTimer();
    }

    /**
     * 60-second timer for during the game
     */
    public void initInGameTimer() {
        inGameTimer = new Countdown((JavaPlugin)plugin,
                60,
                //Timer Start
                () -> {
                    StartGameCommand.timerStatus = "Round ends in";
                },

                //Timer End
                () -> {
                    StartGameCommand.timeVar = 0;
                    Bukkit.broadcastMessage(ChatColor.RED + "Round " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + round + ChatColor.RESET + ChatColor.RED + " is Over!");

                    roundEndHandler.endRound();
                },

                //Each Second
                (t) -> {
                    StartGameCommand.timeVar = t.getSecondsLeft();
                    if(t.getSecondsLeft() <= 10) {
                        Bukkit.broadcastMessage(ChatColor.RED + "Round ends in " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + t.getSecondsLeft() + ChatColor.RESET + ChatColor.RED + " Seconds!");
                    }
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        inGameTimer.scheduleTimer();
    }

    /**
     * Cancels the main game timer
     */
    public void cancelInGameTimer() {
        inGameTimer.cancelTimer();
    }

    public void endRound() {
        roundEndHandler.endRound();
    }

    public ArrayList<TeamKits> getTeamKitsList() {
        return teamKitsList;
    }

}