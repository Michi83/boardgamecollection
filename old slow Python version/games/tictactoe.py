import pygame


class TicTacToeState:
    def __init__(self, other=None):
        if other is None:
            self.board = [0, 0, 0, 0, 0, 0, 0, 0, 0]
            self.player = 1
        else:
            self.board = list(other.board)
            self.player = other.player

    def click(self, x, y):
        row = (y - 128) // 256
        col = (x - 128) // 256
        if row >= 0 and row < 3 and col >= 0 and col < 3:
            i = 3 * row + col
            if self.board[i] == 0:
                self.board[i] = self.player
                self.player *= -1
        return self

    def draw(self, surface):
        blacklargepiece = pygame.image.load("img/png/blacklargepiece.png")
        whitelargepiece = pygame.image.load("img/png/whitelargepiece.png")
        tictactoe = pygame.image.load("img/png/tictactoe.png")
        surface.blit(tictactoe, (0, 0))
        for i in range(9):
            x = i % 3 * 256 + 192
            y = i // 3 * 256 + 192
            if self.board[i] == 1:
                surface.blit(whitelargepiece, (x, y))
            elif self.board[i] == -1:
                surface.blit(blacklargepiece, (x, y))

    def evaluate(self):
        rows = (
            (0, 1, 2),
            (3, 4, 5),
            (6, 7, 8),
            (0, 3, 6),
            (1, 4, 7),
            (2, 5, 8),
            (0, 4, 8),
            (2, 4, 6)
        )
        for row in rows:
            i, j, k = row
            sum = self.board[i] + self.board[j] + self.board[k]
            if sum == 3:
                return 1
            elif sum == -3:
                return -1
        return 0

    def generate_moves(self):
        moves = []
        if self.evaluate() == 0:
            for i in range(9):
                if self.board[i] == 0:
                    move = TicTacToeState(self)
                    move.board[i] = move.player
                    move.player *= -1
                    moves.append(move)
        return moves
