import java.util.ArrayList;

public class Minimax {

    public static void main(String[] args) {
        Game g = new Game(5);
        g.play(true);
    }

    public static ArrayList<Team> possibleTeams(int size, ArrayList<Player> players) {
        int[] indices = new int[size];
        for (int i = 0; i < size; i++) {
            indices[i] = i;
        }
        ArrayList<Team> teams = new ArrayList<>();
        while (true) {
            // Add a team
            Team nextTeam = new Team();
            for (int i = 0; i < size; i++) {
                nextTeam.players.add(players.get(indices[i]));
            }
            teams.add(nextTeam);
            // Update indices
            for (int i = size - 1; i >= 0; i--) {
                indices[i]++;
                // This index is good. (The earlier the index, the earlier it stops being good.)
                if (indices[i] <= players.size() + i - size) {
                    // Move all the later indices to trail after it. (If this is the last index,
                    // nothing changes)
                    for (int j = i + 1; j < size; j++) {
                        indices[j] = indices[j - 1] + 1;
                    }
                    // Now we've got good indices; add the next team
                    break;
                }
                // This index ran off the end. Move down to the next index.
                else {
                    // ... unless this is index 0, in which case we're just done
                    if (i == 0) {
                        return teams;
                    }
                }
            }
        }

    }

    public static boolean isSubteam(Team small, Team big) {
        if (small.players.size() > big.players.size()) {
            return false;
        }
        for (int i = 0; i < small.players.size(); i++) {
            boolean found = false;
            for (int j = 0; j < big.players.size(); j++) {
                if (small.players.get(i).equals(big.players.get(j))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param teams
     * @return
     */
    public static ArrayList<Team> filterTeams(ArrayList<Team> teams, Game g) {
        ArrayList<Team> filteredTeams = new ArrayList<>();
        for (Team currentTeam : teams) {
            boolean safe = true;
            for (int i = 0; i < currentTeam.players.size(); i++) {
                if (currentTeam.players.get(i).knownSpy) {
                    safe = false;
                    break;
                }
            }
            if (!safe) {
                continue;
            }
            for (Team badTeam : g.badTeams) {
                if (isSubteam(badTeam, currentTeam)) {
                    safe = false;
                    break;
                }
            }
            if (safe) {
                filteredTeams.add(currentTeam);
            }
        }
        return filteredTeams;
    }

    /**
     * PARTIAL IMPLEMENTATION; ALWAYS TRUE AND FALSE
     * 
     * @param t
     * @param g
     * @return
     */
    public static ArrayList<Boolean> getPossibleVotes(Team t, Game g) {
        ArrayList<Boolean> results = new ArrayList<>();
        results.add(true);
        results.add(false);
        return results;
    }

    /**
     * There's a temporary assumption here: votes always go both ways
     * 
     * @param index
     * @param g
     * @param chatty
     * @return
     */
    public static int branchTeamChoice(int index, Game g, boolean max, boolean chatty) {

        ArrayList<Team> teams = possibleTeams(g.missions[index].numPlayers, g.players);
        teams = filterTeams(teams, g);
        for (Team currentTeam : teams) {
            boolean votePassed = false;
            int voteRounds = 0;
            while (!votePassed) {
                if (chatty) {
                    System.out.println("Picked team " + currentTeam);
                }
                ArrayList<Boolean> votes = getPossibleVotes(currentTeam, g);
                for (Boolean vote : votes) {
                    if (vote) {
                        System.out.println("Vote " + (votePassed ? "passed" : "failed"));
                        branchMissions(index, g, currentTeam, chatty, max);
                    } else {
                        voteRounds++;
                        if (voteRounds >= 5) {
                            return -1;
                        }
                        g.nextLeader();
                        if (chatty) {
                            System.out.println("Next leader is " + g.leader);
                        }
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Ugh, this needs rework - what we need is a list of *possible* spy
     * affiliations, filtered down as we work our way down the tree.
     * 
     * @param t
     * @return
     */
    public static ArrayList<Boolean> getMissionOutcomes(Team t, Game g) {
        ArrayList<Boolean> outcomes = new ArrayList<>();
        outcomes.add(true);
        for (Player p : t.players) {
            if (g.possibleSpies.contains(p)) {
                outcomes.add(false);
                break;
            }
        }
        return outcomes;
    }

    public static int branchMissions(int index, Game g, Team currentTeam, boolean chatty, boolean max) {
        ArrayList<Boolean> outcomes = getMissionOutcomes(currentTeam);
        if (outcomes.size() == 1) {
            g.missions[index].succeeded = outcomes.get(0);
            return assessVictory(index, g, chatty);
        }

        for (Boolean outcome : outcomes) {
            g.missions[index].succeeded = outcome;
            if (chatty) {
                System.out.println("Mission " + (g.missions[index].succeeded ? "succeeded" : "failed"));
            }
            int victory = assessVictory(index, g, chatty);
            // There are two outcomes possible. If either is an outright loss (for the
            // current player), report that.
            if (max && victory == -1) {
                return -1;
            } else if (!max && victory == 1) {
                return 1;
            }
            // Otherwise the game is still in play and branches further.
            index++;
            g.nextLeader();
            branchTeamChoice(index, g, true, chatty);
        }
        return 0;
    }

    public static int assessVictory(int index, Game g, boolean chatty) {
        int successCount = 0;
        int failCount = 0;
        for (int j = 0; j <= index; j++) {
            if (g.missions[j].succeeded) {
                successCount++;
            } else {
                failCount++;
            }
        }
        if (successCount >= 3) {
            if (chatty) {
                System.out.println("Three successful missions, victory!");
            }
            return 1;
        }
        if (failCount >= 3) {
            if (chatty) {
                System.out.println("Three failed missions, defeat!");
            }
            return -1;
        }
        return 0;
    }

    public int miniPlay(boolean chatty) {
        Game g = new Game(5);
        boolean won = false;
        branchTeamChoice(0, g, true, chatty);
        return 0;
    }

}
