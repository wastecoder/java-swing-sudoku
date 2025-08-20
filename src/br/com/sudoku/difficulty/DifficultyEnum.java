package br.com.sudoku.difficulty;

import java.util.Arrays;

public enum DifficultyEnum {
    EASY("easy", "Fácil"),
    MEDIUM("medium", "Médio"),
    HARD("hard", "Difícil");

    private final String label;       // usado nos arquivos
    private final String displayName; // mostrado na UI

    DifficultyEnum(String label, String displayName) {
        this.label = label;
        this.displayName = displayName;
    }

    public String getLabel() {
        return label;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DifficultyEnum fromLabel(String displayName) {
        return Arrays.stream(values())
                .filter(d -> d.displayName.equalsIgnoreCase(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Dificuldade inválida: " + displayName));
    }
}
