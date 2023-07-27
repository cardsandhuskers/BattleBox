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


    private Countdown kitTimer;
    private Countdown pregameTimer;
    private Countdown inGameTimer;


    private PlayerTeleportHandler teleporter;
    private ArrayList<TeamKits> teamKitsList;
    Bracket bracket;
    RoundEndHandler roundEndHandler;
    ArenaWallHandler wallHandler;



    public RoundStartHandler(BattleBox plugin, ArenaWallHandler wallHandler) {

        this.plugin = plugin;
        this.wallHandler = wallHandler;
        blockList = new ArrayList<>();
        centerBlockList = new ArrayList<>();
        bracket = new Bracket();
        roundEndHandler = new RoundEndHandler(plugin, this);
        //matchups = bracket.getMatchups(handler.getTeams(), round);
        teleporter = new PlayerTeleportHandler(plugin, matchups);
        teamKitsList = new ArrayList<>();
    }


    public void cancelTimers() {
        if(kitTimer != null) kitTimer.cancelTimer();
        if(pregameTimer != null) pregameTimer.cancelTimer();
        if(inGameTimer != null) inGameTimer.cancelTimer();
        roundEndHandler.cancelTimers();
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

            //swap initial group so team 1 can play in both arenas
            int numTeams = (totalRounds + 1);
            int swapLoc = 0;

            if(round % 4 == 0) {
                swapLoc = numTeams / 2 - 1;
            } else if(round % 2 == 0) {
                swapLoc = numTeams / 4 - 1;
            }
            if(swapLoc > 0) {
                Team swapTeamA = matchups[swapLoc][0];
                Team swapTeamB = matchups[swapLoc][1];

                matchups[swapLoc][0] = matchups[0][0];
                matchups[swapLoc][1] = matchups[0][1];

                matchups[0][0] = swapTeamA;
                matchups[0][1] = swapTeamB;
            }


            init();
        }
    }

    /**
     * Initializes a new round
     */
    public void init() {
        centerBlockList.clear();
        blockList.clear();
        storedAttackers.clear();


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
            wallHandler.buildWalls();

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
        int totalTime = plugin.getConfig().getInt("KitTime");
        kitTimer = new Countdown((JavaPlugin)plugin,
                totalTime,
                //Timer Start
                () -> {
                    for(Team t: handler.getTeams()) {
                        for(int i = 0; i < matchups.length; i++) {
                            if(t.equals(matchups[i][0])) {
                                for(Player p:t.getOnlinePlayers()) {
                                    p.sendTitle("Round: " + round,matchups[i][0].color + matchups[i][0].getTeamName() + ChatColor.RESET + " vs. " + matchups[i][1].color + matchups[i][1].getTeamName() ,3,40,3);
                                    p.sendMessage("Round " + round + ": " + matchups[i][0].color + matchups[i][0].getTeamName() + ChatColor.RESET + " vs. " + matchups[i][1].color + matchups[i][1].getTeamName());
                                }
                            } else if(t.equals(matchups[i][1])) {
                                for(Player p:t.getOnlinePlayers()) {
                                    p.sendTitle("Round: " + round, matchups[i][1].color + matchups[i][1].getTeamName() + ChatColor.RESET + " vs. " + matchups[i][0].color + matchups[i][0].getTeamName(), 3, 40, 3);
                                    p.sendMessage("Round "+ round + ": " + matchups[i][1].color + matchups[i][1].getTeamName() + ChatColor.RESET + " vs. " + matchups[i][0].color + matchups[i][0].getTeamName());
                                }
                            }
                        }
                    }
                    gameState = GameState.KIT_SELECTION;
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
                    if(t.getSecondsLeft() == totalTime) {
                        Bukkit.broadcastMessage(ChatColor.RED + "You have " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + t.getSecondsLeft() + ChatColor.RESET + ChatColor.RED + " seconds to select your kit!");
                    }

                    if(t.getSecondsLeft() == 5) {
                        Bukkit.broadcastMessage(ChatColor.RED + "You have " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + t.getSecondsLeft() + ChatColor.RESET + ChatColor.RED + " seconds to select your kit!");
                        for(Player p:Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }
                    }
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        kitTimer.scheduleTimer();
    }

    /**
     * 5-second countdown to game start
     */
    public void initGameCountdownTimer() {
        pregameTimer = new Countdown((JavaPlugin)plugin,
                plugin.getConfig().getInt("RoundPrepTime"),
                //Timer Start
                () -> {
                    gameState = GameState.GAME_STARTING;
                    Bukkit.broadcastMessage(ChatColor.RED + "Box Opening Soon!");
                    teleporter.teleportToArenas();
                },

                //Timer End
                () -> {
                    StartGameCommand.timeVar = 0;

                    initInGameTimer();
                    for(Team t: handler.getTeams()) {
                        for(Player p:t.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                            p.sendTitle(handler.getPlayerTeam(p).color + "GO!", "", 2, 12, 2);
                            wallHandler.deleteWalls();
                        }
                    }
                },

                //Each Second
                (t) -> {
                    StartGameCommand.timeVar = t.getSecondsLeft();
                    for(Team team: handler.getTeams()) {
                        for(Player p:team.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                            p.sendTitle(handler.getPlayerTeam(p).color + "Game Starts in", ">" + t.getSecondsLeft() + "<", 0, 20, 0);
                        }
                    }
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        pregameTimer.scheduleTimer();
    }

    /**
     * 60-second timer for during the game
     */
    public void initInGameTimer() {
        inGameTimer = new Countdown((JavaPlugin)plugin,
                plugin.getConfig().getInt("RoundTime"),
                //Timer Start
                () -> {
                    gameState = GameState.ROUND_ACTIVE;
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

    public int getInGameTimer() {
        if(inGameTimer!= null) {
            return inGameTimer.getSecondsLeft();
        } else {
            return 0;
        }

    }

    public void endRound() {
        roundEndHandler.endRound();
    }

    public ArrayList<TeamKits> getTeamKitsList() {
        return teamKitsList;
    }

}
