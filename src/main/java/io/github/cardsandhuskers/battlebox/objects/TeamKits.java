package io.github.cardsandhuskers.battlebox.objects;

import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

import static io.github.cardsandhuskers.battlebox.BattleBox.handler;

public class TeamKits {

    private Team team;
    private ArrayList<String> unusedKits = new ArrayList<>();
    private ArrayList<Player> unkittedPlayers = new ArrayList<>();
    private boolean marksmanSelected;
    private String marksman;
    private boolean potionMasterSelected;
    private String potionMaster;
    private boolean swordsmanSelected;
    private String swordsman;
    private boolean armorerSelected;
    private String armorer;

    private Location baseLoc;
    private int teamSide;


    public TeamKits(Team t) {
        team = t;
        marksmanSelected = false;
        potionMasterSelected = false;
        swordsmanSelected = false;
        armorerSelected = false;

        unusedKits.add("marksman");
        unusedKits.add("potionmaster");
        unusedKits.add("swordsman");
        unusedKits.add("armorer");

        for(Player p: t.getOnlinePlayers()) {
            prepPlayer(p);
            healPlayer(p);
            unkittedPlayers.add(p);
        }
    }

    /**
     * Selects marksman kit
     * @param p
     * @return boolean of whether marksman kit was already selected
     */
    public boolean selectMarksman(Player p) {
        if(marksmanSelected) {
            return false;
        } else {
            prepPlayer(p);
            Inventory inv = p.getInventory();
            ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
            ItemMeta crossbowMeta = crossbow.getItemMeta();
            //crossbowMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
            crossbowMeta.addEnchant(Enchantment.QUICK_CHARGE, 1, false);
            crossbowMeta.addEnchant(Enchantment.MULTISHOT, 1, false);
            crossbowMeta.setDisplayName("Marksman's Crossbow");
            crossbowMeta.setUnbreakable(true);
            crossbow.setItemMeta(crossbowMeta);
            inv.setItem(2, crossbow);

            ItemStack arrow = new ItemStack(Material.ARROW, 12);
            inv.setItem(17, arrow);

            removePlayer(p);
            marksmanSelected = true;
            marksman = p.getDisplayName();
            unkittedPlayers.remove(p);
            unusedKits.remove("marksman");

            return true;
        }
    }

    /**
     * Selects potionMaster kit
     * @param p
     * @return boolean of whether potionMaster kit was already selected
     */
    public boolean selectPotionMaster(Player p) {
        if(potionMasterSelected) {
            return false;
        } else {

            prepPlayer(p);
            Inventory inv = p.getInventory();
            ItemStack healthPotion = new ItemStack(Material.SPLASH_POTION, 1);
            PotionMeta healthPotionMeta = (PotionMeta) healthPotion.getItemMeta();
            healthPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 0, 0 ), true);
            healthPotionMeta.setColor(Color.fromRGB(252,37,36));
            healthPotionMeta.setDisplayName("Splash Potion of Healing");
            healthPotion.setItemMeta(healthPotionMeta);

