import java.util.ArrayList;

public class Team {
    public ArrayList<Player> players;

    public Team() {
        players = new ArrayList<>();
    }

    @Override
    public String toString() {
        String result = "[";
        for (int i = 0; i < players.size() - 1; i++) {
            result += players.get(i).num + ", ";
        }
        if (players.size() > 0) {
            result += players.get(players.size() - 1).num + "]";
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Team other)) {
            return false;
        }
        if (players.size() != other.players.size()) {
            return false;
        }
        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).equals(other.players.get(i))) {
                return false;
            }
        }
        return true;
    }
}
