# Battlebox
Battlebox is a round robin PVP game where you face off against all other teams in a round robin format.

## Commands:
**/startsurvivalgames [multiplier] **
- Starts the game with a multiplier, which can be any double.

**/setBattleboxSpawn **
- sets the spawn point of the game lobby

**/setBattleboxArena [arenaNumber]**
- sets the center of a battlebox arena, you should be standing on a 3x3 square of wool

**/setBattleboxArenaSpawn [arenaNumber] [teamSide]**
- sets the spawn point in the arena for each team
- teamSide should be 1 or 2

**/setBattleboxKitSpawn [arenaNumber] [teamSide]**
- sets the position of the spawn point for each kit room
- teamSide should be 1 or 2

**/setBattleboxArenaWall [arenaNumber] [teamSide]**
- sets the location of the arena wall
- you should be standing on the floor in the middle of the wall

**/setLobby**
- sets the location players will be teleported to after the end of the game

**/cancelBattlebox**
- cancels an in-progress game

**/reloadBattlebox**
- reloads the game's config file

## Setup:
1. The center of the arena should be a 3x3 of wool (doesn't technically have to be real center)
2. Each kit room should contain 4 buttons on its wall, with 1 block of space between each of them
3. The arenaWalls are the walls that disappear and let players into the arena when the round starts, their size is (for now) hard-coded
4. Arenas should be on a north-south alignment, (kit rooms should be on north and south ends)

## PlaceholerAPI hooks:
%Battlebox_timer% - returns current time remaining
<br>%Battlebox_timerstage% - returns the stage of the game
<br>%Battlebox_round% - returns the current round / total rounds
<br>%Battlebox_roundswon% - returns the number of rounds the player has won

### Stat Leaderboard Hooks:
%Battlebox_playerKills_[index]% - returns the number of single game kills the person in that place has (single-game kills leaderboard)
<br>%Battlebox_totalKills_[index]% - returns the number of total kills the person in that place has (lifetime kills leaderboard)
<br>%Battlebox_wins_[index]% - returns the number of wins the person in that place has (lifetime wins leaderboard)
<br>%Battlebox_yourwins% - returns your placement and win count
<br>%Battlebox_yourkills% - returns your placement and kill count

## Dependencies:
- Teams Plugin (https://github.com/cardsandhuskers/TeamsPlugin)
  - note: this must be manually set up as a local library on your machine to build this plugin
- optional: placeholderAPI