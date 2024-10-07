import pygame


WQ = 9  # white queen
WK = 8  # white king
WB = 7  # white bishop
WU = 6  # white unicorn
WR = 5  # white rook
WD = 4  # white dragon
WL = 3  # white lion
WN = 2  # white knight
WP = 1  # white pawn
BQ = -9  # black queen
BK = -8  # black king
BB = -7  # black bishop
BU = -6  # black unicorn
BR = -5  # black rook
BD = -4  # black dragon
BL = -3  # black lion
BN = -2  # black knight
BP = -1  # black pawn
EM = 0  # empty
LV = -10  # lava


class CaissaBritanniaState:
    def __init__(self, other=None):
        if other is None:
            self.black_pieces = [0, 0, 10, 0, 0, 0, 0, 0, 0]
            self.black_queen = 34
            self.board = [
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
                LV, LV, BD, BR, BU, BB, BQ, BK, BB, BU, BR, BD, LV, LV,
                LV, LV, EM, BL, EM, EM, EM, EM, EM, EM, BL, EM, LV, LV,
                LV, LV, BP, BP, BP, BP, BP, BP, BP, BP, BP, BP, LV, LV,
                LV, LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV, LV,
                LV, LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV, LV,
                LV, LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV, LV,
                LV, LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV, LV,
                LV, LV, WP, WP, WP, WP, WP, WP, WP, WP, WP, WP, LV, LV,
                LV, LV, EM, WL, EM, EM, EM, EM, EM, EM, WL, EM, LV, LV,
                LV, LV, WD, WR, WU, WB, WQ, WK, WB, WU, WR, WD, LV, LV,
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV
            ]
            self.halfmove_clock = 0
            self.player = 1
            self.white_pieces = [0, 0, 10, 0, 0, 0, 0, 0, 0]
            self.white_queen = 160
        else:
            self.black_pieces = list(other.black_pieces)
            self.black_queen = other.black_queen
            self.board = list(other.board)
            self.halfmove_clock = other.halfmove_clock
            self.player = other.player
            self.white_pieces = list(other.white_pieces)
            self.white_queen = other.white_queen
        self.en_passant = None
        self.user_clicks = []

    def attacked(self, target, attacker):
        if self.attacked_by_queen(target, attacker):
            return True
        if self.attacked_by_king(target, attacker):
            return True
        if self.attacked_by_bishop(target, attacker):
            return True
        if self.attacked_by_unicorn(target, attacker):
            return True
        if self.attacked_by_rook(target, attacker):
            return True
        if self.attacked_by_dragon(target, attacker):
            return True
        if self.attacked_by_lion(target, attacker):
            return True
        if self.attacked_by_knight(target, attacker):
            return True
        if self.attacked_by_pawn(target, attacker):
            return True
        return False

    def attacked_by_bishop(self, target, attacker):
        for offset in (-15, -13, 13, 15):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WB:
                return True
        return False

    def attacked_by_dragon(self, target, attacker):
        for offset in (-30, -28, -26, -2, 2, 26, 28, 30):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WD:
                return True
        return False

    def attacked_by_king(self, target, attacker):
        for offset in (-15, -14, -13, -1, 1, 13, 14, 15):
            origin = target - offset
            if self.board[origin] == attacker * WK:
                return True
        return False

    def attacked_by_knight(self, target, attacker):
        for offset in (-29, -27, -16, -12, 12, 16, 27, 29):
            origin = target - offset
            if self.board[origin] == attacker * WN:
                return True
        return False

    def attacked_by_lion(self, target, attacker):
        for offset in (-15, -14, -13, -1, 1, 13, 14, 15):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            origin -= offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WL:
                return True
        return False

    def attacked_by_pawn(self, target, attacker):
        for offset in (-15, -13):
            origin = target - attacker * offset
            if self.board[origin] == attacker * WP:
                return True
        return False

    def attacked_by_queen(self, target, attacker):
        for offset in (-15, -14, -13, -1, 1, 13, 14, 15):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WQ:
                return True
        return False

    def attacked_by_rook(self, target, attacker):
        for offset in (-14, -1, 1, 14):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WR:
                return True
        return False

    def attacked_by_unicorn(self, target, attacker):
        for offset in (-29, -27, -16, -15, -13, -12, 12, 13, 15, 16, 27, 29):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WU:
                return True
        return False

    def capturable(self, square):
        return (self.board[square] != LV
            and self.player * self.board[square] < EM)

    def capturable_or_empty(self, square):
        return (self.board[square] != LV
            and self.player * self.board[square] <= EM)

    def check(self, defender):
        if defender == 1:
            return self.attacked(self.white_queen, -1)
        else:
            return self.attacked(self.black_queen, 1)

    def click(self, x, y):
        col, row = (x - 192) // 64, (y - 192) // 64
        if row == -1:
            square = 9 - col
        elif row == 10:
            square = col - 9
        else:
            square = 14 * row + col + 30
        self.user_clicks.append(square)
        moves = self.generate_moves()
        for move in moves:
            if len(self.user_clicks) <= len(move.clicks):
                match = True
                for i in range(len(self.user_clicks)):
                    if self.user_clicks[i] != move.clicks[i]:
                        match = False
                        break
                if match:
                    if len(self.user_clicks) == len(move.clicks):
                        return move
                    else:
                        return self
        self.user_clicks = []
        return self

    def double_step(self, square):
        if self.player == 1:
            return square // 14 == 9
        else:
            return square // 14 == 4

    def draw(self, surface):
        grandchess = pygame.image.load("img/png/grandchess.png")
        whitequeen = pygame.image.load("img/png/whitequeen.png")
        whiteking = pygame.image.load("img/png/whiteking.png")
        whitebishop = pygame.image.load("img/png/whitebishop.png")
        whitearchbishop = pygame.image.load("img/png/whitearchbishop.png")
        whiterook = pygame.image.load("img/png/whiterook.png")
        whitechancellor = pygame.image.load("img/png/whitechancellor.png")
        whiteamazon = pygame.image.load("img/png/whiteamazon.png")
        whiteknight = pygame.image.load("img/png/whiteknight.png")
        whitepawn = pygame.image.load("img/png/whitepawn.png")
        blackqueen = pygame.image.load("img/png/blackqueen.png")
        blackking = pygame.image.load("img/png/blackking.png")
        blackbishop = pygame.image.load("img/png/blackbishop.png")
        blackarchbishop = pygame.image.load("img/png/blackarchbishop.png")
        blackrook = pygame.image.load("img/png/blackrook.png")
        blackchancellor = pygame.image.load("img/png/blackchancellor.png")
        blackamazon = pygame.image.load("img/png/blackamazon.png")
        blackknight = pygame.image.load("img/png/blackknight.png")
        blackpawn = pygame.image.load("img/png/blackpawn.png")
        smallselection = pygame.image.load("img/png/smallselection.png")
        surface.blit(grandchess, (0, 0))
        for square in range(30, 166):
            col, row = square % 14 - 2, square // 14 - 2
            x, y = 64 * col + 192, 64 * row + 192
            if self.board[square] == WQ:
                surface.blit(whitequeen, (x, y))
            elif self.board[square] == WK:
                surface.blit(whiteking, (x, y))
            elif self.board[square] == WB:
                surface.blit(whitebishop, (x, y))
            elif self.board[square] == WU:
                surface.blit(whitearchbishop, (x, y))
            elif self.board[square] == WR:
                surface.blit(whiterook, (x, y))
            elif self.board[square] == WD:
                surface.blit(whitechancellor, (x, y))
            elif self.board[square] == WL:
                surface.blit(whiteamazon, (x, y))
            elif self.board[square] == WN:
                surface.blit(whiteknight, (x, y))
            elif self.board[square] == WP:
                surface.blit(whitepawn, (x, y))
            elif self.board[square] == BQ:
                surface.blit(blackqueen, (x, y))
            elif self.board[square] == BK:
                surface.blit(blackking, (x, y))
            elif self.board[square] == BB:
                surface.blit(blackbishop, (x, y))
            elif self.board[square] == BU:
                surface.blit(blackarchbishop, (x, y))
            elif self.board[square] == BR:
                surface.blit(blackrook, (x, y))
            elif self.board[square] == BD:
                surface.blit(blackchancellor, (x, y))
            elif self.board[square] == BL:
                surface.blit(blackamazon, (x, y))
            elif self.board[square] == BN:
                surface.blit(blackknight, (x, y))
            elif self.board[square] == BP:
                surface.blit(blackpawn, (x, y))
            if square in self.user_clicks:
                surface.blit(smallselection, (x, y))
        if len(self.user_clicks) == 2:
            if self.player == 1:
                if self.white_pieces[WK] > 0:
                    surface.blit(whiteking, (256, 128))
                if self.white_pieces[WB] > 0:
                    surface.blit(whitebishop, (320, 128))
                if self.white_pieces[WU] > 0:
                    surface.blit(whitearchbishop, (384, 128))
                if self.white_pieces[WR] > 0:
                    surface.blit(whiterook, (448, 128))
                if self.white_pieces[WD] > 0:
                    surface.blit(whitechancellor, (512, 128))
                if self.white_pieces[WL] > 0:
                    surface.blit(whiteamazon, (576, 128))
                if self.white_pieces[WN] > 0:
                    surface.blit(whiteknight, (640, 128))
            else:
                if self.black_pieces[WK] > 0:
                    surface.blit(blackking, (256, 832))
                if self.black_pieces[WB] > 0:
                    surface.blit(blackbishop, (320, 832))
                if self.black_pieces[WU] > 0:
                    surface.blit(blackarchbishop, (384, 832))
                if self.black_pieces[WR] > 0:
                    surface.blit(blackrook, (448, 832))
                if self.black_pieces[WD] > 0:
                    surface.blit(blackchancellor, (512, 832))
                if self.black_pieces[WL] > 0:
                    surface.blit(blackamazon, (576, 832))
                if self.black_pieces[WN] > 0:
                    surface.blit(blackknight, (640, 832))

    def evaluate(self):
        if self.halfmove_clock >= 100:
            return 0
        if len(self.generate_moves()) == 0:
            if self.check(self.player):
                return -self.player
            else:
                return 0
        score = 0
        for square in range(30, 166):
            if self.board[square] == WK:
                score += 7
            elif self.board[square] == WB:
                score += 5
            elif self.board[square] == WU:
                score += 9
            elif self.board[square] == WR:
                score += 5
            elif self.board[square] == WD:
                score += 6
            elif self.board[square] == WL:
                score += 4
            elif self.board[square] == WN:
                score += 3
            elif self.board[square] == WP:
                score += 1
            elif self.board[square] == BK:
                score -= 7
            elif self.board[square] == BB:
                score -= 5
            elif self.board[square] == BU:
                score -= 9
            elif self.board[square] == BR:
                score -= 5
            elif self.board[square] == BD:
                score -= 6
            elif self.board[square] == BL:
                score -= 4
            elif self.board[square] == BN:
                score -= 3
            elif self.board[square] == BP:
                score -= 1
        return score / 200

    def generate_bishop_moves(self, origin):
        moves = []
        for offset in (-15, -13, 13, 15):
            target = origin + offset
            while self.capturable_or_empty(target):
                move = self.make_move(origin, target)
                moves.append(move)
                if self.board[target] != EM:
                    break
                target += offset
        for offset in (-14, -1, 1, 14):
            target = origin + offset
            if self.board[target] == EM:
                move = self.make_move(origin, target)
                moves.append(move)
        return moves

    def generate_dragon_moves(self, origin):
        moves = []
        for offset in (-30, -28, -26, -2, 2, 26, 28, 30):
            target = origin + offset
            while self.capturable_or_empty(target):
                move = self.make_move(origin, target)
                moves.append(move)
                if self.board[target] != EM:
                    break
                target += offset
        return moves

    def generate_king_moves(self, origin):
        moves = []
        for offset in (-15, -14, -13, -1, 1, 13, 14, 15):
            target = origin + offset
            if self.capturable(target):
                move = self.make_move(origin, target)
                moves.append(move)
            else:
                while self.board[target] == EM:
                    move = self.make_move(origin, target)
                    moves.append(move)
                    target += offset
        return moves

    def generate_knight_moves(self, origin):
        moves = []
        for offset in (-29, -27, -16, -12, 12, 16, 27, 29):
            target = origin + offset
            if self.capturable_or_empty(target):
                move = self.make_move(origin, target)
                moves.append(move)
        return moves

    def generate_lion_moves(self, origin):
        moves = []
        for offset in (-15, -14, -13, -1, 1, 13, 14, 15):
            target = origin + offset
            while self.board[target] == EM:
                move = self.make_move(origin, target)
                moves.append(move)
                target += offset
            target += offset
            while self.board[target] == EM:
                target += offset
            if self.capturable(target):
                move = self.make_move(origin, target)
                moves.append(move)
        return moves

    def generate_moves(self):
        moves = []
        if self.halfmove_clock >= 100:
            return moves
        for origin in range(30, 166):
            piece = self.player * self.board[origin]
            if piece == WQ:
                moves += self.generate_queen_moves(origin)
            elif piece == WK:
                moves += self.generate_king_moves(origin)
            elif piece == WB:
                moves += self.generate_bishop_moves(origin)
            elif piece == WU:
                moves += self.generate_unicorn_moves(origin)
            elif piece == WR:
                moves += self.generate_rook_moves(origin)
            elif piece == WD:
                moves += self.generate_dragon_moves(origin)
            elif piece == WL:
                moves += self.generate_lion_moves(origin)
            elif piece == WN:
                moves += self.generate_knight_moves(origin)
            elif piece == WP:
                moves += self.generate_pawn_moves(origin)
        legal_moves = []
        for move in moves:
            if move.legal():
                legal_moves.append(move)
        return legal_moves

    def generate_pawn_moves(self, origin):
        moves = []
        pieces = self.white_pieces if self.player == 1 else self.black_pieces
        # captures
        for offset in (-15, -13):
            target = origin + self.player * offset
            if self.capturable(target):
                # promotions
                if self.promotes(target):
                    for promotion in (WK, WB, WU, WR, WD, WL, WN):
                        if pieces[promotion] > 0:
                            move = self.make_move(origin, target,
                                self.player * promotion)
                            moves.append(move)
                else:
                    move = self.make_move(origin, target)
                    moves.append(move)
            # en passant
            elif target == self.en_passant:
                move = self.make_move(origin, target)
                move.board[self.en_passant + self.player * 14] = EM
                moves.append(move)
        # non-captures
        target = origin + self.player * -14
        if self.board[target] == EM:
            # promotions
            if self.promotes(target):
                for promotion in (WK, WB, WU, WR, WD, WL, WN):
                    if pieces[promotion] > 0:
                        move = self.make_move(origin, target,
                            self.player * promotion)
                        moves.append(move)
            else:
                move = self.make_move(origin, target)
                moves.append(move)
                # double step
                if self.double_step(origin):
                    en_passant = target
                    target += self.player * -14
                    if self.board[target] == EM:
                        move = self.make_move(origin, target)
                        move.en_passant = en_passant
                        moves.append(move)
        return moves

    def generate_queen_moves(self, origin):
        moves = []
        for offset in (-15, -14, -13, -1, 1, 13, 14, 15):
            target = origin + offset
            while (self.capturable_or_empty(target)
                    and not self.attacked(target, -self.player)):
                move = self.make_move(origin, target)
                moves.append(move)
                if self.board[target] != EM:
                    break
                target += offset
        return moves

    def generate_rook_moves(self, origin):
        moves = []
        for offset in (-14, -1, 1, 14):
            target = origin + offset
            while self.capturable_or_empty(target):
                move = self.make_move(origin, target)
                moves.append(move)
                if self.board[target] != EM:
                    break
                target += offset
        return moves

    def generate_unicorn_moves(self, origin):
        moves = []
        for offset in (-29, -27, -16, -15, -13, -12, 12, 13, 15, 16, 27, 29):
            target = origin + offset
            while self.capturable_or_empty(target):
                move = self.make_move(origin, target)
                moves.append(move)
                if self.board[target] != EM:
                    break
                target += offset
        return moves

    def legal(self):
        return not self.check(-self.player)

    def make_move(self, origin, target, promotion=None):
        move = CaissaBritanniaState(self)
        move.board[origin] = EM
        if promotion is not None:
            move.board[target] = promotion
            if move.player == 1:
                move.white_pieces[promotion] -= 1
            else:
                move.black_pieces[-promotion] -= 1
            move.clicks = (origin, target, promotion)
        else:
            move.board[target] = self.board[origin]
            move.clicks = (origin, target)
        # halfmove clock
        if abs(self.board[origin]) == WP or self.board[target] != EM:
            move.halfmove_clock = 0
        else:
            move.halfmove_clock += 1
        # queen positions
        if origin == move.white_queen:
            move.white_queen = target
        elif origin == move.black_queen:
            move.black_queen = target
        # captured pieces
        piece = abs(self.board[target])
        if piece not in (EM, WP):
            if move.player == 1:
                move.black_pieces[piece] += 1
            else:
                move.white_pieces[piece] += 1
        move.player *= -1
        return move

    def promotes(self, square):
        if self.player == 1:
            return square // 14 == 2
        else:
            return square // 14 == 11

