#!/usr/bin/env python3
from tkinter import *
import webbrowser

from config import config


class MainWindow(Tk):
    def __init__(self):
        super().__init__()
        main_frame = Frame(self, bg="#008000")
        main_frame.grid(column=0, row=0)
        self.games_frame = GamesFrame(main_frame)
        self.games_frame.grid(column=0, row=0, sticky="n")
        self.games_frame.listbox.select_set(0)
        self.white_algorithm_frame = AlgorithmFrame(main_frame, "White")
        self.white_algorithm_frame.grid(column=1, row=0, sticky="n")
        self.white_algorithm_frame.player.set("human")
        self.black_algorithm_frame = AlgorithmFrame(main_frame, "Black")
        self.black_algorithm_frame.grid(column=2, row=0, sticky="n")
        self.black_algorithm_frame.player.set("computer")
        button = Button(main_frame, bg="#FFFFFF", command= self.start_game,
            text="Play")
        button.grid(column=0, columnspan=3, row=1)

    def start_game(self):
        i = self.games_frame.listbox.curselection()[0]
        launcher = config[i]["launcher"]
        white = self.white_algorithm_frame.player.get()
        black = self.black_algorithm_frame.player.get()
        launcher(white, black)


class GamesFrame(LabelFrame):
    def __init__(self, parent):
        super().__init__(parent, bg="#008000", fg="#FFFFFF", text="Games")
        self.listbox = Listbox(self, exportselection=False)
        self.listbox.grid(column=0, row=0)
        for game in config:
            self.listbox.insert(END, game["name"])
        label = Label(self, bg="#008000", fg="#0000EE", text="Rules")
        label.grid(column=0, row=1)
        label.bind("<Button-1>", self.show_rules)

    def show_rules(self, event):
        i = self.listbox.curselection()[0]
        url = config[i]["rules"]
        webbrowser.open(url)


class AlgorithmFrame(LabelFrame):
    def __init__(self, parent, text):
        super().__init__(parent, bg="#008000", fg="#FFFFFF", text=text)
        self.player = StringVar()
        radiobutton = Radiobutton(self, activebackground="#008000",
            activeforeground="#FFFFFF", bg="#008000", fg="#FFFFFF",
            highlightthickness=0, selectcolor="#008000", text="Human",
            variable=self.player, value="human")
        radiobutton.grid(column=0, row=0, sticky="w")
        radiobutton = Radiobutton(self, activebackground="#008000",
            activeforeground="#FFFFFF", bg="#008000", fg="#FFFFFF",
            highlightthickness=0, selectcolor="#008000", text="Computer",
            variable=self.player, value="computer")
        radiobutton.grid(column=0, row=1, sticky="w")
        radiobutton = Radiobutton(self, activebackground="#008000",
            activeforeground="#FFFFFF", bg="#008000", fg="#FFFFFF",
            highlightthickness=0, selectcolor="#008000", text="Random moves",
            variable=self.player, value="random")
        radiobutton.grid(column=0, row=2, sticky="w")


window = MainWindow()
window.mainloop()