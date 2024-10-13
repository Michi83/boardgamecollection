import pygame


#  0-----------1-----------2
#  |           |           |
#  |   3-------4-------5   |
#  |   |       |       |   |
#  |   |   6---7---8   |   |
#  |   |   |       |   |   |
#  9--10--11      12--13--14
#  |   |   |       |   |   |
#  |   |  15--16--17   |   |
#  |   |       |       |   |
#  |  18------19------20   |
#  |           |           |
# 21----------22----------23

coords = (
    (176, 176),
    (464, 176),
    (752, 176),
    (272, 272),
    (464, 272),
    (656, 272),
    (368, 368),
    (464, 368),
    (560, 368),
    (176, 464),
    (272, 464),
    (368, 464),
    (560, 464),
    (656, 464),
    (752, 464),
    (368, 560),
    (464, 560),
    (560, 560),
    (272, 656),
    (464, 656),
    (656, 656),
    (176, 752),
    (464, 752),
    (752, 752)
)

mills = (
    (0, 1, 2),
    (0, 9, 21),
    (1, 4, 7),
    (2, 14, 23),
    (3, 4, 5),
    (3, 10, 18),
    (5, 13, 20),
    (6, 7, 8),
    (6, 11, 15),
    (8, 12, 17),
    (9, 10, 11),
    (12, 13, 14),
    (15, 16, 17),
    (16, 19, 22),
    (18, 19, 20),
    (21, 22, 23)
)

targets = (
    (1, 9),
    (0, 2, 4),
    (1, 14),
    (4, 10),
    (1, 3, 5, 7),
    (4, 13),
    (7, 11),
    (4, 6, 8),
    (7, 12),
    (0, 10, 21),
    (3, 9, 11, 18),
    (6, 10, 15),
    (8, 13, 17),
    (5, 12, 14, 20),
    (2, 13, 23),
    (11, 16),
    (15, 17, 19),
    (12, 16),
    (10, 19),
    (16, 18, 20, 22),
    (13, 19),
    (9, 22),
    (19, 21, 23),
    (14, 22)
)


class MorrisState:
    def __init__(self, other=None):
        if other is None:
            self.black_pieces = 9
            self.board = [
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
            ]
            self.pieces = 18  # pieces not yet placed
            self.player = 1
            self.white_pieces = 9
        else:
            self.black_pieces = other.black_pieces
            self.board = list(other.board)
            self.pieces = other.pieces
            self.player = other.player
            self.white_pieces = other.white_pieces
        self.user_clicks = []

    def click(self, x, y):
        click = None
        for i in range(24):
            x2, y2 = coords[i]
            if x2 <= x < x2 + 96 and y2 <= y < y2 + 96:
                click = i
                break
        if click is not None:
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

    def draw(self, surface):
        blackpiece = pygame.image.load("img/png/blackpiece.png")
        morris = pygame.image.load("img/png/morris.png")
        whitepiece = pygame.image.load("img/png/whitepiece.png")
        selection = pygame.image.load("img/png/selection.png")
        surface.blit(morris, (0, 0))
        for i in range(24):
            x, y = coords[i]
            if self.board[i] == 1:
                surface.blit(whitepiece, (x + 16, y + 16))
            elif self.board[i] == -1:
                surface.blit(blackpiece, (x + 16, y + 16))
        for click in self.user_clicks:
            x, y = coords[click]
            surface.blit(selection, (x, y))
        for i in range(self.pieces):
            if i % 2 == 0:
                x, y = i // 2 * 96 + 80, 80
                surface.blit(blackpiece, (x + 16, y + 16))
            else:
                x, y = i // 2 * 96 + 80, 848
                surface.blit(whitepiece, (x + 16, y + 16))

    def evaluate(self):
        if len(self.generate_moves()) == 0:
            return -self.player
        else:
            return (self.white_pieces - self.black_pieces) / 10

    def generate_captures(self, origin, target):
        moves1 = []  # non-mill captures
        moves2 = []  # captures from mills
        for capture in range(24):
            if self.board[capture] == -self.player:
                move = self.make_move(origin, target, capture)
                if not self.mill(capture):
                    moves1.append(move)
                else:
                    moves2.append(move)
        if len(moves1) > 0:
            return moves1
        else:
            return moves2

    def generate_moves(self):
        if self.pieces > 0:
            return self.generate_phase1_moves()
        if self.player == 1:
            player_pieces = self.white_pieces
        else:
            player_pieces = self.black_pieces
        if player_pieces > 3:
            return self.generate_phase2_moves()
        elif player_pieces == 3:
            return self.generate_phase3_moves()
        else:
            return []

    def generate_phase1_moves(self):
        moves = []
        for target in range(24):
            if self.board[target] == 0:
                move = self.make_move(None, target)
                if move.mill(target):
                    moves += self.generate_captures(None, target)
                else:
                    moves.append(move)
        return moves

    def generate_phase2_moves(self):
        moves = []
        for origin in range(24):
            if self.board[origin] == self.player:
                for target in targets[origin]:
                    if self.board[target] == 0:
                        move = self.make_move(origin, target)
                        if move.mill(target):
                            moves += self.generate_captures(origin, target)
                        else:
                            moves.append(move)
        return moves

    def generate_phase3_moves(self):
        moves = []
        for origin in range(24):
            if self.board[origin] == self.player:
                for target in range(24):
                    if self.board[target] == 0:
                        move = self.make_move(origin, target)
                        if move.mill(target):
                            moves += self.generate_captures(origin, target)
                        else:
                            moves.append(move)
        return moves

    def make_move(self, origin, target, capture=None):
        move = MorrisState(self)
        clicks = []
        if origin is not None:
            move.board[origin] = 0
            clicks.append(origin)
        else:
            move.pieces -= 1
        move.board[target] = move.player
        clicks.append(target)
        if capture is not None:
            move.board[capture] = 0
            if move.player == 1:
                move.black_pieces -= 1
            else:
                move.white_pieces -= 1
            clicks.append(capture)
        move.clicks = tuple(clicks)
        move.player *= -1
        return move

    def mill(self, point):
        for mill in mills:
            if point in mill:
                i, j, k = mill
                sum = self.board[i] + self.board[j] + self.board[k]
                if sum == 3 or sum == -3:
                    return True
        return False
