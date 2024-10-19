import java.util.ArrayList;
import java.util.List;

public class GomokuState implements GameState {
    private static final int WHITE = 1;
    private static final int EMPTY = 0;
    private static final int BLACK = -1;

    private int[] board;
    private int clicks;
    private int player;

    public GomokuState() {
        board = new int[225];
        player = BLACK;
    }

    private GomokuState(GomokuState that) {
        board = that.board.clone();
        player = -that.player;
    }

    public GameState click(int id) {
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            GomokuState gomokuMove = (GomokuState)move;
            if (gomokuMove.clicks == id) {
                return gomokuMove;
            }
        }
        return this;
    }

    public GameImage draw() {
        GameImage image = new GameImage();
        image.fillTile(0, 0, "gomoku.png");
        for (int point = 0; point < 225; point++) {
            int row = point / 15;
            int col = point % 15;
            int x = 4 * col + 2;
            int y = 4 * row + 2;
            switch (board[point]) {
                case WHITE:
                    image.fillTile(x, y, "whitepiece.png");
                    break;
                case BLACK:
                    image.fillTile(x, y, "blackpiece.png");
            }
            image.addRegion(point, x, y, 4, 4);
        }
        return image;
    }

    public double evaluate() {
        double score = fiveInARow();
        if (score == 0) {
            score = twoInARow() / 512.0;
        }
        return score;
    }

    public int fiveInARow() {
        // horizontal rows
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col <= 10; col++) {
                int point = 15 * row + col;
                int sum = 0;
                for (int i = 0; i < 5; i++) {
                    sum += board[point + i];
                }
                if (sum == 5) {
                    return WHITE;
                } else if (sum == -5) {
                    return BLACK;
                }
            }
        }
        // vertical rows
        for (int row = 0; row <= 10; row++) {
            for (int col = 0; col < 15; col++) {
                int point = 15 * row + col;
                int sum = 0;
                for (int i = 0; i < 5; i++) {
                    sum += board[point + 15 * i];
                }
                if (sum == 5) {
                    return WHITE;
                } else if (sum == -5) {
                    return BLACK;
                }
            }
        }
        // \-diagonals
        for (int row = 0; row <= 10; row++) {
            for (int col = 0; col <= 10; col++) {
                int point = 15 * row + col;
                int sum = 0;
                for (int i = 0; i < 5; i++) {
                    sum += board[point + 16 * i];
                }
                if (sum == 5) {
                    return WHITE;
                } else if (sum == -5) {
                    return BLACK;
                }
            }
        }
        // /-diagonals
        for (int row = 0; row <= 10; row++) {
            for (int col = 4; col < 15; col++) {
                int point = 15 * row + col;
                int sum = 0;
                for (int i = 0; i < 5; i++) {
                    sum += board[point + 14 * i];
                }
                if (sum == 5) {
                    return WHITE;
                } else if (sum == -5) {
                    return BLACK;
                }
            }
        }
        return 0;
    }

    public List<GameState> generateMoves() {
        List<GameState> moves = new ArrayList<GameState>();
        if (fiveInARow() == 0) {
            for (int point = 0; point < 225; point++) {
                if (board[point] == EMPTY) {
                    GomokuState move = new GomokuState(this);
                    move.board[point] = player;
                    move.clicks = point;
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    public String getNotation() {
        String notation = "";
        notation += (char)(clicks % 15 + 97); // file
        notation += 15 - clicks / 15; // rank
        return notation;
    }

    public int getPlayer() {
        return player;
    }

    // This is for the benefit of computer players encouraging them to form and
    // defend against long rows.
    public int twoInARow() {
        int score = 0;
        // horizontal rows
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col <= 13; col++) {
                int point = 15 * row + col;
                int sum = 0;
                for (int i = 0; i < 2; i++) {
                    sum += board[point + i];
                }
                if (sum == 2) {
                    score++;
                } else if (sum == -2) {
                    score--;
                }
            }
        }
        // vertical rows
        for (int row = 0; row <= 13; row++) {
            for (int col = 0; col < 15; col++) {
                int point = 15 * row + col;
                int sum = 0;
                for (int i = 0; i < 2; i++) {
                    sum += board[point + i];
                }
                if (sum == 2) {
                    score++;
                } else if (sum == -2) {
                    score--;
                }
            }
        }
        // \-diagonals
        for (int row = 0; row <= 13; row++) {
            for (int col = 0; col <= 13; col++) {
                int point = 15 * row + col;
                int sum = 0;
                for (int i = 0; i < 2; i++) {
                    sum += board[point + i];
                }
                if (sum == 2) {
                    score++;
                } else if (sum == -2) {
                    score--;
                }
            }
        }
        // /-diagonals
        for (int row = 0; row <= 13; row++) {
            for (int col = 1; col < 15; col++) {
                int point = 15 * row + col;
                int sum = 0;
                for (int i = 0; i < 2; i++) {
                    sum += board[point + i];
                }
                if (sum == 2) {
                    score++;
                } else if (sum == -2) {
                    score--;
                }
            }
        }
        return score;
    }
}