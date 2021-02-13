package me.svenkal.sleep;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // register this class as event listener
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // empty
    }

    @EventHandler
    private void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Player p = event.getPlayer();
        World w = event.getBed().getWorld();
        long time = event.getBed().getWorld().getTime();

        if(time >= 12550 || w.isThundering()) {
            // clear weather and start new day
            w.setStorm(false);
            w.setThundering(false);
            w.setTime(0);

            // inform players who slept
            for(Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(ChatColor.GREEN + p.getDisplayName() + " hat die Nacht übersprungen!");
            }
        }
    }
}
