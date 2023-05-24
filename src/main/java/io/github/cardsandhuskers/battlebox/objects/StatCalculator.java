package io.github.cardsandhuskers.battlebox.objects;

import io.github.cardsandhuskers.battlebox.BattleBox;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class StatCalculator {
    private BattleBox plugin;
    private ArrayList<PlayerStatsHolder> playerStatsHolders;
    private ArrayList<SingleGameKillsHolder> sgKillsHolders;


    public StatCalculator(BattleBox plugin) {
        this.plugin = plugin;

    }

    public void calculateStats() throws Exception {
        HashMap<String, PlayerStatsHolder> playerStatsMap = new HashMap<>();
        sgKillsHolders = new ArrayList<>();
        playerStatsHolders = new ArrayList<>();

        FileReader reader = null;
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

        HashMap<Integer, ArrayList<CSVRecord>> recordsMap = new HashMap<>();
        for (CSVRecord r : recordList) {
            //skip header
            if (r.getRecordNumber() == 1) continue;
            String name = r.get(2);
            if(playerStatsMap.containsKey(name)) {
                PlayerStatsHolder h = playerStatsMap.get(name);
                h.kills += Integer.parseInt(r.get(3));
                h.wins += Integer.parseInt(r.get(4));
            } else {
                PlayerStatsHolder h = new PlayerStatsHolder(name);
                h.kills += Integer.parseInt(r.get(3));
                h.wins += Integer.parseInt(r.get(4));
                playerStatsMap.put(name, h);
            }
            SingleGameKillsHolder kh = new SingleGameKillsHolder();
            kh.name = name;
            kh.kills = Integer.parseInt(r.get(3));
            kh.eventNum = Integer.parseInt(r.get(0));
            sgKillsHolders.add(kh);
        }
        playerStatsHolders = new ArrayList<>(playerStatsMap.values());

    }




    public ArrayList<PlayerStatsHolder> getStatsHolders(PlayerStatsComparator.SortType sortType) {

        ArrayList<PlayerStatsHolder> psh= new ArrayList<>(playerStatsHolders);

        Comparator PlayerStatsCompare = new PlayerStatsComparator(sortType);
        psh.sort(PlayerStatsCompare);
        Collections.reverse(psh);
        return new ArrayList<>(psh);
    }

    public ArrayList<SingleGameKillsHolder> getSGKillsHolders() {
        ArrayList<SingleGameKillsHolder> sgkh= new ArrayList<>(sgKillsHolders);

        Comparator SGKHComparator = new SGKHComparator();
        sgkh.sort(SGKHComparator);
        Collections.reverse(sgkh);
        return sgkh;
    }




    public class PlayerStatsHolder {
        int kills = 0;
        int wins = 0;
        String name;
        public PlayerStatsHolder(String name) {
            this.name = name;
        }
    }

    class PlayerStatsComparator implements Comparator<PlayerStatsHolder> {
        public SortType sortType;
        public PlayerStatsComparator(SortType sortType) {
            this.sortType = sortType;
        }
        public int compare(PlayerStatsHolder h1, PlayerStatsHolder h2) {
            if(sortType == SortType.KILLS) {
                int compare = Integer.compare(h1.kills, h2.kills);
                if(compare == 0) h1.name.compareTo(h2.name);
                if(compare == 0) compare = 1;
                return  compare;
            } else {
                int compare = Integer.compare(h1.wins, h2.wins);
                if(compare == 0) compare = h1.name.compareTo(h2.name);
                if(compare == 0) compare = 1;
                return  compare;
            }
        }

        enum SortType {
            KILLS,
            WINS
        }
    }

    public class SingleGameKillsHolder {
        String name;
        int eventNum;
        int kills;
    }

    class SGKHComparator implements Comparator<SingleGameKillsHolder> {
        public int compare(SingleGameKillsHolder kh1, SingleGameKillsHolder kh2) {
            int compare = Integer.compare(kh1.kills, kh2.kills);
            if(compare == 0) compare = kh1.name.compareTo(kh2.name);
            if(compare == 0) compare = 1;
            return compare;
        }
    }

}
