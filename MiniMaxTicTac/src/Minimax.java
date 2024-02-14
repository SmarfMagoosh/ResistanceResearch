public class Minimax {

    public static void main(String[] args) {
        Minimax m = new Minimax();
        Board b = new Board();
        System.out.println(b);
        //b.board[0][0] = 'X';
        //b.board[0][1] = 'X';
        System.out.println(m.play(true, b));
    }
    public int play(boolean max, Board b) {
        int best;
        if (max) {
            best = -1;
        } else {
            best = 1;
        }
        // Handle draws
        if (full(b)) {
            return 0;
        }
        // Main minimax
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // Skip full locations
                if (b.board[i][j] != ' ') {
                    continue;
                }
                if (max) {
                    Board next = new Board(b);
                    next.board[i][j] = 'X';
                    System.out.println(next);
                    if (isWin(next, 'X')) {
                        return 1;
                    } else {
                        best = Math.max(best, play(false, next));
                    }
                } else {
                    Board next = new Board(b);
                    next.board[i][j] = 'O';
                    System.out.println(next);
                    if (isWin(next, 'O')) {
                        return -1;
                    }
                    best = Math.min(best, play(true, next));
                }
            }
        }
        return best;
    }

    boolean isWin(Board b, char c) {
        for (int i = 0; i < 3; i++) {
            if (b.board[i][0] == c && b.board[i][1] == c && b.board[i][2] == c){
                return true;
            }
            if (b.board[0][i] == c && b.board[1][i] == c && b.board[2][i] == c){
                return true;
            }
        }

        if (b.board[0][0] == c && b.board[1][1] == c && b.board[2][2] == c){
            return true;
        }

        if (b.board[0][2] == c && b.board[1][1] == c && b.board[2][0] == c){
            return true;
        }
        return false;
    }

    boolean full(Board b) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (b.board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
}
