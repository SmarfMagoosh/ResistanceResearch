import java.sql.Array;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    public ArrayList<Player> players;
    public int numResistance;
    public int numSpies;
    public Mission[] missions;

    public int leader;

    public ArrayList<Team> badTeams;
    public ArrayList<Team> possibleSpyCombos;
    public ArrayList<Integer> possibleSpies;

    public Game(int numPlayers) {
        players = new ArrayList<>();
        badTeams = new ArrayList<>();
        leader = 0;
        // Assume player 0 is not a spy.
        for (int i = 1; i < numPlayers; i++) {
            possibleSpies.add(i);
        }

        if (numPlayers == 5) {
            numResistance = 3;
            numSpies = 2;

            // Add players
            for (int i = 0; i < numPlayers; i++) {
                if (i < numResistance) {
                    players.add(new Player(i));
                }
                else {
                    players.add(new Player(i));
                }
            }

            ArrayList<Player> listWithoutFirst = new ArrayList<Player>(players.subList(1, players.size() - 1));
            // Find possible spy combos
            possibleSpyCombos = Minimax.possibleTeams(numSpies, listWithoutFirst);

            missions = new Mission[5];
            missions[0] = new Mission(2);
            missions[1] = new Mission(3);
            missions[2] = new Mission(2);
            missions[3] = new Mission(3);
            missions[4] = new Mission(3);
        }
        else {
            System.out.println("Player count not handled.");
        }

    }

    public void nextLeader() {
        if (players.size() == 0) {
            return;
        }
        leader = (leader + 1) % players.size();
    }

    public boolean missionSucceeded(boolean[] missionActions) {
        for (boolean missionAction : missionActions) {
            if (!missionAction) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return -1 if the game is lost, or 1 if it is won
     */
    public int play(boolean chatty) {
        boolean won = false;
        for (int i = 0; i < missions.length; i++) {
            boolean votePassed = false;
            int voteRounds = 0;
            Team currentTeam = null;
            while (!votePassed) {
                currentTeam = pickTeam(missions[i].numPlayers);
                if (chatty) {
                    System.out.println("Picked team " + currentTeam);
                }
                votePassed = vote(currentTeam);
                if (chatty) {
                    System.out.println("Vote " + (votePassed ? "passed" : "failed"));
                }
                if (!votePassed) {
                    voteRounds++;
                    if (voteRounds >= 5) {
                        return -1;
                    }
                    nextLeader();
                    if (chatty) {
                        System.out.println("Next leader is " + leader);
                    }
                }
            }
            boolean[] missionActions = getMissionActions(currentTeam);
            missions[i].succeeded = missionSucceeded(missionActions);
            if (chatty) {
                System.out.println("Mission " + (missions[i].succeeded ? "succeeded" : "failed"));
            }
            int successCount = 0;
            int failCount = 0;
            for (int j = 0; j <= i; j++) {
                if (missions[j].succeeded) {
                    successCount++;
                }
                else {
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
        }
        if (chatty) {
            System.out.println("ERROR - NO VICTORY STATUS");
        }
        return 0;
    }

    public Team pickTeam(int numPlayers) {
        ArrayList<Integer> options = new ArrayList<Integer>(players.size());
        for (int i = 0; i < players.size(); i++ ) {
            options.add(i);
        }
        Team t = new Team();
        Random rand = new Random();
        for (int i = 0; i < numPlayers; i++) {
            int next = rand.nextInt(options.size());
            t.players.add(players.get(options.get(next)));
            options.remove(next);
        }
        return t;
    }

    public boolean vote(Team t) {

        return true;
    }

    /**
     * NOT IMPLEMENTED
     * @param t
     * @return
     */
    public boolean[] getMissionActions(Team t) {
        boolean[] results = new boolean[t.players.size()];
        for (int i = 0; i < results.length; i++) {
            //results[i] = t.players.get(i).isResistance;
            results[i] = true;
        }
        return results;
    }
}
