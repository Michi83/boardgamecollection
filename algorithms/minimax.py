from random import shuffle


class MinimaxAlgorithm:
    def __init__(self, max_depth):
        self.max_depth = max_depth

    def select_move(self, state):
        def minimax(state, depth, alpha=-2, beta=2):
            if depth == 0:
                return state.player * state.evaluate(), None
            moves = state.generate_moves()
            if len(moves) == 0:
                return state.player * state.evaluate(), None
            # When two moves are equally good, the algorithm will select the
            # first one, so we shuffle the moves to make it less predictable.
            shuffle(moves)
            top_score = -2
            for move in moves:
                score = -minimax(move, depth - 1, -beta, -alpha)[0]
                if score > top_score:
                    top_score = score
                    top_move = move
                    if score > alpha:
                        alpha = score
                        if score >= beta:
                            break
            return top_score, top_move

        self.move = minimax(state, self.max_depth)[1]