import pygame


W = 1  # white
B = -1  # black
E = 0  # empty
L = -2  # lava


class ReversiState:
    def __init__(self, other=None):
        if other is None:
            self.board = [
                L, L, L, L, L, L, L, L, L, L,
                L, E, E, E, E, E, E, E, E, L,
                L, E, E, E, E, E, E, E, E, L,
                L, E, E, E, E, E, E, E, E, L,
                L, E, E, E, W, B, E, E, E, L,
                L, E, E, E, B, W, E, E, E, L,
                L, E, E, E, E, E, E, E, E, L,
                L, E, E, E, E, E, E, E, E, L,
                L, E, E, E, E, E, E, E, E, L,
                L, L, L, L, L, L, L, L, L, L
            ]
            self.player = B
        else:
            self.board = list(other.board)
            self.player = other.player

    def click(self, x, y):
        row = (y - 128) // 96
        col = (x - 128) // 96
        if row >= 0 and row < 8 and col >= 0 and col < 8:
            i = 10 * row + col + 11
            moves = self.generate_moves()
            for move in moves:
                if move.clicks == i or move.clicks == -1:
                    return move
        return self

    def draw(self, surface):
        blackpiece = pygame.image.load("img/png/blackpiece.png")
        whitepiece = pygame.image.load("img/png/whitepiece.png")
        reversi = pygame.image.load("img/png/reversi.png")
        surface.blit(reversi, (0, 0))
        for row in range(8):
            for col in range(8):
                x = col * 96 + 144
                y = row * 96 + 144
                i = 10 * row + col + 11
                if self.board[i] == W:
                    surface.blit(whitepiece, (x, y))
                elif self.board[i] == B:
                    surface.blit(blackpiece, (x, y))

    def evaluate(self):
        score = 0
        for i in range(11, 89):
            if self.board[i] != L:
                score += self.board[i]
        if len(self.generate_moves()) == 0:
            if score > 0:
                return W
            elif score < 0:
                return B
            else:
                return 0
        else:
            return score / 100

    def generate_regular_moves(self):
        moves = []
        for origin in range(11, 89):
            if self.board[origin] == 0:
                move = self.make_move(origin)
                if move is not None:
                    moves.append(move)
        return moves

    def generate_moves(self):
        moves = self.generate_regular_moves()
        if len(moves) == 0:
            # If a player cannot make a move, they sit out, i.e. they make a
            # "null move". If the other player cannot make a regular move
            # either, the game ends.
            move = ReversiState(self)
            move.clicks = -1
            move.player *= -1
            if len(move.generate_regular_moves()) > 0:
                moves.append(move)
        return moves

    def make_move(self, origin):
        # Strategy: In each direction we iterate over the squares until we find
        # something that is not an opponent's piece. If it is a player's piece
        # (not empty or lava), we backtrack flipping all the opponent's pieces.
        # If we flipped at least one, the move is valid.
        move = ReversiState(self)
        move.board[origin] = move.player
        captures = False
        for offset in (-11, -10, -9, -1, 1, 9, 10, 11):
            target = origin + offset
            while move.board[target] == -move.player:
                target += offset
            if move.board[target] == move.player:
                target -= offset
                while target != origin:
                    move.board[target] *= -1
                    captures = True
                    target -= offset
        if not captures:
            return None
        move.clicks = origin
        move.player *= -1
        return move
