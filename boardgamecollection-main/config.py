from algorithms.human import HumanAlgorithm
from algorithms.mcts import MCTSAlgorithm
from algorithms.minimax import MinimaxAlgorithm
from algorithms.nmc import NMCAlgorithm
from algorithms.random import RandomAlgorithm
from games.caissa import CaissaBritanniaState
from games.capablanca import CapablancaChessState
from games.checkers import CheckersState
from games.chess import ChessState
from games.grandchess import GrandChessState
from games.morris import MorrisState
from games.reversi import ReversiState
from games.tictactoe import TicTacToeState


config = {
    "algorithms": (
        {
            "name": "Human",
            "class": HumanAlgorithm,
            "settings": ()
        },
        {
            "name": "Minimax",
            "class": MinimaxAlgorithm,
            "settings": (
                {
                    "name": "Maximum Depth",
                    "min": 1,
                    "default": 4,
                    "max": 10,
                    "step": 1
                },
            )
        },
        {
            "name": "Monte Carlo Tree Search",
            "class": MCTSAlgorithm,
            "settings": (
                {
                    "name": "Iterations",
                    "min": 100,
                    "default": 1000,
                    "max": 2500,
                    "step": 100
                },
                {
                    "name": "Maximum Depth",
                    "min": 10,
                    "default": 100,
                    "max": 250,
                    "step": 10
                }
            )
        },
        {
            "name": "Naive Monte Carlo",
            "class": NMCAlgorithm,
            "settings": (
                {
                    "name": "Iterations",
                    "min": 10,
                    "default": 100,
                    "max": 250,
                    "step": 10
                },
                {
                    "name": "Maximum Depth",
                    "min": 10,
                    "default": 100,
                    "max": 250,
                    "step": 10
                }
            )
        },
        {
            "name": "Random",
            "class": RandomAlgorithm,
            "settings": ()
        }
    ),
    "games": (
        {
            "name": "Caissa Britannia",
            "class": CaissaBritanniaState,
            "rules": "https://www.chessvariants.com/large.dir/british.html"
        },
        {
            "name": "Capablanca Chess",
            "class": CapablancaChessState,
            "rules": "https://www.chessvariants.org/large.dir/capablanca.html"
        },
        {
            "name": "Checkers/Draughts",
            "class": CheckersState,
            "rules": "https://wcdf.net/rules.htm"
        },
        {
            "name": "Chess",
            "class": ChessState,
            "rules": "https://handbook.fide.com/chapter/E012023"
        },
        {
            "name": "Grand Chess",
            "class": GrandChessState,
            "rules": "https://www.chessvariants.com/large.dir/freeling.html"
        },
        {
            "name": "Nine Men's Morris",
            "class": MorrisState,
            "rules": "https://library.slmath.org/books/Book29/files/gasser.pdf"
        },
        {
            "name": "Reversi",
            "class": ReversiState,
            "rules": "https://www.worldothello.org/about/about-othello/othello-rules/official-rules/english"
        },
        {
            "name": "Tic-Tac-Toe",
            "class": TicTacToeState,
            "rules": "https://en.wikipedia.org/wiki/Tic-tac-toe"
        }
    )
}