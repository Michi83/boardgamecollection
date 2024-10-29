// Some of the explanatory comments of CheckersState also apply to
// InternationalCheckersState. They are not repeated here.
import java.util.ArrayList;
import java.util.List;

public class InternationalCheckersState implements GameState {
    private static final int WK = 2; // white king
    private static final int WM = 1; // white man
    private static final int EM = 0; // empty
    private static final int BM = -1; // black man
    private static final int BK = -2; // black king
    private static final int CP = -3; // captured piece
    private static final int LV = -4; // lava
    private static final int[] KING_OFFSETS = new int[] { -13, -11, 11, 13 };
    private static final int[] MAN_OFFSETS = new int[] { -13, -11 };

    private int[] board;
    private List<Integer> clicks;
    private int player;
    private List<Integer> userClicks;

    public InternationalCheckersState() {
        board = new int[] {
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
            LV, EM, BM, EM, BM, EM, BM, EM, BM, EM, BM, LV,
            LV, BM, EM, BM, EM, BM, EM, BM, EM, BM, EM, LV,
            LV, EM, BM, EM, BM, EM, BM, EM, BM, EM, BM, LV,
            LV, BM, EM, BM, EM, BM, EM, BM, EM, BM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, WM, EM, WM, EM, WM, EM, WM, EM, WM, LV,
            LV, WM, EM, WM, EM, WM, EM, WM, EM, WM, EM, LV,
            LV, EM, WM, EM, WM, EM, WM, EM, WM, EM, WM, LV,
            LV, WM, EM, WM, EM, WM, EM, WM, EM, WM, EM, LV,
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
        };
        player = 1;
    }

    private InternationalCheckersState(InternationalCheckersState that) {
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
            InternationalCheckersState checkersMove =
                (InternationalCheckersState)move;
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
        image.fillTile(0, 0, "chess10x10.png");
        for (int square = 14; square <= 129; square++) {
            int row = square / 12 - 1;
            int col = square % 12 - 1;
            int x = 6 * col + 2;
            int y = 6 * row + 2;
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
        for (int square = 14; square <= 129; square++) {
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
        return score / 64;
    }

    public List<GameState> generateCaptures() {
        List<GameState> moves = new ArrayList<GameState>();
        for (int origin = 14; origin <= 129; origin++) {
            switch (player * board[origin]) {
            case WK:
                generateKingCaptures(origin, moves);
                break;
            case WM:
                generateManCaptures(origin, moves);
                break;
            }
        }
        int topScore = Integer.MIN_VALUE;
        List<GameState> topMoves = new ArrayList<GameState>();
        for (GameState move : moves) {
            int score =
                ((InternationalCheckersState)move).removeCapturedPieces();
            if (score > topScore) {
                topScore = score;
                topMoves.clear();
                topMoves.add(move);
            } else if (score == topScore) {
                topMoves.add(move);
            }
        }
        return topMoves;
    }

    public void generateKingCaptures(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int capture = origin + offset;
            while (board[capture] == EM) {
                capture += offset;
            }
            if (capturable(capture)) {
                int target = capture + offset;
                while (board[target] == EM) {
                    InternationalCheckersState move = makeMove(origin, target,
                        capture);
                    int countBefore = moves.size();
                    move.generateKingCaptures(target, moves);
                    if (moves.size() == countBefore) {
                        moves.add(move);
                    }
                    target += offset;
                }
            }
        }
    }

    public void generateKingNonCaptures(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int target = origin + offset;
            while (board[target] == EM) {
                InternationalCheckersState move = makeMove(origin, target);
                moves.add(move);
                target += offset;
            }
        }
    }

    public void generateManCaptures(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int capture = origin + offset;
            int target = capture + offset;
            if (capturable(capture) && board[target] == EM) {
                InternationalCheckersState move = makeMove(origin, target,
                    capture);
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

    public void generateManNonCaptures(int origin, List<GameState> moves) {
        for (int offset : MAN_OFFSETS) {
            int target = origin + player * offset;
            if (board[target] == EM) {
                InternationalCheckersState move = makeMove(origin, target);
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
            ((InternationalCheckersState)move).player *= -1;
        }
        return moves;
    }

    public List<GameState> generateNonCaptures() {
        List<GameState> moves = new ArrayList<GameState>();
        for (int origin = 14; origin <= 129; origin++) {
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
        for (int square : clicks) {
            notation += (char)(square % 12 + 96);
            notation += 11 - square / 12;
        }
        return notation;
    }

    public int getPlayer() {
        return player;
    }

    private InternationalCheckersState makeMove(int origin, int target) {
        InternationalCheckersState move = new InternationalCheckersState(this);
        move.board[origin] = EM;
        move.board[target] = board[origin];
        move.clicks = new ArrayList<Integer>();
        move.clicks.add(origin);
        move.clicks.add(target);
        return move;
    }

    private InternationalCheckersState makeMove(int origin, int target,
            int capture) {
        InternationalCheckersState move = new InternationalCheckersState(this);
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
            return square / 12 == 1;
        } else {
            return square / 12 == 10;
        }
    }

    private int removeCapturedPieces() {
        int score = 0;
        for (int square = 14; square <= 129; square++) {
            if (board[square] == CP) {
                board[square] = EM;
                score++;
            }
        }
        return score;
    }
}