package br.com.sudoku.gui.custom.button;

import javax.swing.*;
import java.awt.event.ActionListener;

public class FinishGameButton extends JButton {

    public FinishGameButton(final ActionListener actionListener){
        this.setText("Concluir");
        this.addActionListener(actionListener);
    }

}
