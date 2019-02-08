package me.calebbassham.tournament;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

import static me.calebbassham.pluginmessageformat.PluginMessageFormat.getMainColorPallet;
import static me.calebbassham.pluginmessageformat.PluginMessageFormat.getPrefix;

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

    @EventHandler
    public void removeArrows(ProjectileHitEvent e) {
        final Projectile entity = e.getEntity();
        if (entity.getWorld() != Tournament.getSpectatorSpawnLocation().getWorld()) return;

        Entity shooter = (Entity) entity.getShooter();
        if (!tournament.isInMatch(shooter.getUniqueId())) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                entity.remove();
            }
        }.runTaskLater(TournamentPlugin.instance, 20 * 20);
    }

    @EventHandler
    public void arrowHealth(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Arrow)) return;
        Projectile arrow = (Arrow) e.getDamager();

        if (!(arrow.getShooter() instanceof Player)) return;
        Player shooter = (Player) arrow.getShooter();

        if (!(e.getEntity() instanceof Player)) return;
        Player victim = (Player) e.getEntity();

        if (!tournament.isInMatch(shooter.getUniqueId()) || !tournament.isInMatch(victim.getUniqueId())) return;

        double health = Math.max(0, (victim.getHealth() - e.getFinalDamage()) / 2);

        shooter.sendMessage(getPrefix() + getMainColorPallet().getHighlightTextColor() + victim.getDisplayName() + getMainColorPallet().getPrimaryTextColor() +
                " is now at " + getMainColorPallet().getValueTextColor() +
                new DecimalFormat("#.#").format(health) + ChatColor.RED + "♥" +
                getMainColorPallet().getExtraTextColor() + " (" + getMainColorPallet().getValueTextColor() +
                DecimalFormat.getPercentInstance().format(health/victim.getMaxHealth()) + getMainColorPallet().getExtraTextColor() +
                ")" + getMainColorPallet().getPrimaryTextColor() + ".");
    }

}
