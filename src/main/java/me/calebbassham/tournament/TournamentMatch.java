package me.calebbassham.tournament;

import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

public class TournamentMatch {

    private final int round;
    private final int match;

    private TournamentTeam team1;
    private TournamentTeam team2;

    private TournamentTeam winner;

    private TournamentMatch parent;

    private final TournamentMatch child1;
    private final TournamentMatch child2;

    private boolean inProgress = false;

    public TournamentMatch(int round, int match, TournamentTeam team1, TournamentTeam team2, TournamentMatch child1, TournamentMatch child2) {
        this.round = round;
        this.match = match;
        this.team1 = team1;
        this.team2 = team2;
        this.child1 = child1;
        this.child2 = child2;
    }

    public void setTeam1(TournamentTeam team1) {
        this.team1 = team1;
    }

    public void setTeam2(TournamentTeam team2) {
        this.team2 = team2;
    }

    public int getRound() {
        return round;
    }

    public int getMatch() {
        return match;
    }

    public TournamentTeam getTeam1() {
        return team1;
    }

    public TournamentTeam getTeam2() {
        return team2;
    }

    @Deprecated
    public UUID getPlayer1() {
        return team1.getPlayers().length == 1 ? team1.getPlayers()[0] : null;
    }

    @Deprecated
    public UUID getPlayer2() {
        return team2.getPlayers().length == 1 ? team2.getPlayers()[0] : null;
    }

    public TournamentMatch getChild1() {
        return child1;
    }

    public TournamentMatch getChild2() {
        return child2;
    }

    public TournamentMatch getParent() {
        return parent;
    }

    public void setParent(TournamentMatch parent) {
        this.parent = parent;
    }

    public TournamentTeam getWinner() {
        return winner;
    }

    public void setWinner(TournamentTeam winner) {
        if (parent != null) {
            if (parent.getTeam1() == null) {
                parent.setTeam1(winner);
            } else {
                parent.setTeam2(winner);
            }
        }

        this.winner = winner;
    }

    public TournamentTeam getLoser() {
        return winner == team1 ? team2 : team1;
    }

    boolean isInProgress() {
        return inProgress;
    }

    void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public UUID[] getParticipants() {
        return Stream.of(team1.getPlayers(), team2.getPlayers())
                .flatMap(Arrays::stream)
                .toArray(UUID[]::new);
    }

}
