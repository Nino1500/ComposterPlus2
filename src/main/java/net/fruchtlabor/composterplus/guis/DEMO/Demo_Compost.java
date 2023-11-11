package net.fruchtlabor.composterplus.guis.DEMO;

import net.fruchtlabor.composterplus.misc.Compost;
import net.fruchtlabor.composterplus.ComposterPlus;
import net.fruchtlabor.composterplus.builder.inventory.pageable.PageableItem;
import net.fruchtlabor.composterplus.builder.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class Demo_Compost implements PageableItem {

    private Compost compost;

    public Demo_Compost(Compost compost) {
        this.compost = compost;
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemBuilder(compost.getItem())
                .addLore(ComposterPlus.plugin.getConfig().getString("Language.CompostLevelChanceLore") + compost.getCompost_chance() + "%")
                .build();
    }

    @Override
    public String getID() {
        return null;
    }

    public Compost getCompost() {
        return compost;
    }
}
