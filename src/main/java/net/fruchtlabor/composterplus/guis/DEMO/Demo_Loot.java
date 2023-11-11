package net.fruchtlabor.composterplus.guis.DEMO;

import net.fruchtlabor.composterplus.ComposterPlus;
import net.fruchtlabor.composterplus.misc.Loot;
import net.fruchtlabor.composterplus.builder.inventory.pageable.PageableItem;
import net.fruchtlabor.composterplus.builder.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class Demo_Loot implements PageableItem {

    private Loot loot;

    public Demo_Loot(Loot loot) {
        this.loot = loot;
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemBuilder(loot.getItem())
                .addLore(ComposterPlus.plugin.getConfig().getString("Language.LootGuiChanceReward") + loot.getChance() + "%")
                .addLore(ComposterPlus.plugin.getConfig().getString("Language.LootGuiExpReward") + loot.getPlayer_exp())
                .build();
    }

    @Override
    public String getID() {
        return null;
    }

    public Loot getLoot() {
        return loot;
    }
}
