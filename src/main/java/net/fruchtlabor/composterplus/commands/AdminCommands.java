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
        String prefix = ComposterPlus.plugin.getConfig().getString("Language.Prefix");
        String adminPermission = ComposterPlus.plugin.getConfig().getString("Permissions.AdminCommand");

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.NotPlayer"));
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission(adminPermission)) {
            player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.NoPermission"));
            return true;
        }

        if (args.length == 0) {
            displayHelp(commandSender, prefix);
            return true;
        }
        // cp admin
        if (args.length == 1 && args[0].equalsIgnoreCase("admin")){
            player.openInventory(new AdminGui().getMainAdminPanel(player));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        switch (subCommand) {
            case "addcompost":
                if (itemInHand.getType() == Material.AIR){
                    player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.AttemptAir"));
                    return false;
                }
                if (args.length != 2) {
                    player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.AddCompostUsage"));
                    return true;
                }
                int compostChance;
                try {
                    compostChance = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.InvalidCompostChance"));
                    return true;
                }
                ItemStack copy = new ItemStack(itemInHand);
                copy.setAmount(1);
                if (compostExists(copy)){
                    player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.AlreadyAddedCompost"));
                    return false;
                }
                Compost compost = new Compost(copy, compostChance);
                ComposterPlus.composts.add(compost);
                ComposterPlus.database.insertCompost(compost);
                player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.CompostAdded"));
                break;

            case "addloot":
                if (itemInHand.getType() == Material.AIR){
                    player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.AttemptAir"));
                    return false;
                }
                if (args.length != 3) {
                    player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.AddLootUsage"));
                    return true;
                }
                double chance;
                try {
                    chance = Double.parseDouble(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.InvalidChanceValue"));
                    return true;
                }
                int playerExp;
                try {
                    playerExp = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.InvalidPlayerExpValue"));
                    return true;
                }
                ItemStack copyLoot = new ItemStack(itemInHand);
                if (lootExists(copyLoot)){
                    player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.AlreadyAddedLoot"));
                    return false;
                }
                Loot loot = new Loot(copyLoot, chance, playerExp);
                ComposterPlus.loots.add(loot);
                ComposterPlus.database.insertLoot(loot);
                player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.LootAdded"));
                break;


            case "removecompost":
                if (itemInHand.getType() == Material.AIR){
                    player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.AttemptAir"));
                    return false;
                }
                if (removeCompost(itemInHand)) {
                    player.sendMessage(prefix + " Compost item removed successfully.");
                } else {
                    player.sendMessage(prefix + " No compost item found to remove.");
                }
                break;

            case "removeloot":
                if (itemInHand.getType() == Material.AIR){
                    player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.AttemptAir"));
                    return false;
                }
                if (removeLoot(itemInHand)) {
                    player.sendMessage(prefix + " Loot item removed successfully.");
                } else {
                    player.sendMessage(prefix + " No loot item found to remove.");
                }
                break;

            case "help":
                displayHelp(commandSender, prefix);
                break;

            default:
                player.sendMessage(prefix + ComposterPlus.plugin.getConfig().getString("Messages.UnknownSubCommand"));
                break;
        }
        return true;
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
