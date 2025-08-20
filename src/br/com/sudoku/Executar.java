package br.com.sudoku;

import br.com.sudoku.difficulty.DifficultyEnum;
import br.com.sudoku.difficulty.FileSudokuPuzzle;
import br.com.sudoku.model.Board;
import br.com.sudoku.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static br.com.sudoku.util.BoardTemplate.BOARD_TEMPLATE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Executar {

    private final static Scanner scanner = new Scanner(System.in);

    private static Board board;

    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        var option = -1;
        while (option != 8) {
            System.out.print("""
                
                +===================================+
                | 1 - Iniciar um novo Jogo          |
                | 2 - Colocar um novo número        |
                | 3 - Remover um número             |
                | 4 - Visualizar jogo atual         |
                | 5 - Verificar status do jogo      |
                | 6 - Limpar jogo                   |
                | 7 - Finalizar jogo                |
                | 8 - Sair                          |
                | >> Selecione uma das opções acima |
                +===================================+
                """);
            option = scanner.nextInt();

            switch (option){
                case 1 -> startGame();
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> displayBoard();
                case 5 -> showGameStatus();
                case 6 -> resetBoard();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println(">> Opção inválida, selecione uma das opções do menu");
            }
        }
    }

    private static void startGame() {
        if (nonNull(board)) {
            System.out.println(">> O jogo já foi iniciado");
            return;
        }

        System.out.println(">> Escolha a dificuldade do jogo:");
        System.out.println("1 - Fácil");
        System.out.println("2 - Médio");
        System.out.println("3 - Difícil");
        int difficultyOption = runUntilGetValidNumber(1, 3);

        DifficultyEnum difficulty = switch (difficultyOption) {
            case 1 -> DifficultyEnum.EASY;
            case 2 -> DifficultyEnum.MEDIUM;
            case 3 -> DifficultyEnum.HARD;
            default -> throw new IllegalStateException(">> Opção inválida");
        };

        System.out.printf(">> Escolha o jogo da dificuldade %s (1 ou 2):%n", difficulty.getDisplayName());
        int gameNumber = runUntilGetValidNumber(1, 2);

        FileSudokuPuzzle puzzle = new FileSudokuPuzzle(difficulty, gameNumber);
        Map<String, String> positions = puzzle.toGameConfig();

        List<List<Space>> spaces = new ArrayList<>();
        for (int row = 0; row < BOARD_LIMIT; row++) {
            spaces.add(new ArrayList<>());
            for (int col = 0; col < BOARD_LIMIT; col++) {
                var positionConfig = positions.get("%s,%s".formatted(row, col));
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed);
                spaces.get(row).add(currentSpace);
            }
        }

        board = new Board(spaces);
        System.out.println(">> O jogo está pronto para começar");
    }


    private static void inputNumber() {
        if (isGameNotStarted()) return;

        var col = askColumn();
        var row = askRow();
        var value = askValue();

        if (!board.changeValue(col, row, value)){
            System.out.printf(">> A posição [%s,%s] tem um valor fixo%n", col, row);
        }
    }

    private static void removeNumber() {
        if (isGameNotStarted()) return;

        var col = askColumn();
        var row = askRow();

        if (!board.clearValue(col, row)){
            System.out.printf(">> A posição [%s,%s] tem um valor fixo%n", col, row);
        }
    }

    private static void displayBoard() {
        if (isGameNotStarted()) return;

        var args = new Object[81];
        var argPos = 0;
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col: board.getSpaces()){
                args[argPos ++] = " " + ((isNull(col.get(i).getActual())) ? " " : col.get(i).getActual());
            }
        }
        System.out.println(">> Seu jogo se encontra da seguinte forma");
        System.out.printf(BOARD_TEMPLATE.formatted(args));
    }

    private static void showGameStatus() {
        if (isGameNotStarted()) return;

        System.out.printf(">> O jogo atualmente se encontra no status %s%n", board.getStatus().getLabel());
        if(board.hasErrors()){
            System.out.println(">> O jogo contém erros");
        } else {
            System.out.println(">> O jogo não contém erros");
        }
    }

    private static void resetBoard() {
        if (isGameNotStarted()) return;

        if (askConfirmation(">> Tem certeza que deseja limpar seu jogo e perder todo seu progresso?")) {
            board.reset();
        }
    }

    private static void finishGame() {
        if (isGameNotStarted()) return;

        if (board.gameIsFinished()){
            System.out.println(">> Parabéns, você concluiu o jogo!");
            displayBoard();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println(">> Seu jogo contém erros, verifique seu board e ajuste-o");
        } else {
            System.out.println(">> Você ainda precisa preencher algum espaço");
        }
    }


    private static int askColumn() {
        System.out.println(">> Informe a coluna que em que o número será inserido (0 a 8)");
        return runUntilGetValidNumber(0, 8);
    }

    private static int askRow() {
        System.out.println(">> Informe a linha que em que o número será inserido (0 a 8)");
        return runUntilGetValidNumber(0, 8);
    }

    private static int askValue() {
        System.out.println(">> Informe o número que vai entrar na posição");
        return runUntilGetValidNumber(1, 9);
    }

    private static boolean askConfirmation(String message) {
        System.out.println(message + " (sim/não)");
        String confirm = scanner.next();
        while (!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("não")) {
            System.out.println(">> Informe 'sim' ou 'não'");
            confirm = scanner.next();
        }
        return confirm.equalsIgnoreCase("sim");
    }

    private static int runUntilGetValidNumber(final int min, final int max){
        var current = scanner.nextInt();
        while (current < min || current > max){
            System.out.printf(">> Informe um número entre %s e %s%n", min, max);
            current = scanner.nextInt();
        }
        return current;
    }

    private static boolean isGameNotStarted() {
        if (isNull(board)) {
            System.out.println(">> O jogo ainda não foi iniciado");
            return true;
        }
        return false;
    }

}
