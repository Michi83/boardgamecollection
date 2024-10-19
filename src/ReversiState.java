import java.util.ArrayList;
import java.util.List;

public class ReversiState implements GameState {
    private static final int WH = 1; // white
    private static final int EM = 0; // empty
    private static final int BL = -1; // black
    private static final int LV = -2; // lava
    private static final int[] OFFSETS = new int[] {
        -11, -10, -9, -1, 1, 9, 10, 11 };

    private int[] board;
    private int clicks;
    private int player;

    public ReversiState() {
        board = new int[] {
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, WH, BL, EM, EM, EM, LV,
            LV, EM, EM, EM, BL, WH, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV
        };
        player = BL;
    }

    public ReversiState(ReversiState that) {
        board = that.board.clone();
        player = -that.player;
    }

    public GameState click(int id) {
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            ReversiState reversiMove = (ReversiState)move;
            // If the player must pass, any click will return the passing move.
            if (reversiMove.clicks == id || reversiMove.clicks == -1) {
                return move;
            }
        }
        return this;
    }

    public GameImage draw() {
        GameImage image = new GameImage();
        image.fillTile(0, 0, "reversi.png");
        for (int square = 11; square <= 88; square++) {
            int row = square / 10 - 1;
            int col = square % 10 - 1;
            int x = 6 * col + 8;
            int y = 6 * row + 8;
            switch (board[square]) {
                case WH:
                    image.fillTile(x + 1, y + 1, "whitepiece.png");
                    break;
                case BL:
                    image.fillTile(x + 1, y + 1, "blackpiece.png");
            }
            image.addRegion(square, x, y, 6, 6);
        }
        return image;
    }

    public double evaluate() {
        int score = 0;
        for (int square = 11; square <= 88; square++) {
            if (board[square] != LV) {
                score += board[square];
            }
        }
        if (generateMoves().size() == 0) {
            if (score > 0) {
                return WH;
            } else if (score < 0) {
                return BL;
            } else {
                return 0;
            }
        } else {
            return score / 64;
        }
    }

    private int flipCapturedPieces(ReversiState move, int square) {
        int count = 0;
        for (int offset : OFFSETS) {
            int target = square + offset;
            while (move.board[target] == -player) {
                target += offset;
            }
            if (move.board[target] == player) {
                target -= offset;
                while (target != square) {
                    move.board[target] = player;
                    count++;
                    target -= offset;
                }
            }
        }
        return count;
    }

    public List<GameState> generateMoves() {
        List<GameState> moves = generateRegularMoves();
        // If a player can't move, the player must pass. If the other player
        // can't move either, the game ends.
        if (moves.size() == 0) {
            ReversiState move = new ReversiState(this);
            move.clicks = -1;
            if (move.generateRegularMoves().size() > 0) {
                moves.add(move);
            }
        }
        return moves;
    }

    public List<GameState> generateRegularMoves() {
        List<GameState> moves = new ArrayList<GameState>();
        for (int square = 11; square <= 88; square++) {
            if (board[square] == EM) {
                ReversiState move = new ReversiState(this);
                move.board[square] = player;
                move.clicks = square;
                int count = flipCapturedPieces(move, square);
                if (count > 0) {
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    public String getNotation() {
        if (clicks == -1) {
            return "pass";
        }
        String notation = "";
        notation += (char)(clicks % 10 + 96);
        notation += 9 - clicks / 10;
        return notation;
    }

    public int getPlayer() {
        return player;
    }
}