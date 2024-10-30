// Go is really not my game, but it's popular, so I'll include at least a
// simplified version. Area scoring is used. No attempt is made to identify
// dead groups. Just play it out until they are captured. Don't expect the
// computer opponent to be good.
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Go19State implements GameState {
    private static final int WHITE = 1;
    private static final int EMPTY = 0;
    private static final int BLACK = -1;
    private static final int LAVA = -2;
    private static final int[] OFFSETS = new int[] { -21, -1, 1, 21 };

    private int[] board;
    private int clicks;
    private int ko;
    private int passes;
    private int player;

    public Go19State() {
        board = new int[441];
        for (int point = 0; point < 441; point++) {
            if (point / 21 == 0 || point / 21 == 20 || point % 21 == 0
                    || point % 21 == 20) {
                board[point] = LAVA;
            }
        }
        ko = -1;
        passes = 0;
        player = BLACK;
    }

    private Go19State(Go19State that) {
        board = that.board.clone();
        ko = -1;
        passes = 0;
        player = -that.player;
    }

    public GameState click(int id) {
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            if (((Go19State)move).clicks == id) {
                return move;
            }
        }
        // Any illegal move, e.g. clicking on a stone or outside the board
        // results in a pass.
        return moves.get(moves.size() - 1);
    }

    public GameImage draw() {
        GameImage image = new GameImage();
        image.fillTile(0, 0, "go19.png");
        for (int point = 22; point <= 418; point++) {
            int row = point / 21 - 1;
            int col = point % 21 - 1;
            int x = 2 * col + 13;
            int y = 2 * row + 13;
            switch (board[point]) {
            case WHITE:
                image.fillTile(x, y, "whitesmallpiece.png");
                break;
            case BLACK:
                image.fillTile(x, y, "blacksmallpiece.png");
                break;
            }
            image.addRegion(point, x, y, 2, 2);
        }
        // show territory when game is over
        if (generateMoves().size() == 0) {
            boolean[] whiteArea = getArea(WHITE);
            boolean[] blackArea = getArea(BLACK);
            for (int point = 22; point <= 418; point++) {
                int row = point / 21 - 1;
                int col = point % 21 - 1;
                int x = 2 * col + 13;
                int y = 2 * row + 13;
                if (board[point] == EMPTY) {
                    if (whiteArea[point] && !blackArea[point]) {
                        image.fillTile(x, y, "whitesmallghostpiece.png");
                    } else if (blackArea[point] && !whiteArea[point]) {
                        image.fillTile(x, y, "blacksmallghostpiece.png");
                    }
                }
            }
        }
        return image;
    }

    public double evaluate() {
        if (generateMoves().size() == 0) {
            boolean[] whiteArea = getArea(WHITE);
            boolean[] blackArea = getArea(BLACK);
            int score = 0;
            for (int point = 22; point <= 418; point++) {
                if (whiteArea[point] && !blackArea[point]) {
                    score++;
                } else if (blackArea[point] && !whiteArea[point]) {
                    score--;
                }
            }
            if (score > 0) {
                return WHITE;
            } else if (score < 0) {
                return BLACK;
            } else {
                return 0;
            }
        } else {
            double score = 0;
            for (int point = 22; point <= 418; point++) {
                if (board[point] == WHITE) {
                    score++;
                } else if (board[point] == BLACK) {
                    score--;
                }
            }
            return score / 512;
        }
    }

    public List<GameState> generateMoves() {
        List<GameState> moves = new ArrayList<GameState>();
        if (passes >= 2) {
            return moves;
        }
        for (int point = 22; point <= 418; point++) {
            if (board[point] == EMPTY) {
                Go19State move = new Go19State(this);
                move.board[point] = player;
                int count = move.removeCapturedStones(-player);
                if (count == 1 && point == ko) {
                    continue; // move violates ko rule
                }
                count = move.removeCapturedStones(player);
                if (count > 0) {
                    continue; // move violates no-suicide rule
                }
                move.clicks = point;
                moves.add(move);
            }
        }
        // passing move
        Go19State move = new Go19State(this);
        move.clicks = -1;
        move.passes = passes + 1;
        moves.add(move);
        return moves;
    }

    public boolean[] getArea(int player) {
        boolean[] area = new boolean[441];
        Queue<Integer> fillQueue = new ArrayDeque<Integer>();
        for (int point = 22; point <= 418; point++) {
            if (board[point] == player) {
                area[point] = true;
                fillQueue.add(point);
            }
        }
        while (fillQueue.size() > 0) {
            int point = fillQueue.remove();
            for (int offset : OFFSETS) {
                int neighbor = point + offset;
                if (board[neighbor] == EMPTY && !area[neighbor]) {
                    area[neighbor] = true;
                    fillQueue.add(neighbor);
                }
            }
        }
        return area;
    }

    public String getNotation() {
        if (clicks == -1) {
            return "pass";
        }
        String notation = "";
        notation += (char)(clicks % 21 + 96); // file
        notation += 20 - clicks / 21; // rank
        return notation;
    }

    public int getPlayer() {
        return player;
    }

    private int removeCapturedStones(int player) {
        // 1) Mark empty points as safe and add them to a queue.
        boolean[] safe = new boolean[441];
        Queue<Integer> fillQueue = new ArrayDeque<Integer>();
        for (int point = 22; point <= 418; point++) {
            if (board[point] == EMPTY) {
                safe[point] = true;
                fillQueue.add(point);
            }
        }
        // 2) Remove a point from the queue. Mark its neighbors as safe and add
        //    them to the queue if they are the right color and not yet marked
        //    as safe. Repeat until queue is empty.
        while (fillQueue.size() > 0) {
            int point = fillQueue.remove();
            for (int offset : OFFSETS) {
                int neighbor = point + offset;
                if (board[neighbor] == player && !safe[neighbor]) {
                    safe[neighbor] = true;
                    fillQueue.add(neighbor);
                }
            }
        }
        // 3) Remove unsafe stones.
        int count = 0;
        for (int point = 22; point <= 418; point++) {
            if (board[point] == player && !safe[point]) {
                board[point] = EMPTY;
                count++;
                if (count == 1) {
                    ko = point;
                } else {
                    ko = -1;
                }
            }
        }
        return count;
    }
}