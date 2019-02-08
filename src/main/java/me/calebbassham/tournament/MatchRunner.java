package me.calebbassham.tournament;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.calebbassham.pluginmessageformat.PluginMessageFormat.getMainColorPallet;
import static me.calebbassham.pluginmessageformat.PluginMessageFormat.getPrefix;

public class MatchRunner implements Listener {

    private final TournamentMatch match;
    private final Arena arena;
    private ArrayList<UUID> dead = new ArrayList<>();

    public MatchRunner(final TournamentMatch match, final Arena arena) {
        this.match = match;
        this.arena = arena;
    }

    public void start() {
        match.setInProgress(true);

        if (match.getTeam1().isBye()) {
            match.setWinner(match.getTeam2());
            stopBye();
            return;
        }

        if (match.getTeam2().isBye()) {
            match.setWinner(match.getTeam1());
            stopBye();
            return;
        }

        if (!hasOnlinePlayer(getTeam1Players())) {
            match.setWinner(match.getTeam2());
            stopOffline();
            return;
        }

        if (!hasOnlinePlayer(getTeam2Players())) {
            match.setWinner(match.getTeam1());
            stopOffline();
            return;
        }

        arena.setAvailable(false);

        Arrays.stream(getTeam1Players())
                .forEach(player -> player.teleport(arena.getSpawn1()));

        Arrays.stream(getTeam2Players())
                .forEach(player -> player.teleport(arena.getSpawn2()));

        Arrays.stream(getParticipants())
                .forEach(player -> {
                    player.setGameMode(GameMode.ADVENTURE);
                    resetPlayer(player);
                    Tournament.getTournament().getKit().equipPlayer(player);
                });

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Player participant : getParticipants()) {
                player.showPlayer(participant);
            }
        }

        Bukkit.broadcastMessage(getPrefix() + getMainColorPallet().getHighlightTextColor() + "Round " + getMainColorPallet().getValueTextColor() +
                match.getRound() + getMainColorPallet().getHighlightTextColor() + " Match " + getMainColorPallet().getValueTextColor() + match.getMatch() +
                getMainColorPallet().getExtraTextColor() + " – " + getMainColorPallet().getHighlightTextColor() + match.getTeam1().getName() +
                getMainColorPallet().getPrimaryTextColor() + " v " + getMainColorPallet().getHighlightTextColor() + match.getTeam2().getName());

        Bukkit.getPluginManager().registerEvents(this, TournamentPlugin.instance);
    }

    public void stopOffline() {
        Bukkit.broadcastMessage(getPrefix() + getMainColorPallet().getHighlightTextColor() + "Round " + getMainColorPallet().getValueTextColor() +
                match.getRound() + getMainColorPallet().getHighlightTextColor() + " Match " + getMainColorPallet().getValueTextColor() + match.getMatch() +
                getMainColorPallet().getExtraTextColor() + " – " + getMainColorPallet().getValueTextColor() + match.getWinner().getName() +
                getMainColorPallet().getPrimaryTextColor() + " has " + getMainColorPallet().getHighlightTextColor() + "won" +
                getMainColorPallet().getPrimaryTextColor() + " because " + getMainColorPallet().getValueTextColor() + match.getLoser().getName() +
                getMainColorPallet().getPrimaryTextColor() + " is " + getMainColorPallet().getHighlightTextColor() + "offline" +
                getMainColorPallet().getPrimaryTextColor() + ".");

//        Bukkit.broadcastMessage(String.format("{pre}{m.h} Round {m.v}%s {m.h}Match {m.v}%s {m.e}- {m.v}%s {m.p}has {m.h}won {m.p}because {m.v}%s {m.p}is {m.h}offline{m.p}.",
//                match.getRound(), match.getMatch(), match.getWinner().getName(), match.getLoser().getName()));
        cleanup();
    }

    private void stopBye() {
        Bukkit.broadcastMessage(getPrefix() + getMainColorPallet().getHighlightTextColor() + "Round " + getMainColorPallet().getValueTextColor() +
                match.getRound() + getMainColorPallet().getHighlightTextColor() + " Match " + getMainColorPallet().getValueTextColor() + match.getMatch() +
                getMainColorPallet().getExtraTextColor() + " – " + getMainColorPallet().getValueTextColor() + match.getWinner().getName() +
                getMainColorPallet().getPrimaryTextColor() + " has a " + getMainColorPallet().getHighlightTextColor() + "bye"
                + getMainColorPallet().getPrimaryTextColor() + " this round.");
        cleanup();
    }

    private void stop() {
        Bukkit.broadcastMessage(getPrefix() + getMainColorPallet().getHighlightTextColor() + "Round " + getMainColorPallet().getValueTextColor() +
                match.getRound() + getMainColorPallet().getHighlightTextColor() + " Match " + getMainColorPallet().getValueTextColor() + match.getMatch() +
                getMainColorPallet().getExtraTextColor() + " – " + getMainColorPallet().getValueTextColor() + match.getWinner().getName() + getMainColorPallet().getPrimaryTextColor()
                + " has " + getMainColorPallet().getHighlightTextColor() + "defeated " + getMainColorPallet().getValueTextColor() + match.getLoser().getName() +
                getMainColorPallet().getPrimaryTextColor() + ".");

        Arrays.stream(getParticipants())
                .forEach(player -> {
                    resetPlayer(player);
                    player.setGameMode(GameMode.SPECTATOR);
                    player.teleport(Tournament.getSpectatorSpawnLocation());
                });

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Player participant : getParticipants()) {
                player.hidePlayer(participant);
            }
        }

        cleanup();
    }

    private void resetPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.resetMaxHealth();
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
    }

    private void cleanup() {
        HandlerList.unregisterAll(this);
        arena.setAvailable(true);
        match.setInProgress(false);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!Arrays.stream(getParticipants()).collect(Collectors.toList()).contains(e.getEntity())) return;

        e.setDeathMessage(null);
        e.getDrops().clear();

        UUID player = e.getEntity().getUniqueId();

        // Shortcut
        dead.add(player);

        checkForWinner();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // shortcut
        dead.add(e.getPlayer().getUniqueId());

        checkForWinner();
    }

    private void checkForWinner() {
        if (getTeam1Remaining().length == 0) {
            match.setWinner(match.getTeam2());
            stop();
        }

        if (getTeam2Remaining().length == 0) {
            match.setWinner(match.getTeam1());
            stop();
        }
    }

    private Player[] getParticipants() {
        return Stream.of(match.getTeam1().getPlayers(), match.getTeam2().getPlayers())
                .flatMap(Arrays::stream)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toArray(Player[]::new);
    }

    private Player[] getTeam1Players() {
        return Arrays.stream(match.getTeam1().getPlayers())
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toArray(Player[]::new);
    }

    private Player[] getTeam2Players() {
        return Arrays.stream(match.getTeam2().getPlayers())
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toArray(Player[]::new);
    }

    private void sendMessageToParticipants(String message) {
        Arrays.stream(getParticipants()).forEach(player -> player.sendMessage(message));
    }

    private UUID[] getTeam1Remaining() {
        return Stream.of(match.getTeam1().getPlayers()).filter(uuid -> !dead.contains(uuid)).toArray(UUID[]::new);
    }

    private UUID[] getTeam2Remaining() {
        return Stream.of(match.getTeam2().getPlayers()).filter(uuid -> !dead.contains(uuid)).toArray(UUID[]::new);
    }

    private boolean hasOnlinePlayer(Player[] players) {
        return Arrays.stream(getTeam1Players()).anyMatch(Player::isOnline);
    }

}
