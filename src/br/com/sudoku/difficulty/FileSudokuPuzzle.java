package br.com.sudoku.difficulty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSudokuPuzzle {

    private final String board;

    public FileSudokuPuzzle(DifficultyEnum difficulty, int fileNumber) {
        String filePath = String.format(
                "resources/difficulty/%s/%s%d.txt",
                difficulty.getLabel(),
                difficulty.getLabel(),
                fileNumber
        );

        try {
            this.board = new String(
                    Files.readAllBytes(Paths.get(filePath))
            );
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar o arquivo: " + filePath, e);
        }
    }

    public Map<String, String> toGameConfig() {
        return Stream.of(board.split(" "))
                .collect(Collectors.toMap(
                        k -> k.split(";")[0],
                        v -> v.split(";")[1]
                ));
    }
}
