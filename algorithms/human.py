class HumanAlgorithm:
    def click(self, x, y):
        self.move = self.state.click(x, y)

    def select_move(self, state):
        self.state = state