package me.calebbassham.tournament;

import java.util.*;

public class RoundRobinTournament extends Tournament {

    // <Round, Matches<MatchNumber, Match>>
    private HashMap<Integer, HashMap<Integer, TournamentMatch>> rounds = new HashMap<>();

    private int totalRounds;
    private int matchesInRound;

    public RoundRobinTournament(ArrayDeque<TournamentTeam> teams, final Kit kit) {
        super(kit);

        if(teams.size() % 2 != 0) {
            teams.add(TournamentTeam.BYE);
        }

        totalRounds = teams.size() - 1;
        matchesInRound = teams.size() / 2;

        for (int round  = 1; round <= totalRounds; round++) {
            HashMap<Integer, TournamentMatch> roundMap = new HashMap<>();
            HashMap<Integer, TournamentMatch> prevRound = rounds.get(round - 1);
            this.rounds.put(round, roundMap);
            for (int match = 1; match <= matchesInRound; match++) {
                TournamentMatch tournamentMatch;
                if (round == 1) {
                    TournamentTeam team1 = teams.poll();
                    TournamentTeam team2 = teams.pollLast();
                    tournamentMatch = new TournamentMatch(round, match, team1, team2);
                } else {
                    TournamentTeam team1;
                    TournamentTeam team2;

                    if (match == 1) {
                        team1 = prevRound.get(1).getTeam1();
                    } else if (match == 2) {
                        team1 = prevRound.get(1).getTeam2();
                    } else {
                        team1 = prevRound.get(match - 1).getTeam1();
                    }

                    if (match == matchesInRound) {
                        team2 = prevRound.get(matchesInRound).getTeam1();
                    } else {
                        team2 = prevRound.get(match + 1).getTeam2();
                    }

                    tournamentMatch = new TournamentMatch(round, match, team1, team2);
                }

                roundMap.put(match, tournamentMatch);
            }
        }

    }

    @Override
    public TournamentTeam getWinner() {
        if (rounds.get(totalRounds).get(matchesInRound).getWinner() == null) return null;

        HashMap<TournamentTeam, Integer> wins = new HashMap<>();

        for (TournamentMatch match : getMatches()) {
            TournamentTeam winner = match.getWinner();
            if (winner.isBye()) continue;
            wins.put(winner, wins.getOrDefault(winner, 0) + 1);
        }

        TournamentTeam winner = null;
        int totalWins = -1;

        for (TournamentTeam team : wins.keySet()) {
            int teamsWins = wins.getOrDefault(team, 0);
            if (teamsWins > totalWins) {
                winner = team;
                totalWins = teamsWins;
            }
        }

        return winner;
    }

    @Override
    public TournamentMatch getNextMatch() {
        for (int round  = 1; round <= totalRounds; round++) {
            HashMap<Integer, TournamentMatch> roundMap = rounds.get(round);
            for (int match = 1; match <= matchesInRound; match++) {
                TournamentMatch tournamentMatch = roundMap.get(match);
                if (tournamentMatch.isInProgress()) continue;
                if (tournamentMatch.getWinner() == null) return tournamentMatch;
            }
        }

        return null;
    }

    @Override
    public boolean isInMatch(UUID player) {
        return getMatches().stream()
                .filter(TournamentMatch::isInProgress)
                .map(TournamentMatch::getParticipants)
                .flatMap(Arrays::stream)
                .anyMatch(uuid -> uuid.equals(player));
    }

    private List<TournamentMatch> getMatches() {
        ArrayList<TournamentMatch> matches = new ArrayList<>();
        for (int round  = 1; round <= totalRounds; round++) {
            HashMap<Integer, TournamentMatch> roundMap = rounds.get(round);
            for (int match = 1; match <= matchesInRound; match++) {
                TournamentMatch tournamentMatch = roundMap.get(match);
                matches.add(tournamentMatch);
            }
        }
        return matches;
    }



}
