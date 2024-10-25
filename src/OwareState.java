import java.util.ArrayList;
import java.util.List;

public class OwareState implements GameState {
    private static final int[][] COORDS = new int[][] {
        new int[] { 2, 32 },
        new int[] { 12, 32 },
        new int[] { 22, 32 },
        new int[] { 32, 32 },
        new int[] { 42, 32 },
        new int[] { 52, 32 },
        new int[] { 52, 22 },
        new int[] { 42, 22 },
        new int[] { 32, 22 },
        new int[] { 22, 22 },
        new int[] { 12, 22 },
        new int[] { 2, 22 }
    };

    private static final double[][] SEED_COORDS = new double[][] {
        new double[] { 4, 4 },
        new double[] { 4, 2 },
        new double[] { 5.75, 3 },
        new double[] { 5.75, 5 },
        new double[] { 4, 6 },
        new double[] { 2.25, 5 },
        new double[] { 2.25, 3 },
        new double[] { 2.25, 1 },
        new double[] { 4, 0 },
        new double[] { 5.75, 1 },
        new double[] { 7.5, 2 },
        new double[] { 7.5, 4 },
        new double[] { 7.5, 6 },
        new double[] { 5.75, 7 },
        new double[] { 4, 8 },
        new double[] { 2.25, 7 },
        new double[] { 0.5, 6 },
        new double[] { 0.5, 4 },
        new double[] { 0.5, 2 },
        new double[] { 0.5, 0 },
        new double[] { 2.25, -1 },
        new double[] { 4, -2 },
        new double[] { 5.75, -1 },
        new double[] { 7.5, 0 },
        new double[] { 9.25, 1 },
        new double[] { 9.25, 3 },
        new double[] { 9.25, 5 },
        new double[] { 9.25, 7 },
        new double[] { 7.5, 8 },
        new double[] { 5.75, 9 },
        new double[] { 4, 10 },
        new double[] { 2.25, 9 },
        new double[] { 0.5, 8 },
        new double[] { -1.25, 7 },
        new double[] { -1.25, 5 },
        new double[] { -1.25, 3 },
        new double[] { -1.25, 1 },
        new double[] { -1.25, -1 },
        new double[] { 0.5, -2 },
        new double[] { 2.25, -3 },
        new double[] { 4, -4 },
        new double[] { 5.75, -3 },
        new double[] { 7.5, -2 },
        new double[] { 9.25, -1 },
        new double[] { 11, 0 },
        new double[] { 11, 2 },
        new double[] { 11, 4 },
        new double[] { 11, 6 }
    };

    private int blackSeeds;
    private int[] board;
    private int clicks;
    private int player;
    private int whiteSeeds;

    public OwareState() {
        blackSeeds = 0;
        // 11 10  9  8  7  6
        //  0  1  2  3  4  5
        board = new int[] { 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 };
        player = 1;
        whiteSeeds = 0;
    }

    private OwareState(OwareState that) {
        blackSeeds = that.blackSeeds;
        board = that.board.clone();
        player = that.player;
        whiteSeeds = that.whiteSeeds;
    }

    public GameState click(int id) {
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            if (((OwareState)move).clicks == id) {
                return move;
            }
        }
        return this;
    }

    public GameImage draw() {
        GameImage image = new GameImage();
        image.fillTile(0, 0, "mancala.png");
        for (int house = 0; house < 12; house++) {
            int x = COORDS[house][0];
            int y = COORDS[house][1];
            for (int i = 0; i < board[house]; i++) {
                if (i >= 19) {
                    break; // don't show more than 19 seeds per house
                }
                double dx = SEED_COORDS[i][0];
                double dy = SEED_COORDS[i][1];
                image.fillTile(x + dx, y + dy, "neutralsmallpiece.png");
                image.addRegion(house, x, y, 10, 10);
            }
        }
        for (int i = 0; i < whiteSeeds; i++) {
            double dx = SEED_COORDS[i][0];
            double dy = SEED_COORDS[i][1];
            image.fillTile(27 + dx, 48 + dy, "neutralsmallpiece.png");
        }
        for (int i = 0; i < blackSeeds; i++) {
            double dx = SEED_COORDS[i][0];
            double dy = SEED_COORDS[i][1];
            image.fillTile(27 + dx, 6 + dy, "neutralsmallpiece.png");
        }
        return image;
    }

    public double evaluate() {
        int score = whiteSeeds - blackSeeds;
        if (generateMoves().size() == 0) {
            for (int house = 0; house < 6; house++) {
                score += board[house];
            }
            for (int house = 6; house < 12; house++) {
                score -= board[house];
            }
            if (score > 0) {
                return 1;
            } else if (score < 0) {
                return -1;
            } else {
                return 0;
            }
        } else {
            return score / 64.0;
        }
    }

    // Oware is unique in that it discourages moves that would be called
    // checkmate or stalemate in other games. The objective is to capture most
    // seeds, not to immobilize the other player.
    // - Moves that would capture all the opponent's seeds are allowed but
    //   don't capture anything.
    // - After a move that leaves oneself out of seeds, the other player is
    //   obliged to make a move that returns at least one seed.
    // Stalemates can still occur. Then the players capture the remaining seeds
    // in their houses and the game is over.
    public List<GameState> generateMoves() {
        List<GameState> moves = new ArrayList<GameState>();
        if (whiteSeeds > 24 || blackSeeds > 24) {
            return moves;
        }
        for (int i = 0; i < 6; i++) {
            int origin = 3 - 3 * player + i;
            if (board[origin] > 0) {
                OwareState move = makeMove(origin);
                if (!move.outOfSeeds()) {
                    move.player *= -1;
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    public String getNotation() {
        return (clicks % 6) + 1 + "";
    }

    public int getPlayer() {
        return player;
    }

    private OwareState makeMove(int origin) {
        OwareState move = new OwareState(this);
        // move seeds
        move.board[origin] = 0;
        int target = origin;
        for (int i = 0; i < board[origin]; i++) {
            target = (target + 1) % 12;
            if (target == origin) {
                target = (target + 1) % 12;
            }
            move.board[target]++;
        }
        OwareState copy = new OwareState(move);
        move.removeCapturedSeeds(target);
        if (move.outOfSeeds()) {
            move = copy;
        }
        move.clicks = origin;
        return move;
    }

    private boolean outOfSeeds() {
        if (player == 1) {
            for (int house = 6; house < 12; house++) {
                if (board[house] > 0) {
                    return false;
                }
            }
        } else {
            for (int house = 0; house < 6; house++) {
                if (board[house] > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void removeCapturedSeeds(int house) {
        if (player == 1) {
            while (house >= 6 && (board[house] == 2 || board[house] == 3)) {
                whiteSeeds += board[house];
                board[house] = 0;
                house = (house + 11) % 12;
            }
        } else {
            while (house < 6 && (board[house] == 2 || board[house] == 3)) {
                blackSeeds += board[house];
                board[house] = 0;
                house = (house + 11) % 12;
            }
        }
    }
}