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
                .addLore(ComposterPlus.languageManager.getMessage("Gui.LootGuiChanceReward") + loot.getChance() + "%")
                .addLore(ComposterPlus.languageManager.getMessage("Gui.LootGuiExpReward") + loot.getPlayer_exp())
                .setAmount(loot.getItem().getAmount())
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
