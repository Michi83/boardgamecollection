import java.util.ArrayList;
import java.util.List;

public class XiangqiState implements GameState {
    private static final int WK = 7; // white king
    private static final int WQ = 6; // white queen
    private static final int WB = 5; // white bishop
    private static final int WN = 4; // white knight
    private static final int WR = 3; // white rook
    private static final int WC = 2; // white cannon
    private static final int WP = 1; // white pawn
    private static final int EM = 0; // empty
    private static final int BP = -1; // black pawn
    private static final int BC = -2; // black cannon
    private static final int BR = -3; // black rook
    private static final int BN = -4; // black knight
    private static final int BB = -5; // black bishop
    private static final int BQ = -6; // black queen
    private static final int BK = -7; // black king
    private static final int LV = -8; // lava
    private static final int[] KING_OFFSETS = new int[] { -11, -1, 1, 11 };
    private static final int[] QUEEN_OFFSETS = new int[] { -12, -10, 10, 12 };
    private static final int[] BISHOP_OFFSETS = new int[] { -12, -10, 10, 12 };
    private static final int[] KNIGHT_OFFSETS = new int[] {
        -23, -21, -13, -9, 9, 13, 21, 23
    };
    private static final int[] KNIGHT_INTERMEDIATES = new int[] {
        -11, -11, -1, 1, -1, 1, 11, 11
    };
    private static final int[] ROOK_OFFSETS = new int[] { -11, -1, 1, 11 };
    private static final int[] CANNON_OFFSETS = new int[] { -11, -1, 1, 11 };
    private static final int[] PAWN_OFFSETS = new int[] { -11, -1, 1 };

    private int blackKing;
    private int[] board;
    private int[] clicks;
    private int player;
    private List<Integer> userClicks;
    private int whiteKing;

    public XiangqiState() {
        blackKing = 16;
        board = new int[] {
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
            LV, BR, BN, BB, BQ, BK, BQ, BB, BN, BR, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, BC, EM, EM, EM, EM, EM, BC, EM, LV,
            LV, BP, EM, BP, EM, BP, EM, BP, EM, BP, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, WP, EM, WP, EM, WP, EM, WP, EM, WP, LV,
            LV, EM, WC, EM, EM, EM, EM, EM, WC, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, WR, WN, WB, WQ, WK, WQ, WB, WN, WR, LV,
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV
        };
        player = 1;
        whiteKing = 115;
    }

    private XiangqiState(XiangqiState that) {
        blackKing = that.blackKing;
        board = that.board.clone();
        player = -that.player;
        whiteKing = that.whiteKing;
    }

    private static void addIfLegal(XiangqiState state,
            List<GameState> states) {
        if (state.legal()) {
            states.add(state);
        }
    }

    // Since we are only interested in attacks on kings, we can simplify things
    // a bit.
    // - Kings only attack kings via the flying kings rule.
    // - Queens and bishops never attack kings.
    // - Pawns only attack kings with their beyond-the-river move.
    private boolean attacked(int target, int attacker) {
        return attackedByKing(target, attacker)
            || attackedByKnight(target, attacker)
            || attackedByRook(target, attacker)
            || attackedByCannon(target, attacker)
            || attackedByPawn(target, attacker);
    }

