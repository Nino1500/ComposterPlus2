package net.fruchtlabor.composterplus.events;

import net.fruchtlabor.composterplus.ComposterPlus;
import net.fruchtlabor.composterplus.misc.Compost;
import net.fruchtlabor.composterplus.misc.Composter;
import net.fruchtlabor.composterplus.misc.Loot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.Levelled;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class ComposterInteraction implements Listener {

    FileConfiguration config = ComposterPlus.plugin.getConfig();
    boolean removeBoneMeal = config.getBoolean("BoneMeal.Remove");
    boolean reduceBoneMeal = config.getBoolean("BoneMeal.Reduce.Use");
    int reducePercentage = config.getInt("BoneMeal.Reduce.Percentage");

    @EventHandler
    public void onHand(PlayerInteractEvent event){
        Block block = event.getClickedBlock();
        if(block != null && block.getType() == Material.COMPOSTER){
            Player player = event.getPlayer();
            ItemStack item = event.getItem();
            for(Compost compost : ComposterPlus.composts){
                if(compost.getItem().isSimilar(item)){
                    if(shouldIncreaseLevel(compost.getCompost_chance())){
                        Levelled levelled = (Levelled) block.getBlockData();
                        levelled.setLevel(Math.min(levelled.getLevel() + 1, levelled.getMaximumLevel()));
                        block.setBlockData(levelled);
                    }
                    item.setAmount(item.getAmount() - 1);
                    player.setItemInHand(item);
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onHoppers(InventoryMoveItemEvent event){
        if (event.getDestination().getHolder() instanceof Hopper hopper) {

            Composter composter = isNextToComposter(hopper.getLocation());
            if (composter == null){
                return;
            }

            ItemStack originalItemStack = event.getItem();
            if (originalItemStack.getType().equals(Material.AIR)){
                return;
            }

            Compost compost = getCompost(originalItemStack);
            if (compost == null){
                return;
            }

            ItemStack itemStack = originalItemStack.clone();
            itemStack.setAmount(1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    System.out.println("Started Hopper-Action: "+ itemStack.getType().name() + " Amount: " + itemStack.getAmount());
                    doHopperAction(composter, compost, itemStack, hopper);
                }
            }.runTaskLater(ComposterPlus.plugin, 1);

            if (originalItemStack.getAmount() > 1) {
                originalItemStack.setAmount(originalItemStack.getAmount() - 1);
            } else {
                event.getSource().removeItem(originalItemStack);
            }
        }
    }

    public void doHopperAction(Composter composter, Compost compost, ItemStack itemStack, Hopper hopper){
        if (shouldIncreaseLevel(compost.getCompost_chance())) {
            Block composterBlock = composter.getBlock();
            if (composterBlock.getBlockData() instanceof Levelled) {
                Levelled levelled = (Levelled) composterBlock.getBlockData();
                int newLevel = Math.min(levelled.getLevel() + 1, levelled.getMaximumLevel());
                levelled.setLevel(newLevel);
                composterBlock.setBlockData(levelled);
                Inventory hopperInv = hopper.getInventory();
                hopperInv.remove(itemStack);
            }
        } else {
            Inventory hopperInv = hopper.getInventory();
            hopperInv.remove(itemStack);
        }
    }

    public Composter isNextToComposter(Location location){
        if(!ComposterPlus.plugin.getConfig().getStringList("Worlds").contains(location.getWorld().getName())){
            return null;
        }
        BlockFace[] faces = new BlockFace[]{
                BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST
        };
        for (BlockFace face : faces){
            if(location.getBlock().getRelative(face).getType().equals(Material.COMPOSTER)){
                return new Composter(location.getBlock().getRelative(face));
            }
        }
        return null;
    }

    @EventHandler
    public void onCompostComplete(BlockPhysicsEvent event){
        Block block = event.getBlock();
        if(block.getType() == Material.COMPOSTER){
            Composter composter = new Composter(block);
            if(composter.isCompostingComplete()){
                if(removeBoneMeal) {
                    return;
                }

                boolean shouldGenerateBoneMeal = shouldGenerateBoneMeal();

                Block hopperBlock = block.getRelative(BlockFace.DOWN);
                if(hopperBlock.getType() == Material.HOPPER){
                    Hopper hopper = (Hopper) hopperBlock.getState();
                    for(Loot loot : ComposterPlus.loots){
                        if(shouldGenerateBoneMeal && Math.random() * 100 < loot.getChance()){
                            hopper.getInventory().addItem(loot.getItem());
                            if(loot.getPlayer_exp() > 0.0){
                                composter.addExp(loot.getPlayer_exp());
                            }
                        }
                    }

                    if(shouldGenerateBoneMeal) {
                        ItemStack boneMeal = new ItemStack(Material.BONE_MEAL);
                        hopper.getInventory().addItem(boneMeal);
                    }

                } else {
                    Location location = block.getLocation().add(0.5, 1, 0.5);
                    for(Loot loot : ComposterPlus.loots){
                        if(shouldGenerateBoneMeal && Math.random() * 100 < loot.getChance()){
                            block.getWorld().dropItemNaturally(location, loot.getItem());
                            if(loot.getPlayer_exp() > 0.0){
                                composter.addExp(loot.getPlayer_exp());
                            }
                        }
                    }

                    if(shouldGenerateBoneMeal) {
                        ItemStack boneMeal = new ItemStack(Material.BONE_MEAL);
                        block.getWorld().dropItemNaturally(location, boneMeal);
                    }
                }
            }
        }
    }

    public boolean shouldIncreaseLevel(int compost_chance){
        double chance = compost_chance / 100.0;
        return Math.random() < chance;
    }

    private boolean shouldGenerateBoneMeal() {
        if(reduceBoneMeal) {
            return Math.random() < (reducePercentage / 100.0);
        }
        return true;
    }

    private Compost getCompost(ItemStack itemStack){
        for (Compost compost : ComposterPlus.composts){
            if (itemStack.isSimilar(compost.getItem())){
                return compost;
            }
        }
        return null;
    }
}
