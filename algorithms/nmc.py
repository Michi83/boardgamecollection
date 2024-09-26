from random import choice


class NMCAlgorithm:
    def __init__(self, iterations, max_depth):
        self.iterations = iterations
        self.max_depth = max_depth

    def select_move(self, state):
        moves = state.generate_moves()
        scores = len(moves) * [0]
        for _ in range(self.iterations):
            for i in range(len(moves)):
                move = moves[i]
                for __ in range(self.max_depth):
                    submoves = move.generate_moves()
                    if len(submoves) == 0:
                        break
                    move = choice(submoves)
                scores[i] += state.player * move.evaluate()
        topscore = -float("inf")
        topmoves = []
        for i in range(len(moves)):
            if scores[i] > topscore:
                topscore = scores[i]
                topmoves = [moves[i]]
            elif scores[i] == topscore:
                topmoves.append(moves[i])
        self.move = choice(topmoves)