    private boolean attackedByCannon(int target, int attacker) {
        for (int offset : CANNON_OFFSETS) {
            int origin = target - offset;
            while (board[origin] == EM) {
                origin -= offset;
            }
            if (board[origin] != LV) {
                origin -= offset;
                while (board[origin] == EM) {
                    origin -= offset;
                }
                if (board[origin] == attacker * WC) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean attackedByKing(int target, int attacker) {
        int origin = target - attacker * -11;
        while (board[origin] == EM) {
            origin -= attacker * -11;
        }
        return board[origin] == attacker * WK;
    }

    private boolean attackedByKnight(int target, int attacker) {
        for (int i = 0; i < 8; i++) {
            int origin = target - KNIGHT_OFFSETS[i];
            int intermediate = origin + KNIGHT_INTERMEDIATES[i];
            if (board[intermediate] == EM && board[origin] == attacker * WN) {
                return true;
            }
        }
        return false;
    }

    private boolean attackedByPawn(int target, int attacker) {
        for (int offset : PAWN_OFFSETS) {
            int origin = target - attacker * offset;
            if (board[origin] == attacker * WP) {
                return true;
            }
        }
        return false;
    }

    private boolean attackedByRook(int target, int attacker) {
        for (int offset : ROOK_OFFSETS) {
            int origin = target - offset;
            while (board[origin] == EM) {
                origin -= offset;
            }
            if (board[origin] == attacker * WR) {
                return true;
            }
        }
        return false;
    }

    private boolean beyondRiver(int point) {
        if (player == 1) {
            return point < 66;
        } else {
            return point >= 66;
        }
    }

    private boolean capturable(int point) {
        return board[point] != LV && player * board[point] < EM;
    }

    private boolean capturableOrEmpty(int point) {
        return board[point] != LV && player * board[point] <= EM;
    }

    private boolean check(int defender) {
        if (defender == 1) {
            return attacked(whiteKing, -1);
        } else {
            return attacked(blackKing, 1);
        }
    }

    public GameState click(int id) {
        userClicks.add(id);
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            XiangqiState xiangqiMove = (XiangqiState)move;
            if (userClicks.size() <= xiangqiMove.clicks.length) {
                boolean match = true;
                for (int i = 0; i < userClicks.size(); i++) {
                    if (userClicks.get(i) != xiangqiMove.clicks[i]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    if (userClicks.size() == xiangqiMove.clicks.length) {
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
        image.fillTile(0, 0, "xiangqi.png");
        for (int point = 12; point <= 119; point++) {
            int row = point / 11 - 1;
            int col = point % 11 - 1;
            int x = 6 * col + 5;
            int y = 6 * row + 2;
            switch (board[point]) {
            case WK:
                image.fillTile(x + 1, y + 1, "whiteking.png");
                break;
            case WQ:
                image.fillTile(x + 1, y + 1, "whitequeen.png");
                break;
            case WB:
                image.fillTile(x + 1, y + 1, "whitebishop.png");
                break;
            case WN:
                image.fillTile(x + 1, y + 1, "whiteknight.png");
                break;
            case WR:
                image.fillTile(x + 1, y + 1, "whiterook.png");
                break;
            case WC:
                image.fillTile(x + 1, y + 1, "whitecannon.png");
                break;
            case WP:
                image.fillTile(x + 1, y + 1, "whitepawn.png");
                break;
            case BK:
                image.fillTile(x + 1, y + 1, "blackking.png");
                break;
            case BQ:
                image.fillTile(x + 1, y + 1, "blackqueen.png");
                break;
            case BB:
                image.fillTile(x + 1, y + 1, "blackbishop.png");
                break;
            case BN:
                image.fillTile(x + 1, y + 1, "blackknight.png");
                break;
            case BR:
                image.fillTile(x + 1, y + 1, "blackrook.png");
                break;
            case BC:
                image.fillTile(x + 1, y + 1, "blackcannon.png");
                break;
            case BP:
                image.fillTile(x + 1, y + 1, "blackpawn.png");
                break;
            }
            if (userClicks.contains(point)) {
                image.fillTile(x, y, "selection.png");
            }
            image.addRegion(point, x, y, 6, 6);
        }
        return image;
    }

    public double evaluate() {
        if (generateMoves().size() == 0) {
            return -player;
        }
        double score = 0;
        for (int point = 12; point <= 119; point++) {
            switch (board[point]) {
            case WQ:
                score += 2;
                break;
            case WB:
                score += 2;
                break;
            case WN:
                score += 4;
                break;
            case WR:
                score += 9;
                break;
            case WC:
                score += 4.5;
                break;
            case WP:
                score += point < 66 ? 2 : 1;
                break;
            case BQ:
                score -= 2;
                break;
            case BB:
                score -= 2;
                break;
            case BN:
                score -= 4;
                break;
            case BR:
                score -= 9;
                break;
            case BC:
                score -= 4.5;
                break;
            case BP:
                score -= point >= 66 ? 2 : 1;
                break;
            }
        }
        return score / 64;
    }

    private void generateBishopMoves(int origin, List<GameState> moves) {
        for (int offset : BISHOP_OFFSETS) {
            int intermediate = origin + offset;
            int target = intermediate + offset;
            if (board[intermediate] == EM && capturableOrEmpty(target)
                    && !beyondRiver(target)) {
                XiangqiState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
        }
    }

    private void generateCannonMoves(int origin, List<GameState> moves) {
        for (int offset : CANNON_OFFSETS) {
            int target = origin + offset;
            // non-captures
            while (board[target] == EM) {
                XiangqiState move = makeMove(origin, target);
                addIfLegal(move, moves);
                target += offset;
            }
            // captures
            if (board[target] != LV) {
                target += offset;
                while (board[target] == EM) {
                    target += offset;
                }
                if (capturable(target)) {
                    XiangqiState move = makeMove(origin, target);
                    addIfLegal(move, moves);
                }
            }
        }
    }

    private void generateKingMoves(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int target = origin + offset;
            if (capturableOrEmpty(target) && palace(target)) {
                XiangqiState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
        }
    }

    private void generateKnightMoves(int origin, List<GameState> moves) {
        for (int i = 0; i < 8; i++) {
            int intermediate = origin + KNIGHT_INTERMEDIATES[i];
            int target = origin + KNIGHT_OFFSETS[i];
            if (board[intermediate] == EM && capturableOrEmpty(target)) {
                XiangqiState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
        }
    }

    public List<GameState> generateMoves() {
        List<GameState> moves = new ArrayList<GameState>();
        for (int origin = 12; origin <= 119; origin++) {
            switch (player * board[origin]) {
            case WK:
                generateKingMoves(origin, moves);
                break;
            case WQ:
                generateQueenMoves(origin, moves);
                break;
            case WB:
                generateBishopMoves(origin, moves);
                break;
            case WN:
                generateKnightMoves(origin, moves);
                break;
            case WR:
                generateRookMoves(origin, moves);
                break;
            case WC:
                generateCannonMoves(origin, moves);
                break;
            case WP:
                generatePawnMoves(origin, moves);
                break;
            }
        }
        return moves;
    }

    private void generatePawnMoves(int origin, List<GameState> moves) {
        if (beyondRiver(origin)) {
            for (int offset : PAWN_OFFSETS) {
                int target = origin + player * offset;
                if (capturableOrEmpty(target)) {
                    XiangqiState move = makeMove(origin, target);
                    addIfLegal(move, moves);
                }
            }
        } else {
            int target = origin + player * -11;
            if (capturableOrEmpty(target)) {
                XiangqiState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
        }
    }

    private void generateQueenMoves(int origin, List<GameState> moves) {
        for (int offset : QUEEN_OFFSETS) {
            int target = origin + offset;
            if (capturableOrEmpty(target) && palace(target)) {
                XiangqiState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
        }
    }

    private void generateRookMoves(int origin, List<GameState> moves) {
        for (int offset : ROOK_OFFSETS) {
            int target = origin + offset;
            while (capturableOrEmpty(target)) {
                XiangqiState move = makeMove(origin, target);
                addIfLegal(move, moves);
                if (board[target] != EM) {
                    break;
                }
                target += offset;
            }
        }
    }

    public String getNotation() {
        String notation = "";
        notation += (char)(clicks[0] % 11 + 96);
        notation += 11 - clicks[0] / 11;
        notation += (char)(clicks[1] % 11 + 96);
        notation += 11 - clicks[1] / 11;
        return notation;
    }

    public int getPlayer() {
        return player;
    }

    private boolean legal() {
        return !check(-player);
    }

    private XiangqiState makeMove(int origin, int target) {
        XiangqiState move = new XiangqiState(this);
        move.board[origin] = EM;
        move.board[target] = board[origin];
        if (origin == whiteKing) {
            move.whiteKing = target;
        } else if (origin == blackKing) {
            move.blackKing = target;
        }
        move.clicks = new int[] { origin, target };
        return move;
    }

    private static boolean palace(int point) {
        int row = point / 11 - 1;
        int col = point % 11 - 1;
        return (row < 3 || row >= 7) && col >= 3 && col < 6;
    }
}