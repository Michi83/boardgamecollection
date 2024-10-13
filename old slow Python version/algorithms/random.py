from random import choice


class RandomAlgorithm:
    def select_move(self, state):
        moves = state.generate_moves()
        self.move = choice(moves)