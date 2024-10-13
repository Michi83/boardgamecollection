import pygame


WK = 2  # white king
WM = 1  # white man
BK = -2  # black king
BM = -1  # black man
EM = 0  # empty
LV = -3  # lava


class CheckersState:
    def __init__(self, other=None):
        if other is None:
            # For many games it is a good strategy to surround the board with a
            # border of "lava" squares. Border detection may then become
            # easier. Note that the board is a one-dimensional list.
            self.board = [
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV,
                LV, EM, BM, EM, BM, EM, BM, EM, BM, LV,
                LV, BM, EM, BM, EM, BM, EM, BM, EM, LV,
                LV, EM, BM, EM, BM, EM, BM, EM, BM, LV,
                LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
                LV, EM, EM, EM, EM, EM, EM, EM, EM, LV,
                LV, WM, EM, WM, EM, WM, EM, WM, EM, LV,
                LV, EM, WM, EM, WM, EM, WM, EM, WM, LV,
                LV, WM, EM, WM, EM, WM, EM, WM, EM, LV,
                LV, LV, LV, LV, LV, LV, LV, LV, LV, LV
            ]
            self.player = -1
            self.clicks = (-1,)
        else:
            self.board = list(other.board)
            self.player = other.player
        self.user_clicks = []

    def capturable(self, square):
        return BK <= self.player * self.board[square] <= BM

    def click(self, x, y):
        # In this and many other games we collect user clicks and compare them
        # to the clicks required for each possible move. If we find a full
        # match, we return that move. If we find a partial match, we let the
        # user make more clicks. If we can't even find a partial match, we
        # reset the user clicks, assuming the user wants to cancel previous
        # inputs.
        row = (y - 128) // 96
        col = (x - 128) // 96
        if 0 <= row < 8 and 0 <= col < 8:
            click = 10 * row + col + 11
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
                            return move  # full match
                        else:
                            return self  # partial match
        # no match
        self.user_clicks = []
        return self

    def draw(self, surface):
        blackking = pygame.image.load("img/png/blackking.png")
        blackpiece = pygame.image.load("img/png/blackpiece.png")
        chess = pygame.image.load("img/png/chess.png")
        whiteking = pygame.image.load("img/png/whiteking.png")
        whitepiece = pygame.image.load("img/png/whitepiece.png")
        selection = pygame.image.load("img/png/selection.png")
        surface.blit(chess, (0, 0))
        for square in range(11, 89):
            col, row = square % 10 - 1, square // 10 - 1
            x, y = 96 * col + 128, 96 * row + 128
            if self.board[square] == WK:
                surface.blit(whiteking, (x + 16, y + 16))
            elif self.board[square] == WM:
                surface.blit(whitepiece, (x + 16, y + 16))
            elif self.board[square] == BK:
                surface.blit(blackking, (x + 16, y + 16))
            elif self.board[square] == BM:
                surface.blit(blackpiece, (x + 16, y + 16))
            if square in self.user_clicks:
                surface.blit(selection, (x, y))

    def evaluate(self):
        if len(self.generate_moves()) == 0:
            return -self.player
        score = 0
        for square in range(11, 89):
            if self.board[square] != LV:
                score += self.board[square]
        return score / 25

    def generate_captures(self):
        moves = []
        for origin in range(11, 89):
            piece = self.player * self.board[origin]
            if piece == WK:
                moves += self.generate_king_captures(origin)
            elif piece == WM:
                moves += self.generate_man_captures(origin)
        return moves

    def generate_king_captures(self, origin):
        moves = []
        for offset in (-11, -9, 9, 11):
            capture = origin + offset
            target = capture + offset
            if self.capturable(capture) and self.board[target] == EM:
                move = self.make_move(origin, target, capture)
                multi_captures = move.generate_king_captures(target)
                if len(multi_captures) > 0:
                    moves += multi_captures
                else:
                    moves.append(move)
        return moves

    def generate_king_non_captures(self, origin):
        moves = []
        for offset in (-11, -9, 9, 11):
            target = origin + offset
            if self.board[target] == EM:
                move = self.make_move(origin, target)
                moves.append(move)
        return moves

    def generate_man_captures(self, origin):
        moves = []
        for offset in (-11, -9):
            capture = origin + self.player * offset
            target = capture + self.player * offset
            if self.capturable(capture) and self.board[target] == EM:
                move = self.make_move(origin, target, capture)
                if move.promotes(target):
                    move.board[target] = move.player * WK
                    moves.append(move)
                else:
                    multi_captures = move.generate_man_captures(target)
                    if len(multi_captures) > 0:
                        moves += multi_captures
                    else:
                        moves.append(move)
        return moves

    def generate_man_non_captures(self, origin):
        moves = []
        for offset in (-11, -9):
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
        for move in moves:
            move.player *= -1
        return moves

    def generate_non_captures(self):
        moves = []
        for origin in range(11, 89):
            piece = self.player * self.board[origin]
            if piece == WK:
                moves += self.generate_king_non_captures(origin)
            elif piece == WM:
                moves += self.generate_man_non_captures(origin)
        return moves

    def make_move(self, origin, target, capture=None):
        move = CheckersState(self)
        move.board[origin] = EM
        move.board[target] = self.board[origin]
        if capture is not None:
            move.board[capture] = EM
        # Many games require a "clicks" tuple, i.e. the squares the user needs
        # to click in order to select this move. For most checkers moves it
        # contains only the origin and the target, but for multiple captures it
        # also contains all the targets in between. Here is a little trick to
        # distinguish the two cases: In multiple captures the origin is the
        # same as the previous target.
        if origin == self.clicks[-1]:
            move.clicks = self.clicks + (target,)
        else:
            move.clicks = (origin, target)
        return move

    def promotes(self, square):
        if self.player == 1:
            return square // 10 == 1
        else:
            return square // 10 == 8
