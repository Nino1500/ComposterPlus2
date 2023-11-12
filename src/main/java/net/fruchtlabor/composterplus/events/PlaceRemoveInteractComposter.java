package net.fruchtlabor.composterplus.events;

import net.fruchtlabor.composterplus.ComposterPlus;
import net.fruchtlabor.composterplus.guis.ComposterGui;
import net.fruchtlabor.composterplus.misc.Composter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;

public class PlaceRemoveInteractComposter implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if (event.getBlockPlaced().getType().equals(Material.COMPOSTER)){
            if (!ComposterPlus.plugin.getConfig().getStringList("Worlds").contains(event.getBlockPlaced().getWorld().getName())){
                return;
            }
            Block block = event.getBlockPlaced();
            World world = block.getWorld();
            Location location = block.getLocation().add(0.5, 0, 0.5);
            ArmorStand armorStand = world.spawn(location, ArmorStand.class, armorStandSetup -> {
                armorStandSetup.setVisible(false);
                armorStandSetup.setInvulnerable(true);
                armorStandSetup.setGravity(false);
                armorStandSetup.setBasePlate(false);
                armorStandSetup.setMarker(true);
                PersistentDataContainer dataContainer = armorStandSetup.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("composterplus", "exp");
                dataContainer.set(key, PersistentDataType.DOUBLE, 0.0);
            });
        }
    }

    @EventHandler
    public void onRemove(BlockBreakEvent event){
        if (event.getBlock().getType().equals(Material.COMPOSTER)){
            if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
                event.setCancelled(true);
                return;
            }
            if (!ComposterPlus.plugin.getConfig().getStringList("Worlds").contains(event.getBlock().getWorld().getName())){
                return;
            }
            Block block = event.getBlock();
            Location location = block.getLocation().add(0.5, 0, 0.5);
            Collection<ArmorStand> armorStands = block.getWorld().getEntitiesByClass(ArmorStand.class);
            for (ArmorStand armorStand : armorStands) {
                if (armorStand.getLocation().equals(location)) {
                    PersistentDataContainer dataContainer = armorStand.getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey("composterplus", "exp");
                    Double exp = dataContainer.get(key, PersistentDataType.DOUBLE);
                    if (exp != null && exp > 0) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ComposterPlus.languageManager.getMessage("Messages.BreakComposterEXP"));
                    }
                    armorStand.remove();
                    break;
                }
            }
        }
    }


    @EventHandler
    public void onClick(PlayerInteractEvent event){
        Block block = event.getClickedBlock();
        if (block != null && !event.getAction().isRightClick()){
            if (block.getType().equals(Material.COMPOSTER)){
                if (ComposterPlus.plugin.getConfig().getStringList("Worlds").contains(block.getWorld().getName())){
                    Composter composter = new Composter(block);
                    ComposterGui composterGui = new ComposterGui(composter);
                    event.getPlayer().openInventory(composterGui.getComposterMainGui(event.getPlayer()));
                }
            }
        }
    }


}
