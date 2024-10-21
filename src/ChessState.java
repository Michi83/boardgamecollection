import java.util.ArrayList;
import java.util.List;

public class ChessState implements GameState {
    private static final int WK = 6; // white king
    private static final int WQ = 5; // white queen
    private static final int WB = 4; // white bishop
    private static final int WN = 3; // white knight
    private static final int WR = 2; // white rook
    private static final int WP = 1; // white pawn
    private static final int EM = 0; // empty
    private static final int BP = -1; // black pawn
    private static final int BR = -2; // black rook
    private static final int BN = -3; // black knight
    private static final int BB = -4; // black bishop
    private static final int BQ = -5; // black queen
    private static final int BK = -6; // black king
    private static final int LV = -7; // lava
    private static final int[] KING_OFFSETS = new int[] {
        -11, -10, -9, -1, 1, 9, 10, 11 };
    private static final int[] QUEEN_OFFSETS = new int[] {
        -11, -10, -9, -1, 1, 9, 10, 11 };
    private static final int[] BISHOP_OFFSETS = new int[] { -11, -9, 9, 11 };
    private static final int[] KNIGHT_OFFSETS = new int[] {
        -21, -19, -12, -8, 8, 12, 19, 21 };
    private static final int[] ROOK_OFFSETS = new int[] { -10, -1, 1, 10 };
    private static final int[] PAWN_OFFSETS = new int[] { -11, -9 };
    private static final int[] PROMOTIONS = new int[] { WQ, WB, WN, WR };

    private int blackKing;
    private int[] board;
    private boolean[] castling;
    private int[] clicks;
    private int enPassant;
    private int player;
    private List<Integer> userClicks;
    private int whiteKing;

    public ChessState() {
        blackKing = 25;
        // For many games it is a good idea to surround the board with a border
        // of "lava" squares. Moves across the edge can then be handled more
        // easily. Note that the board is actually one-dimensional!
        board = new int[] {
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
            LV, BR, BN, BB, BQ, BK, BB, BN, BR, LV,
            LV, BP, BP, BP, BP, BP, BP, BP, BP, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, WP, WP, WP, WP, WP, WP, WP, WP, LV,
            LV, WR, WN, WB, WQ, WK, WB, WN, WR, LV,
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV
        };
        castling = new boolean[] { true, true, true, true };
        enPassant = -1;
        player = 1;
        userClicks = new ArrayList<Integer>();
        whiteKing = 95;
    }

    private ChessState(ChessState that) {
        blackKing = that.blackKing;
        board = that.board.clone();
        castling = that.castling.clone();
        enPassant = -1;
        player = -that.player;
        userClicks = new ArrayList<Integer>();
        whiteKing = that.whiteKing;
    }

    private static void addIfLegal(ChessState state, List<GameState> states) {
        if (state.legal()) {
            states.add(state);
        }
    }

    private boolean attacked(int target, int attacker) {
        return attackedByKing(target, attacker)
            || attackedByQueen(target, attacker)
            || attackedByBishop(target, attacker)
            || attackedByKnight(target, attacker)
            || attackedByRook(target, attacker)
            || attackedByPawn(target, attacker);
    }

    private boolean attackedByBishop(int target, int attacker) {
        for (int offset : BISHOP_OFFSETS) {
            int origin = target - offset;
            while (board[origin] == EM) {
                origin -= offset;
            }
            if (board[origin] == attacker * WB) {
                return true;
            }
        }
        return false;
    }

    private boolean attackedByKing(int target, int attacker) {
        for (int offset : KING_OFFSETS) {
            int origin = target - offset;
            if (board[origin] == attacker * WK) {
                return true;
            }
        }
        return false;
    }

