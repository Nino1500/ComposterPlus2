package net.fruchtlabor.composterplus.commands;

import net.fruchtlabor.composterplus.ComposterPlus;
import net.fruchtlabor.composterplus.guis.admin.AdminGui;
import net.fruchtlabor.composterplus.misc.Compost;
import net.fruchtlabor.composterplus.misc.Loot;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class AdminCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String prefix = ComposterPlus.languageManager.getMessage("General.Prefix");
        String adminPermission = ComposterPlus.plugin.getConfig().getString("Permissions.AdminCommand");

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.NotPlayer"));
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission(adminPermission)) {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.NoPermission"));
            return true;
        }

        if (args.length == 0) {
            displayHelp(player, prefix);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        switch (subCommand) {
            case "addcompost":
                return handleAddCompost(player, itemInHand, args, prefix);
            case "addloot":
                return handleAddLoot(player, itemInHand, args, prefix);
            case "removecompost":
                return handleRemoveCompost(player, itemInHand, prefix);
            case "removeloot":
                return handleRemoveLoot(player, itemInHand, prefix);
            case "help":
                displayHelp(player, prefix);
                return true;
            default:
                player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.UnknownSubCommand"));
                return true;
        }
    }

    private void displayHelp(CommandSender sender, String prefix) {
        sender.sendMessage(prefix + " Available commands:");
        sender.sendMessage("§9/cp §aaddcompost §f<compost_chance> §7- Adds a compost item with the item in your hand.");
        sender.sendMessage("§9/cp §aaddloot §f<chance> <player_exp> §7- Adds a loot item with the item in your hand.");
        sender.sendMessage("§9/cp §aremovecompost §7- Removes a compost item with the item in your hand.");
        sender.sendMessage("§9/cp §aremoveloot §7- Removes a loot item with the item in your hand.");
        sender.sendMessage("§9/cp §aadmin §7- Opens the Remove-Gui for both Compost and Loot.");
        sender.sendMessage("§9/cp §ahelp §7- Displays this help message.");
    }

    private boolean handleAddCompost(Player player, ItemStack itemInHand, String[] args, String prefix) {
        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.AttemptAir"));
            return true;
        }
        if (args.length != 2) {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.AddCompostUsage"));
            return true;
        }
        try {
            int compostChance = Integer.parseInt(args[1]);
            ItemStack copy = new ItemStack(itemInHand);
            copy.setAmount(1);
            if (compostExists(copy)) {
                player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.AlreadyAddedCompost"));
                return true;
            }
            Compost compost = new Compost(copy, compostChance);
            ComposterPlus.composts.add(compost);
            ComposterPlus.database.insertCompost(compost);
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.CompostAdded"));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.InvalidCompostChance"));
            return true;
        }
    }

    private boolean handleAddLoot(Player player, ItemStack itemInHand, String[] args, String prefix) {
        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.AttemptAir"));
            return true;
        }
        if (args.length != 3) {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.AddLootUsage"));
            return true;
        }
        try {
            double chance = Double.parseDouble(args[1]);
            int playerExp = Integer.parseInt(args[2]);
            ItemStack copyLoot = new ItemStack(itemInHand);
            if (lootExists(copyLoot)) {
                player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.AlreadyAddedLoot"));
                return true;
            }
            Loot loot = new Loot(copyLoot, chance, playerExp);
            ComposterPlus.loots.add(loot);
            ComposterPlus.database.insertLoot(loot);
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.LootAdded"));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.InvalidChanceValue"));
            return true;
        }
    }

    private boolean handleRemoveCompost(Player player, ItemStack itemInHand, String prefix) {
        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.AttemptAir"));
            return true;
        }
        if (removeCompost(itemInHand)) {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.RemoveCompostSuccess"));
        } else {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.RemoveCompostFail"));
        }
        return true;
    }

    private boolean handleRemoveLoot(Player player, ItemStack itemInHand, String prefix) {
        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.AttemptAir"));
            return true;
        }
        if (removeLoot(itemInHand)) {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.RemoveLootSuccess"));
        } else {
            player.sendMessage(prefix + ComposterPlus.languageManager.getMessage("Messages.RemoveLootFail"));
        }
        return true;
    }

    private boolean compostExists(ItemStack itemStack) {
        for (Compost compost : ComposterPlus.composts) {
            if (itemStack.isSimilar(compost.getItem())) {
                return true;
            }
        }
        return false;
    }

    private boolean lootExists(ItemStack itemStack) {
        for (Loot loot : ComposterPlus.loots) {
            if (itemStack.isSimilar(loot.getItem())) {
                return true;
            }
        }
        return false;
    }

    private boolean removeCompost(ItemStack itemStack) {
        Iterator<Compost> iterator = ComposterPlus.composts.iterator();
        while (iterator.hasNext()) {
            Compost compost = iterator.next();
            if (itemStack.isSimilar(compost.getItem())) {
                iterator.remove();
                ComposterPlus.database.deleteCompost(compost.getItem());
                return true;
            }
        }
        return false;
    }

    private boolean removeLoot(ItemStack itemStack) {
        Iterator<Loot> iterator = ComposterPlus.loots.iterator();
        while (iterator.hasNext()) {
            Loot loot = iterator.next();
            if (itemStack.isSimilar(loot.getItem())) {
                iterator.remove();
                ComposterPlus.database.deleteLoot(loot.getItem());
                return true;
            }
        }
        return false;
    }
}
