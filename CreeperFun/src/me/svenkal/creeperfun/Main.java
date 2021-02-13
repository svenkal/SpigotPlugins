package me.svenkal.creeperfun;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main plugin;

    public static Main getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {

        // singleton init
        plugin = this;

        // save config to file and write defaults
        getConfig().options().copyDefaults(true);
        saveConfig();

        // load reload command
        getCommand("creeperfunreload").setExecutor(new ReloadCommand());

        // load event listener
        Bukkit.getPluginManager().registerEvents(new CreeperListener(), this);
    }

    @Override
    public void onDisable() {
        // cleanup singleton
        plugin = null;
    }

}
