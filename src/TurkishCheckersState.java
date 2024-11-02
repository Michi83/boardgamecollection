import java.util.ArrayList;
import java.util.List;

public class TurkishCheckersState implements GameState {
    private static final int WK = 2; // white king
    private static final int WM = 1; // white man
    private static final int EM = 0; // empty
    private static final int BM = -1; // black man
    private static final int BK = -2; // black king
    private static final int LV = -3; // lava
    private static final int[] KING_OFFSETS = new int[] { -10, -1, 1, 10 };
    private static final int[] MAN_OFFSETS = new int[] { -10, -1, 1 };

    private int[] board;
    private List<Integer> clicks;
    private int player;
    private List<Integer> userClicks;

    public TurkishCheckersState() {
        board = new int[] {
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, BM, BM, BM, BM, BM, BM, BM, BM, LV,
            LV, BM, BM, BM, BM, BM, BM, BM, BM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, WM, WM, WM, WM, WM, WM, WM, WM, LV,
            LV, WM, WM, WM, WM, WM, WM, WM, WM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
        };
        player = 1;
    }

    private TurkishCheckersState(TurkishCheckersState that) {
        board = that.board.clone();
        player = that.player;
    }

    private boolean capturable(int square) {
        return board[square] != LV && player * board[square] < EM;
    }

    public GameState click(int id) {
        userClicks.add(id);
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            TurkishCheckersState checkersMove = (TurkishCheckersState)move;
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

    private int countRemainingPieces() {
        int count = 0;
        for (int square = 11; square <= 88; square++) {
            if (capturable(square)) {
                count++;
            }
        }
        return count;
    }

    public GameImage draw() {
        if (userClicks == null) {
            userClicks = new ArrayList<Integer>();
        }
        GameImage image = new GameImage();
        image.fillTile(0, 0, "turkishcheckers.png");
        for (int square = 11; square <= 88; square++) {
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
        for (int origin = 11; origin <= 88; origin++) {
            switch (player * board[origin]) {
            case WK:
                generateKingCaptures(origin, moves);
                break;
            case WM:
                generateManCaptures(origin, moves);
                break;
            }
        }
        int topScore = Integer.MAX_VALUE;
        List<GameState> topMoves = new ArrayList<GameState>();
        for (GameState move : moves) {
            int score = ((TurkishCheckersState)move).countRemainingPieces();
            if (score < topScore) {
                topScore = score;
                topMoves.clear();
                topMoves.add(move);
            } else if (score == topScore) {
                topMoves.add(move);
            }
        }
        return topMoves;
    }

    private void generateKingCaptures(int origin, List<GameState> moves) {
        generateKingCaptures(origin, moves, 0);
    }

    private void generateKingCaptures(int origin, List<GameState> moves,
            int forbiddenOffset) {
        for (int offset : KING_OFFSETS) {
            // A king must not turn 180 degrees in an multi-capture sequence.
            // In most variants this is ruled out automatically by the rule
            // that captured pieces remain on the board until the sequence is
            // over and must not be jumped again. But in Turkish checkers
            // pieces are removed after every jump.
            if (offset == forbiddenOffset) {
                continue;
            }
            int capture = origin + offset;
            while (board[capture] == EM) {
                capture += offset;
            }
            if (capturable(capture)) {
                int target = capture + offset;
                while (board[target] == EM) {
                    TurkishCheckersState move = makeMove(origin, target,
                        capture);
                    int countBefore = moves.size();
                    move.generateKingCaptures(target, moves, -offset);
                    if (moves.size() == countBefore) {
                        moves.add(move);
                    }
                    target += offset;
                }
            }
        }
    }

    private void generateKingNonCaptures(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int target = origin + offset;
            while (board[target] == EM) {
                TurkishCheckersState move = makeMove(origin, target);
                moves.add(move);
                target += offset;
            }
        }
    }

    private void generateManCaptures(int origin, List<GameState> moves) {
        for (int offset : MAN_OFFSETS) {
            int capture = origin + player * offset;
            int target = capture + player * offset;
            if (capturable(capture) && board[target] == EM) {
                TurkishCheckersState move = makeMove(origin, target, capture);
                int countBefore = moves.size();
                move.generateManCaptures(target, moves);
                if (moves.size() == countBefore) {
                    if (promotes(target)) {
                        move.board[target] = player * WK;
                    }
                    moves.add(move);
                }
            }
        }
    }

    private void generateManNonCaptures(int origin, List<GameState> moves) {
        for (int offset : MAN_OFFSETS) {
            int target = origin + player * offset;
            if (board[target] == EM) {
                TurkishCheckersState move = makeMove(origin, target);
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
            ((TurkishCheckersState)move).player *= -1;
        }
        return moves;
    }

    private List<GameState> generateNonCaptures() {
        List<GameState> moves = new ArrayList<GameState>();
        for (int origin = 11; origin <= 88; origin++) {
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

    private TurkishCheckersState makeMove(int origin, int target) {
        TurkishCheckersState move = new TurkishCheckersState(this);
        move.board[origin] = EM;
        move.board[target] = board[origin];
        move.clicks = new ArrayList<Integer>();
        move.clicks.add(origin);
        move.clicks.add(target);
        return move;
    }

    private TurkishCheckersState makeMove(int origin, int target,
            int capture) {
        TurkishCheckersState move = new TurkishCheckersState(this);
        move.board[origin] = EM;
        move.board[target] = board[origin];
        move.board[capture] = EM;
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
}