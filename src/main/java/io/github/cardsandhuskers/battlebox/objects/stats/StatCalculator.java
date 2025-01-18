package io.github.cardsandhuskers.battlebox.objects.stats;

import io.github.cardsandhuskers.battlebox.BattleBox;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.bukkit.Bukkit;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class StatCalculator {
    private BattleBox plugin;

    private int currentEvent;

    private HashMap<String, PlayerStatsHolder> playerStatsMap;
    ArrayList<SingleGameHolder> singleGameHolders = new ArrayList<>();

    public StatCalculator(BattleBox plugin) {
        this.plugin = plugin;
        try {currentEvent = Bukkit.getPluginManager().getPlugin("LobbyPlugin").getConfig().getInt("eventNum");}
        catch (Exception e) {currentEvent = 1;}

    }

    public void calculateStats() throws Exception {
        playerStatsMap = new HashMap<>();

        FileReader reader = null;

        for(int event = 1; event <= currentEvent; event++) {
            //wins
            try {
                reader = new FileReader(plugin.getDataFolder() + "/battleBoxWinStats" + event + ".csv");
            } catch (IOException e) {
                plugin.getLogger().warning("Win Stats file not found for event " + event + "!");
                continue;
            }

            String[] headers = {"Round", "winningPlayer", "winningTeam", "losingTeam"};

            CSVFormat.Builder builder = CSVFormat.Builder.create();
            builder.setHeader(headers);
            CSVFormat format = builder.build();

            CSVParser parser = new CSVParser(reader, format);
            List<CSVRecord> recordList = parser.getRecords();
            reader.close();

            System.out.println("EVENT " + event + "\n ----------WINS----------");
            for(CSVRecord r:recordList) {
                System.out.println(r);
                if (r.getRecordNumber() == 1) continue;

                String winner = r.get(1);
                PlayerStatsHolder holder = playerStatsMap.get(winner);
                if(holder == null) {
                    holder = new PlayerStatsHolder(winner);
                    playerStatsMap.put(winner, holder);
                }
                holder.addWin(event);
            }

            //kills
            try {
                reader = new FileReader(plugin.getDataFolder() + "/battleBoxKillStats" + event + ".csv");
            } catch (IOException e) {
                plugin.getLogger().warning("Kill Stats file not found for event " + event + "!");
                continue;
            }

            headers = new String[]{"round", "killer", "killerTeam", "prey", "preyTeam", "time"};

            builder = CSVFormat.Builder.create();
            builder.setHeader(headers);
            format = builder.build();

            parser = new CSVParser(reader, format);
            recordList = parser.getRecords();
            reader.close();

            System.out.println("-----KILLS-----");
            for(CSVRecord r:recordList) {
                System.out.println(r);
                if (r.getRecordNumber() == 1) continue;

                String killer = r.get(1);
                PlayerStatsHolder holder = playerStatsMap.get(killer);
                if(holder == null) {
                    holder = new PlayerStatsHolder(killer);
                    playerStatsMap.put(killer, holder);
                }
                holder.addKill(event);

            }
        }

        //old file
        try {
            reader = new FileReader(plugin.getDataFolder() + "/stats.csv");
        } catch (IOException e) {
            plugin.getLogger().warning("Stats file not found!");
            return;
        }

        String[] headers = {"Event", "Team", "Name", "Kills", "Wins"};
        CSVFormat.Builder builder = CSVFormat.Builder.create();
        builder.setHeader(headers);
        CSVFormat format = builder.build();

        CSVParser parser;
        try {
            parser = new CSVParser(reader, format);
        } catch (IOException e) {
            throw new Exception(e);
        }
        List<CSVRecord> recordList = parser.getRecords();

        try {
            reader.close();
        } catch (IOException e) {
            throw new Exception(e);
        }

        for (CSVRecord r : recordList) {
            //skip header
            if (r.getRecordNumber() == 1) continue;
            String name = r.get(2);

            PlayerStatsHolder h = playerStatsMap.get(name);
            if(h == null) {
                h = new PlayerStatsHolder(name);
            }

            int kills = Integer.parseInt(r.get(3));
            int event = Integer.parseInt(r.get(0));
            int wins = Integer.parseInt(r.get(4));

            h.addKills(event, kills);
            h.addWins(event, wins);
        }

        createSingleHolders();
    }

    public ArrayList<PlayerStatsHolder> getStatsHolders(PlayerStatsComparator.SortType sortType) {
        ArrayList<PlayerStatsHolder> psh= new ArrayList<>(playerStatsMap.values());

        Comparator playerStatsCompare = new PlayerStatsComparator(sortType);
        psh.sort(playerStatsCompare);
        Collections.reverse(psh);
        return psh;
    }

    public ArrayList<SingleGameHolder> getSingleGameHolders(PlayerStatsComparator.SortType sortType) {
        ArrayList<SingleGameHolder> sgh = new ArrayList<>(singleGameHolders);

        Comparator singleGameStatsCompare = new SingleGameStatsComparator(sortType);
        sgh.sort(singleGameStatsCompare);
        Collections.reverse(sgh);
        return sgh;
    }


    public ArrayList<SingleGameHolder> createSingleHolders() {
        for (PlayerStatsHolder psh: playerStatsMap.values()) {
            for(int event = 1; event <= currentEvent; event++) {
                if(psh.wins.get(event) == null && psh.kills.get(event) == null) {
                    continue;
                }

                int wins = psh.wins.getOrDefault(event, 0);
                int kills = psh.kills.getOrDefault(event, 0);

                SingleGameHolder sgh = new SingleGameHolder(psh.name, event, kills, wins);
                singleGameHolders.add(sgh);
            }


        }

        return singleGameHolders;
    }

    public class PlayerStatsHolder {
        HashMap<Integer, Integer> kills, wins;
        public String name;
        public PlayerStatsHolder(String name) {
            this.name = name;
            kills = new HashMap<>();
            wins = new HashMap<>();
        }

        public void addWin(int event) {
            int currWins = wins.getOrDefault(event, 0);
            wins.put(event, currWins + 1);
        }

        public void addKill(int event) {
            int currKills = kills.getOrDefault(event, 0);
            kills.put(event, currKills + 1);
        }

        public void addKills(int event, int eKills) {
            int currKills = kills.getOrDefault(event, 0);
            kills.put(event, currKills + eKills);
        }

        public void addWins(int event, int eWins) {
            int currWins = wins.getOrDefault(event, 0);
            wins.put(event, currWins + eWins);
        }

        public int getTotalWins() {
            int totalWins = 0;
            for(Integer i: wins.values()) totalWins += i;

            return totalWins;
        }

        public int getTotalKills() {
            int totalKills = 0;
            for(Integer i: kills.values()) totalKills += i;

            return totalKills;
        }
    }

    public class SingleGameHolder {
        public final String name;
        public final int event, kills, wins;

        /**
         *
         * @param name
         * @param event
         * @param kills
         * @param wins
         */
        public SingleGameHolder(String name, int event, int kills, int wins) {
            this.name = name;
            this.event = event;
            this.kills = kills;
            this.wins = wins;
        }
    }

    public class PlayerStatsComparator implements Comparator<PlayerStatsHolder> {
        public SortType sortType;
        public PlayerStatsComparator(SortType sortType) {
            this.sortType = sortType;
        }
        public int compare(PlayerStatsHolder h1, PlayerStatsHolder h2) {
            if(sortType == SortType.KILLS) {
                int compare = Integer.compare(h1.getTotalKills(), h2.getTotalKills());
                if(compare == 0) h1.name.compareTo(h2.name);
                if(compare == 0) compare = 1;
                return  compare;
            } else {
                int compare = Integer.compare(h1.getTotalWins(), h2.getTotalWins());
                if(compare == 0) compare = h1.name.compareTo(h2.name);
                if(compare == 0) compare = 1;
                return  compare;
            }
        }

        public enum SortType {
            KILLS,
            WINS
        }
    }

    public class SingleGameStatsComparator implements Comparator<SingleGameHolder> {
        public PlayerStatsComparator.SortType sortType;

        public SingleGameStatsComparator(PlayerStatsComparator.SortType sortType) {
            this.sortType = sortType;
        }

        public int compare(SingleGameHolder h1, SingleGameHolder h2) {
            if(sortType == sortType.KILLS) {
                int compare = Integer.compare(h1.kills, h2.kills);
                if(compare == 0) h1.name.compareTo(h2.name);
                if(compare == 0) compare = 1;
                return compare;
            } else {
                int compare = Integer.compare(h1.wins, h2.wins);
                if(compare == 0) h1.name.compareTo(h2.name);
                if(compare == 0) compare = 1;
                return compare;
            }
        }
    }

}
