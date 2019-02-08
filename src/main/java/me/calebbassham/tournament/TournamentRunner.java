package me.calebbassham.tournament;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class TournamentRunner implements Listener {

    private Tournament tournament;

    public TournamentRunner(Tournament tournament) {
        this.tournament = tournament;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().setGameMode(GameMode.SPECTATOR);
        e.getPlayer().teleport(Tournament.getSpectatorSpawnLocation());

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(e.getPlayer());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld() != Tournament.getSpectatorSpawnLocation().getWorld()) return;
        if (tournament.isInMatch(player.getUniqueId())) return;
        if (player.getGameMode() != GameMode.SPECTATOR) return;

        Location location = e.getTo();

        if (location.getBlockX() < -1594 || location.getBlockX() > -1495) {
            e.setCancelled(true);
        } else if (location.getY() > 110 || location.getY() < 60) {
            e.setCancelled(true);
        } else if (location.getBlockZ() < -405 || location.getBlockZ() > -307) {
            e.setCancelled(true);
        }
    }

}