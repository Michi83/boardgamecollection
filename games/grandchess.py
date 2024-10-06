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


class GrandChessState:
    def __init__(self, other=None):
        if other is None:
            self.black_king = 41
            self.black_pieces = [0, 0, 0, 0, 0, 0, 0, 0]
            self.board = [
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
                LV, BR, EM, EM, EM, EM, EM, EM, EM, EM, BR, LV,
                LV, EM, BN, BB, BQ, BK, BC, BA, BB, BN, EM, LV,
                LV, BP, BP, BP, BP, BP, BP, BP, BP, BP, BP, LV,
                LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
                LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
                LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
                LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
                LV, WP, WP, WP, WP, WP, WP, WP, WP, WP, WP, LV,
                LV, EM, WN, WB, WQ, WK, WC, WA, WB, WN, EM, LV,
                LV, WR, EM, EM, EM, EM, EM, EM, EM, EM, WR, LV,
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV
            ]
            self.halfmove_clock = 0
            self.player = 1
            self.white_king = 125
            self.white_pieces = [0, 0, 0, 0, 0, 0, 0, 0]
        else:
            self.black_king = other.black_king
            self.black_pieces = list(other.black_pieces)
            self.board = list(other.board)
            self.halfmove_clock = other.halfmove_clock
            self.player = other.player
            self.white_king = other.white_king
            self.white_pieces = list(other.white_pieces)
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

    def can_promote(self, square):
        if self.player == 1:
            return square // 12 <= 4
        else:
            return square // 12 >= 9

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
        col, row = (x - 192) // 64, (y - 192) // 64
        if 0 <= col < 10 and -1 <= row < 11:
            if row == -1:
                click = 8 - col
            elif row == 10:
                click = col - 8
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
            return square // 12 == 9
        else:
            return square // 12 == 4

    def draw(self, surface):
        grandchess = pygame.image.load("img/png/grandchess.png")
        whiteking = pygame.image.load("img/png/whiteking.png")
        whitequeen = pygame.image.load("img/png/whitequeen.png")
        whitechancellor = pygame.image.load("img/png/whitechancellor.png")
        whitearchbishop = pygame.image.load("img/png/whitearchbishop.png")
        whitebishop = pygame.image.load("img/png/whitebishop.png")
        whiteknight = pygame.image.load("img/png/whiteknight.png")
        whiterook = pygame.image.load("img/png/whiterook.png")
        whitepawn = pygame.image.load("img/png/whitepawn.png")
        whitepiece = pygame.image.load("img/png/whitepiece.png")
        blackking = pygame.image.load("img/png/blackking.png")
        blackqueen = pygame.image.load("img/png/blackqueen.png")
        blackchancellor = pygame.image.load("img/png/blackchancellor.png")
        blackarchbishop = pygame.image.load("img/png/blackarchbishop.png")
        blackbishop = pygame.image.load("img/png/blackbishop.png")
        blackknight = pygame.image.load("img/png/blackknight.png")
        blackrook = pygame.image.load("img/png/blackrook.png")
        blackpawn = pygame.image.load("img/png/blackpawn.png")
        blackpiece = pygame.image.load("img/png/blackpiece.png")
        smallselection = pygame.image.load("img/png/smallselection.png")
        surface.blit(grandchess, (0, 0))
        for square in range(25, 143):
            col, row = square % 12 - 1, square // 12 - 2
            x, y = 64 * col + 192, 64 * row + 192
            if self.board[square] == WK:
                surface.blit(whiteking, (x, y))
            elif self.board[square] == WQ:
                surface.blit(whitequeen, (x, y))
            elif self.board[square] == WC:
                surface.blit(whitechancellor, (x, y))
            elif self.board[square] == WA:
                surface.blit(whitearchbishop, (x, y))
            elif self.board[square] == WB:
                surface.blit(whitebishop, (x, y))
            elif self.board[square] == WN:
                surface.blit(whiteknight, (x, y))
            elif self.board[square] == WR:
                surface.blit(whiterook, (x, y))
            elif self.board[square] == WP:
                surface.blit(whitepawn, (x, y))
            elif self.board[square] == BK:
                surface.blit(blackking, (x, y))
            elif self.board[square] == BQ:
                surface.blit(blackqueen, (x, y))
            elif self.board[square] == BC:
                surface.blit(blackchancellor, (x, y))
            elif self.board[square] == BA:
                surface.blit(blackarchbishop, (x, y))
            elif self.board[square] == BB:
                surface.blit(blackbishop, (x, y))
            elif self.board[square] == BN:
                surface.blit(blackknight, (x, y))
            elif self.board[square] == BR:
                surface.blit(blackrook, (x, y))
            elif self.board[square] == BP:
                surface.blit(blackpawn, (x, y))
            if square in self.user_clicks:
                surface.blit(smallselection, (x, y))
        if len(self.user_clicks) == 2:
            # piece selection for pawn promotion
            if self.player == 1:
                if self.white_pieces[WQ] > 0:
                    surface.blit(whitequeen, (256, 128))
                if self.white_pieces[WC] > 0:
                    surface.blit(whitechancellor, (320, 128))
                if self.white_pieces[WA] > 0:
                    surface.blit(whitearchbishop, (384, 128))
                if self.white_pieces[WB] > 0:
                    surface.blit(whitebishop, (448, 128))
                if self.white_pieces[WN] > 0:
                    surface.blit(whiteknight, (512, 128))
                if self.white_pieces[WR] > 0:
                    surface.blit(whiterook, (576, 128))
                if not self.must_promote(self.user_clicks[-1]):
                    surface.blit(whitepiece, (640, 128))
            else:
                if self.black_pieces[WQ] > 0:
                    surface.blit(blackqueen, (256, 832))
                if self.black_pieces[WC] > 0:
                    surface.blit(blackchancellor, (320, 832))
                if self.black_pieces[WA] > 0:
                    surface.blit(blackarchbishop, (384, 832))
                if self.black_pieces[WB] > 0:
                    surface.blit(blackbishop, (448, 832))
                if self.black_pieces[WN] > 0:
                    surface.blit(blackknight, (512, 832))
                if self.black_pieces[WR] > 0:
                    surface.blit(blackrook, (576, 832))
                if not self.must_promote(self.user_clicks[-1]):
                    surface.blit(blackpiece, (640, 832))

    def evaluate(self):
        if self.halfmove_clock >= 100:
            return 0
        if len(self.generate_moves()) == 0:
            if self.check(self.player):
                return -self.player  # checkmate
            else:
                return 0  # stalemate
        score = 0
        for square in range(25, 143):
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
        for origin in range(25, 143):
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
        pieces = self.white_pieces if self.player == 1 else self.black_pieces
        # captures
        for offset in (-13, -11):
            target = origin + self.player * offset
            if self.capturable(target):
                # promotions
                if self.can_promote(target):
                    for promotion in range(7, 1, -1):
                        if pieces[promotion] > 0:
                            move = self.make_move(origin, target,
                                self.player * promotion)
                            moves.append(move)
                    if not self.must_promote(target):
                        move = self.make_move(origin, target, self.player)
                        moves.append(move)
                else:
                    move = self.make_move(origin, target)
                    moves.append(move)
            # en passant captures
            elif target == self.en_passant:
                move = self.make_move(origin, target)
                move.board[self.en_passant + self.player * 12] = EM
                moves.append(move)
        # non-captures
        target = origin + self.player * -12
        if self.board[target] == EM:
            # promotions
            if self.can_promote(target):
                for promotion in range(7, 1, -1):
                    if pieces[promotion] > 0:
                        move = self.make_move(origin, target,
                            self.player * promotion)
                        moves.append(move)
                if not self.must_promote(target):
                    move = self.make_move(origin, target, self.player)
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
        move = GrandChessState(self)
        # move piece
        move.board[origin] = EM
        if promotion is not None:
            move.board[target] = promotion
            if abs(promotion) != WP:
                if move.player == 1:
                    move.white_pieces[promotion] -= 1
                else:
                    move.black_pieces[-promotion] -= 1
            move.clicks = (origin, target, promotion)
        else:
            move.board[target] = self.board[origin]
            move.clicks = (origin, target)
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
        # update captured pieces
        piece = abs(self.board[target])
        if piece > WP:
            if move.player == 1:
                move.black_pieces[piece] += 1
            else:
                move.white_pieces[piece] += 1
        # switch player
        move.player *= -1
        return move

    def must_promote(self, square):
        if self.player == 1:
            return square // 12 == 2
        else:
            return square // 12 == 11
