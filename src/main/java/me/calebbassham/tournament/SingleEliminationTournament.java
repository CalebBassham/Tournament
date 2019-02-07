package me.calebbassham.tournament;


import java.util.*;
import java.util.stream.Collectors;

public class SingleEliminationTournament extends Tournament {

    private TournamentMatch masterMatch;
    private final int rounds;

    public SingleEliminationTournament(ArrayDeque<TournamentTeam> teams, Kit kit) {
        super(kit);
        padTeams(teams);

        rounds = (int) Util.log2(teams.size());

        ArrayDeque<TournamentMatch> prevRound = new ArrayDeque<>();
        ArrayDeque<TournamentMatch> currentRound = new ArrayDeque<>();
        for (int round = 1; round <= rounds; round++) {
            int matches = (int) Math.pow(2, rounds - round + 1);
            for (int match = 1; match <= matches / 2; match++) {
                TournamentMatch tournamentMatch;

                if (prevRound.isEmpty()) {
                    tournamentMatch = new TournamentMatch(round, match, teams.poll(), teams.poll(), null, null);
                } else {
                    TournamentMatch child1 = prevRound.poll();
                    TournamentMatch child2 = prevRound.poll();
                    tournamentMatch = new TournamentMatch(round, match, null, null, child1, child2);
                    child1.setParent(tournamentMatch);
                    child2.setParent(tournamentMatch);
                }

                currentRound.add(tournamentMatch);
                if (round == rounds) {
                    masterMatch = tournamentMatch;
                }
            }

            prevRound = currentRound.clone();
            currentRound.clear();
        }

    }

    private void padTeams(ArrayDeque<TournamentTeam> teams) {
        if (teams.size() == 2) return;
        if (teams.size() == 1) {
            teams.add(TournamentTeam.BYE);
            return;
        }

        if (teams.size() % 2 != 0) {
            teams.add(TournamentTeam.BYE);
        }

        while (true) {
            if (Util.log2(teams.size()) % 1 == 0) {
                break;
            }
            teams.add(TournamentTeam.BYE);
            teams.add(TournamentTeam.BYE);
        }
    }

    public TournamentMatch getNextMatch() {
        ArrayList<TournamentMatch> matches = getMatches();

        for (int round = 1; round <= rounds; round++) {
            final int r = round;
            TournamentMatch match = matches.stream()
                    .filter(m -> m.getRound() == r)
                    .filter(m -> m.getWinner() == null)
                    .filter(m -> !m.isInProgress())
                    .filter(m -> m.getTeam1() != null)
                    .filter(m -> m.getTeam2() != null)
                    .findFirst()
                    .orElse(null);
            if (match != null) return match;
        }

        return null;
    }

    public ArrayList<TournamentMatch> getMatches() {
        return getMatches(masterMatch);
    }

    private ArrayList<TournamentMatch> getMatches(TournamentMatch match) {
        return getMatches(match, new ArrayList<>());
    }

    private ArrayList<TournamentMatch> getMatches(TournamentMatch match, ArrayList<TournamentMatch> matches) {
        matches.add(match);

        if (match.getChild1() != null) {
            getMatches(match.getChild1(), matches);
        }

        if (match.getChild2() != null) {
            getMatches(match.getChild2(), matches);
        }

        return matches;
    }

    public TournamentTeam getWinner() {
        return masterMatch.getWinner();
    }

    public boolean isInMatch(UUID player) {
        return getMatches().stream()
                .filter(TournamentMatch::isInProgress)
                .map(TournamentMatch::getParticipants)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList())
                .contains(player);

    }

}
