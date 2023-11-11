package net.fruchtlabor.composterplus.misc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Composter {
    private Block block;
    private NamespacedKey expKey;
    private ArmorStand armorStand;

    public Composter(Block block) {
        if (block.getType().equals(Material.COMPOSTER)){
            this.block = block;
            this.expKey = new NamespacedKey("composterplus", "exp");
            this.armorStand = findOrCreateArmorStand();
        } else {
            throw new IllegalArgumentException("Block is not a composter");
        }
    }

    private ArmorStand findOrCreateArmorStand() {
        Location location = block.getLocation().add(0.5, 0, 0.5);
        for (ArmorStand existing : block.getWorld().getEntitiesByClass(ArmorStand.class)) {
            if (existing.getLocation().equals(location)) {
                return existing;
            }
        }
        return createArmorStand(location);
    }

    private ArmorStand createArmorStand(Location location) {
        return block.getWorld().spawn(location, ArmorStand.class, armorStandSetup -> {
            armorStandSetup.setVisible(false);
            armorStandSetup.setInvulnerable(true);
            armorStandSetup.setGravity(false);
            armorStandSetup.setBasePlate(false);
            armorStandSetup.setMarker(true);
        });
    }

    public double getExp(){
        PersistentDataContainer dataContainer = armorStand.getPersistentDataContainer();
        return dataContainer.getOrDefault(expKey, PersistentDataType.DOUBLE, 0.0);
    }

    public void addExp(double new_exp){
        PersistentDataContainer dataContainer = armorStand.getPersistentDataContainer();
        double currentExp = dataContainer.getOrDefault(expKey, PersistentDataType.DOUBLE, 0.0);
        dataContainer.set(expKey, PersistentDataType.DOUBLE, currentExp + new_exp);
    }

    public void removeExp(Player player){
        PersistentDataContainer dataContainer = armorStand.getPersistentDataContainer();
        double expToGive = dataContainer.getOrDefault(expKey, PersistentDataType.DOUBLE, 0.0);
        player.giveExp((int) expToGive);
        dataContainer.set(expKey, PersistentDataType.DOUBLE, 0.0);
    }

    public boolean isCompostingComplete(){
        Levelled levelled = (Levelled) block.getBlockData();
        return levelled.getLevel() == levelled.getMaximumLevel();
    }

    public Block getBlock() {
        return block;
    }
}
