package net.fruchtlabor.composterplus.guis.admin;

import net.fruchtlabor.composterplus.ComposterPlus;
import net.fruchtlabor.composterplus.builder.inventory.InventoryBuilder;
import net.fruchtlabor.composterplus.builder.inventory.pageable.PageableInventory;
import net.fruchtlabor.composterplus.builder.item.ItemBuilder;
import net.fruchtlabor.composterplus.guis.DEMO.Demo_Compost;
import net.fruchtlabor.composterplus.guis.DEMO.Demo_Loot;
import net.fruchtlabor.composterplus.misc.Compost;
import net.fruchtlabor.composterplus.misc.Loot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Iterator;

public class AdminGui {
    public Inventory getMainAdminPanel(Player player){
        InventoryBuilder inventoryBuilder = new InventoryBuilder("§7ComposterPlus - AdminGui", 9*1);
        inventoryBuilder.setItem(3, new ItemBuilder(Material.COMPOSTER).setDisplayName("§7Remove Composts").build(), e -> {
            e.getWhoClicked().openInventory(getRemoveGuiCompost(player, 0));
        });
        inventoryBuilder.setItem(5, new ItemBuilder(Material.DIAMOND).setDisplayName("§7Remove Loot").build(), e -> {
            e.getWhoClicked().openInventory(getRemoveGuiLoot(player, 0));
        });
        return inventoryBuilder.build();
    }

    public Inventory getRemoveGuiCompost(Player player, int page){
        ArrayList<Demo_Compost> demoComposts = new ArrayList<>();
        for (Compost compost : ComposterPlus.composts){
            demoComposts.add(new Demo_Compost(compost));
        }
        PageableInventory.PageableBuilder inv = new PageableInventory.PageableBuilder(player, ComposterPlus.languageManager.getMessage("Gui.CompostGui"), 9*6, demoComposts);
        inv.setItemAction(((pageableItem, inventoryBuilderItemClickEvent) -> {
            InventoryBuilder inventoryBuilder = new InventoryBuilder("§7ComposterPlus - Remove Compost", 9*1);
            inventoryBuilder.setItem(3, new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§aRemove Compost").build(), e2 -> {
                Iterator<Compost> iterator = ComposterPlus.composts.iterator();
                while (iterator.hasNext()) {
                    Compost compost = iterator.next();
                    if (compost != null && ((Demo_Compost) pageableItem).getCompost().getItem().isSimilar(compost.getItem())) {
                        iterator.remove();
                        ComposterPlus.database.deleteCompost(compost.getItem());
                        e2.getWhoClicked().openInventory(getRemoveGuiCompost(player, page));
                        break;
                    }
                }
                e2.getWhoClicked().openInventory(getRemoveGuiCompost(player, page));
            });
            inventoryBuilder.setItem(5, new ItemBuilder(Material.RED_WOOL).setDisplayName("§cCancel").build(), e2 -> {
                e2.getWhoClicked().openInventory(getRemoveGuiCompost(player, page));
            });
            player.openInventory(inventoryBuilder.build());
        }));
        inv.setPreviousInventory(getMainAdminPanel(player));
        return inv.build().getInventory(page);
    }

    public Inventory getRemoveGuiLoot(Player player, int page){
        ArrayList<Demo_Loot> demoLoots = new ArrayList<>();
        for (Loot loot : ComposterPlus.loots){
            demoLoots.add(new Demo_Loot(loot));
        }
        PageableInventory.PageableBuilder inv = new PageableInventory.PageableBuilder(player, ComposterPlus.languageManager.getMessage("Gui.LootGui"), 9*6, demoLoots);
        inv.setItemAction(((pageableItem, inventoryBuilderItemClickEvent) -> {
            InventoryBuilder inventoryBuilder = new InventoryBuilder("§7ComposterPlus - Remove Loot", 9*1);
            inventoryBuilder.setItem(3, new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§aRemove Loot").build(), e2 -> {
                Iterator<Loot> iterator = ComposterPlus.loots.iterator();
                while (iterator.hasNext()) {
                    Loot loot = iterator.next();
                    if (loot != null && ((Demo_Loot) pageableItem).getLoot().getItem().isSimilar(loot.getItem())) {
                        iterator.remove();
                        ComposterPlus.database.deleteLoot(loot.getItem());
                        e2.getWhoClicked().openInventory(getRemoveGuiLoot(player, page));
                        break;
                    }
                }
                e2.getWhoClicked().openInventory(getRemoveGuiLoot(player, page));
            });
            inventoryBuilder.setItem(5, new ItemBuilder(Material.RED_WOOL).setDisplayName("§cCancel").build(), e2 -> {
                e2.getWhoClicked().openInventory(getRemoveGuiLoot(player, page));
            });
            player.openInventory(inventoryBuilder.build());
        }));
        inv.setPreviousInventory(getMainAdminPanel(player));
        return inv.build().getInventory(page);
    }


}
