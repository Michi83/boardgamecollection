from threading import Thread
from time import sleep

import pygame

from algorithms.idle import IdleAlgorithm


def play(state, white_algorithm, black_algorithm):
    def click(x, y):
        if hasattr(algorithm, "click"):
            width, height = screen.get_size()
            size = min(width, height)
            if width >= height:
                x -= (width - size) / 2
            else:
                y -= (height - size) / 2
            x = int(x / size * 1024)
            y = int(y / size * 1024)
            if x >= 0 and x < 1024 and y >= 0 and y < 1024:
                algorithm.click(x, y)

    def draw():
        # let game draw itself
        surface = pygame.Surface((1024, 1024))
        state.draw(surface)
        # add indicator symbols
        blacksmallking = pygame.image.load("img/png/blacksmallking.png")
        blacksmallpiece = pygame.image.load("img/png/blacksmallpiece.png")
        whitesmallking = pygame.image.load("img/png/whitesmallking.png")
        whitesmallpiece = pygame.image.load("img/png/whitesmallpiece.png")
        if len(state.generate_moves()) == 0:
            score = state.evaluate()
            if score == 1:
                # white wins
                surface.blit(whitesmallking, (976, 976))
            elif score == -1:
                # black wins
                surface.blit(blacksmallking, (976, 976))
        else:
            if state.player == 1:
                # white to move
                surface.blit(whitesmallpiece, (976, 976))
            elif state.player == -1:
                # black to move
                surface.blit(blacksmallpiece, (976, 976))
        # resize
        width, height = screen.get_size()
        size = min(width, height)
        surface = pygame.transform.smoothscale(surface, (size, size))
        if width >= height:
            x, y = (width - size) / 2, 0
        else:
            x, y = 0, (height - size) / 2
        screen.fill((0, 128, 0))
        screen.blit(surface, (x, y))
        pygame.display.flip()

    def start_algorithm():
        if len(state.generate_moves()) == 0:
            algorithm = IdleAlgorithm()
        elif state.player == 1:
            algorithm = white_algorithm
        else:
            algorithm = black_algorithm
        algorithm.move = None
        thread = Thread(target=algorithm.select_move, args=(state,))
        thread.daemon = True
        thread.start()
        return algorithm

    pygame.init()
    screen = pygame.display.set_mode((512, 512), pygame.RESIZABLE)
    draw()
    algorithm = start_algorithm()
    while True:
        for event in pygame.event.get():
            if event.type == pygame.ACTIVEEVENT:
                draw()
            elif event.type == pygame.MOUSEBUTTONUP:
                x, y = event.pos
                click(x, y)
            elif event.type == pygame.QUIT:
                pygame.quit()
                return
            elif event.type == pygame.VIDEORESIZE:
                draw()
        if algorithm.move is not None:
            state = algorithm.move
            draw()
            algorithm = start_algorithm()
        sleep(0.1)