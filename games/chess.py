import pygame


WK = 6  # white king
WQ = 5  # white queen
WB = 4  # white bishop
WN = 3  # white knight
WR = 2  # white rook
WP = 1  # white pawn
BK = -6  # black king
BQ = -5  # black queen
BB = -4  # black bishop
BN = -3  # black knight
BR = -2  # black rook
BP = -1  # black pawn
EM = 0  # empty
LV = -7  # lava


class ChessState:
    def __init__(self, other=None):
        if other is None:
            self.black_king = 25
            self.castling = [True, True, True, True]
            self.board = [
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
            ]
            self.player = 1
            self.white_king = 95
        else:
            self.black_king = other.black_king
            self.castling = list(other.castling)
            self.board = list(other.board)
            self.player = other.player
            self.white_king = other.white_king
        self.en_passant_pawn = None
        self.en_passant_target = None
        self.user_clicks = []

    def attacked(self, target, attacker):
        if self.attacked_by_king(target, attacker):
            return True
        elif self.attacked_by_queen(target, attacker):
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

    def attacked_by_bishop(self, target, attacker):
        for offset in (-11, -9, 9, 11):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WB:
                return True
        return False

    def attacked_by_king(self, target, attacker):
        for offset in (-11, -10, -9, -1, 1, 9, 10, 11):
            origin = target - offset
            if self.board[origin] == attacker * WK:
                return True
        return False

    def attacked_by_knight(self, target, attacker):
        for offset in (-21, -19, -12, -8, 8, 12, 19, 21):
            origin = target - offset
            if self.board[origin] == attacker * WN:
                return True
        return False

    def attacked_by_pawn(self, target, attacker):
        for offset in (-11, -9):
            origin = target - attacker * offset
            if self.board[origin] == attacker * WP:
                return True
        return False

    def attacked_by_queen(self, target, attacker):
        for offset in (-11, -10, -9, -1, 1, 9, 10, 11):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WQ:
                return True
        return False

    def attacked_by_rook(self, target, attacker):
        for offset in (-10, -1, 1, 10):
            origin = target - offset
            while self.board[origin] == EM:
                origin -= offset
            if self.board[origin] == attacker * WR:
                return True
        return False

    def black_kingside_castling(self):
        return (self.castling[2] and self.board[26] == EM
            and self.board[27] == EM and not self.attacked(25, 1)
            and not self.attacked(26, 1))

    def black_queenside_castling(self):
        return (self.castling[3] and self.board[24] == EM
            and self.board[23] == EM and self.board[22] == EM
            and not self.attacked(25, 1) and not self.attacked(24, 1))

    def capturable(self, square):
        return BK <= self.player * self.board[square] <= BP

    def capturable_or_empty(self, square):
        return BK <= self.player * self.board[square] <= EM

    def check(self, defender):
        if defender == 1:
            return self.attacked(self.white_king, -1)
        else:
            return self.attacked(self.black_king, 1)

    def click(self, x, y):
        col, row = (x - 128) // 96, (y - 128) // 96
        if 0 <= col < 8 and -1 <= row < 9:
            if row == -1:
                click = 7 - col
            elif row == 8:
                click = col - 7
            else:
                click = 10 * row + col + 21
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
            return square // 10 == 8
        else:
            return square // 10 == 3

    def draw(self, surface):
        chess = pygame.image.load("img/png/chess.png")
        whiteking = pygame.image.load("img/png/whiteking.png")
        whitequeen = pygame.image.load("img/png/whitequeen.png")
        whitebishop = pygame.image.load("img/png/whitebishop.png")
        whiteknight = pygame.image.load("img/png/whiteknight.png")
        whiterook = pygame.image.load("img/png/whiterook.png")
        whitepawn = pygame.image.load("img/png/whitepawn.png")
        blackking = pygame.image.load("img/png/blackking.png")
        blackqueen = pygame.image.load("img/png/blackqueen.png")
        blackbishop = pygame.image.load("img/png/blackbishop.png")
        blackknight = pygame.image.load("img/png/blackknight.png")
        blackrook = pygame.image.load("img/png/blackrook.png")
        blackpawn = pygame.image.load("img/png/blackpawn.png")
        selection = pygame.image.load("img/png/selection.png")
        surface.blit(chess, (0, 0))
        for square in range(21, 99):
            col, row = square % 10 - 1, square // 10 - 2
            x, y = 96 * col + 128, 96 * row + 128
            if self.board[square] == WK:
                surface.blit(whiteking, (x + 16, y + 16))
            elif self.board[square] == WQ:
                surface.blit(whitequeen, (x + 16, y + 16))
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
                surface.blit(whitequeen, (336, 48))
                surface.blit(whitebishop, (432, 48))
                surface.blit(whiteknight, (528, 48))
                surface.blit(whiterook, (624, 48))
            else:
                surface.blit(blackqueen, (336, 912))
                surface.blit(blackbishop, (432, 912))
                surface.blit(blackknight, (528, 912))
                surface.blit(blackrook, (624, 912))

    def evaluate(self):
        if len(self.generate_moves()) == 0:
            if self.insufficient_material():
                return 0
            elif self.check(self.player):
                return -self.player  # checkmate
            else:
                return 0  # stalemate
        score = 0
        for square in range(21, 99):
            if self.board[square] == WQ:
                score += 9
            elif self.board[square] == WB:
                score += 3
            elif self.board[square] == WN:
                score += 3
            elif self.board[square] == WR:
                score += 5
            elif self.board[square] == WP:
                score += 1
            elif self.board[square] == BQ:
                score -= 9
            elif self.board[square] == BB:
                score -= 3
            elif self.board[square] == BN:
                score -= 3
            elif self.board[square] == BR:
                score -= 5
            elif self.board[square] == BP:
                score -= 1
        return score / 200

    def generate_bishop_moves(self, origin):
        moves = []
        for offset in (-11, -9, 9, 11):
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
        for offset in (-11, -10, -9, -1, 1, 9, 10, 11):
            target = origin + offset
            if self.capturable_or_empty(target):
                move = self.make_move(origin, target)
                moves.append(move)
        # castling
        if self.player == 1:
            if self.white_kingside_castling():
                move = self.make_move(95, 97)
                move.board[98] = EM
                move.board[96] = WR
                moves.append(move)
            if self.white_queenside_castling():
                move = self.make_move(95, 93)
                move.board[91] = EM
                move.board[94] = WR
                moves.append(move)
        else:
            if self.black_kingside_castling():
                move = self.make_move(25, 27)
                move.board[28] = EM
                move.board[26] = BR
                moves.append(move)
            if self.black_queenside_castling():
                move = self.make_move(25, 23)
                move.board[21] = EM
                move.board[24] = BR
                moves.append(move)
        return moves

    def generate_knight_moves(self, origin):
        moves = []
        for offset in (-21, -19, -12, -8, 8, 12, 19, 21):
            target = origin + offset
            if self.capturable_or_empty(target):
                move = self.make_move(origin, target)
                moves.append(move)
        return moves

    def generate_moves(self):
        moves = []
        if self.insufficient_material():
            return moves
        for origin in range(21, 99):
            piece = self.player * self.board[origin]
            if piece == WK:
                moves += self.generate_king_moves(origin)
            elif piece == WQ:
                moves += self.generate_queen_moves(origin)
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
        for offset in (-11, -9):
            target = origin + self.player * offset
            if self.capturable(target):
                # promotions
                if self.promotes(target):
                    for promotion in (WQ, WB, WN, WR):
                        promotion *= self.player
                        move = self.make_move(origin, target, promotion)
                        moves.append(move)
                else:
                    move = self.make_move(origin, target)
                    moves.append(move)
            # en passant captures
            elif target == self.en_passant_target:
                move = self.make_move(origin, target)
                move.board[self.en_passant_pawn] = EM
                moves.append(move)
        # non-captures
        target = origin + self.player * -10
        if self.board[target] == EM:
            # promotions
            if self.promotes(target):
                for promotion in (WQ, WB, WN, WR):
                    promotion *= self.player
                    move = self.make_move(origin, target, promotion)
                    moves.append(move)
            else:
                move = self.make_move(origin, target)
                moves.append(move)
                # double step
                if self.double_step(origin):
                    en_passant_pawn = target + self.player * -10
                    if self.board[en_passant_pawn] == EM:
                        move = self.make_move(origin, en_passant_pawn)
                        move.en_passant_target = target
                        move.en_passant_pawn = en_passant_pawn
                        moves.append(move)
        return moves

    def generate_queen_moves(self, origin):
        moves = []
        for offset in (-11, -10, -9, -1, 1, 9, 10, 11):
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
        for offset in (-10, -1, 1, 10):
            target = origin + offset
            while self.board[target] == EM:
                move = self.make_move(origin, target)
                moves.append(move)
                target += offset
            if self.capturable(target):
                move = self.make_move(origin, target)
                moves.append(move)
        return moves

    def insufficient_material(self):
        # The following are considered insufficient material:
        # - king vs king
        # - king + knight vs king
        # - king + bishops vs king + bishops, if all bishops are on the same
        #   square color
        knight = False
        bishops_on_white = False
        bishops_on_black = False
        for square in range(21, 99):
            if self.board[square] in (WQ, BQ, WR, BR, WP, BP):
                return False
            elif self.board[square] in (WB, BB):
                if square // 10 % 2 != square % 2:
                    # white square
                    if knight or bishops_on_black:
                        return False
                    bishops_on_white = True
                else:
                    # black square
                    if knight or bishops_on_white:
                        return False
                    bishops_on_black = True
            elif self.board[square] in (WN, BN):
                if knight or bishops_on_white or bishops_on_black:
                    return False
                knight = True
        return True

    def legal(self):
        return not self.check(-self.player)

    def make_move(self, origin, target, promotion=None):
        # copy state
        move = ChessState(self)
        # move piece
        move.board[origin] = EM
        if promotion is not None:
            move.board[target] = promotion
            move.clicks = (origin, target, promotion)
        else:
            move.board[target] = self.board[origin]
            move.clicks = (origin, target)
        # update castling rights
        if origin == 95 or origin == 98 or target == 98:
            move.castling[0] = False
        if origin == 95 or origin == 91 or target == 91:
            move.castling[1] = False
        if origin == 25 or origin == 28 or target == 28:
            move.castling[2] = False
        if origin == 25 or origin == 21 or target == 21:
            move.castling[3] = False
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
            return square // 10 == 2
        else:
            return square // 10 == 9

    def white_kingside_castling(self):
        return (self.castling[0] and self.board[96] == EM
            and self.board[97] == EM and not self.attacked(95, -1)
            and not self.attacked(96, -1))

    def white_queenside_castling(self):
        return (self.castling[1] and self.board[94] == EM
            and self.board[93] == EM and self.board[92] == EM
            and not self.attacked(95, -1) and not self.attacked(94, -1))
