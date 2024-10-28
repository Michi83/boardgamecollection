import java.util.ArrayList;
import java.util.List;

public class ShogiState implements GameState {
    private static final int WK = 14; // white king
    private static final int WG = 13; // white gold
    private static final int WPS = 12; // white promoted silver
    private static final int WS = 11; // white silver
    private static final int WPN = 10; // white promoted knight
    private static final int WN = 9; // white knight
    private static final int WPL = 8; // white promoted lance
    private static final int WL = 7; // white lance
    private static final int WPB = 6; // white promoted bishop
    private static final int WB = 5; // white bishop
    private static final int WPR = 4; // white promoted rook
    private static final int WR = 3; // white rook
    private static final int WPP = 2; // white promoted pawn
    private static final int WP = 1; // white pawn
    private static final int EM = 0; // empty
    private static final int BP = -1; // black pawn
    private static final int BPP = -2; // black promoted pawn
    private static final int BR = -3; // black rook
    private static final int BPR = -4; // black promoted rook
    private static final int BB = -5; // black bishop
    private static final int BPB = -6; // black promoted bishop
    private static final int BL = -7; // black lance
    private static final int BPL = -8; // black promoted lance
    private static final int BN = -9; // black knight
    private static final int BPN = -10; // black promoted knight
    private static final int BS = -11; // black silver
    private static final int BPS = -12; // black promoted silver
    private static final int BG = -13; // black gold
    private static final int BK = -14; // black king
    private static final int LV = -15; // lava
    private static final int[] KING_OFFSETS = new int[] {
        -12, -11, -10, -1, 1, 10, 11, 12 };
    private static final int[] GOLD_OFFSETS = new int[] {
        -12, -11, -10, -1, 1, 11 };
    private static final int[] SILVER_OFFSETS = new int[] {
        -12, -11, -10, 10, 12 };
    private static final int[] KNIGHT_OFFSETS = new int[] { -23, -21 };
    private static final int[] PROMOTED_BISHOP_OFFSETS = new int[] {
        -11, -1, 1, 11 };
    private static final int[] BISHOP_OFFSETS = new int[] { -12, -10, 10, 12 };
    private static final int[] PROMOTED_ROOK_OFFSETS = new int[] {
        -12, -10, 10, 12 };
    private static final int[] ROOK_OFFSETS = new int[] { -11, -1, 1, 11 };

    private int blackKing;
    private int[] blackPieces;
    private int[] board;
    private int[] clicks;
    private int player;
    private List<Integer> userClicks;
    private int whiteKing;
    private int[] whitePieces;

    public ShogiState() {
        blackKing = 27;
        blackPieces = new int[14];
        board = new int[] {
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
            LV, BL, BN, BS, BG, BK, BG, BS, BN, BL, LV,
            LV, EM, BR, EM, EM, EM, EM, EM, BB, EM, LV,
            LV, BP, BP, BP, BP, BP, BP, BP, BP, BP, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
            LV, WP, WP, WP, WP, WP, WP, WP, WP, WP, LV,
            LV, EM, WB, EM, EM, EM, EM, EM, WR, EM, LV,
            LV, WL, WN, WS, WG, WK, WG, WS, WN, WL, LV,
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
            LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV
        };
        player = -1;
        whiteKing = 115;
        whitePieces = new int[14];
    }

    private ShogiState(ShogiState that) {
        blackKing = that.blackKing;
        blackPieces = that.blackPieces.clone();
        board = that.board.clone();
        player = -that.player;
        whiteKing = that.whiteKing;
        whitePieces = that.whitePieces.clone();
    }

    private static void addIfLegal(ShogiState state, List<GameState> states) {
        if (state.legal()) {
            states.add(state);
        }
    }

