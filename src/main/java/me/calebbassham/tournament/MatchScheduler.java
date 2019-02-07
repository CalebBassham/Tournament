package me.calebbassham.tournament;

import org.bukkit.scheduler.BukkitRunnable;

public class MatchScheduler extends BukkitRunnable {

    @Override
    public void run() {

        Arena arena = Tournament.getAvailableArena();
        if (arena == null) return;

        TournamentMatch match = Tournament.getTournament().getNextMatch();
        if (match == null) {
            if (Tournament.getTournament().getWinner() != null) {
                Tournament.stop();
            }
            return;
        }

        new MatchRunner(match, arena).start();
    }
}
