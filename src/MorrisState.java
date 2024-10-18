import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class MorrisState implements GameState {
    private static final int WHITE = 1;
    private static final int EMPTY = 0;
    private static final int BLACK = -1;

    // 0-----------1-----------2
    // |           |           |
    // |   3-------4-------5   |
    // |   |       |       |   |
    // |   |   6---7---8   |   |
    // |   |   |       |   |   |
    // 9---10--11      12--13--14
    // |   |   |       |   |   |
    // |   |   15--16--17  |   |
    // |   |       |       |   |
    // |   18------19------20  |
    // |           |           |
    // 21----------22----------23

    private static final int[][] COORDS = new int[][] {
        new int[] { 176, 176 },
        new int[] { 464, 176 },
        new int[] { 752, 176 },
        new int[] { 272, 272 },
        new int[] { 464, 272 },
        new int[] { 656, 272 },
        new int[] { 368, 368 },
        new int[] { 464, 368 },
        new int[] { 560, 368 },
        new int[] { 176, 464 },
        new int[] { 272, 464 },
        new int[] { 368, 464 },
        new int[] { 560, 464 },
        new int[] { 656, 464 },
        new int[] { 752, 464 },
        new int[] { 368, 560 },
        new int[] { 464, 560 },
        new int[] { 560, 560 },
        new int[] { 272, 656 },
        new int[] { 464, 656 },
        new int[] { 656, 656 },
        new int[] { 176, 752 },
        new int[] { 464, 752 },
        new int[] { 752, 752 }
    };

    private static final int[][][] MILLS = new int[][][] {
        new int[][] { new int[] { 1, 2 }, new int[] { 9, 21 } },
        new int[][] { new int[] { 0, 2 }, new int[] { 4, 7 } },
        new int[][] { new int[] { 0, 1 }, new int[] { 14, 23 } },
        new int[][] { new int[] { 4, 5 }, new int[] { 10, 18 } },
        new int[][] { new int[] { 1, 7 }, new int[] { 3, 5 } },
        new int[][] { new int[] { 3, 4 }, new int[] { 13, 20 } },
        new int[][] { new int[] { 7, 8 }, new int[] { 11, 15 } },
        new int[][] { new int[] { 1, 4 }, new int[] { 6, 8 } },
        new int[][] { new int[] { 6, 7 }, new int[] { 12, 17 } },
        new int[][] { new int[] { 0, 21 }, new int[] { 10, 11 } },
        new int[][] { new int[] { 3, 18 }, new int[] { 9, 11 } },
        new int[][] { new int[] { 6, 15 }, new int[] { 9, 10 } },
        new int[][] { new int[] { 8, 17 }, new int[] { 13, 14 } },
        new int[][] { new int[] { 5, 20 }, new int[] { 12, 14 } },
        new int[][] { new int[] { 2, 23 }, new int[] { 12, 13 } },
        new int[][] { new int[] { 6, 11 }, new int[] { 16, 17 } },
        new int[][] { new int[] { 15, 17 }, new int[] { 19, 22 } },
        new int[][] { new int[] { 8, 12 }, new int[] { 15, 16 } },
        new int[][] { new int[] { 3, 10 }, new int[] { 19, 20 } },
        new int[][] { new int[] { 16, 22 }, new int[] { 18, 20 } },
        new int[][] { new int[] { 5, 13 }, new int[] { 18, 19 } },
        new int[][] { new int[] { 0, 9 }, new int[] { 22, 23 } },
        new int[][] { new int[] { 16, 19 }, new int[] { 21, 23 } },
        new int[][] { new int[] { 2, 14 }, new int[] { 21, 22 } }
    };

    private static final int[][] TARGETS = new int[][] {
        new int[] { 1, 9 },
        new int[] { 0, 2, 4 },
        new int[] { 1, 14 },
        new int[] { 4, 10 },
        new int[] { 1, 3, 5, 7 },
        new int[] { 4, 13 },
        new int[] { 7, 11 },
        new int[] { 4, 6, 8 },
        new int[] { 7, 12 },
        new int[] { 0, 10, 21 },
        new int[] { 3, 9, 11, 18 },
        new int[] { 6, 10, 15 },
        new int[] { 8, 13, 17 },
        new int[] { 5, 12, 14, 20 },
        new int[] { 2, 13, 23 },
        new int[] { 11, 16 },
        new int[] { 15, 17, 19 },
        new int[] { 12, 16 },
        new int[] { 10, 19 },
        new int[] { 16, 18, 20, 22 },
        new int[] { 13, 19 },
        new int[] { 9, 22 },
        new int[] { 19, 21, 23 },
        new int[] { 14, 22 }
    };

    private int blackPieces;
    private int[] board;
    private int[] clicks;
    private int player;
    private int unplacedPieces;
    private List<Integer> userClicks;
    private int whitePieces;

    public MorrisState() {
        blackPieces = 9;
        board = new int[24];
        player = WHITE;
        unplacedPieces = 18;
        userClicks = new ArrayList<Integer>();
        whitePieces = 9;
    }

    private MorrisState(MorrisState that) {
        blackPieces = that.blackPieces;
        board = that.board.clone();
        player = -that.player;
        unplacedPieces = that.unplacedPieces;
        userClicks = new ArrayList<Integer>();
        whitePieces = that.whitePieces;
    }

    public GameState click(int x, int y) {
        for (int point = 0; point < 24; point++) {
            int x2 = COORDS[point][0];
            int y2 = COORDS[point][1];
            if (x2 <= x && x < x2 + 96 && y2 <= y && y < y2 + 96) {
                userClicks.add(point);
                break;
            }
        }
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            MorrisState morrisMove = (MorrisState)move;
            if (userClicks.size() <= morrisMove.clicks.length) {
                boolean match = true;
                for (int i = 0; i < userClicks.size(); i++) {
                    if (userClicks.get(i) != morrisMove.clicks[i]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    if (userClicks.size() == morrisMove.clicks.length) {
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

    public void draw(Graphics2D graphics) {
        try {
            Image morris = ImageIO.read(new File("img/png/morris.png"));
            Image whitepiece = ImageIO.read(new File(
                "img/png/whitepiece.png"));
            Image blackpiece = ImageIO.read(new File(
                "img/png/blackpiece.png"));
            Image selection = ImageIO.read(new File(
                "img/png/selection.png"));
            graphics.drawImage(morris, 0, 0, null);
            for (int point = 0; point < 24; point++) {
                int x = COORDS[point][0];
                int y = COORDS[point][1];
                switch (board[point]) {
                    case WHITE:
                        graphics.drawImage(whitepiece, x + 16, y + 16, null);
                        break;
                    case BLACK:
                        graphics.drawImage(blackpiece, x + 16, y + 16, null);
                }
                if (userClicks.contains(point)) {
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
        } else {
            return (whitePieces - blackPieces) / 16.0;
        }
    }

    private void generateCaptures(int origin, int target,
            List<GameState> moves) {
        boolean captureFound = false;
        for (int capture = 0; capture < 24; capture++) {
            if (board[capture] == -player && !mill(capture)) {
                MorrisState move = makeMove(origin, target, capture);
                moves.add(move);
                captureFound = true;
            }
        }
        if (!captureFound) {
            for (int capture = 0; capture < 24; capture++) {
                if (board[capture] == -player) {
                    MorrisState move = makeMove(origin, target, capture);
                    moves.add(move);
                    captureFound = true;
                }
            }
        }
    }

    public List<GameState> generateMoves() {
        List<GameState> moves = new ArrayList<GameState>();
        if (unplacedPieces > 0) {
            generatePhase1Moves(moves);
        } else {
            int pieces = player == WHITE ? whitePieces : blackPieces;
            if (pieces > 3) {
                generatePhase2Moves(moves);
            } else if (pieces == 3) {
                generatePhase3Moves(moves);
            }
        }
        return moves;
    }

    private void generatePhase1Moves(List<GameState> moves) {
        for (int target = 0; target < 24; target++) {
            if (board[target] == EMPTY) {
                MorrisState move = makeMove(-1, target);
                if (move.mill(target)) {
                    generateCaptures(-1, target, moves);
                } else {
                    moves.add(move);
                }
            }
        }
    }

    private void generatePhase2Moves(List<GameState> moves) {
        for (int origin = 0; origin < 24; origin++) {
            if (board[origin] == player) {
                for (int target : TARGETS[origin]) {
                    if (board[target] == EMPTY) {
                        MorrisState move = makeMove(origin, target);
                        if (move.mill(target)) {
                            generateCaptures(origin, target, moves);
                        } else {
                            moves.add(move);
                        }
                    }
                }
            }
        }
    }

    private void generatePhase3Moves(List<GameState> moves) {
        for (int origin = 0; origin < 24; origin++) {
            if (board[origin] == player) {
                for (int target = 0; target < 24; target++) {
                    if (board[target] == EMPTY) {
                        MorrisState move = makeMove(origin, target);
                        if (move.mill(target)) {
                            generateCaptures(origin, target, moves);
                        } else {
                            moves.add(move);
                        }
                    }
                }
            }
        }
    }

    public int getPlayer() {
        return player;
    }

    private MorrisState makeMove(int origin, int target) {
        MorrisState move = new MorrisState(this);
        if (origin == -1) {
            move.clicks = new int[] { target };
            move.unplacedPieces--;
        } else {
            move.board[origin] = EMPTY;
            move.clicks = new int[] { origin, target };
        }
        move.board[target] = player;
        return move;
    }

    private MorrisState makeMove(int origin, int target, int capture) {
        MorrisState move = new MorrisState(this);
        if (origin == -1) {
            move.clicks = new int[] { target, capture };
            move.unplacedPieces--;
        } else {
            move.board[origin] = EMPTY;
            move.clicks = new int[] { origin, target, capture };
        }
        move.board[target] = player;
        move.board[capture] = EMPTY;
        if (player == WHITE) {
            move.blackPieces--;
        } else {
            move.whitePieces--;
        }
        return move;
    }

    private boolean mill(int point) {
        for (int[] mill : MILLS[point]) {
            int sum = board[point] + board[mill[0]] + board[mill[1]];
            if (sum == 3 || sum == -3) {
                return true;
            }
        }
        return false;
    }
}