package net.fruchtlabor.composterplus.misc;

import org.bukkit.inventory.ItemStack;

public class Loot {
    private ItemStack item;
    private double chance;
    private int player_exp;

    public Loot(ItemStack item, double chance, int player_exp) {
        this.item = item;
        this.chance = chance;
        this.player_exp = player_exp;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getChance() {
        return chance;
    }

    public int getPlayer_exp() {
        return player_exp;
    }
}
