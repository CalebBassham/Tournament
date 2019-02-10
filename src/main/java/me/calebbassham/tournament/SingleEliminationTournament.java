package me.calebbassham.tournament;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class SingleEliminationTournament extends Tournament {

    private TreeTournamentMatch masterMatch;
    private final int rounds;

    public SingleEliminationTournament(ArrayDeque<TournamentTeam> teams, Kit kit) {
        super(kit);
        padTeams(teams);

        rounds = (int) Util.log2(teams.size());

        ArrayDeque<TreeTournamentMatch> prevRound = new ArrayDeque<>();
        ArrayDeque<TreeTournamentMatch> currentRound = new ArrayDeque<>();
        for (int round = 1; round <= rounds; round++) {
            int matches = (int) Math.pow(2, rounds - round + 1);
            for (int match = 1; match <= matches / 2; match++) {
                TreeTournamentMatch tournamentMatch;

                if (prevRound.isEmpty()) {
                    tournamentMatch = new TreeTournamentMatch(round, match, teams.poll(), teams.poll(), null, null);
                } else {
                    TreeTournamentMatch child1 = prevRound.poll();
                    TreeTournamentMatch child2 = prevRound.pollLast();
                    tournamentMatch = new TreeTournamentMatch(round, match, null, null, child1, child2);
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

    @Override
    public TreeTournamentMatch getNextMatch() {
        ArrayList<TreeTournamentMatch> matches = getMatches();

        for (int round = 1; round <= rounds; round++) {
            final int r = round;
            TreeTournamentMatch match = matches.stream()
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

    public ArrayList<TreeTournamentMatch> getMatches() {
        return getMatches(masterMatch);
    }

    private ArrayList<TreeTournamentMatch> getMatches(TreeTournamentMatch match) {
        return getMatches(match, new ArrayList<>());
    }

    private ArrayList<TreeTournamentMatch> getMatches(TreeTournamentMatch match, ArrayList<TreeTournamentMatch> matches) {
        matches.add(match);

        if (match.getChild1() != null) {
            getMatches(match.getChild1(), matches);
        }

        if (match.getChild2() != null) {
            getMatches(match.getChild2(), matches);
        }

        return matches;
    }

    @Override
    public TournamentTeam getWinner() {
        return masterMatch.getWinner();
    }

    @Override
    public boolean isInMatch(UUID player) {
        return getMatches().stream()
                .filter(TreeTournamentMatch::isInProgress)
                .map(TreeTournamentMatch::getParticipants)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList())
                .contains(player);

    }

}
