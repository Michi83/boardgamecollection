from collections import deque

import pygame


WHITE = 1
BLACK = -1
EMPTY = 0
LAVA = -2


class Go9State:
    def __init__(self, other=None):
        if other is None:
            self.board = 11 * [LAVA]
            for i in range(9):
                self.board += [LAVA] + 9 * [EMPTY] + [LAVA]
            self.board += 11 * [LAVA]
            self.player = BLACK
        else:
            self.board = list(other.board)
            self.player = other.player
        self.ko = None
        self.passes = 0

    def click(self, x, y):
        moves = self.generate_moves()
        row, col = (y - 224) // 64, (x - 224) // 64
        if 0 <= row < 9 and 0 <= col < 9:
            point = 11 * row + col + 12
            for move in moves:
                if move.clicks == point:
                    return move
        # anything else counts as passing
        return moves[-1]

    def draw(self, surface):
        go9 = pygame.image.load("img/png/go9.png")
        whitepiece = pygame.image.load("img/png/whitepiece.png")
        blackpiece = pygame.image.load("img/png/blackpiece.png")
        surface.blit(go9, (0, 0))
        for point in range(12, 109):
            row = point // 11 - 1
            col = point % 11 - 1
            x, y = col * 64 + 224, row * 64 + 224
            if self.board[point] == WHITE:
                surface.blit(whitepiece, (x, y))
            elif self.board[point] == BLACK:
                surface.blit(blackpiece, (x, y))

    def evaluate(self):
        white_area = self.get_area(WHITE)
        black_area = self.get_area(BLACK)
        score = 7
        for point in range(12, 109):
            if white_area[point] and not black_area[point]:
                score += 1
            elif black_area[point] and not white_area[point]:
                score -= 1
        if len(self.generate_moves()) == 0:
            if score > 0:
                return WHITE
            elif score < 0:
                return BLACK
            else:
                return 0
        else:
            return score / 81

    def generate_moves(self):
        moves = []
        if self.passes >= 2:
            return moves
        for point in range(12, 109):
            if self.board[point] == EMPTY:
                move = Go9State(other=self)
                move.board[point] = move.player
                count = move.remove_captured_stones(-move.player)
                if count == 1 and point == self.ko:
                    continue
                count = move.remove_captured_stones(move.player)
                if count > 0:
                    continue  # no-suicide rule
                move.clicks = point
                move.player *= -1
                moves.append(move)
        # passing move
        move = Go9State(other=self)
        move.passes = self.passes + 1
        move.clicks = None
        move.player *= -1
        moves.append(move)
        return moves

    def get_area(self, player):
        area = 121 * [False]
        fill_queue = deque()
        for point in range(12, 109):
            if self.board[point] == player:
                area[point] = True
                fill_queue.append(point)
        while len(fill_queue) > 0:
            point = fill_queue.popleft()
            for offset in (-11, -1, 1, 11):
                neighbor = point + offset
                if self.board[neighbor] == EMPTY and not area[neighbor]:
                    area[neighbor] = True
                    fill_queue.append(neighbor)
        return area

    def remove_captured_stones(self, player):
        # 1) Mark empty points as safe and put them on a queue.
        safe = 121 * [False]
        fill_queue = deque()
        for point in range(12, 109):
            if self.board[point] == EMPTY:
                safe[point] = True
                fill_queue.append(point)
        # 2) Take a point from the queue and analyze its neighbors. If the
        #    neighbor is the right color and not marked as safe, mark it as
        #    safe and put it on the queue. Repeat until queue is empty.
        while len(fill_queue) > 0:
            point = fill_queue.popleft()
            for offset in (-11, -1, 1, 11):
                neighbor = point + offset
                if self.board[neighbor] == player and not safe[neighbor]:
                    safe[neighbor] = True
                    fill_queue.append(neighbor)
        # 3) Remove captured stones.
        count = 0
        for point in range(12, 109):
            if self.board[point] == player and not safe[point]:
                self.board[point] = EMPTY
                count += 1
                if count == 1:
                    self.ko = point
                else:
                    self.ko = None
        return count
