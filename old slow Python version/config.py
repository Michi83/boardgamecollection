from algorithms.chess import ChessAlgorithm
from algorithms.human import HumanAlgorithm
from algorithms.mcts import MCTSAlgorithm
from algorithms.minimax import MinimaxAlgorithm
from algorithms.random import RandomAlgorithm
from games.caissa import CaissaBritanniaState
from games.capablanca import CapablancaChessState
from games.checkers import CheckersState
from games.chess import ChessState
from games.go9 import Go9State
from games.grandchess import GrandChessState
from games.internationalcheckers import InternationalCheckersState
from games.morris import MorrisState
from games.reversi import ReversiState
from games.tictactoe import TicTacToeState
from utils import play


def launch_caissa_britannia(white, black):
    state = CaissaBritanniaState()
    algorithms = []
    for player in (white, black):
        if player == "human":
            algorithm = HumanAlgorithm()
        elif player == "computer":
            algorithm = ChessAlgorithm(4)
        elif player == "random":
            algorithm = RandomAlgorithm()
        algorithms.append(algorithm)
    play(state, *algorithms)


def launch_capablanca_chess(white, black):
    state = CapablancaChessState()
    algorithms = []
    for player in (white, black):
        if player == "human":
            algorithm = HumanAlgorithm()
        elif player == "computer":
            algorithm = ChessAlgorithm(4)
        elif player == "random":
            algorithm = RandomAlgorithm()
        algorithms.append(algorithm)
    play(state, *algorithms)


def launch_checkers(white, black):
    state = CheckersState()
    algorithms = []
    for player in (white, black):
        if player == "human":
            algorithm = HumanAlgorithm()
        elif player == "computer":
            algorithm = MinimaxAlgorithm(12)
        elif player == "random":
            algorithm = RandomAlgorithm()
        algorithms.append(algorithm)
    play(state, *algorithms)


def launch_chess(white, black):
    state = ChessState()
    algorithms = []
    for player in (white, black):
        if player == "human":
            algorithm = HumanAlgorithm()
        elif player == "computer":
            algorithm = ChessAlgorithm(4)
        elif player == "random":
            algorithm = RandomAlgorithm()
        algorithms.append(algorithm)
    play(state, *algorithms)


def launch_go9(white, black):
    state = Go9State()
    algorithms = []
    for player in (white, black):
        if player == "human":
            algorithm = HumanAlgorithm()
        elif player == "computer":
            algorithm = MCTSAlgorithm(10)
        elif player == "random":
            algorithm = RandomAlgorithm()
        algorithms.append(algorithm)
    play(state, *algorithms)


def launch_grand_chess(white, black):
    state = GrandChessState()
    algorithms = []
    for player in (white, black):
        if player == "human":
            algorithm = HumanAlgorithm()
        elif player == "computer":
            algorithm = ChessAlgorithm(4)
        elif player == "random":
            algorithm = RandomAlgorithm()
        algorithms.append(algorithm)
    play(state, *algorithms)


def launch_international_checkers(white, black):
    state = InternationalCheckersState()
    algorithms = []
    for player in (white, black):
        if player == "human":
            algorithm = HumanAlgorithm()
        elif player == "computer":
            algorithm = MCTSAlgorithm(10)
        elif player == "random":
            algorithm = RandomAlgorithm()
        algorithms.append(algorithm)
    play(state, *algorithms)


def launch_nine_mens_morris(white, black):
    state = MorrisState()
    algorithms = []
    for player in (white, black):
        if player == "human":
            algorithm = HumanAlgorithm()
        elif player == "computer":
            algorithm = MinimaxAlgorithm(6)
        elif player == "random":
            algorithm = RandomAlgorithm()
        algorithms.append(algorithm)
    play(state, *algorithms)


def launch_reversi(white, black):
    state = ReversiState()
    algorithms = []
    for player in (white, black):
        if player == "human":
            algorithm = HumanAlgorithm()
        elif player == "computer":
            algorithm = MCTSAlgorithm(10)
        elif player == "random":
            algorithm = RandomAlgorithm()
        algorithms.append(algorithm)
    play(state, *algorithms)


def launch_tic_tac_toe(white, black):
    state = TicTacToeState()
    algorithms = []
    for player in (white, black):
        if player == "human":
            algorithm = HumanAlgorithm()
        elif player == "computer":
            algorithm = MinimaxAlgorithm(10)
        elif player == "random":
            algorithm = RandomAlgorithm()
        algorithms.append(algorithm)
    play(state, *algorithms)


config = (
    {
        "name": "Caïssa Britannia",
        "launcher": launch_caissa_britannia,
        "rules": "https://www.chessvariants.com/large.dir/british.html"
    },
    {
        "name": "Capablanca Chess",
        "launcher": launch_capablanca_chess,
        "rules": "https://www.chessvariants.org/large.dir/capablanca.html"
    },
    {
        "name": "Checkers/Draughts",
        "launcher": launch_checkers,
        "rules": "https://wcdf.net/rules.htm"
    },
    {
        "name": "Chess",
        "launcher": launch_chess,
        "rules": "https://handbook.fide.com/chapter/E012023"
    },
    {
        "name": "Go (9x9)",
        "launcher": launch_go9,
        "rules": "https://en.wikipedia.org/wiki/Rules_of_Go"
    },
    {
        "name": "Grand Chess",
        "launcher": launch_grand_chess,
        "rules": "https://www.chessvariants.com/large.dir/freeling.html"
    },
    {
        "name": "International Checkers/Draughts",
        "launcher": launch_international_checkers,
        "rules": "https://www.fmjd.org/docs/Annex_1.pdf"
    },
    {
        "name": "Nine Men's Morris",
        "launcher": launch_nine_mens_morris,
        "rules": "https://library.slmath.org/books/Book29/files/gasser.pdf"
    },
    {
        "name": "Reversi",
        "launcher": launch_reversi,
        "rules": "https://www.worldothello.org/about/about-othello/othello-rules/official-rules/english"
    },
    {
        "name": "Tic-Tac-Toe",
        "launcher": launch_tic_tac_toe,
        "rules": "https://en.wikipedia.org/wiki/Tic-tac-toe"
    }
)
