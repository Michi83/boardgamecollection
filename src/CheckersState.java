import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class CheckersState implements GameState {
    private static final int WHITE_KING = 2;
    private static final int WHITE_MAN = 1;
    private static final int EMPTY = 0;
    private static final int BLACK_MAN = -1;
    private static final int BLACK_KING = -2;
    private static final int LAVA = -3;
    private static final int[] KING_OFFSETS = new int[] { -11, -9, 9, 11 };
    private static final int[] MAN_OFFSETS = new int[] { -11, -9 };

    private int[] board;
    private List<Integer> clicks;
    private int player;
    private List<Integer> userClicks;

    public CheckersState() {
        board = new int[100];
        for (int square = 0; square < 100; square++) {
            if (square / 10 == 0 || square / 10 == 9 || square % 10 == 0
                    || square % 10 == 9) {
                board[square] = LAVA;
            } else if (square / 10 % 2 != square % 2) {
                if (square <= 38) {
                    board[square] = BLACK_MAN;
                } else if (square >= 61) {
                    board[square] = WHITE_MAN;
                }
            }
        }
        player = -1;
        userClicks = new ArrayList<Integer>();
    }

    private CheckersState(CheckersState that) {
        board = that.board.clone();
        player = that.player;
        userClicks = new ArrayList<Integer>();
    }

    private boolean capturable(int square) {
        return board[square] != LAVA && player * board[square] < 0;
    }

    public GameState click(int x, int y) {
        int row = (y - 128) / 96;
        int col = (x - 128) / 96;
        int square = 10 * row + col + 11;
        userClicks.add(square);
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            CheckersState checkersMove = (CheckersState)move;
            if (userClicks.size() <= checkersMove.clicks.size()) {
                boolean match = true;
                for (int i = 0; i < userClicks.size(); i++) {
                    if (userClicks.get(i) != checkersMove.clicks.get(i)) {
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

    public void draw(Graphics2D graphics) {
        try {
            BufferedImage chess = ImageIO.read(new File(
                "../img/png/chess.png"));
            BufferedImage whiteking = ImageIO.read(new File(
                "../img/png/whiteking.png"));
            BufferedImage whitepiece = ImageIO.read(new File(
                "../img/png/whitepiece.png"));
            BufferedImage blackking = ImageIO.read(new File(
                "../img/png/blackking.png"));
            BufferedImage blackpiece = ImageIO.read(new File(
                "../img/png/blackpiece.png"));
            BufferedImage selection = ImageIO.read(new File(
                "../img/png/selection.png"));
            graphics.drawImage(chess, 0, 0, null);
            for (int square = 12; square <= 87; square++) {
                int row = square / 10 - 1;
                int col = square % 10 - 1;
                int x = 96 * col + 128;
                int y = 96 * row + 128;
                switch (board[square]) {
                    case WHITE_KING:
                        graphics.drawImage(whiteking, x + 16, y + 16, null);
                        break;
                    case WHITE_MAN:
                        graphics.drawImage(whitepiece, x + 16, y + 16, null);
                        break;
                    case BLACK_KING:
                        graphics.drawImage(blackking, x + 16, y + 16, null);
                        break;
                    case BLACK_MAN:
                        graphics.drawImage(blackpiece, x + 16, y + 16, null);
                }
                if (userClicks.contains(square)) {
                    graphics.drawImage(selection, x, y, null);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public double evaluate() {
        if (generateMoves().size() == 0) {
            return -player;
        }
        int score = 0;
        for (int square = 12; square <= 87; square++) {
            switch (board[square]) {
                case WHITE_KING:
                    score += 2;
                    break;
                case WHITE_MAN:
                    score++;
                    break;
                case BLACK_KING:
                    score -= 2;
                    break;
                case BLACK_MAN:
                    score--;
                    break;
            }
        }
        return score / 100;
    }

    private List<GameState> generateCaptures() {
        List<GameState> moves = new ArrayList<GameState>();
        for (int origin = 12; origin <= 87; origin++) {
            switch (player * board[origin]) {
                case WHITE_KING:
                    generateKingCaptures(origin, moves);
                    break;
                case WHITE_MAN:
                    generateManCaptures(origin, moves);
            }
        }
        return moves;
    }

    private void generateKingCaptures(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int capture = origin + offset;
            int target = capture + offset;
            if (capturable(capture) && board[target] == EMPTY) {
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
            if (board[target] == EMPTY) {
                CheckersState move = makeMove(origin, target);
                moves.add(move);
            }
        }
    }

    private void generateManCaptures(int origin, List<GameState> moves) {
        for (int offset : MAN_OFFSETS) {
            int capture = origin + player * offset;
            int target = capture + player * offset;
            if (capturable(capture) && board[target] == EMPTY) {
                CheckersState move = makeMove(origin, target, capture);
                if (promotes(target)) {
                    move.board[target] = player * WHITE_KING;
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
            if (board[target] == EMPTY) {
                CheckersState move = makeMove(origin, target);
                if (promotes(target)) {
                    move.board[target] = player * WHITE_KING;
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
                case WHITE_KING:
                    generateKingNonCaptures(origin, moves);
                    break;
                case WHITE_MAN:
                    generateManNonCaptures(origin, moves);
            }
        }
        return moves;
    }

    public int getPlayer() {
        return player;
    }

    private CheckersState makeMove(int origin, int target) {
        CheckersState move = new CheckersState(this);
        move.board[origin] = EMPTY;
        move.board[target] = board[origin];
        move.clicks = new ArrayList<Integer>();
        move.clicks.add(origin);
        move.clicks.add(target);
        return move;
    }

    private CheckersState makeMove(int origin, int target, int capture) {
        CheckersState move = new CheckersState(this);
        move.board[origin] = EMPTY;
        move.board[target] = board[origin];
        move.board[capture] = EMPTY;
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