    private boolean attackedByKnight(int target, int attacker) {
        for (int offset : KNIGHT_OFFSETS) {
            int origin = target - offset;
            if (board[origin] == attacker * WN) {
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

    private boolean attackedByQueen(int target, int attacker) {
        for (int offset : QUEEN_OFFSETS) {
            int origin = target - offset;
            while (board[origin] == EM) {
                origin -= offset;
            }
            if (board[origin] == attacker * WQ) {
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

    private boolean capturable(int square) {
        return board[square] != LV && player * board[square] < EM;
    }

    private boolean capturableOrEmpty(int square) {
        return board[square] != LV && player * board[square] <= EM;
    }

    private boolean check(int defender) {
        if (defender == 1) {
            return attacked(whiteKing, -1);
        } else {
            return attacked(blackKing, 1);
        }
    }

    public GameState click(int id) {
        // The idea is that the state collects the squares clicked by the user
        // in userClicks and compares them to the clicks necessary to select a
        // particular move.
        userClicks.add(id);
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            ChessState chessMove = (ChessState)move;
            if (userClicks.size() <= chessMove.clicks.length) {
                boolean match = true;
                for (int i = 0; i < userClicks.size(); i++) {
                    if (userClicks.get(i) != chessMove.clicks[i]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    if (userClicks.size() == chessMove.clicks.length) {
                        return chessMove; // full match
                    } else {
                        return this; // partial match
                    }
                }
            }
        }
        // No match, assuming user wants to cancel previous clicks.
        userClicks.clear();
        return this;
    }

    private boolean doubleStep(int square) {
        if (player == 1) {
            return square / 10 == 8;
        } else {
            return square / 10 == 3;
        }
    }

    public GameImage draw() {
        GameImage image = new GameImage();
        image.fillTile(0, 0, "chess.png");
        for (int square = 21; square <= 98; square++) {
            int row = square / 10 - 2;
            int col = square % 10 - 1;
            int x = 6 * col + 8;
            int y = 6 * row + 8;
            switch (board[square]) {
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
                case BP:
                    image.fillTile(x + 1, y + 1, "blackpawn.png");
            }
            if (userClicks.contains(square)) {
                image.fillTile(x, y, "selection.png");
            }
            image.addRegion(square, x, y, 6, 6);
        }
        // show promotion options
        if (userClicks.size() == 2) {
            if (player == 1) {
                image.fillTile(21, 3, "whitequeen.png");
                image.fillTile(27, 3, "whitebishop.png");
                image.fillTile(33, 3, "whiteknight.png");
                image.fillTile(39, 3, "whiterook.png");
                image.addRegion(WQ, 20, 2, 6, 6);
                image.addRegion(WB, 26, 2, 6, 6);
                image.addRegion(WN, 32, 2, 6, 6);
                image.addRegion(WR, 38, 2, 6, 6);
            } else {
                image.fillTile(21, 57, "blackqueen.png");
                image.fillTile(27, 57, "blackbishop.png");
                image.fillTile(33, 57, "blackknight.png");
                image.fillTile(39, 57, "blackrook.png");
                image.addRegion(BQ, 20, 56, 6, 6);
                image.addRegion(BB, 26, 56, 6, 6);
                image.addRegion(BN, 32, 56, 6, 6);
                image.addRegion(BR, 38, 56, 6, 6);
            }
        }
        return image;
    }

    public double evaluate() {
        if (generateMoves().size() == 0) {
            if (check(player)) {
                return -player; // checkmate
            } else {
                return 0; // stalemate
            }
        }
        int score = 0;
        for (int square = 21; square <= 98; square++) {
            switch (board[square]) {
                case WQ:
                    score += 9;
                    break;
                case WB:
                    score += 3;
                    break;
                case WN:
                    score += 3;
                    break;
                case WR:
                    score += 5;
                    break;
                case WP:
                    score++;
                    break;
                case BQ:
                    score -= 9;
                    break;
                case BB:
                    score -= 3;
                    break;
                case BN:
                    score -= 3;
                    break;
                case BR:
                    score -= 5;
                    break;
                case BP:
                    score--;
                    break;
            }
        }
        return score / 256.0;
    }

    private void generateBishopMoves(int origin, List<GameState> moves) {
        for (int offset : BISHOP_OFFSETS) {
            int target = origin + offset;
            while (capturableOrEmpty(target)) {
                ChessState move = makeMove(origin, target);
                addIfLegal(move, moves);
                if (board[target] != EM) {
                    break;
                }
                target += offset;
            }
        }
    }

    private void generateKingMoves(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int target = origin + offset;
            if (capturableOrEmpty(target)) {
                ChessState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
        }
        // castling
        if (player == 1) {
            // white kingside castling
            if (castling[0] && board[96] == EM && board[97] == EM &&
                    !attacked(95, -1) && !attacked(96, -1)) {
                ChessState move = makeMove(95, 97);
                move.board[98] = EM;
                move.board[96] = WR;
                addIfLegal(move, moves);
            }
            // white queenside castling
            if (castling[1] && board[94] == EM && board[93] == EM
                    && board[92] == EM && !attacked(95, -1)
                    && !attacked(94, -1)) {
                ChessState move = makeMove(95, 93);
                move.board[91] = EM;
                move.board[94] = WR;
                addIfLegal(move, moves);
            }
        } else {
            // black kingside castling
            if (castling[2] && board[26] == EM && board[27] == EM &&
                    !attacked(25, 1) && !attacked(26, 1)) {
                ChessState move = makeMove(25, 27);
                move.board[28] = EM;
                move.board[26] = BR;
                addIfLegal(move, moves);
            }
            // black queenside castling
            if (castling[3] && board[24] == EM && board[23] == EM
                    && board[22] == EM && !attacked(25, 1)
                    && !attacked(24, 1)) {
                ChessState move = makeMove(25, 23);
                move.board[21] = EM;
                move.board[24] = BR;
                addIfLegal(move, moves);
            }
        }
    }

    private void generateKnightMoves(int origin, List<GameState> moves) {
        for (int offset : KNIGHT_OFFSETS) {
            int target = origin + offset;
            if (capturableOrEmpty(target)) {
                ChessState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
        }
    }

    public List<GameState> generateMoves() {
        List<GameState> moves = new ArrayList<GameState>();
        for (int origin = 21; origin <= 98; origin++) {
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
                case WP:
                    generatePawnMoves(origin, moves);
            }
        }
        return moves;
    }

    private void generatePawnMoves(int origin, List<GameState> moves) {
        // captures
        for (int offset : PAWN_OFFSETS) {
            int target = origin + player * offset;
            if (capturable(target)) {
                // pawn promotions
                if (promotes(target)) {
                    for (int promotion : PROMOTIONS) {
                        promotion *= player;
                        ChessState move = makeMove(origin, target, promotion);
                        addIfLegal(move, moves);
                    }
                } else {
                    ChessState move = makeMove(origin, target);
                    addIfLegal(move, moves);
                }
            } else if (target == enPassant) {
                // en passant captures
                ChessState move = makeMove(origin, target);
                move.board[enPassant + player * 10] = EM;
                addIfLegal(move, moves);
            }
        }
        // non-captures
        int target = origin + player * -10;
        if (board[target] == EM) {
            // pawn promotions
            if (promotes(target)) {
                for (int promotion : PROMOTIONS) {
                    promotion *= player;
                    ChessState move = makeMove(origin, target, promotion);
                    addIfLegal(move, moves);
                }
            } else {
                ChessState move = makeMove(origin, target);
                addIfLegal(move, moves);
                // double step
                if (doubleStep(origin)) {
                    int enPassant = target;
                    target += player * -10;
                    if (board[target] == EM) {
                        move = makeMove(origin, target);
                        move.enPassant = enPassant;
                        addIfLegal(move, moves);
                    }
                }
            }
        }
    }

    private void generateQueenMoves(int origin, List<GameState> moves) {
        for (int offset : QUEEN_OFFSETS) {
            int target = origin + offset;
            while (capturableOrEmpty(target)) {
                ChessState move = makeMove(origin, target);
                addIfLegal(move, moves);
                if (board[target] != EM) {
                    break;
                }
                target += offset;
            }
        }
    }

    private void generateRookMoves(int origin, List<GameState> moves) {
        for (int offset : ROOK_OFFSETS) {
            int target = origin + offset;
            while (capturableOrEmpty(target)) {
                ChessState move = makeMove(origin, target);
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
        notation += (char)(clicks[0] % 10 + 96); // origin file
        notation += 10 - clicks[0] / 10; // origin rank
        notation += (char)(clicks[1] % 10 + 96); // target file
        notation += 10 - clicks[1] / 10; // target rank
        // promotion
        if (clicks.length == 3) {
            switch (clicks[2]) {
                case WQ:
                case BQ:
                    notation += "q";
                    break;
                case WB:
                case BB:
                    notation += "b";
                    break;
                case WN:
                case BN:
                    notation += "n";
                    break;
                case WR:
                case BR:
                    notation += "r";
            }
        }
        return notation;
    }

    public int getPlayer() {
        return player;
    }

    private boolean legal() {
        return !check(-player);
    }

    private ChessState makeMove(int origin, int target) {
        // copy state
        ChessState move = new ChessState(this);
        // move piece
        move.board[origin] = EM;
        move.board[target] = board[origin];
        // update castling rights
        if (origin == 95 || origin == 98 || target == 98) {
            move.castling[0] = false;
        }
        if (origin == 95 || origin == 91 || target == 91) {
            move.castling[1] = false;
        }
        if (origin == 25 || origin == 28 || target == 28) {
            move.castling[2] = false;
        }
        if (origin == 25 || origin == 21 || target == 21) {
            move.castling[3] = false;
        }
        // update king positions
        if (origin == whiteKing) {
            move.whiteKing = target;
        } else if (origin == blackKing) {
            move.blackKing = target;
        }
        // set clicks
        move.clicks = new int[] { origin, target };
        return move;
    }

    private ChessState makeMove(int origin, int target, int promotion) {
        // copy state
        ChessState move = new ChessState(this);
        // move piece
        move.board[origin] = EM;
        move.board[target] = promotion;
        // update castling rights
        if (origin == 95 || origin == 98 || target == 98) {
            move.castling[0] = false;
        }
        if (origin == 95 || origin == 91 || target == 91) {
            move.castling[1] = false;
        }
        if (origin == 25 || origin == 28 || target == 28) {
            move.castling[2] = false;
        }
        if (origin == 25 || origin == 21 || target == 21) {
            move.castling[3] = false;
        }
        // set clicks
        move.clicks = new int[] { origin, target, promotion };
        return move;
    }

    private boolean promotes(int square) {
        if (player == 1) {
            return square / 10 == 2;
        } else {
            return square / 10 == 9;
        }
    }
}