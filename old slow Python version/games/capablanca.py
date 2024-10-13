import pygame


WK = 8  # white king
WQ = 7  # white queen
WC = 6  # white chancellor
WA = 5  # white archbishop
WB = 4  # white bishop
WN = 3  # white knight
WR = 2  # white rook
WP = 1  # white pawn
BK = -8  # black king
BQ = -7  # black queen
BC = -6  # black chancellor
BA = -5  # black archbishop
BB = -4  # black bishop
BN = -3  # black knight
BR = -2  # black rook
BP = -1  # black pawn
EM = 0  # empty
LV = -9  # lava


class CapablancaChessState:
    def __init__(self, other=None):
        if other is None:
            self.black_king = 30
            self.board = [
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
                LV, BR, BN, BA, BB, BQ, BK, BB, BC, BN, BR, LV,
                LV, BP, BP, BP, BP, BP, BP, BP, BP, BP, BP, LV,
                LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
                LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
                LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
                LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
                LV, WP, WP, WP, WP, WP, WP, WP, WP, WP, WP, LV,
                LV, WR, WN, WA, WB, WQ, WK, WB, WC, WN, WR, LV,
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV
            ]
            self.castling = [True, True, True, True]
            self.halfmove_clock = 0
            self.player = 1
            self.white_king = 114
        else:
            self.black_king = other.black_king
            self.board = list(other.board)
            self.castling = list(other.castling)
            self.halfmove_clock = other.halfmove_clock
            self.player = other.player
            self.white_king = other.white_king
        self.en_passant = None
        self.user_clicks = []

    def attacked(self, target, attacker):
        if self.attacked_by_king(target, attacker):
            return True
        elif self.attacked_by_queen(target, attacker):
            return True
        elif self.attacked_by_chancellor(target, attacker):
            return True
        elif self.attacked_by_archbishop(target, attacker):
            return True
        elif self.attacked_by_bishop(target, attacker):
            return True
        elif self.attacked_by_knight(target, attacker):
            return True
        elif self.attacked_by_rook(target, attacker):
            return True
        elif self.attacked_by_pawn(target, attacker):
            return True
        else:
            return False

    def attacked_by_archbishop(self, target, attacker):
        for offset in (-25, -23, -14, -10, 10, 14, 23, 25):
            origin = target - offset
            if self.board[origin] == attacker * WA:
                return True
        for offset in (-13, -11, 11, 13):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WA:
                return True
        return False

    def attacked_by_bishop(self, target, attacker):
        for offset in (-13, -11, 11, 13):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WB:
                return True
        return False

    def attacked_by_chancellor(self, target, attacker):
        for offset in (-25, -23, -14, -10, 10, 14, 23, 25):
            origin = target - offset
            if self.board[origin] == attacker * WC:
                return True
        for offset in (-12, -1, 1, 12):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WC:
                return True
        return False

    def attacked_by_king(self, target, attacker):
        for offset in (-13, -12, -11, -1, 1, 11, 12, 13):
            origin = target - offset
            if self.board[origin] == attacker * WK:
                return True
        return False

    def attacked_by_knight(self, target, attacker):
        for offset in (-25, -23, -14, -10, 10, 14, 23, 25):
            origin = target - offset
            if self.board[origin] == attacker * WN:
                return True
        return False

    def attacked_by_pawn(self, target, attacker):
        for offset in (-13, -11):
            origin = target - attacker * offset
            if self.board[origin] == attacker * WP:
                return True
        return False

    def attacked_by_queen(self, target, attacker):
        for offset in (-13, -12, -11, -1, 1, 11, 12, 13):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WQ:
                return True
        return False

    def attacked_by_rook(self, target, attacker):
        for offset in (-12, -1, 1, 12):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WR:
                return True
        return False

    def black_kingside_castling(self):
        return (self.castling[2] and self.board[31] == EM
            and self.board[32] == EM and self.board[33] == EM
            and not self.attacked(30, 1) and not self.attacked(31, 1)
            and not self.attacked(32, 1))

    def black_queenside_castling(self):
        return (self.castling[3] and self.board[29] == EM
            and self.board[28] == EM and self.board[27] == EM
            and self.board[26] == EM and not self.attacked(30, 1)
            and not self.attacked(29, 1) and not self.attacked(28, 1))

    def capturable(self, square):
        return (self.board[square] != LV
            and self.player * self.board[square] < EM)

    def capturable_or_empty(self, square):
        return (self.board[square] != LV
            and self.player * self.board[square] <= EM)

    def check(self, defender):
        if defender == 1:
            return self.attacked(self.white_king, -1)
        else:
            return self.attacked(self.black_king, 1)

    def click(self, x, y):
        col, row = (x - 32) // 96, (y - 128) // 96
        if 0 <= col < 10 and -1 <= row < 9:
            if row == -1:
                click = 9 - col
            elif row == 8:
                click = col - 9
            else:
                click = 12 * row + col + 25
            self.user_clicks.append(click)
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
            return square // 12 == 8
        else:
            return square // 12 == 3

    def draw(self, surface):
        capablanca = pygame.image.load("img/png/capablanca.png")
        whiteking = pygame.image.load("img/png/whiteking.png")
        whitequeen = pygame.image.load("img/png/whitequeen.png")
        whitechancellor = pygame.image.load("img/png/whitechancellor.png")
        whitearchbishop = pygame.image.load("img/png/whitearchbishop.png")
        whitebishop = pygame.image.load("img/png/whitebishop.png")
        whiteknight = pygame.image.load("img/png/whiteknight.png")
        whiterook = pygame.image.load("img/png/whiterook.png")
        whitepawn = pygame.image.load("img/png/whitepawn.png")
        blackking = pygame.image.load("img/png/blackking.png")
        blackqueen = pygame.image.load("img/png/blackqueen.png")
        blackchancellor = pygame.image.load("img/png/blackchancellor.png")
        blackarchbishop = pygame.image.load("img/png/blackarchbishop.png")
        blackbishop = pygame.image.load("img/png/blackbishop.png")
        blackknight = pygame.image.load("img/png/blackknight.png")
        blackrook = pygame.image.load("img/png/blackrook.png")
        blackpawn = pygame.image.load("img/png/blackpawn.png")
        selection = pygame.image.load("img/png/selection.png")
        surface.blit(capablanca, (0, 0))
        for square in range(25, 119):
            col, row = square % 12 - 1, square // 12 - 2
            x, y = 96 * col + 32, 96 * row + 128
            if self.board[square] == WK:
                surface.blit(whiteking, (x + 16, y + 16))
            elif self.board[square] == WQ:
                surface.blit(whitequeen, (x + 16, y + 16))
            elif self.board[square] == WC:
                surface.blit(whitechancellor, (x + 16, y + 16))
            elif self.board[square] == WA:
                surface.blit(whitearchbishop, (x + 16, y + 16))
            elif self.board[square] == WB:
                surface.blit(whitebishop, (x + 16, y + 16))
            elif self.board[square] == WN:
                surface.blit(whiteknight, (x + 16, y + 16))
            elif self.board[square] == WR:
                surface.blit(whiterook, (x + 16, y + 16))
            elif self.board[square] == WP:
                surface.blit(whitepawn, (x + 16, y + 16))
            elif self.board[square] == BK:
                surface.blit(blackking, (x + 16, y + 16))
            elif self.board[square] == BQ:
                surface.blit(blackqueen, (x + 16, y + 16))
            elif self.board[square] == BC:
                surface.blit(blackchancellor, (x + 16, y + 16))
            elif self.board[square] == BA:
                surface.blit(blackarchbishop, (x + 16, y + 16))
            elif self.board[square] == BB:
                surface.blit(blackbishop, (x + 16, y + 16))
            elif self.board[square] == BN:
                surface.blit(blackknight, (x + 16, y + 16))
            elif self.board[square] == BR:
                surface.blit(blackrook, (x + 16, y + 16))
            elif self.board[square] == BP:
                surface.blit(blackpawn, (x + 16, y + 16))
            if square in self.user_clicks:
                surface.blit(selection, (x, y))
        if len(self.user_clicks) == 2:
            # piece selection for pawn promotion
            if self.player == 1:
                surface.blit(whitequeen, (240, 48))
                surface.blit(whitechancellor, (336, 48))
                surface.blit(whitearchbishop, (432, 48))
                surface.blit(whitebishop, (528, 48))
                surface.blit(whiteknight, (624, 48))
                surface.blit(whiterook, (720, 48))
            else:
                surface.blit(blackqueen, (240, 912))
                surface.blit(blackchancellor, (336, 912))
                surface.blit(blackarchbishop, (432, 912))
                surface.blit(blackbishop, (528, 912))
                surface.blit(blackknight, (624, 912))
                surface.blit(blackrook, (720, 912))

    def evaluate(self):
        if self.halfmove_clock >= 100:
            return 0
        if len(self.generate_moves()) == 0:
            if self.check(self.player):
                return -self.player  # checkmate
            else:
                return 0  # stalemate
        score = 0
        for square in range(25, 119):
            if self.board[square] == WQ:
                score += 9
            elif self.board[square] == WC:
                score += 8
            elif self.board[square] == WA:
                score += 7
            elif self.board[square] == WB:
                score += 4
            elif self.board[square] == WN:
                score += 3
            elif self.board[square] == WR:
                score += 5
            elif self.board[square] == WP:
                score += 1
            elif self.board[square] == BQ:
                score -= 9
            elif self.board[square] == BC:
                score -= 8
            elif self.board[square] == BA:
                score -= 7
            elif self.board[square] == BB:
                score -= 4
            elif self.board[square] == BN:
                score -= 3
            elif self.board[square] == BR:
                score -= 5
            elif self.board[square] == BP:
                score -= 1
        return score / 200

    def generate_archbishop_moves(self, origin):
        moves = []
        for offset in (-25, -23, -14, -10, 10, 14, 23, 25):
            target = origin + offset
            if self.capturable_or_empty(target):
                move = self.make_move(origin, target)
                moves.append(move)
        for offset in (-13, -11, 11, 13):
            target = origin + offset
            while self.board[target] == EM:
                move = self.make_move(origin, target)
                moves.append(move)
                target += offset
            if self.capturable(target):
                move = self.make_move(origin, target)
                moves.append(move)
        return moves

    def generate_bishop_moves(self, origin):
        moves = []
        for offset in (-13, -11, 11, 13):
            target = origin + offset
            while self.board[target] == EM:
                move = self.make_move(origin, target)
                moves.append(move)
                target += offset
            if self.capturable(target):
                move = self.make_move(origin, target)
                moves.append(move)
        return moves

    def generate_chancellor_moves(self, origin):
        moves = []
        for offset in (-25, -23, -14, -10, 10, 14, 23, 25):
            target = origin + offset
            if self.capturable_or_empty(target):
                move = self.make_move(origin, target)
                moves.append(move)
        for offset in (-12, -1, 1, 12):
            target = origin + offset
            while self.board[target] == EM:
                move = self.make_move(origin, target)
                moves.append(move)
                target += offset
            if self.capturable(target):
                move = self.make_move(origin, target)
                moves.append(move)
        return moves

    def generate_king_moves(self, origin):
        moves = []
        for offset in (-13, -12, -11, -1, 1, 11, 12, 13):
            target = origin + offset
            if self.capturable_or_empty(target):
                move = self.make_move(origin, target)
                moves.append(move)
        # castling
        if self.player == 1:
            if self.white_kingside_castling():
                move = self.make_move(114, 117)
                move.board[118] = EM
                move.board[116] = WR
                moves.append(move)
            if self.white_queenside_castling():
                move = self.make_move(114, 111)
                move.board[109] = EM
                move.board[112] = WR
                moves.append(move)
        else:
            if self.black_kingside_castling():
                move = self.make_move(30, 33)
                move.board[34] = EM
                move.board[32] = BR
                moves.append(move)
            if self.black_queenside_castling():
                move = self.make_move(30, 27)
                move.board[25] = EM
                move.board[28] = BR
                moves.append(move)
        return moves

    def generate_knight_moves(self, origin):
        moves = []
        for offset in (-25, -23, -14, -10, 10, 14, 23, 25):
            target = origin + offset
            if self.capturable_or_empty(target):
                move = self.make_move(origin, target)
                moves.append(move)
        return moves

    def generate_moves(self):
        moves = []
        if self.halfmove_clock >= 100:
            return moves
        for origin in range(25, 119):
            piece = self.player * self.board[origin]
            if piece == WK:
                moves += self.generate_king_moves(origin)
            elif piece == WQ:
                moves += self.generate_queen_moves(origin)
            elif piece == WC:
                moves += self.generate_chancellor_moves(origin)
            elif piece == WA:
                moves += self.generate_archbishop_moves(origin)
            elif piece == WB:
                moves += self.generate_bishop_moves(origin)
            elif piece == WN:
                moves += self.generate_knight_moves(origin)
            elif piece == WR:
                moves += self.generate_rook_moves(origin)
            elif piece == WP:
                moves += self.generate_pawn_moves(origin)
        legal_moves = []
        for move in moves:
            if move.legal():
                legal_moves.append(move)
        return legal_moves

    def generate_pawn_moves(self, origin):
        moves = []
        # captures
        for offset in (-13, -11):
            target = origin + self.player * offset
            if self.capturable(target):
                # promotions
                if self.promotes(target):
                    for promotion in (WQ, WC, WA, WB, WN, WR):
                        promotion *= self.player
                        move = self.make_move(origin, target, promotion)
                        moves.append(move)
                else:
                    move = self.make_move(origin, target)
                    moves.append(move)
            # en passant captures
            elif target == self.en_passant:
                move = self.make_move(origin, target)
                move.board[self.en_passant + 12 * self.player] = EM
                moves.append(move)
        # non-captures
        target = origin + self.player * -12
        if self.board[target] == EM:
            # promotions
            if self.promotes(target):
                for promotion in (WQ, WC, WA, WB, WN, WR):
                    promotion *= self.player
                    move = self.make_move(origin, target, promotion)
                    moves.append(move)
            else:
                move = self.make_move(origin, target)
                moves.append(move)
                # double step
                if self.double_step(origin):
                    en_passant = target
                    target = en_passant - 12 * self.player
                    if self.board[target] == EM:
                        move = self.make_move(origin, target)
                        move.en_passant = en_passant
                        moves.append(move)
        return moves

    def generate_queen_moves(self, origin):
        moves = []
        for offset in (-13, -12, -11, -1, 1, 11, 12, 13):
            target = origin + offset
            while self.board[target] == EM:
                move = self.make_move(origin, target)
                moves.append(move)
                target += offset
            if self.capturable(target):
                move = self.make_move(origin, target)
                moves.append(move)
        return moves

    def generate_rook_moves(self, origin):
        moves = []
        for offset in (-12, -1, 1, 12):
            target = origin + offset
            while self.board[target] == EM:
                move = self.make_move(origin, target)
                moves.append(move)
                target += offset
            if self.capturable(target):
                move = self.make_move(origin, target)
                moves.append(move)
        return moves

    def legal(self):
        return not self.check(-self.player)

    def make_move(self, origin, target, promotion=None):
        # copy state
        move = CapablancaChessState(self)
        # move piece
        move.board[origin] = EM
        if promotion is not None:
            move.board[target] = promotion
            move.clicks = (origin, target, promotion)
        else:
            move.board[target] = self.board[origin]
            move.clicks = (origin, target)
        # update castling rights
        if origin == 114 or origin == 118 or target == 118:
            move.castling[0] = False
        if origin == 114 or origin == 109 or target == 109:
            move.castling[1] = False
        if origin == 30 or origin == 34 or target == 34:
            move.castling[2] = False
        if origin == 30 or origin == 25 or target == 25:
            move.castling[3] = False
        # update halfmove clock
        if abs(self.board[origin]) == WP or self.board[target] != EM:
            move.halfmove_clock = 0
        else:
            move.halfmove_clock += 1
        # update king positions
        if origin == move.white_king:
            move.white_king = target
        elif origin == move.black_king:
            move.black_king = target
        # switch player
        move.player *= -1
        return move

    def promotes(self, square):
        if self.player == 1:
            return square // 12 == 2
        else:
            return square // 12 == 9

    def white_kingside_castling(self):
        return (self.castling[0] and self.board[115] == EM
            and self.board[116] == EM and self.board[117] == EM
            and not self.attacked(114, -1) and not self.attacked(115, -1)
            and not self.attacked(116, -1))

    def white_queenside_castling(self):
        return (self.castling[1] and self.board[113] == EM
            and self.board[112] == EM and self.board[111] == EM
            and self.board[110] == EM and not self.attacked(114, -1)
            and not self.attacked(113, -1) and not self.attacked(112, -1))
