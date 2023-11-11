package net.fruchtlabor.composterplus.misc;

import org.bukkit.inventory.ItemStack;

public class Compost {
    private ItemStack item;
    private int compost_chance;


    public Compost(ItemStack item, int compost_chance) {
        this.item = item;
        this.compost_chance = compost_chance;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getCompost_chance() {
        return compost_chance;
    }

    @Override
    public String toString() {
        return "Compost{" +
                "item=" + item +
                ", compost_chance=" + compost_chance +
                '}';
    }
}
