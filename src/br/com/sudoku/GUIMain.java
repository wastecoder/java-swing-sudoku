package br.com.sudoku;

import br.com.sudoku.gui.custom.screen.MainScreen;
import br.com.sudoku.difficulty.DifficultyEnum;
import br.com.sudoku.difficulty.FileSudokuPuzzle;

public class GUIMain {

    public static void main(String[] args) {
        FileSudokuPuzzle chosenGame = new FileSudokuPuzzle(DifficultyEnum.MEDIUM, 2);
        var mainScreen = new MainScreen(chosenGame.toGameConfig());
        mainScreen.buildMainScreen();
    }

}