    private boolean attacked(int target, int attacker) {
        return attackedByKing(target, attacker)
            || attackedByGold(target, attacker)
            || attackedByPromotedSilver(target, attacker)
            || attackedBySilver(target, attacker)
            || attackedByPromotedKnight(target, attacker)
            || attackedByKnight(target, attacker)
            || attackedByPromotedLance(target, attacker)
            || attackedByLance(target, attacker)
            || attackedByPromotedBishop(target, attacker)
            || attackedByBishop(target, attacker)
            || attackedByPromotedRook(target, attacker)
            || attackedByRook(target, attacker)
            || attackedByPromotedPawn(target, attacker)
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

    private boolean attackedByGold(int target, int attacker) {
        for (int offset : GOLD_OFFSETS) {
            int origin = target - attacker * offset;
            if (board[origin] == attacker * WG) {
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
            int origin = target - attacker * offset;
            if (board[origin] == attacker * WN) {
                return true;
            }
        }
        return false;
    }

    private boolean attackedByLance(int target, int attacker) {
        int origin = target - attacker * -11;
        while (board[origin] == EM) {
            origin -= attacker * -11;
        }
        if (board[origin] == attacker * WL) {
            return true;
        }
        return false;
    }

    private boolean attackedByPawn(int target, int attacker) {
        int origin = target - attacker * -11;
        if (board[origin] == attacker * WP) {
            return true;
        }
        return false;
    }

    private boolean attackedByPromotedBishop(int target, int attacker) {
        for (int offset : BISHOP_OFFSETS) {
            int origin = target - offset;
            while (board[origin] == EM) {
                origin -= offset;
            }
            if (board[origin] == attacker * WPB) {
                return true;
            }
        }
        for (int offset : PROMOTED_BISHOP_OFFSETS) {
            int origin = target - offset;
            if (board[origin] == attacker * WPB) {
                return true;
            }
        }
        return false;
    }

    private boolean attackedByPromotedKnight(int target, int attacker) {
        for (int offset : GOLD_OFFSETS) {
            int origin = target - attacker * offset;
            if (board[origin] == attacker * WPN) {
                return true;
            }
        }
        return false;
    }

    private boolean attackedByPromotedLance(int target, int attacker) {
        for (int offset : GOLD_OFFSETS) {
            int origin = target - attacker * offset;
            if (board[origin] == attacker * WPL) {
                return true;
            }
        }
        return false;
    }

    private boolean attackedByPromotedPawn(int target, int attacker) {
        for (int offset : GOLD_OFFSETS) {
            int origin = target - attacker * offset;
            if (board[origin] == attacker * WPP) {
                return true;
            }
        }
        return false;
    }

    private boolean attackedByPromotedRook(int target, int attacker) {
        for (int offset : ROOK_OFFSETS) {
            int origin = target - offset;
            while (board[origin] == EM) {
                origin -= offset;
            }
            if (board[origin] == attacker * WPR) {
                return true;
            }
        }
        for (int offset : PROMOTED_ROOK_OFFSETS) {
            int origin = target - offset;
            if (board[origin] == attacker * WPR) {
                return true;
            }
        }
        return false;
    }

    private boolean attackedByPromotedSilver(int target, int attacker) {
        for (int offset : GOLD_OFFSETS) {
            int origin = target - attacker * offset;
            if (board[origin] == attacker * WPS) {
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

    private boolean attackedBySilver(int target, int attacker) {
        for (int offset : SILVER_OFFSETS) {
            int origin = target - attacker * offset;
            if (board[origin] == attacker * WS) {
                return true;
            }
        }
        return false;
    }

    private boolean canDrop(int piece, int target) {
        int row = target / 11 - 2;
        switch (piece) {
        case WN:
            return row > 1;
        case WL:
            return row > 0;
        case WP:
            return row > 0 && canDropPawn(target);
        case BN:
            return row < 7;
        case BL:
            return row < 8;
        case BP:
            return row < 8 && canDropPawn(target);
        default:
            return true;
        }
    }

    private boolean canDropPawn(int target) {
        int col = target % 11 - 1;
        for (int row = 0; row < 9; row++) {
            int square = 11 * row + col + 23;
            // Note that players have the same numbers as their unpromoted
            // pawns.
            if (board[square] == player) {
                return false;
            }
        }
        return true;
    }

    private boolean canPromote(int origin, int target) {
        int row1 = origin / 11 - 2;
        int row2 = target / 11 - 2;
        switch (board[origin]) {
        case WS:
        case WN:
        case WL:
        case WB:
        case WR:
        case WP:
            return row1 < 3 || row2 < 3;
        case BS:
        case BN:
        case BL:
        case BB:
        case BR:
        case BP:
            return row1 > 5 || row2 > 5;
        default:
            // kings, golds and already promoted pieces
            return false;
        }
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
        userClicks.add(id);
        List<GameState> moves = generateMoves();
        for (GameState move : moves) {
            ShogiState shogiMove = (ShogiState)move;
            if (userClicks.size() <= shogiMove.clicks.length) {
                boolean match = true;
                for (int i = 0; i < userClicks.size(); i++) {
                    if (userClicks.get(i) != shogiMove.clicks[i]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    if (userClicks.size() == shogiMove.clicks.length) {
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
        image.fillTile(0, 0, "shogi.png");
        for (int square = 23; square <= 119; square++) {
            if (board[square] == LV) {
                continue;
            }
            int row = square / 11 - 2;
            int col = square % 11 - 1;
            int x = 6 * col + 5;
            int y = 6 * row + 5;
            switch (board[square]) {
            case WK:
                image.fillTile(x + 1, y + 1, "whiteshogiking.png");
                break;
            case WG:
                image.fillTile(x + 1, y + 1, "whiteshogigold.png");
                break;
            case WPS:
                image.fillTile(x + 1, y + 1, "whitepromotedsilver.png");
                break;
            case WS:
                image.fillTile(x + 1, y + 1, "whiteshogisilver.png");
                break;
            case WPN:
                image.fillTile(x + 1, y + 1, "whitepromotedknight.png");
                break;
            case WN:
                image.fillTile(x + 1, y + 1, "whiteshogiknight.png");
                break;
            case WPL:
                image.fillTile(x + 1, y + 1, "whitepromotedlance.png");
                break;
            case WL:
                image.fillTile(x + 1, y + 1, "whiteshogilance.png");
                break;
            case WPB:
                image.fillTile(x + 1, y + 1, "whitepromotedbishop.png");
                break;
            case WB:
                image.fillTile(x + 1, y + 1, "whiteshogibishop.png");
                break;
            case WPR:
                image.fillTile(x + 1, y + 1, "whitepromotedrook.png");
                break;
            case WR:
                image.fillTile(x + 1, y + 1, "whiteshogirook.png");
                break;
            case WPP:
                image.fillTile(x + 1, y + 1, "whitepromotedpawn.png");
                break;
            case WP:
                image.fillTile(x + 1, y + 1, "whiteshogipawn.png");
                break;
            case BK:
                image.fillTile(x + 1, y + 1, "blackshogiking.png");
                break;
            case BG:
                image.fillTile(x + 1, y + 1, "blackshogigold.png");
                break;
            case BPS:
                image.fillTile(x + 1, y + 1, "blackpromotedsilver.png");
                break;
            case BS:
                image.fillTile(x + 1, y + 1, "blackshogisilver.png");
                break;
            case BPN:
                image.fillTile(x + 1, y + 1, "blackpromotedknight.png");
                break;
            case BN:
                image.fillTile(x + 1, y + 1, "blackshogiknight.png");
                break;
            case BPL:
                image.fillTile(x + 1, y + 1, "blackpromotedlance.png");
                break;
            case BL:
                image.fillTile(x + 1, y + 1, "blackshogilance.png");
                break;
            case BPB:
                image.fillTile(x + 1, y + 1, "blackpromotedbishop.png");
                break;
            case BB:
                image.fillTile(x + 1, y + 1, "blackshogibishop.png");
                break;
            case BPR:
                image.fillTile(x + 1, y + 1, "blackpromotedrook.png");
                break;
            case BR:
                image.fillTile(x + 1, y + 1, "blackshogirook.png");
                break;
            case BPP:
                image.fillTile(x + 1, y + 1, "blackpromotedpawn.png");
                break;
            case BP:
                image.fillTile(x + 1, y + 1, "blackshogipawn.png");
                break;
            }
            if (userClicks.contains(square)) {
                image.fillTile(x, y, "selection.png");
            }
            image.addRegion(square, x, y, 6, 6);
        }
        // show drop options
        if (whitePieces[WG] > 0) {
            image.fillTile(12, 60, "whiteshogigold.png");
            image.addRegion(123, 11, 59, 6, 5);
            if (userClicks.contains(123)) {
                image.fillTile(11, 59, "selection.png");
            }
        }
        if (whitePieces[WS] > 0) {
            image.fillTile(18, 60, "whiteshogisilver.png");
            image.addRegion(124, 17, 59, 6, 5);
            if (userClicks.contains(124)) {
                image.fillTile(17, 59, "selection.png");
            }
        }
        if (whitePieces[WN] > 0) {
            image.fillTile(24, 60, "whiteshogiknight.png");
            image.addRegion(125, 23, 59, 6, 5);
            if (userClicks.contains(125)) {
                image.fillTile(23, 59, "selection.png");
            }
        }
        if (whitePieces[WL] > 0) {
            image.fillTile(30, 60, "whiteshogilance.png");
            image.addRegion(126, 29, 59, 6, 5);
            if (userClicks.contains(126)) {
                image.fillTile(29, 59, "selection.png");
            }
        }
        if (whitePieces[WB] > 0) {
            image.fillTile(36, 60, "whiteshogibishop.png");
            image.addRegion(127, 35, 59, 6, 5);
            if (userClicks.contains(127)) {
                image.fillTile(35, 59, "selection.png");
            }
        }
        if (whitePieces[WR] > 0) {
            image.fillTile(42, 60, "whiteshogirook.png");
            image.addRegion(128, 41, 59, 6, 5);
            if (userClicks.contains(128)) {
                image.fillTile(41, 59, "selection.png");
            }
        }
        if (whitePieces[WP] > 0) {
            image.fillTile(48, 60, "whiteshogipawn.png");
            image.addRegion(129, 47, 59, 6, 5);
            if (userClicks.contains(129)) {
                image.fillTile(47, 59, "selection.png");
            }
        }
        if (blackPieces[WG] > 0) {
            image.fillTile(48, 0, "blackshogigold.png");
            image.addRegion(19, 47, 0, 6, 5);
            if (userClicks.contains(19)) {
                image.fillTile(47, -1, "selection.png");
            }
        }
        if (blackPieces[WS] > 0) {
            image.fillTile(42, 0, "blackshogisilver.png");
            image.addRegion(18, 41, 0, 6, 5);
            if (userClicks.contains(18)) {
                image.fillTile(41, -1, "selection.png");
            }
        }
        if (blackPieces[WN] > 0) {
            image.fillTile(36, 0, "blackshogiknight.png");
            image.addRegion(17, 35, 0, 6, 5);
            if (userClicks.contains(17)) {
                image.fillTile(35, -1, "selection.png");
            }
        }
        if (blackPieces[WL] > 0) {
            image.fillTile(30, 0, "blackshogilance.png");
            image.addRegion(16, 29, 0, 6, 5);
            if (userClicks.contains(16)) {
                image.fillTile(29, -1, "selection.png");
            }
        }
        if (blackPieces[WB] > 0) {
            image.fillTile(24, 0, "blackshogibishop.png");
            image.addRegion(15, 23, 0, 6, 5);
            if (userClicks.contains(15)) {
                image.fillTile(23, -1, "selection.png");
            }
        }
        if (blackPieces[WR] > 0) {
            image.fillTile(18, 0, "blackshogirook.png");
            image.addRegion(14, 17, 0, 6, 5);
            if (userClicks.contains(14)) {
                image.fillTile(17, -1, "selection.png");
            }
        }
        if (blackPieces[WP] > 0) {
            image.fillTile(12, 0, "blackshogipawn.png");
            image.addRegion(13, 11, 0, 6, 5);
            if (userClicks.contains(13)) {
                image.fillTile(11, -1, "selection.png");
            }
        }
        // show promotion options
        if (userClicks.size() == 2) {
            int origin = userClicks.get(0);
            int target = userClicks.get(1);
            switch (board[origin]) {
            case WS:
                image.fillTile(60, 26, "whitepromotedsilver.png");
                image.fillTile(60, 32, "whiteshogisilver.png");
                break;
            case WN:
                image.fillTile(60, 26, "whitepromotedknight.png");
                break;
            case WL:
                image.fillTile(60, 26, "whitepromotedlance.png");
                break;
            case WB:
                image.fillTile(60, 26, "whitepromotedbishop.png");
                image.fillTile(60, 32, "whiteshogibishop.png");
                break;
            case WR:
                image.fillTile(60, 26, "whitepromotedrook.png");
                image.fillTile(60, 32, "whiteshogirook.png");
                break;
            case WP:
                image.fillTile(60, 26, "whitepromotedpawn.png");
                break;
            case BS:
                image.fillTile(0, 32, "blackpromotedsilver.png");
                image.fillTile(0, 26, "blackshogisilver.png");
                break;
            case BN:
                image.fillTile(0, 32, "blackpromotedknight.png");
                break;
            case BL:
                image.fillTile(0, 32, "blackpromotedlance.png");
                break;
            case BB:
                image.fillTile(0, 32, "blackpromotedbishop.png");
                image.fillTile(0, 26, "blackshogibishop.png");
                break;
            case BR:
                image.fillTile(0, 32, "blackpromotedrook.png");
                image.fillTile(0, 26, "blackshogirook.png");
                break;
            case BP:
                image.fillTile(0, 32, "blackpromotedpawn.png");
                break;
            }
            switch (player) {
            case 1:
                image.addRegion(1, 59, 26, 5, 6);
                break;
            case -1:
                image.addRegion(1, 0, 32, 5, 6);
                break;
            }
            if (!mustPromote(origin, target)) {
                switch (board[origin]) {
                case WN:
                    image.fillTile(60, 32, "whiteshogiknight.png");
                    break;
                case WL:
                    image.fillTile(60, 32, "whiteshogilance.png");
                    break;
                case WP:
                    image.fillTile(60, 32, "whiteshogipawn.png");
                    break;
                case BN:
                    image.fillTile(0, 26, "blackshogiknight.png");
                    break;
                case BL:
                    image.fillTile(0, 26, "blackshogilance.png");
                    break;
                case BP:
                    image.fillTile(0, 26, "blackshogipawn.png");
                    break;
                }
                switch (player) {
                case 1:
                    image.addRegion(0, 59, 32, 5, 6);
                    break;
                case -1:
                    image.addRegion(0, 0, 26, 5, 6);
                    break;
                }
            }
        }
        return image;
    }

    public double evaluate() {
        if (generateMoves().size() == 0) {
            return -player;
        }
        int score = 0;
        for (int square = 23; square <= 119; square++) {
            switch (board[square]) {
            case WG:
            case WPS:
            case WPN:
            case WPL:
            case WPP:
                score += 4;
                break;
            case WS:
                score += 3;
                break;
            case WN:
                score += 2;
                break;
            case WL:
                score += 2;
                break;
            case WPB:
                score += 7;
                break;
            case WB:
                score += 5;
                break;
            case WPR:
                score += 8;
                break;
            case WR:
                score += 6;
                break;
            case WP:
                score += 1;
                break;
            case BG:
            case BPS:
            case BPN:
            case BPL:
            case BPP:
                score -= 4;
                break;
            case BS:
                score -= 3;
                break;
            case BN:
                score -= 2;
                break;
            case BL:
                score -= 2;
                break;
            case BPB:
                score -= 7;
                break;
            case BB:
                score -= 5;
                break;
            case BPR:
                score -= 8;
                break;
            case BR:
                score -= 6;
                break;
            case BP:
                score -= 1;
                break;
            }
        }
        return score / 256.0;
    }

    private void generateBishopMoves(int origin, List<GameState> moves) {
        for (int offset : BISHOP_OFFSETS) {
            int target = origin + offset;
            while (capturableOrEmpty(target)) {
                if (canPromote(origin, target)) {
                    ShogiState move;
                    move = makeMove(origin, target, 1);
                    addIfLegal(move, moves);
                    move = makeMove(origin, target, 0);
                    addIfLegal(move, moves);
                } else {
                    ShogiState move = makeMove(origin, target);
                    addIfLegal(move, moves);
                }
                if (board[target] != EM) {
                    break;
                }
                target += offset;
            }
        }
    }

    private void generateDrops(List<GameState> moves) {
        for (int target = 23; target <= 119; target++) {
            if (board[target] == EM) {
                generateDropsTo(target, moves);
            }
        }
    }

    private void generateDropsTo(int target, List<GameState> moves) {
        int[] pieces = player == 1 ? whitePieces : blackPieces;
        for (int piece = WG; piece >= WP; piece--) {
            if (pieces[piece] > 0 && canDrop(player * piece, target)) {
                ShogiState move = makeDrop(player * piece, target);
                addIfLegal(move, moves);
            }
        }
    }

    private void generateGoldMoves(int origin, List<GameState> moves) {
        for (int offset : GOLD_OFFSETS) {
            int target = origin + player * offset;
            if (capturableOrEmpty(target)) {
                ShogiState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
        }
    }

    private void generateKingMoves(int origin, List<GameState> moves) {
        for (int offset : KING_OFFSETS) {
            int target = origin + offset;
            if (capturableOrEmpty(target)) {
                ShogiState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
        }
    }

    private void generateKnightMoves(int origin, List<GameState> moves) {
        for (int offset : KNIGHT_OFFSETS) {
            int target = origin + player * offset;
            if (capturableOrEmpty(target)) {
                if (canPromote(origin, target)) {
                    ShogiState move;
                    move = makeMove(origin, target, 1);
                    addIfLegal(move, moves);
                    if (!mustPromote(origin, target)) {
                        move = makeMove(origin, target, 0);
                        addIfLegal(move, moves);
                    }
                } else {
                    ShogiState move = makeMove(origin, target);
                    addIfLegal(move, moves);
                }
            }
        }
    }

    private void generateLanceMoves(int origin, List<GameState> moves) {
        int target = origin + player * -11;
        while (capturableOrEmpty(target)) {
            if (canPromote(origin, target)) {
                ShogiState move;
                move = makeMove(origin, target, 1);
                addIfLegal(move, moves);
                if (!mustPromote(origin, target)) {
                    move = makeMove(origin, target, 0);
                    addIfLegal(move, moves);
                }
            } else {
                ShogiState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
            if (board[target] != EM) {
                break;
            }
            target += player * -11;
        }
    }

    public List<GameState> generateMoves() {
        return generateMoves(true);
    }

    private List<GameState> generateMoves(boolean filterPawnDropCheckmates) {
        List<GameState> moves = new ArrayList<GameState>();
        for (int origin = 23; origin <= 119; origin++) {
            switch (player * board[origin]) {
            case WK:
                generateKingMoves(origin, moves);
                break;
            case WG:
            case WPS:
            case WPN:
            case WPL:
            case WPP:
                generateGoldMoves(origin, moves);
                break;
            case WS:
                generateSilverMoves(origin, moves);
                break;
            case WN:
                generateKnightMoves(origin, moves);
                break;
            case WL:
                generateLanceMoves(origin, moves);
                break;
            case WPB:
                generatePromotedBishopMoves(origin, moves);
                break;
            case WB:
                generateBishopMoves(origin, moves);
                break;
            case WPR:
                generatePromotedRookMoves(origin, moves);
                break;
            case WR:
                generateRookMoves(origin, moves);
                break;
            case WP:
                generatePawnMoves(origin, moves);
                break;
            }
        }
        generateDrops(moves);
        // I hate the pawn-drop checkmate rule and I really hope I got this
        // right.
        if (filterPawnDropCheckmates) {
            List<GameState> filteredMoves = new ArrayList<GameState>();
            for (GameState move : moves) {
                ShogiState shogiMove = (ShogiState)move;
                if (shogiMove.clicks[0] == 129 || shogiMove.clicks[0] == 13) {
                    if (shogiMove.generateMoves(false).size() == 0) {
                        continue;
                    }
                }
                filteredMoves.add(move);
            }
            moves = filteredMoves;
        }
        return moves;
    }

    private void generatePawnMoves(int origin, List<GameState> moves) {
        int target = origin + player * -11;
        if (capturableOrEmpty(target)) {
            if (canPromote(origin, target)) {
                ShogiState move;
                move = makeMove(origin, target, 1);
                addIfLegal(move, moves);
                if (!mustPromote(origin, target)) {
                    move = makeMove(origin, target, 0);
                    addIfLegal(move, moves);
                }
            } else {
                ShogiState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
        }
    }

    private void generatePromotedBishopMoves(int origin,
            List<GameState> moves) {
        generateBishopMoves(origin, moves);
        for (int offset : PROMOTED_BISHOP_OFFSETS) {
            int target = origin + offset;
            if (capturableOrEmpty(target)) {
                ShogiState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
        }
    }

    private void generatePromotedRookMoves(int origin, List<GameState> moves) {
        generateRookMoves(origin, moves);
        for (int offset : PROMOTED_ROOK_OFFSETS) {
            int target = origin + offset;
            if (capturableOrEmpty(target)) {
                ShogiState move = makeMove(origin, target);
                addIfLegal(move, moves);
            }
        }
    }

    private void generateRookMoves(int origin, List<GameState> moves) {
        for (int offset : ROOK_OFFSETS) {
            int target = origin + offset;
            while (capturableOrEmpty(target)) {
                if (canPromote(origin, target)) {
                    ShogiState move;
                    move = makeMove(origin, target, 1);
                    addIfLegal(move, moves);
                    move = makeMove(origin, target, 0);
                    addIfLegal(move, moves);
                } else {
                    ShogiState move = makeMove(origin, target);
                    addIfLegal(move, moves);
                }
                if (board[target] != EM) {
                    break;
                }
                target += offset;
            }
        }
    }

    private void generateSilverMoves(int origin, List<GameState> moves) {
        for (int offset : SILVER_OFFSETS) {
            int target = origin + player * offset;
            if (capturableOrEmpty(target)) {
                if (canPromote(origin, target)) {
                    ShogiState move;
                    move = makeMove(origin, target, 1);
                    addIfLegal(move, moves);
                    move = makeMove(origin, target, 0);
                    addIfLegal(move, moves);
                } else {
                    ShogiState move = makeMove(origin, target);
                    addIfLegal(move, moves);
                }
            }
        }
    }

    public String getNotation() {
        String notation = "";
        switch (clicks[0]) {
        case 123:
        case 19:
            notation += "G";
            break;
        case 124:
        case 18:
            notation += "S";
            break;
        case 125:
        case 17:
            notation += "N";
            break;
        case 126:
        case 16:
            notation += "L";
            break;
        case 127:
        case 15:
            notation += "B";
            break;
        case 128:
        case 14:
            notation += "R";
            break;
        case 129:
        case 13:
            notation += "P";
            break;
        default:
            notation += (char)(clicks[0] % 11 + 96);
            notation += 11 - clicks[0] / 11;
            break;
        }
        notation += (char)(clicks[1] % 11 + 96);
        notation += 11 - clicks[1] / 11;
        if (clicks.length == 3 && clicks[2] == 1) {
            notation += "p";
        }
        return notation;
    }

    public int getPlayer() {
        return player;
    }

    private boolean legal() {
        return !check(-player);
    }

    private ShogiState makeDrop(int piece, int target) {
        ShogiState move = new ShogiState(this);
        move.board[target] = piece;
        int origin = 0;
        // update players' prisoners
        // drops have special pseudo-origins on the player's "zeroth" rank
        switch (piece) {
        case WG:
            move.whitePieces[WG]--;
            origin = 123;
            break;
        case WS:
            move.whitePieces[WS]--;
            origin = 124;
            break;
        case WN:
            move.whitePieces[WN]--;
            origin = 125;
            break;
        case WL:
            move.whitePieces[WL]--;
            origin = 126;
            break;
        case WB:
            move.whitePieces[WB]--;
            origin = 127;
            break;
        case WR:
            move.whitePieces[WR]--;
            origin = 128;
            break;
        case WP:
            move.whitePieces[WP]--;
            origin = 129;
            break;
        case BG:
            move.blackPieces[WG]--;
            origin = 19;
            break;
        case BS:
            move.blackPieces[WS]--;
            origin = 18;
            break;
        case BN:
            move.blackPieces[WN]--;
            origin = 17;
            break;
        case BL:
            move.blackPieces[WL]--;
            origin = 16;
            break;
        case BB:
            move.blackPieces[WB]--;
            origin = 15;
            break;
        case BR:
            move.blackPieces[WR]--;
            origin = 14;
            break;
        case BP:
            move.blackPieces[WP]--;
            origin = 13;
            break;
        }
        move.clicks = new int[] { origin, target };
        return move;
    }

    private ShogiState makeMove(int origin, int target) {
        ShogiState move = new ShogiState(this);
        move.board[origin] = EM;
        move.board[target] = board[origin];
        // update players' prisoners
        switch (board[target]) {
        case WG:
            move.blackPieces[WG]++;
            break;
        case WPS:
        case WS:
            move.blackPieces[WS]++;
            break;
        case WPN:
        case WN:
            move.blackPieces[WN]++;
            break;
        case WPL:
        case WL:
            move.blackPieces[WL]++;
            break;
        case WPB:
        case WB:
            move.blackPieces[WB]++;
            break;
        case WPR:
        case WR:
            move.blackPieces[WR]++;
            break;
        case WPP:
        case WP:
            move.blackPieces[WP]++;
            break;
        case BG:
            move.whitePieces[WG]++;
            break;
        case BPS:
        case BS:
            move.whitePieces[WS]++;
            break;
        case BPN:
        case BN:
            move.whitePieces[WN]++;
            break;
        case BPL:
        case BL:
            move.whitePieces[WL]++;
            break;
        case BPB:
        case BB:
            move.whitePieces[WB]++;
            break;
        case BPR:
        case BR:
            move.whitePieces[WR]++;
            break;
        case BPP:
        case BP:
            move.whitePieces[WP]++;
            break;
        }
        // update king positions
        if (origin == whiteKing) {
            move.whiteKing = target;
        } else if (origin == blackKing) {
            move.blackKing = target;
        }
        move.clicks = new int[] { origin, target };
        return move;
    }

    private ShogiState makeMove(int origin, int target, int promotion) {
        ShogiState move = new ShogiState(this);
        move.board[origin] = EM;
        if (promotion == 1) {
            // promotion == 1 -> promotion
            move.board[target] = board[origin] + player;
        } else {
            // promotion == 0 -> no promotion (voluntary)
            move.board[target] = board[origin];
        }
        // update players' prisoners
        switch (board[target]) {
        case WG:
            move.blackPieces[WG]++;
            break;
        case WPS:
        case WS:
            move.blackPieces[WS]++;
            break;
        case WPN:
        case WN:
            move.blackPieces[WN]++;
            break;
        case WPL:
        case WL:
            move.blackPieces[WL]++;
            break;
        case WPB:
        case WB:
            move.blackPieces[WB]++;
            break;
        case WPR:
        case WR:
            move.blackPieces[WR]++;
            break;
        case WPP:
        case WP:
            move.blackPieces[WP]++;
            break;
        case BG:
            move.whitePieces[WG]++;
            break;
        case BPS:
        case BS:
            move.whitePieces[WS]++;
            break;
        case BPN:
        case BN:
            move.whitePieces[WN]++;
            break;
        case BPL:
        case BL:
            move.whitePieces[WL]++;
            break;
        case BPB:
        case BB:
            move.whitePieces[WB]++;
            break;
        case BPR:
        case BR:
            move.whitePieces[WR]++;
            break;
        case BPP:
        case BP:
            move.whitePieces[WP]++;
            break;
        }
        move.clicks = new int[] { origin, target, promotion };
        return move;
    }

    private boolean mustPromote(int origin, int target) {
        int row = target / 11 - 2;
        switch (board[origin]) {
        case WN:
            return row < 2;
        case WL:
        case WP:
            return row == 0;
        case BN:
            return row > 6;
        case BL:
        case BP:
            return row == 8;
        default:
            // all other pieces are never forced to promote.
            return false;
        }
    }
}