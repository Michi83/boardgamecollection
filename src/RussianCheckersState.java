import java.util.ArrayList;
import java.util.List;

public class RussianCheckersState implements GameState {
    private static final int WK = 2; // white king
    private static final int WM = 1; // white man
    private static final int EM = 0; // empty
    private static final int BM = -1; // black man
    private static final int BK = -2; // black king
    private static final int CP = -3; // captured piece
    private static final int LV = -4; // lava
    private static final int[] KING_OFFSETS = new int[] { -11, -9, 9, 11 };
    private static final int[] MAN_OFFSETS = new int[] { -11, -9 };

    private int[] board;
    private List<Integer> clicks;
    private int player;
    private List<Integer> userClicks;

    public RussianCheckersState() {
        board = new int[] {
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
            LV, EM, BM, EM, BM, EM, BM, EM, BM, LV,
            LV, BM, EM, BM, EM, BM, EM, BM, EM, LV,
            LV, EM, BM, EM, BM, EM, BM, EM, BM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, WM, EM, WM, EM, WM, EM, WM, EM, LV,
            LV, EM, WM, EM, WM, EM, WM, EM, WM, LV,
            LV, WM, EM, WM, EM, WM, EM, WM, EM, LV,
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
        };
        player = 1;
    }

    private RussianCheckersState(RussianCheckersState that) {
        board = that.board.clone();
        player = that.player;
    }

    private boolean capturable(int square) {
        return board[square] > CP && player * board[square] < EM;
    }

    public GameState click(int id) {
        userClicks.add(id);
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            RussianCheckersState checkersMove = (RussianCheckersState)move;
            if (userClicks.size() <= checkersMove.clicks.size()) {
                boolean match = true;
                for (int i = 0; i < userClicks.size(); i++) {
                    if ((int)userClicks.get(i)
                            != (int)checkersMove.clicks.get(i)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    if (userClicks.size() == checkersMove.clicks.size()) {
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
        image.fillTile(0, 0, "chess.png");
        for (int square = 12; square <= 87; square++) {
            int row = square / 10 - 1;
            int col = square % 10 - 1;
            int x = 6 * col + 8;
            int y = 6 * row + 8;
            switch (board[square]) {
            case WK:
                image.fillTile(x + 1, y + 1, "whiteking.png");
                break;
            case WM:
                image.fillTile(x + 1, y + 1, "whitepiece.png");
                break;
            case BK:
                image.fillTile(x + 1, y + 1, "blackking.png");
                break;
            case BM:
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
            return -player;
        }
        double score = 0;
        for (int square = 12; square <= 87; square++) {
            switch (board[square]) {
            case WK:
                score += 2;
                break;
            case WM:
                score += 1;
                break;
            case BK:
                score -= 2;
                break;
            case BM:
                score -= 1;
                break;
            }
        }
        return score / 32;
    }

    private void generateCaptures(List<GameState> moves) {
        for (int origin = 12; origin <= 87; origin++) {
            switch (player * board[origin]) {
            case WK:
                generateKingCaptures(origin, moves);
                break;
            case WM:
                generateManCaptures(origin, moves);
                break;
            }
        }
        for (GameState move : moves) {
            ((RussianCheckersState)move).removeCapturedPieces();
        }
    }

    private void generateKingCaptures(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int capture = origin + offset;
            while (board[capture] == EM) {
                capture += offset;
            }
            if (capturable(capture)) {
                int target = capture + offset;
                // One thing that isn't well explained in the rules: After
                // capturing, a king must land on a square that allows another
                // capture, if possible. Only if no such square exists, may the
                // king land on any empty square. At least that's how it's
                // played on Lidraughts. In International Checkers this is
                // automatically enforced by the rule that the maximum number
                // of pieces must be captured.
                int countBefore = moves.size();
                while (board[target] == EM) {
                    RussianCheckersState move = makeMove(origin, target,
                        capture);
                    move.generateKingCaptures(target, moves);
                    target += offset;
                }
                if (moves.size() == countBefore) {
                    target = capture + offset;
                    while (board[target] == EM) {
                        RussianCheckersState move = makeMove(origin, target,
                            capture);
                        moves.add(move);
                        target += offset;
                    }
                }
            }
        }
    }

    private void generateKingNonCaptures(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int target = origin + offset;
            while (board[target] == EM) {
                RussianCheckersState move = makeMove(origin, target);
                moves.add(move);
                target += offset;
            }
        }
    }

    private void generateManCaptures(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int capture = origin + offset;
            int target = capture + offset;
            if (capturable(capture) && board[target] == EM) {
                RussianCheckersState move = makeMove(origin, target, capture);
                int countBefore = moves.size();
                if (promotes(target)) {
                    move.board[target] = player * WK;
                    move.generateKingCaptures(target, moves);
                } else {
                    move.generateManCaptures(target, moves);
                }
                if (moves.size() == countBefore) {
                    moves.add(move);
                }
            }
        }
    }

    private void generateManNonCaptures(int origin, List<GameState> moves) {
        for (int offset : MAN_OFFSETS) {
            int target = origin + player * offset;
            if (board[target] == EM) {
                RussianCheckersState move = makeMove(origin, target);
                if (promotes(target)) {
                    move.board[target] = player * WK;
                }
                moves.add(move);
            }
        }
    }

    public List<GameState> generateMoves() {
        List<GameState> moves = new ArrayList<GameState>();
        generateCaptures(moves);
        if (moves.size() == 0) {
            generateNonCaptures(moves);
        }
        for (GameState move : moves) {
            ((RussianCheckersState)move).player *= -1;
        }
        return moves;
    }

    private void generateNonCaptures(List<GameState> moves) {
        for (int origin = 12; origin <= 87; origin++) {
            switch (player * board[origin]) {
            case WK:
                generateKingNonCaptures(origin, moves);
                break;
            case WM:
                generateManNonCaptures(origin, moves);
                break;
            }
        }
    }

    public String getNotation() {
        String notation = "";
        for (int square : clicks) {
            notation += (char)(square % 10 + 96);
            notation += 9 - square / 10;
        }
        return notation;
    }

    public int getPlayer() {
        return player;
    }

    private RussianCheckersState makeMove(int origin, int target) {
        RussianCheckersState move = new RussianCheckersState(this);
        move.board[origin] = EM;
        move.board[target] = board[origin];
        move.clicks = new ArrayList<Integer>();
        move.clicks.add(origin);
        move.clicks.add(target);
        return move;
    }

    private RussianCheckersState makeMove(int origin, int target, int capture) {
        RussianCheckersState move = new RussianCheckersState(this);
        move.board[origin] = EM;
        move.board[target] = board[origin];
        move.board[capture] = CP;
        move.clicks = new ArrayList<Integer>();
        if (clicks.get(clicks.size() - 1) == origin) {
            move.clicks.addAll(clicks);
        } else {
            move.clicks.add(origin);
        }
        move.clicks.add(target);
        return move;
    }

    private boolean promotes(int square) {
        if (player == 1) {
            return square / 10 == 1;
        } else {
            return square / 10 == 8;
        }
    }

    private void removeCapturedPieces() {
        for (int square = 12; square <= 87; square++) {
            if (board[square] == CP) {
                board[square] = EM;
            }
        }
    }
}