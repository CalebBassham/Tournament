package me.calebbassham.tournament;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.calebbassham.pluginmessageformat.PluginMessageFormat.*;

public class TournamentCmd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("start")) {
                Tournament.Type type;
                try {
                    type = Tournament.Type.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(getErrorPrefix() + "The tournament type " + getErrorColorPallet().getValueTextColor() +
                            args[1].toUpperCase() + getErrorColorPallet().getPrimaryTextColor() + " is " + getErrorColorPallet().getHighlightTextColor() +
                            "invalid" + getErrorColorPallet().getPrimaryTextColor() + ".");
                    return true;
                }

                Kit kit = Tournament.getKit(args[2]);
                if (kit == null) {
                    sender.sendMessage(getErrorPrefix() + "The kit " + getErrorColorPallet().getValueTextColor() + args[1] +
                            getErrorColorPallet().getPrimaryTextColor() + " does not exist.");
                    return true;
                }

                Tournament.start(type, kit);
                return true;
            }

            if (args[0].equalsIgnoreCase("config")) {
                if (args[1].equalsIgnoreCase("spawn_location")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(getErrorPrefix() + "Only players can use this command.");
                        return true;
                    }

                    ConfigurationSection config = TournamentPlugin.instance.getConfig().getConfigurationSection("spawn_location");
                    Location loc = ((Player) sender).getLocation();
                    config.set("world", loc.getWorld().getName());
                    config.set("x", loc.getBlockX());
                    config.set("y", loc.getBlockY());
                    config.set("z", loc.getBlockZ());
                    config.set("yaw", (int) loc.getYaw());
                    config.set("pitch", (int) loc.getPitch());

                    TournamentPlugin.instance.saveConfig();

                    sender.sendMessage(getPrefix() + "Updated the config section spawn_location to your location.");

                    return true;
                }

                if (args[1].equalsIgnoreCase("spectator_spawn_location")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(getErrorPrefix() + "Only players can use this command.");
                        return true;
                    }

                    ConfigurationSection config = TournamentPlugin.instance.getConfig().getConfigurationSection("spectator_spawn_location");
                    Location loc = ((Player) sender).getLocation();
                    config.set("world", loc.getWorld().getName());
                    config.set("x", loc.getBlockX());
                    config.set("y", loc.getBlockY());
                    config.set("z", loc.getBlockZ());
                    config.set("yaw", (int) loc.getYaw());
                    config.set("pitch", (int) loc.getPitch());

                    TournamentPlugin.instance.saveConfig();

                    sender.sendMessage(getPrefix() + "Updated the config section spectator_spawn_location to your location.");

                    return true;
                }
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("config")) {
                if (args[1].equalsIgnoreCase("arenas")) {
                    if (!args[3].equalsIgnoreCase("team_1_spawn_location") && !args[3].equalsIgnoreCase("team_2_spawn_location"))
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(getErrorPrefix() + "Only players can use this command.");
                            return true;
                        }

                    if (!TournamentPlugin.instance.getConfig().isConfigurationSection("arenas")) {
                        TournamentPlugin.instance.getConfig().createSection("arenas").createSection(args[2]);
                    }

                    if (!TournamentPlugin.instance.getConfig().getConfigurationSection("arenas").isConfigurationSection(args[2])) {
                        TournamentPlugin.instance.getConfig().getConfigurationSection("arenas").createSection(args[2]);
                    }

                    if (!TournamentPlugin.instance.getConfig().getConfigurationSection("arenas").getConfigurationSection(args[2]).isConfigurationSection(args[3].toLowerCase())) {
                        TournamentPlugin.instance.getConfig().getConfigurationSection("arenas").getConfigurationSection(args[2]).createSection(args[3].toLowerCase());
                    }

                    TournamentPlugin.instance.saveConfig();

                    ConfigurationSection config = TournamentPlugin.instance.getConfig().getConfigurationSection("arenas").getConfigurationSection(args[2]);
                    Location loc = ((Player) sender).getLocation();
                    World world = loc.getWorld();
                    config.set("world", world.getName());

                    ConfigurationSection team = config.getConfigurationSection(args[3].toLowerCase());
                    team.set("x", loc.getBlockX());
                    team.set("y", loc.getBlockY());
                    team.set("z", loc.getBlockZ());
                    team.set("yaw", (int) loc.getYaw());
                    team.set("pitch", (int) loc.getPitch());

                    TournamentPlugin.instance.saveConfig();

                    if (Tournament.getTournament() == null) Tournament.loadArenasFromConfig();

                    sender.sendMessage(getPrefix() + "Updated the config section.");
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Stream.of("config", "start")
                    .filter(s -> s.startsWith(args[0]))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {
                return Arrays.stream(Tournament.Type.values())
                        .map(Enum::name)
                        .filter(s -> s.startsWith(args[1]))
                        .collect(Collectors.toList());
            }

            if (args[0].equalsIgnoreCase("config")) {
                return Stream.of("arenas", "spectator_spawn_location", "spawn_location")
                        .filter(s -> s.startsWith(args[1]))
                        .collect(Collectors.toList());
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("start")) {
                return Tournament.getKits().keySet().stream()
                        .filter(s -> s.startsWith(args[2]))
                        .collect(Collectors.toList());
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("config")) {
                if (args[1].equalsIgnoreCase("arenas")) {
                    return Stream.of("team_1_spawn_location", "team_2_spawn_location")
                            .filter(s -> s.startsWith(args[3]))
                            .collect(Collectors.toList());
                }
            }
        }

        return new ArrayList<>();
    }
}
