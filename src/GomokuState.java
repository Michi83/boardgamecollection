import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

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

    public GameState click(int x, int y) {
        int row = (y - 32) / 64;
        int col = (x - 32) / 64;
        int point = 15 * row + col;
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            GomokuState gomokuMove = (GomokuState)move;
            if (point == gomokuMove.clicks) {
                return gomokuMove;
            }
        }
        return this;
    }

    public void draw(Graphics2D graphics) {
        try {
            Image gomoku = ImageIO.read(new File("img/png/gomoku.png"));
            Image whitepiece = ImageIO.read(new File(
                "img/png/whitepiece.png"));
            Image blackpiece = ImageIO.read(new File(
                "img/png/blackpiece.png"));
            graphics.drawImage(gomoku, 0, 0, null);
            for (int point = 0; point < 225; point++) {
                int row = point / 15;
                int col = point % 15;
                int x = 64 * col + 32;
                int y = 64 * row + 32;
                switch (board[point]) {
                    case WHITE:
                        graphics.drawImage(whitepiece, x, y, null);
                        break;
                    case BLACK:
                        graphics.drawImage(blackpiece, x, y, null);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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

    public int getPlayer() {
        return player;
    }

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