package me.calebbassham.tournament;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public class TournamentPlugin extends JavaPlugin {

    static JavaPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        Tournament.loadArenasFromConfig();
        Tournament.loadKitsFromConfig();

        registerCommand("tournament", new TournamentCmd());
    }

    private void registerCommand(String name, CommandExecutor cmd) {
        PluginCommand command = getCommand(name);
        command.setExecutor(cmd);
        if (cmd instanceof TabCompleter) command.setTabCompleter((TabCompleter) cmd);
    }

}
