package me.svenkal.creeperfun;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // label is command string ($0)

        if (sender instanceof Player && !sender.hasPermission("creeperfun.reload") && !sender.isOp()) {
            sender.sendMessage(ChatColor.DARK_RED + "[CreeperFun]: You do not have permissions to use this command!");
            return true; // return false == show usage information
        }

        // handles console senders too
        Main.getInstance().reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "[CreeperFun]: Configuration reloaded!");
        return true;

    }
}
