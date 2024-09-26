#!/usr/bin/env python3
from multiprocessing import Process
from tkinter import *
import webbrowser

from config import config
from utils import play


class MainWindow(Tk):
    def __init__(self):
        super().__init__()
        self.games_frame = GamesFrame(self)
        self.games_frame.grid(column=0, row=0, rowspan=2)
        self.games_frame.listbox.select_set(0)
        self.white_algorithm_frame = AlgorithmFrame(self, "White")
        self.white_algorithm_frame.grid(column=1, row=0)
        self.white_algorithm_frame.listbox.select_set(0)
        self.white_algorithm_frame.show_settings(None)
        self.black_algorithm_frame = AlgorithmFrame(self, "Black")
        self.black_algorithm_frame.grid(column=1, row=1)
        self.black_algorithm_frame.listbox.select_set(1)
        self.black_algorithm_frame.show_settings(None)
        button = Button(self, command= self.start_game, text="Play")
        button.grid(column=0, columnspan=2, row=2)

    def start_game(self):
        i = self.games_frame.listbox.curselection()[0]
        state = config["games"][i]["class"]()
        i = self.white_algorithm_frame.listbox.curselection()[0]
        settings = []
        for scale in self.white_algorithm_frame.settings_frames[i].scales:
            settings.append(scale.get())
        white_algorithm = config["algorithms"][i]["class"](*settings)
        i = self.black_algorithm_frame.listbox.curselection()[0]
        settings = []
        for scale in self.black_algorithm_frame.settings_frames[i].scales:
            settings.append(scale.get())
        black_algorithm = config["algorithms"][i]["class"](*settings)
        args = (state, white_algorithm, black_algorithm)
        Process(target=play, args=args).start()


class GamesFrame(LabelFrame):
    def __init__(self, parent):
        super().__init__(parent, text="Games")
        self.listbox = Listbox(self, exportselection=False)
        self.listbox.grid(column=0, row=0)
        for game in config["games"]:
            self.listbox.insert(END, game["name"])
        label = Label(self, text="Rules")
        label.grid(column=0, row=1)
        label.bind("<Button-1>", self.show_rules)

    def show_rules(self, event):
        i = self.listbox.curselection()[0]
        url = config["games"][i]["rules"]
        webbrowser.open(url)


class AlgorithmFrame(LabelFrame):
    def __init__(self, parent, text):
        super().__init__(parent, text=text)
        self.listbox = Listbox(self, exportselection=False)
        self.listbox.grid(column=0, row=0)
        self.listbox.bind("<<ListboxSelect>>", self.show_settings)
        self.settings_frames = []
        for algorithm in config["algorithms"]:
            self.listbox.insert(END, algorithm["name"])
            settings_frame = SettingsFrame(self, algorithm["settings"])
            self.settings_frames.append(settings_frame)

    def show_settings(self, event):
        for settings_frame in self.settings_frames:
            settings_frame.grid_forget()
        i = self.listbox.curselection()[0]
        self.settings_frames[i].grid(column=1, row=0)


class SettingsFrame(Frame):
    def __init__(self, parent, settings):
        super().__init__(parent)
        self.scales = []
        row = 0
        for setting in settings:
            label = Label(self, text=setting["name"])
            label.grid(column=0, row=row)
            row += 1
            scale = Scale(self, from_=setting["min"], orient=HORIZONTAL,
                resolution=setting["step"], to=setting["max"])
            scale.grid(column=0, row=row)
            scale.set(setting["default"])
            self.scales.append(scale)
            row += 1


window = MainWindow()
window.mainloop()