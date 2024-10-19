import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import javax.imageio.ImageIO;

public class Go9State implements GameState {
    private static final int WHITE = 1;
    private static final int EMPTY = 0;
    private static final int BLACK = -1;
    private static final int LAVA = -2;
    private static final int[] OFFSETS = new int[] { -11, -1, 1, 11 };

    private int[] board;
    private int clicks;
    private int ko;
    private int passes;
    private int player;

    public Go9State() {
        board = new int[121];
        for (int point = 0; point < 121; point++) {
            if (point / 11 == 0 || point / 11 == 10 || point % 11 == 0
                    || point % 11 == 10) {
                board[point] = LAVA;
            }
        }
        ko = -1;
        passes = 0;
        player = BLACK;
    }

    private Go9State(Go9State that) {
        board = that.board.clone();
        ko = -1;
        passes = 0;
        player = -that.player;
    }

    public GameState click(int id) {
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            if (((Go9State)move).clicks == id) {
                return move;
            }
        }
        return moves.get(moves.size() - 1);
    }

    public GameImage draw() {
        GameImage image = new GameImage();
        image.fillTile(0, 0, "go9.png");
        for (int point = 12; point <= 108; point++) {
            int row = point / 11 - 1;
            int col = point % 11 - 1;
            int x = 4 * col + 14;
            int y = 4 * row + 14;
            switch (board[point]) {
                case WHITE:
                    image.fillTile(x, y, "whitepiece.png");
                    break;
                case BLACK:
                    image.fillTile(x, y, "blackpiece.png");
            }
            image.addRegion(point, x, y, 4, 4);
        }
        return image;
    }

    public double evaluate() {
        if (generateMoves().size() == 0) {
            boolean[] whiteArea = getArea(WHITE);
            boolean[] blackArea = getArea(BLACK);
            int score = 0;
            for (int point = 12; point <= 108; point++) {
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
            int score = 0;
            for (int point = 12; point <= 108; point++) {
                if (board[point] == WHITE) {
                    score++;
                } else if (board[point] == BLACK) {
                    score--;
                }
            }
            return score / 128.0;
        }
    }

    public List<GameState> generateMoves() {
        List<GameState> moves = new ArrayList<GameState>();
        if (passes >= 2) {
            return moves;
        }
        for (int point = 12; point <= 108; point++) {
            if (board[point] == EMPTY) {
                Go9State move = new Go9State(this);
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
        Go9State move = new Go9State(this);
        move.passes = passes + 1;
        moves.add(move);
        return moves;
    }

    public boolean[] getArea(int player) {
        boolean[] area = new boolean[121];
        Queue<Integer> fillQueue = new ArrayDeque<Integer>();
        for (int point = 12; point <= 108; point++) {
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

    public int getPlayer() {
        return player;
    }

    private int removeCapturedStones(int player) {
        // 1) Mark empty points as safe and add them to a queue.
        boolean[] safe = new boolean[121];
        Queue<Integer> fillQueue = new ArrayDeque<Integer>();
        for (int point = 12; point <= 108; point++) {
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
        for (int point = 12; point <= 108; point++) {
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