            ItemStack harmingPotion = new ItemStack(Material.SPLASH_POTION, 1);
            PotionMeta harmingPotionMeta = (PotionMeta) harmingPotion.getItemMeta();
            harmingPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 0, 0), true);
            harmingPotionMeta.setColor(Color.fromRGB(68,10,9));
            harmingPotionMeta.setDisplayName("Splash Potion of Harming");
            harmingPotion.setItemMeta(harmingPotionMeta);


            ItemStack poisonPotion = new ItemStack(Material.SPLASH_POTION, 1);
            PotionMeta poisonPotionMeta = (PotionMeta) poisonPotion.getItemMeta();
            poisonPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 400, 0), false);
            poisonPotionMeta.setColor(Color.fromRGB(79,150,50));
            poisonPotionMeta.setDisplayName("Splash Potion of Poison");
            poisonPotion.setItemMeta(poisonPotionMeta);




            inv.setItem(2, healthPotion);
            inv.setItem(3, healthPotion);

            inv.setItem(4, harmingPotion);
            inv.setItem(5, harmingPotion);

            inv.setItem(6, poisonPotion);

            removePlayer(p);
            potionMasterSelected = true;
            potionMaster = p.getDisplayName();
            unkittedPlayers.remove(p);
            unusedKits.remove("potionmaster");

            return true;
        }
    }

    /**
     * Selects swordsman kit
     * @param p
     * @return boolean of whether swordsman kit was already selected
     */
    public boolean selectSwordsman(Player p) {
        if(swordsmanSelected) {
            return false;
        } else {
            prepPlayer(p);
            Inventory inv = p.getInventory();
            ItemStack ironSword = new ItemStack(Material.IRON_SWORD, 1);
            ItemMeta ironSwordMeta = ironSword.getItemMeta();
            //ironSwordMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            ironSwordMeta.setUnbreakable(true);
            ironSword.setItemMeta(ironSwordMeta);
            inv.setItem(0, ironSword);

            removePlayer(p);
            swordsmanSelected = true;
            swordsman = p.getDisplayName();
            unkittedPlayers.remove(p);
            unusedKits.remove("swordsman");

            return true;
        }
    }

    /**
     * Selects armorer kit
     * @param p
     * @return boolean of whether armorer kit was already selected
     */
    public boolean selectArmorer(Player p) {
        if(armorerSelected) {
            return false;
        } else {
            prepPlayer(p);

            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
            LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
            chestplateMeta.setColor(translateColor(handler.getPlayerTeam(p).color));
            chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, false);
            chestplateMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(p.getDisplayName(), .3, AttributeModifier.Operation.ADD_NUMBER));
            chestplateMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(p.getDisplayName(), 3, AttributeModifier.Operation.ADD_NUMBER));
            chestplateMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            chestplateMeta.setUnbreakable(true);
            chestplate.setItemMeta(chestplateMeta);
            p.getEquipment().setChestplate(chestplate);


            ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
            LeatherArmorMeta leggingsItemMeta = (LeatherArmorMeta) leggings.getItemMeta();
            leggingsItemMeta.setColor(translateColor(handler.getPlayerTeam(p).color));
            leggingsItemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, false);
            leggingsItemMeta.setUnbreakable(true);
            leggings.setItemMeta(leggingsItemMeta);
            p.getEquipment().setLeggings(leggings);


            removePlayer(p);
            armorerSelected = true;
            armorer = p.getDisplayName();
            unkittedPlayers.remove(p);
            unusedKits.remove("armorer");
            return true;
        }
    }


    public Team getTeam() {
        return team;
    }

    public void removePlayer(Player p) {
        String playerName = p.getDisplayName();
        if(playerName.equals(marksman)) {
            marksmanSelected = false;
            marksman = null;
        } else if(playerName.equals(potionMaster)) {
            potionMasterSelected = false;
            potionMaster = null;
        }else if(playerName.equals(swordsman)) {
            swordsmanSelected = false;
            swordsman = null;
        } else if(playerName.equals(armorer)) {
            armorerSelected = false;
            armorer = null;
        }
    }

    /**
     * Updates each kit line for the team
     * @param spot
     * @param backBlock
     * @param side
     */
    public void updateBlocks(int spot, Location backBlock, int side) {
        teamSide = side;
        if(side == 1) {
            backBlock.setZ(backBlock.getZ() - spot * 2);
            baseLoc = new Location(backBlock.getWorld(), backBlock.getX(), backBlock.getY(), backBlock.getZ());
            buildBlocks(backBlock, armorerSelected);
            backBlock.setZ(backBlock.getZ() + 2);
            buildBlocks(backBlock, swordsmanSelected);
            backBlock.setZ(backBlock.getZ() + 2);
            buildBlocks(backBlock, potionMasterSelected);
            backBlock.setZ(backBlock.getZ() + 2);
            buildBlocks(backBlock, marksmanSelected);
        } else {
            backBlock.setZ(backBlock.getZ() + spot * 2);
            baseLoc = new Location(backBlock.getWorld(), backBlock.getX(), backBlock.getY(), backBlock.getZ());
            buildBlocks(backBlock, armorerSelected);
            backBlock.setZ(backBlock.getZ() - 2);
            buildBlocks(backBlock, swordsmanSelected);
            backBlock.setZ(backBlock.getZ() - 2);
            buildBlocks(backBlock, potionMasterSelected);
            backBlock.setZ(backBlock.getZ() - 2);
            buildBlocks(backBlock, marksmanSelected);
        }
    }

    /**
     * Builds the blocks behind the specified button to tell players which kits have been selected
     * @param l
     * @param val
     */
    public void buildBlocks(Location l, boolean val) {
        Material mat;
        if(val) {
            mat = Material.LIME_CONCRETE;
        } else {
            mat = Material.RED_CONCRETE;
        }
        //Build the whole column (shift along y axis)
        for(int i = -1; i <= 2; i++) {
            Location loc = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
            loc.setY(l.getY() + i);
            Block block = loc.getBlock();
            block.setType(mat);
        }
    }

    /**
     * Runs at the end of kit selection time to give kits to anyone who hasn't selected a kit
     */
    public void populateRemainingKits() {
        if(unkittedPlayers != null) {
            //weird error where unusedKits may be empty for some reason, this should stop the error
            while (unkittedPlayers.size() > 0 && !unusedKits.isEmpty()) {
                Player p = unkittedPlayers.get(0);
                if (unusedKits.get(0).equals("marksman")) {
                    selectMarksman(p);
                } else if (unusedKits.get(0).equals("potionmaster")) {
                    selectPotionMaster(p);
                } else if (unusedKits.get(0).equals("swordsman")) {
                    selectSwordsman(p);
                } else if (unusedKits.get(0).equals("armorer")) {
                    selectArmorer(p);
                }

            }
        }
        resetLines();
    }

    public void resetLines() {
        armorerSelected = false;
        swordsmanSelected = false;
        potionMasterSelected = false;
        marksmanSelected = false;

        if(!(baseLoc == null)) {
            updateBlocks(0, baseLoc, teamSide);
        }

    }

    /**
     * Clears inv and gives player generic items
     * @param p
     */
    public void prepPlayer(Player p) {

        Inventory inv = p.getInventory();
        inv.clear();

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        //some error happened here, so I make sure they have a team to color stuff
        if(handler.getPlayerTeam(p) != null) {
            helmetMeta.setColor(translateColor(team.color));
        }
        helmetMeta.setUnbreakable(true);
        helmet.setItemMeta(helmetMeta);
        p.getEquipment().setHelmet(helmet);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        if(handler.getPlayerTeam(p) != null) {
            bootsMeta.setColor(translateColor(team.color));
        }
        bootsMeta.setUnbreakable(true);
        boots.setItemMeta(bootsMeta);
        p.getEquipment().setBoots(boots);

        ItemStack wool = new ItemStack(team.getWoolColor(), 64);
        inv.setItem(8, wool);

        ItemStack shears = new ItemStack(Material.SHEARS, 1);
        inv.setItem(7, shears);

        ItemStack woodenSword = new ItemStack(Material.WOODEN_SWORD, 1);
        ItemMeta woodenSwordMeta = woodenSword.getItemMeta();
        woodenSwordMeta.setUnbreakable(true);
        woodenSword.setItemMeta(woodenSwordMeta);
        inv.setItem(0, woodenSword);

        ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
        ItemMeta crossbowMeta = crossbow.getItemMeta();
        crossbowMeta.addEnchant(Enchantment.QUICK_CHARGE, 2, false);
        crossbowMeta.setUnbreakable(true);
        crossbow.setItemMeta(crossbowMeta);
        inv.setItem(1, crossbow);

        ItemStack arrow = new ItemStack(Material.ARROW, 8);
        inv.setItem(17, arrow);
    }
    public void healPlayer(Player p) {
        p.setHealth(20);
        p.setSaturation(20);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.SURVIVAL);
        for(PotionEffect potionEffect: p.getActivePotionEffects()) {
            p.removePotionEffect(potionEffect.getType());
        }
    }

    /**
     * Returns a Spigot Color for the color string passed
     * @param c
     * @return Color
     */
    public Color translateColor(String c) {
        switch (c) {
            case "§2": return Color.GREEN;
            case "§3": return Color.TEAL;
            case "§5": return Color.PURPLE;
            case "§6": return Color.ORANGE;
            case "§7": return Color.fromRGB(145,145,145); //light gray
            case "§8": return Color.GRAY;
            case "§9": return Color.BLUE;
            case "§a": return Color.LIME;
            case "§b": return Color.AQUA;
            case "§c": return Color.RED;
            case "§d": return Color.fromRGB(255,0,255); //magenta
            case "§e": return Color.YELLOW;
            default: return Color.WHITE;
        }
    }


}
