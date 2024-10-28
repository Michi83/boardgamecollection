import java.util.ArrayList;
import java.util.List;

public class TablutState implements GameState {
    private static final int KI = 2; // king
    private static final int SW = 1; // Swede
    private static final int EM = 0; // empty
    private static final int MU = -1; // Muscovite
    private static final int LV = -2; // lava
    private static final int[] OFFSETS = new int[] { -11, -1, 1, 11 };

    private int[] board;
    private int[] clicks;
    private int king;
    private int player;
    private List<Integer> userClicks;

    public TablutState() {
        board = new int[] {
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
            LV, EM, EM, EM, MU, MU, MU, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, MU, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, SW, EM, EM, EM, EM, LV,
            LV, MU, EM, EM, EM, SW, EM, EM, EM, MU, LV,
            LV, MU, MU, SW, SW, KI, SW, SW, MU, MU, LV,
            LV, MU, EM, EM, EM, SW, EM, EM, EM, MU, LV,
            LV, EM, EM, EM, EM, SW, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, MU, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, MU, MU, MU, EM, EM, EM, LV,
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV
        };
        king = 60;
        player = MU;
    }

    private TablutState(TablutState that) {
        board = that.board.clone();
        king = that.king;
        player = -that.player;
    }

    private void applySpecialRules(TablutState move, int target) {
        // Special rules only apply to moves by the Muscovites.
        if (player == SW) {
            return;
        }
        if (king == 60 && move.board[49] == MU && move.board[59] == MU
                && move.board[61] == MU && move.board[71] == MU) {
            // The king in the castle can only be captured by four Muscovites.
            move.board[60] = LV;
        } else if (king == 60 && board[49] == SW && board[59] == MU
                && board[61] == MU && board[71] == MU && target == 38) {
            // When the king in the castle is surrounded by three Muscovites
            // and a Swede, the Swede can be captured against the king.
            move.board[49] = EM;
        } else if (king == 60 && board[49] == MU && board[59] == SW
                && board[61] == MU && board[71] == MU && target == 58) {
            move.board[59] = EM;
        } else if (king == 60 && board[49] == MU && board[59] == MU
                && board[61] == SW && board[71] == MU && target == 62) {
            move.board[61] = EM;
        } else if (king == 60 && board[49] == MU && board[59] == MU
                && board[61] == MU && board[71] == SW && target == 82) {
            move.board[71] = EM;
        } else if (king == 49 && move.board[38] == MU && move.board[48] == MU
                && move.board[50] == MU && (target == 38 || target == 48
                || target == 50)) {
            // When the king is standing right next to the castle, he can only
            // be captured by three Muscovites.
            move.board[49] = EM;
        } else if (king == 59 && move.board[48] == MU && move.board[58] == MU
                && move.board[70] == MU && (target == 48 || target == 58
                || target == 70)) {
            move.board[59] = EM;
        } else if (king == 61 && move.board[50] == MU && move.board[62] == MU
                && move.board[72] == MU && (target == 50 || target == 62
                || target == 72)) {
            move.board[61] = EM;
        } else if (king == 71 && move.board[70] == MU && move.board[72] == MU
                && move.board[82] == MU && (target == 70 || target == 72
                || target == 82)) {
            move.board[71] = EM;
        }
    }

    private boolean assistsInCapture(int square) {
        // friendly pieces and empty castle
        return movable(square) || square == 60 && board[square] != KI;
    }

    private boolean capturable(int square) {
        // The king is not capturable in the usual way if he is in the castle
        // or right next to it.
        if (square == king && (square == 49 || square == 59 || square == 60
                || square == 61 || square == 71)) {
            return false;
        }
        return board[square] != LV && player * board[square] < EM;
    }

    public GameState click(int id) {
        userClicks.add(id);
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            TablutState tablutMove = (TablutState)move;
            if (userClicks.size() <= tablutMove.clicks.length) {
                boolean match = true;
                for (int i = 0; i < userClicks.size(); i++) {
                    if (userClicks.get(i) != tablutMove.clicks[i]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    if (userClicks.size() == tablutMove.clicks.length) {
                        return move;
                    } else {
                        return this;
                    }
                }
            }
        }
        userClicks.clear();
        return this;
    }

    public GameImage draw() {
        if (userClicks == null) {
            userClicks = new ArrayList<Integer>();
        }
        GameImage image = new GameImage();
        image.fillTile(0, 0, "tablut.png");
        for (int square = 12; square <= 108; square++) {
            int row = square / 11 - 1;
            int col = square % 11 - 1;
            int x = 6 * col + 5;
            int y = 6 * row + 5;
            switch (board[square]) {
            case KI:
                image.fillTile(x + 1, y + 1, "whiteking.png");
                break;
            case SW:
                image.fillTile(x + 1, y + 1, "whitepiece.png");
                break;
            case MU:
                image.fillTile(x + 1, y + 1, "blackpiece.png");
                break;
            }
            if (userClicks.contains(square)) {
                image.fillTile(x, y, "selection.png");
            }
            image.addRegion(square, x, y, 6, 6);
        }
        return image;
    }

    public double evaluate() {
        if (generateMoves().size() == 0) {
            if (kingCaptured()) {
                return MU;
            } else if (kingEscaped()) {
                return SW;
            } else {
                // I don't know if stalemates can occur, but just in case I let
                // them count as a loss for the player unable to move.
                return -player;
            }
        }
        int score = 0;
        for (int square = 12; square <= 108; square++) {
            switch (board[square]) {
            case SW:
                score += 2;
                break;
            case MU:
                score--;
                break;
            }
        }
        return score / 32.0;
    }

    public List<GameState> generateMoves() {
        List<GameState> moves = new ArrayList<GameState>();
        if (kingCaptured() || kingEscaped()) {
            return moves;
        }
        for (int origin = 12; origin <= 108; origin++) {
            if (movable(origin)) {
                for (int offset : OFFSETS) {
                    int target = origin + offset;
                    while (board[target] == EM) {
                        TablutState move = makeMove(origin, target);
                        moves.add(move);
                        target += offset;
                    }
                }
            }
        }
        return moves;
    }

    public String getNotation() {
        String notation = "";
        notation += (char)(clicks[0] % 11 + 96);
        notation += 10 - clicks[0] / 11;
        notation += (char)(clicks[1] % 11 + 96);
        notation += 10 - clicks[1] / 11;
        return notation;
    }

    public int getPlayer() {
        return player;
    }

    private boolean kingCaptured() {
        return board[king] != KI;
    }

    private boolean kingEscaped() {
        int row = king / 11 - 1;
        int col = king % 11 - 1;
        return row == 0 || row == 8 || col == 0 || col == 8;
    }

    private TablutState makeMove(int origin, int target) {
        TablutState move = new TablutState(this);
        move.board[origin] = EM;
        move.board[target] = board[origin];
        // if the king leaves the castle it is replaced by lava
        if (origin == 60) {
            move.board[60] = LV;
        }
        // remove captured pieces
        for (int offset : OFFSETS) {
            int square1 = target + offset;
            int square2 = square1 + offset;
            if (capturable(square1) && assistsInCapture(square2)) {
                move.board[square1] = EM;
            }
        }
        applySpecialRules(move, target);
        // update king position
        if (origin == king) {
            move.king = target;
        }
        move.clicks = new int[] { origin, target };
        return move;
    }

    private boolean movable(int square) {
        return board[square] != LV && player * board[square] > EM;
    }
}