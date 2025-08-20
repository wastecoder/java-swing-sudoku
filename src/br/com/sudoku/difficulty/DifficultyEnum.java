package br.com.sudoku.difficulty;

public enum DifficultyEnum {

    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    private String label;

    DifficultyEnum(final String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
