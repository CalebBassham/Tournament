package me.calebbassham.tournament;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

import static me.calebbassham.pluginmessageformat.PluginMessageFormat.getMainColorPallet;
import static me.calebbassham.pluginmessageformat.PluginMessageFormat.getPrefix;

public abstract class Tournament {

    private static HashSet<Arena> arenas = new HashSet<>();
    private static HashMap<String, Kit> kits = new HashMap<>();
    private static Tournament tournament;
    private static BukkitTask matchScheduler;

    private static Location spawnLocation;
    private static Location spectatorSpawnLocation;

    static Arena getAvailableArena() {
        return arenas.stream().filter(Arena::isAvailable).findAny().orElse(null);
    }

    public static Tournament getTournament() {
        return tournament;
    }

    static void setTournament(SingleEliminationTournament tourney) {
        tournament = tourney;
    }

    private static Location getSpawnLocation() {
        if (spawnLocation == null) {
            ConfigurationSection config = TournamentPlugin.instance.getConfig().getConfigurationSection("spawn_location");
            World world = Bukkit.getWorld(config.getString("world"));
            double x = config.getInt("x") + .5;
            double y = config.getDouble("y");
            double z = config.getInt("z") + .5;
            float yaw = config.getInt("yaw", 0);
            float pitch = config.getInt("pitch", 0);
            spawnLocation = new Location(world, x, y, z, yaw, pitch);
        }

        return spawnLocation;
    }

    static Location getSpectatorSpawnLocation() {
        if (spectatorSpawnLocation == null) {
            ConfigurationSection config = TournamentPlugin.instance.getConfig().getConfigurationSection("spectator_spawn_location");
            World world = Bukkit.getWorld(config.getString("world"));
            double x = config.getInt("x") + .5;
            double y = config.getDouble("y");
            double z = config.getInt("z") + .5;
            float yaw = config.getInt("yaw", 0);
            float pitch = config.getInt("pitch", 0);
            spectatorSpawnLocation = new Location(world, x, y, z, yaw, pitch);
        }

        return spectatorSpawnLocation;
    }


    public static void setSpawnLocation(Location spawnLocation) {
        Tournament.spawnLocation = spawnLocation;
    }

    public static void setSpectatorSpawnLocation(Location spectatorSpawnLocation) {
        Tournament.spectatorSpawnLocation = spectatorSpawnLocation;
    }

    static void loadArenasFromConfig() {
        HashSet<Arena> _arenas = new HashSet<>();
        ConfigurationSection config = TournamentPlugin.instance.getConfig().getConfigurationSection("arenas");

        for (String key : config.getKeys(false)) {
            try {
                ConfigurationSection arenaConfig = config.getConfigurationSection(key);
                World world = Bukkit.getWorld(arenaConfig.getString("world"));

                ConfigurationSection t1 = arenaConfig.getConfigurationSection("team_1_spawn_location");
                double x = t1.getInt("x") + .5;
                double y = t1.getDouble("y");
                double z = t1.getInt("z") + .5;
                float yaw = t1.getInt("yaw", 0);
                float pitch = t1.getInt("pitch", 0);

                Location t1Loc = new Location(world, x, y, z, yaw, pitch);

                ConfigurationSection t2 = arenaConfig.getConfigurationSection("team_2_spawn_location");
                x = t2.getInt("x") + .5;
                y = t2.getDouble("y");
                z = t2.getInt("z") + .5;
                yaw = t2.getInt("yaw", 0);
                pitch = t2.getInt("pitch", 0);

                Location t2Loc = new Location(world, x, y, z, yaw, pitch);

                _arenas.add(new Arena(t1Loc, t2Loc));
            } catch (Exception e) {
                TournamentPlugin.instance.getLogger().info(String.format("Failed to load arena \"%s\" from config.", key));
            }
        }

        arenas = _arenas;
    }

    static void loadKitsFromConfig() {
        ConfigurationSection kitsSection = TournamentPlugin.instance.getConfig().getConfigurationSection("kits");
        if (kitsSection == null) return;

        for (String key : kitsSection.getKeys(false)) {
            Kit kit = ConfigKit.from(kitsSection.getConfigurationSection(key));
            kits.put(key, kit);
        }
    }

    static Kit getKit(String name) {
        return kits.get(name);
    }

    static HashMap<String, Kit> getKits() {
        return (HashMap<String, Kit>) kits.clone();
    }

    static void start(Kit kit) throws IllegalStateException {
        if (tournament != null) {
            throw new IllegalStateException("A tournament is already running.");
        }

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players);

        tournament = new SingleEliminationTournament(players.stream().map(player -> new TournamentTeam(player.getUniqueId(), player.getName())).collect(Collectors.toCollection(ArrayDeque::new)), kit);

        Bukkit.broadcastMessage(getPrefix() + getMainColorPallet().getHighlightTextColor() + "Tournament" + getMainColorPallet().getPrimaryTextColor() +
                " is " + getMainColorPallet().getValueTextColor() + "starting now" + getMainColorPallet().getPrimaryTextColor() + ".");
        Bukkit.getOnlinePlayers()
                .forEach(player -> player.teleport(getSpectatorSpawnLocation()));

        matchScheduler = new MatchScheduler().runTaskTimer(TournamentPlugin.instance, 20, 3 * 20);
    }

    static void stop() {
        Bukkit.broadcastMessage(String.format(getPrefix() + getMainColorPallet().getHighlightTextColor() + "Tournament" +
                getMainColorPallet().getPrimaryTextColor() + " has " + getMainColorPallet().getHighlightTextColor() + "finished" +
                getMainColorPallet().getPrimaryTextColor() + ". The " + getMainColorPallet().getHighlightTextColor() + "winner" +
                getMainColorPallet().getPrimaryTextColor() + " is " + getMainColorPallet().getValueTextColor() + "%s" +
                getMainColorPallet().getPrimaryTextColor() + ".", tournament.getWinner().getName()));
        tournament = null;
        Bukkit.getOnlinePlayers()
                .forEach(player -> player.teleport(getSpawnLocation()));
        matchScheduler.cancel();
    }

    private final Kit kit;
    private TournamentTeam winner;
    private TournamentTeam loser;

    public Tournament(Kit kit) {
        this.kit = kit;
    }

    public static HashSet<Arena> getArenas() {
        return arenas;
    }

    public Kit getKit() {
        return kit;
    }

    public abstract TournamentTeam getWinner();

    public abstract TournamentMatch getNextMatch();
}
