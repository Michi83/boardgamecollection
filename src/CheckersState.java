// Overview of implemented checkers variants.
// What size is the board?
// - English: 8x8, 12 pieces per side.
// - International: 10x10, 20 pieces per side.
// - Russian: 8x8, 12 pieces per side.
// - Turkish: 8x8, 16 pieces per side.
// Who moves first?
// - English: Black.
// - International: White.
// - Russian: White.
// - Turkish: White.
// How do pieces move in general?
// - English: Diagonally.
// - International: Diagonally.
// - Russian: Diagonally.
// - Turkish: Straight.
// Can men capture backwards?
// - English: No.
// - International: Yes.
// - Russian: Yes.
// - Turkish: No.
// When are captured pieces removed in a multi-capture sequence?
// - English: At the end of the sequence.
// - International: At the end of the sequence.
// - Russian: At the end of the sequence.
// - Turkish: After every jump.
// What if there's more than one capture opportunity or more than one way to
// continue a multi-capture sequence?
// - English: Player may choose.
// - International: Player must capture the greatest possible number of pieces.
// - Russian: Player may choose.
// - Turkish: Player must capture the greatest possible number of pieces.
// What happens, when a man captures, lands on the last row, and can capture
// again?
// - English: Never happens, but see next question.
// - International: Man doesn't get promoted and continues to capture.
// - Russian: Man gets promoted and continues to capture as a king.
// - Turkish: Man doesn't get promoted and continues to capture. But since all
//   remaining captures will be sideways, the man will get promoted in the end.
// What happens, when a man captures, lands on the last row, gets promoted, and
// can immediately capture as a king?
// - English: Move ends after promotion.
// - International: Move ends after promotion.
// - Russian: Move continues, king captures.
// - Turkish: Move ends after promotion.
import java.util.ArrayList;
import java.util.List;

public class CheckersState implements GameState {
    private static final int WK = 2; // white king
    private static final int WM = 1; // white man
    private static final int EM = 0; // empty
    private static final int BM = -1; // black man
    private static final int BK = -2; // black king
    private static final int LV = -3; // lava
    private static final int[] KING_OFFSETS = new int[] { -11, -9, 9, 11 };
    private static final int[] MAN_OFFSETS = new int[] { -11, -9 };

    private int[] board;
    private List<Integer> clicks;
    private int player;
    private List<Integer> userClicks;

    public CheckersState() {
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
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV
        };
        player = -1;
    }

    private CheckersState(CheckersState that) {
        board = that.board.clone();
        player = that.player;
    }

    private boolean capturable(int square) {
        return board[square] != LV && player * board[square] < 0;
    }

    public GameState click(int id) {
        userClicks.add(id);
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            CheckersState checkersMove = (CheckersState)move;
            if (userClicks.size() <= checkersMove.clicks.size()) {
                boolean match = true;
                for (int i = 0; i < userClicks.size(); i++) {
                    // Yes, we have to cast from Integer to int, otherwise the
                    // != operator may not work properly. This is the result of
                    // an hour of bug hunting.
                    if ((int)userClicks.get(i)
                            != (int)checkersMove.clicks.get(i)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    if (userClicks.size() == checkersMove.clicks.size()) {
                        return checkersMove; // full match
                    } else {
                        return this; // partial match
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
                score++;
                break;
            case BK:
                score -= 2;
                break;
            case BM:
                score--;
                break;
            }
        }
        return score / 32;
    }

    private List<GameState> generateCaptures() {
        List<GameState> moves = new ArrayList<GameState>();
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
        return moves;
    }

    private void generateKingCaptures(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int capture = origin + offset;
            int target = capture + offset;
            if (capturable(capture) && board[target] == EM) {
                CheckersState move = makeMove(origin, target, capture);
                // multi-captures
                int countBefore = moves.size();
                move.generateKingCaptures(target, moves);
                if (moves.size() == countBefore) {
                    // no multi-captures found
                    moves.add(move);
                }
            }
        }
    }

    private void generateKingNonCaptures(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int target = origin + offset;
            if (board[target] == EM) {
                CheckersState move = makeMove(origin, target);
                moves.add(move);
            }
        }
    }

    private void generateManCaptures(int origin, List<GameState> moves) {
        for (int offset : MAN_OFFSETS) {
            int capture = origin + player * offset;
            int target = capture + player * offset;
            if (capturable(capture) && board[target] == EM) {
                CheckersState move = makeMove(origin, target, capture);
                if (promotes(target)) {
                    move.board[target] = player * WK;
                    moves.add(move);
                    continue; // no multi-captures after promotion
                }
                // multi-captures
                int countBefore = moves.size();
                move.generateManCaptures(target, moves);
                if (moves.size() == countBefore) {
                    // no multi-captures found
                    moves.add(move);
                }
            }
        }
    }

    private void generateManNonCaptures(int origin, List<GameState> moves) {
        for (int offset : MAN_OFFSETS) {
            int target = origin + player * offset;
            if (board[target] == EM) {
                CheckersState move = makeMove(origin, target);
                if (promotes(target)) {
                    move.board[target] = player * WK;
                }
                moves.add(move);
            }
        }
    }

    public List<GameState> generateMoves() {
        List<GameState> moves = generateCaptures();
        if (moves.size() == 0) {
            moves = generateNonCaptures();
        }
        for (GameState move : moves) {
            ((CheckersState)move).player *= -1;
        }
        return moves;
    }

    private List<GameState> generateNonCaptures() {
        List<GameState> moves = new ArrayList<GameState>();
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
        return moves;
    }

    public String getNotation() {
        String notation = "";
        for (int click : clicks) {
            notation += (char)(click % 10 + 96); // file
            notation += 9 - click / 10; // rank
        }
        return notation;
    }

    public int getPlayer() {
        return player;
    }

    private CheckersState makeMove(int origin, int target) {
        CheckersState move = new CheckersState(this);
        move.board[origin] = EM;
        move.board[target] = board[origin];
        move.clicks = new ArrayList<Integer>();
        move.clicks.add(origin);
        move.clicks.add(target);
        return move;
    }

    private CheckersState makeMove(int origin, int target, int capture) {
        CheckersState move = new CheckersState(this);
        move.board[origin] = EM;
        move.board[target] = board[origin];
        move.board[capture] = EM;
        move.clicks = new ArrayList<Integer>();
        // For single captures the clicks list contains the origin and the
        // target, for multi-captures also all the targets in between. Here is
        // a little trick to distinguish the two cases: If this move's origin
        // is the last move's target, they are part of the same multi-capture
        // sequence.
        if (origin == clicks.get(clicks.size() - 1)) {
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
}