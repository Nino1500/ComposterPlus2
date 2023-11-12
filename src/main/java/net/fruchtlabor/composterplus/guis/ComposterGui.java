package net.fruchtlabor.composterplus.guis;

import net.fruchtlabor.composterplus.builder.inventory.events.InventoryBuilderItemClickEvent;
import net.fruchtlabor.composterplus.builder.inventory.pageable.NextPageableAction;
import net.fruchtlabor.composterplus.builder.inventory.pageable.PageableItem;
import net.fruchtlabor.composterplus.misc.Compost;
import net.fruchtlabor.composterplus.ComposterPlus;
import net.fruchtlabor.composterplus.misc.Composter;
import net.fruchtlabor.composterplus.misc.Loot;
import net.fruchtlabor.composterplus.builder.inventory.InventoryBuilder;
import net.fruchtlabor.composterplus.builder.inventory.pageable.PageableInventory;
import net.fruchtlabor.composterplus.builder.item.ItemBuilder;
import net.fruchtlabor.composterplus.guis.DEMO.Demo_Compost;
import net.fruchtlabor.composterplus.guis.DEMO.Demo_Loot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

import static net.fruchtlabor.composterplus.ComposterPlus.plugin;

public class ComposterGui {

    private final Composter composter;

    public ComposterGui(Composter composter){
        this.composter = composter;
    }

    public Inventory getComposterMainGui(Player player){
        double storedEXP = composter.getExp();
        InventoryBuilder inventoryBuilder = new InventoryBuilder("§2ComposterPlus", 9*2);
        inventoryBuilder.setItemWithCoordinates(9, 17, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("").build(), 150);

        inventoryBuilder.setItem(2, new ItemBuilder(Material.DIRT).setDisplayName(ComposterPlus.languageManager.getMessage("Gui.CompostGui")).build(), e -> {
            player.openInventory(getCompostGui(player, 0));
        });

        inventoryBuilder.setItem(4, new ItemBuilder(Material.DIAMOND).setDisplayName(ComposterPlus.languageManager.getMessage("Gui.LootGui")).build(), e -> {
            player.openInventory(getLootGui(player, 0));
        });

        inventoryBuilder.setItem(6, new ItemBuilder(Material.EXPERIENCE_BOTTLE).setDisplayName(ComposterPlus.languageManager.getMessage("Gui.StoredEXP"))
                .addLore(ComposterPlus.languageManager.getMessage("Gui.StoredEXPLore"))
                .addLore(ComposterPlus.languageManager.getMessage("Gui.StoredEXPAmount") + storedEXP).build(), e -> {
            composter.removeExp(player);
            player.openInventory(getComposterMainGui(player));
        });

        return inventoryBuilder.build();
    }

    public Inventory getCompostGui(Player player, int page){
        ArrayList<Demo_Compost> demoComposts = new ArrayList<>();
        for (Compost compost : ComposterPlus.composts){
            demoComposts.add(new Demo_Compost(compost));
        }
        PageableInventory.PageableBuilder inv = new PageableInventory.PageableBuilder(player, ComposterPlus.languageManager.getMessage("Gui.CompostGui"), 9*6, demoComposts);
        inv.setPreviousInventory(getComposterMainGui(player));
        inv.setItemAction(new DummyNextPageableAction());  // Set dummy NextPageableAction
        return inv.build().getInventory(page);
    }

    public Inventory getLootGui(Player player, int page){
        ArrayList<Demo_Loot> demoLoots = new ArrayList<>();
        for (Loot loot : ComposterPlus.loots){
            demoLoots.add(new Demo_Loot(loot));
        }
        PageableInventory.PageableBuilder inv = new PageableInventory.PageableBuilder(player, ComposterPlus.languageManager.getMessage("Gui.LootGui"), 9*6, demoLoots);
        inv.setPreviousInventory(getComposterMainGui(player));
        inv.setItemAction(new DummyNextPageableAction());
        return inv.build().getInventory(page);
    }

    // Dummy implementation of NextPageableAction to avoid NullPointerException
    private static class DummyNextPageableAction implements NextPageableAction {

        @Override
        public void getNextAction(PageableItem pageableItem, InventoryBuilderItemClickEvent e) {

        }
    }


}
