# A version of the minimax algorithm specifically for chess. Still not as good
# as a specialized engine and regular players should be able to beat it. For
# the history heuristic, an improvement upon the killer heuristic, see:
# https://www.chessprogramming.org/History_Heuristic
from collections import defaultdict
from random import shuffle
from time import time


class ChessAlgorithm:
    def __init__(self, max_depth):
        self.max_depth = max_depth

    def select_move(self, state):
        def minimax(state, depth, alpha=-2, beta=2):
            nonlocal nodes
            nodes += 1
            if depth == 0:
                return state.player * state.evaluate(), None
            moves = state.generate_moves()
            if len(moves) == 0:
                return state.player * state.evaluate(), None
            shuffle(moves)
            moves.sort(key=lambda move: killer_moves[move.clicks],
                reverse=True)
            top_score = -2
            for move in moves:
                score = -minimax(move, depth - 1, -beta, -alpha)[0]
                if score > top_score:
                    top_score = score
                    top_move = move
                    if score > alpha:
                        alpha = score
                        if score >= beta:
                            killer_moves[move.clicks] += depth ** 2
                            break
            return top_score, top_move

        killer_moves = defaultdict(int)
        print("ply  score time  nodes pv")
        for depth in range(1, self.max_depth + 1):
            nodes = 0
            time1 = time()
            score, move = minimax(state, depth)
            time2 = time()
            score = round(20000 * score)
            dtime = round(100 * (time2 - time1))
            print("%3d %6d %4d %6d %s" % (depth, score, dtime, nodes,
                move.clicks))
        print()
        self.move = move