import pygame


WK = 2  # white king
WM = 1  # white man
BK = -2  # black king
BM = -1  # black man
EM = 0  # empty
CP = -3  # captured piece
LV = -4  # lava


class InternationalCheckersState:
    def __init__(self, other=None):
        if other is None:
            self.board = [
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
                LV, EM, BM, EM, BM, EM, BM, EM, BM, EM, BM, LV,
                LV, BM, EM, BM, EM, BM, EM, BM, EM, BM, EM, LV,
                LV, EM, BM, EM, BM, EM, BM, EM, BM, EM, BM, LV,
                LV, BM, EM, BM, EM, BM, EM, BM, EM, BM, EM, LV,
                LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
                LV, EM, EM, EM, EM, EM, EM, EM, EM, EM, EM, LV,
                LV, EM, WM, EM, WM, EM, WM, EM, WM, EM, WM, LV,
                LV, WM, EM, WM, EM, WM, EM, WM, EM, WM, EM, LV,
                LV, EM, WM, EM, WM, EM, WM, EM, WM, EM, WM, LV,
                LV, WM, EM, WM, EM, WM, EM, WM, EM, WM, EM, LV,
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV, LV
            ]
            self.player = 1
            self.clicks = (-1,)
        else:
            self.board = list(other.board)
            self.player = other.player
        self.user_clicks = []

    def capturable(self, square):
        return (self.board[square] > CP
            and self.player * self.board[square] < 0)

    def click(self, x, y):
        col, row = (x - 192) // 64, (y - 192) // 64
        square = 12 * row + col + 13
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

    def draw(self, surface):
        grandchess = pygame.image.load("img/png/grandchess.png")
        whiteking = pygame.image.load("img/png/whiteking.png")
        whitepiece = pygame.image.load("img/png/whitepiece.png")
        blackking = pygame.image.load("img/png/blackking.png")
        blackpiece = pygame.image.load("img/png/blackpiece.png")
        smallselection = pygame.image.load("img/png/smallselection.png")
        surface.blit(grandchess, (0, 0))
        for square in range(13, 131):
            col, row = square % 12 - 1, square // 12 - 1
            x, y = 64 * col + 192, 64 * row + 192
            if self.board[square] == WK:
                surface.blit(whiteking, (x, y))
            elif self.board[square] == WM:
                surface.blit(whitepiece, (x, y))
            elif self.board[square] == BK:
                surface.blit(blackking, (x, y))
            elif self.board[square] == BM:
                surface.blit(blackpiece, (x, y))
            if square in self.user_clicks:
                surface.blit(smallselection, (x, y))

    def evaluate(self):
        if len(self.generate_moves()) == 0:
            return -self.player
        score = 0
        for square in range(13, 131):
            if self.board[square] == WK:
                score += 2
            elif self.board[square] == WM:
                score += 1
            elif self.board[square] == BK:
                score -= 2
            elif self.board[square] == BM:
                score -= 1
        return score / 50

    def generate_captures(self):
        moves = []
        for origin in range(13, 131):
            piece = self.player * self.board[origin]
            if piece == WK:
                moves += self.generate_king_captures(origin)
            elif piece == WM:
                moves += self.generate_man_captures(origin)
        top_count = -1
        top_moves = []
        for move in moves:
            count = move.remove_captured_pieces()
            if count > top_count:
                top_count = count
                top_moves = [move]
            elif count == top_count:
                top_moves.append(move)
            move.player *= -1
        return top_moves

    def generate_king_captures(self, origin):
        moves = []
        for offset in (-13, -11, 11, 13):
            capture = origin + offset
            while self.board[capture] == EM:
                capture += offset
            if self.capturable(capture):
                target = capture + offset
                while self.board[target] == EM:
                    move = self.make_move(origin, target, capture)
                    multi_captures = move.generate_king_captures(target)
                    if len(multi_captures) > 0:
                        moves += multi_captures
                    else:
                        moves.append(move)
                    target += offset
        return moves

    def generate_king_non_captures(self, origin):
        moves = []
        for offset in (-13, -11, 11, 13):
            target = origin + offset
            while self.board[target] == EM:
                move = self.make_move(origin, target)
                moves.append(move)
                target += offset
        return moves

    def generate_man_captures(self, origin):
        moves = []
        for offset in (-13, -11, 11, 13):
            capture = origin + offset
            target = capture + offset
            if self.capturable(capture) and self.board[target] == EM:
                move = self.make_move(origin, target, capture)
                multi_captures = move.generate_man_captures(target)
                if len(multi_captures) > 0:
                    moves += multi_captures
                else:
                    if move.promotes(target):
                        move.board[target] = move.player * WK
                    moves.append(move)
        return moves

    def generate_man_non_captures(self, origin):
        moves = []
        for offset in (-13, -11):
            target = origin + self.player * offset
            if self.board[target] == EM:
                move = self.make_move(origin, target)
                if move.promotes(target):
                    move.board[target] = move.player * WK
                moves.append(move)
        return moves

    def generate_moves(self):
        moves = self.generate_captures()
        if len(moves) == 0:
            moves = self.generate_non_captures()
        return moves

    def generate_non_captures(self):
        moves = []
        for origin in range(13, 131):
            piece = self.player * self.board[origin]
            if piece == WK:
                moves += self.generate_king_non_captures(origin)
            elif piece == WM:
                moves += self.generate_man_non_captures(origin)
        for move in moves:
            move.player *= -1
        return moves

    def make_move(self, origin, target, capture=None):
        move = InternationalCheckersState(self)
        move.board[origin] = EM
        move.board[target] = self.board[origin]
        if capture is not None:
            move.board[capture] = CP
        if origin == self.clicks[-1]:
            move.clicks = self.clicks + (target,)
        else:
            move.clicks = (origin, target)
        return move

    def promotes(self, square):
        if self.player == 1:
            return square // 12 == 1
        else:
            return square // 12 == 10

    def remove_captured_pieces(self):
        count = 0
        for square in range(13, 131):
            if self.board[square] == CP:
                self.board[square] = EM
                count += 1
        return count