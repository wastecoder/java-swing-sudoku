package br.com.sudoku.gui.custom.screen;

import br.com.sudoku.difficulty.DifficultyEnum;
import br.com.sudoku.difficulty.FileSudokuPuzzle;
import br.com.sudoku.gui.custom.button.CheckGameStatusButton;
import br.com.sudoku.gui.custom.button.FinishGameButton;
import br.com.sudoku.gui.custom.button.ResetButton;
import br.com.sudoku.gui.custom.button.StartButton;
import br.com.sudoku.gui.custom.frame.MainFrame;
import br.com.sudoku.gui.custom.input.NumberText;
import br.com.sudoku.gui.custom.panel.MainPanel;
import br.com.sudoku.gui.custom.panel.SudokuSector;
import br.com.sudoku.model.Space;
import br.com.sudoku.service.BoardService;
import br.com.sudoku.service.NotifierService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static br.com.sudoku.service.EventEnum.CLEAR_SPACE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

public class MainScreen {

    private final static Dimension dimension = new Dimension(600, 650);

    private final BoardService boardService;
    private final NotifierService notifierService;
    private JPanel boardPanel;

    private JButton startButton;
    private JButton checkGameStatusButton;
    private JButton finishGameButton;
    private JButton resetButton;

    private JComboBox<String> difficultySelect;

    public MainScreen(final Map<String, String> gameConfig) {
        this.boardService = new BoardService(gameConfig);
        this.notifierService = new NotifierService();
    }

    public void buildMainScreen() {
        JPanel mainPanel = new MainPanel(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);

        // === TOPO: seletor de dificuldade + botão iniciar ===
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        String[] difficulties = {"Fácil", "Médio", "Difícil"};
        difficultySelect = new JComboBox<>(difficulties);

        addStartButton(topPanel);

        topPanel.add(difficultySelect);
        topPanel.add(startButton);
        mainPanel.add(topPanel);

        // === TABULEIRO: painel separado ===
        boardPanel = new JPanel(new GridLayout(3, 3, 6, 6));
        addBoardToPanel(boardService.getSpaces());
        mainPanel.add(boardPanel);

        // === BOTÕES DE AÇÃO ===
        addResetButton(mainPanel);
        addCheckGameStatusButton(mainPanel);
        addFinishGameButton(mainPanel);

        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private List<Space> getSpacesFromSector(final List<List<Space>> spaces,
                                            final int initCol, final int endCol,
                                            final int initRow, final int endRow){
        List<Space> spaceSector = new ArrayList<>();
        for (int r = initRow; r <= endRow; r++) {
            for (int c = initCol; c <= endCol; c++) {
                spaceSector.add(spaces.get(c).get(r));
            }
        }
        return spaceSector;
    }

    private JPanel generateSection(final List<Space> spaces){
        List<NumberText> fields = new ArrayList<>(spaces.stream().map(NumberText::new).toList());
        fields.forEach(t -> notifierService.subscribe(CLEAR_SPACE, t));
        return new SudokuSector(fields);
    }

    private void addBoardToPanel(List<List<Space>> spacesMatrix) {
        for (int r = 0; r < 9; r += 3) {
            var endRow = r + 2;
            for (int c = 0; c < 9; c += 3) {
                var endCol = c + 2;
                var spaces = getSpacesFromSector(spacesMatrix, c, endCol, r, endRow);
                JPanel sector = generateSection(spaces);
                boardPanel.add(sector);
            }
        }
    }

    private void updateBoard(Map<String, String> newGameConfig) {
        this.boardService.loadNewConfig(newGameConfig);
        boardPanel.removeAll();
        addBoardToPanel(boardService.getSpaces());
        boardPanel.revalidate();
        boardPanel.repaint();
    }


    private void addStartButton(final JPanel topPanel) {
        startButton = new StartButton(e -> {
            String selected = (String) difficultySelect.getSelectedItem();
            DifficultyEnum difficulty = DifficultyEnum.fromLabel(selected);

            int randomFile = new Random().nextInt(2) + 1; // sorteia 1 ou 2
            FileSudokuPuzzle puzzle = new FileSudokuPuzzle(difficulty, randomFile);

            updateBoard(puzzle.toGameConfig());
        });
        topPanel.add(startButton);
    }

    private void addResetButton(final JPanel mainPanel) {
        resetButton = new ResetButton(e ->{
            var dialogResult = showConfirmDialog(
                    null,
                    "Deseja realmente reiniciar o jogo?",
                    "Limpar o jogo",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE
            );
            if (dialogResult == 0){
                boardService.reset();
                notifierService.notify(CLEAR_SPACE);
            }
        });
        mainPanel.add(resetButton);
    }

    private void addCheckGameStatusButton(final JPanel mainPanel) {
        checkGameStatusButton = new CheckGameStatusButton(e -> {
            var hasErrors = boardService.hasErrors();
            var gameStatus = boardService.getStatus();
            var message = switch (gameStatus){
                case NON_STARTED -> "O jogo não foi iniciado";
                case INCOMPLETE -> "O jogo está incompleto";
                case COMPLETE -> "O jogo está completo";
            };
            message += hasErrors ? " e contém erros" : " e não contém erros";
            showMessageDialog(null, message);
        });
        mainPanel.add(MainScreen.this.checkGameStatusButton);
    }

    private void addFinishGameButton(final JPanel mainPanel) {
        finishGameButton = new FinishGameButton(e -> {
            if (boardService.gameIsFinished()){
                showMessageDialog(null, "Parabéns você concluiu o jogo");
                resetButton.setEnabled(false);
                checkGameStatusButton.setEnabled(false);
                finishGameButton.setEnabled(false);
            } else {
                var message = "Seu jogo tem alguma inconsistência, ajuste e tente novamente";
                showMessageDialog(null, message);
            }
        });
        mainPanel.add(finishGameButton);
    }

}
