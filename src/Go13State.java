import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Go13State implements GameState {
    private static final int WHITE = 1;
    private static final int EMPTY = 0;
    private static final int BLACK = -1;
    private static final int LAVA = -2;
    private static final int[] OFFSETS = new int[] { -15, -1, 1, 15 };

    private int[] board;
    private int clicks;
    private int ko;
    private int passes;
    private int player;

    public Go13State() {
        board = new int[225];
        for (int point = 0; point < 225; point++) {
            if (point / 15 == 0 || point / 15 == 14 || point % 15 == 0
                    || point % 15 == 14) {
                board[point] = LAVA;
            }
        }
        ko = -1;
        passes = 0;
        player = BLACK;
    }

    private Go13State(Go13State that) {
        board = that.board.clone();
        ko = -1;
        passes = 0;
        player = -that.player;
    }

    public GameState click(int id) {
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            if (((Go13State)move).clicks == id) {
                return move;
            }
        }
        // Any illegal move, e.g. clicking on a stone or outside the board
        // results in a pass.
        return moves.get(moves.size() - 1);
    }

    public GameImage draw() {
        GameImage image = new GameImage();
        image.fillTile(0, 0, "go13.png");
        for (int point = 16; point <= 208; point++) {
            int row = point / 15 - 1;
            int col = point % 15 - 1;
            int x = 4 * col + 6;
            int y = 4 * row + 6;
            switch (board[point]) {
            case WHITE:
                image.fillTile(x, y, "whitepiece.png");
                break;
            case BLACK:
                image.fillTile(x, y, "blackpiece.png");
                break;
            }
            image.addRegion(point, x, y, 4, 4);
        }
        // show territory when game is over
        if (generateMoves().size() == 0) {
            boolean[] whiteArea = getArea(WHITE);
            boolean[] blackArea = getArea(BLACK);
            for (int point = 16; point <= 208; point++) {
                int row = point / 15 - 1;
                int col = point % 15 - 1;
                int x = 4 * col + 6;
                int y = 4 * row + 6;
                if (board[point] == EMPTY) {
                    if (whiteArea[point] && !blackArea[point]) {
                        image.fillTile(x, y, "whiteghostpiece.png");
                    } else if (blackArea[point] && !whiteArea[point]) {
                        image.fillTile(x, y, "blackghostpiece.png");
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
            for (int point = 16; point <= 208; point++) {
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
            for (int point = 16; point <= 208; point++) {
                if (board[point] == WHITE) {
                    score++;
                } else if (board[point] == BLACK) {
                    score--;
                }
            }
            return score / 256;
        }
    }

    public List<GameState> generateMoves() {
        List<GameState> moves = new ArrayList<GameState>();
        if (passes >= 2) {
            return moves;
        }
        for (int point = 16; point <= 208; point++) {
            if (board[point] == EMPTY) {
                Go13State move = new Go13State(this);
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
        Go13State move = new Go13State(this);
        move.clicks = -1;
        move.passes = passes + 1;
        moves.add(move);
        return moves;
    }

    public boolean[] getArea(int player) {
        boolean[] area = new boolean[225];
        Queue<Integer> fillQueue = new ArrayDeque<Integer>();
        for (int point = 16; point <= 208; point++) {
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
        notation += (char)(clicks % 15 + 96); // file
        notation += 14 - clicks / 15; // rank
        return notation;
    }

    public int getPlayer() {
        return player;
    }

    private int removeCapturedStones(int player) {
        // 1) Mark empty points as safe and add them to a queue.
        boolean[] safe = new boolean[225];
        Queue<Integer> fillQueue = new ArrayDeque<Integer>();
        for (int point = 16; point <= 208; point++) {
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
        for (int point = 16; point <= 208; point++) {
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