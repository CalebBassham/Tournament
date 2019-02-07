package me.calebbassham.tournament;

import java.util.UUID;

public class TournamentTeam {

    public static TournamentTeam BYE = new TournamentTeam();

    private final UUID[] players;
    private final String name;

    public TournamentTeam(UUID[] players, String name) {
        this.players = players;
        this.name = name;
    }

    public TournamentTeam(UUID player, String name) {
        this.players = new UUID[]{player};
        this.name = name;
    }

    public TournamentTeam() {
        this.players = null;
        this.name = "BYE";
    }

    public UUID[] getPlayers() {
        return players;
    }

    public boolean isBye() {
        if (this == BYE) return true;
        if (players == null) return true;
        return players.length == 0;
    }

    public String getName() {
        return name;
    }
}
