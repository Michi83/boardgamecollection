from math import log, sqrt
from random import choice, shuffle
from time import time


# For an explantion see:
# https://en.wikipedia.org/wiki/Monte_Carlo_tree_search
class MCTSNode:
    def __init__(self, state, parent=None):
        self.state = state
        self.parent = parent
        self.children = []
        self.n = 0  # tries
        self.w = 0  # wins for the previous(!) player

    def backpropagate(self, result):
        node = self
        while node is not None:
            node.n += 1
            node.w += (-node.state.player * result + 1) / 2
            node = node.parent

    def expand(self):
        # If n > 0 we already visited this node and failed to generate any
        # moves, so we don't need to try again.
        if self.n == 0:
            moves = self.state.generate_moves()
            shuffle(moves)
            for move in moves:
                child = MCTSNode(move, self)
                self.children.append(child)
        # If there are any newly created moves, we return the first one, else
        # the node itself.
        if len(self.children) > 0:
            return self.children[0]
        else:
            return self

    def playout(self):
        state = self.state
        for i in range(100):
            moves = state.generate_moves()
            if len(moves) == 0:
                break
            state = choice(moves)
        return state.evaluate()

    def select(self):
        node = self
        while len(node.children) > 0:
            top_score = -1
            for child in node.children:
                score = child.uct()
                if score > top_score:
                    top_score = score
                    top_child = child
            node = top_child
        return node

    def uct(self):
        if self.n == 0:
            return float("inf")
        else:
            return self.w / self.n + sqrt(2 * log(self.parent.n) / self.n)


class MCTSAlgorithm:
    def __init__(self, max_time):
        self.max_time = max_time

    def select_move(self, state):
        root = MCTSNode(state)
        time1 = time()
        while True:
            time2 = time()
            if time2 - time1 > self.max_time:
                break
            node = root.select()
            node = node.expand()
            result = node.playout()
            node.backpropagate(result)
        top_score = -1
        for child in root.children:
            if child.n > top_score:
                top_score = child.n
                top_move = child.state
        self.move = top_move