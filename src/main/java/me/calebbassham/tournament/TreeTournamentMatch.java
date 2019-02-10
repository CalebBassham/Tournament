package me.calebbassham.tournament;

public class TreeTournamentMatch extends TournamentMatch {

    private TreeTournamentMatch parent;

    private final TreeTournamentMatch child1;
    private final TreeTournamentMatch child2;

    public TreeTournamentMatch(int round, int match, TournamentTeam team1, TournamentTeam team2, TreeTournamentMatch child1, TreeTournamentMatch child2) {
        super(round, match, team1, team2);
        this.child1 = child1;
        this.child2 = child2;
    }

    public TreeTournamentMatch getChild1() {
        return child1;
    }

    public TreeTournamentMatch getChild2() {
        return child2;
    }

    public TreeTournamentMatch getParent() {
        return parent;
    }

    public void setParent(TreeTournamentMatch parent) {
        this.parent = parent;
    }

    @Override
    public void setWinner(TournamentTeam winner) {
        if (parent != null) {
            if (parent.getTeam1() == null) {
                parent.setTeam1(winner);
            } else {
                parent.setTeam2(winner);
            }
        }

        super.setWinner(winner);
    }

}
