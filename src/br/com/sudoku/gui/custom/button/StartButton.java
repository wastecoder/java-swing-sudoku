package br.com.sudoku.gui.custom.button;

import javax.swing.*;
import java.awt.event.ActionListener;

public class StartButton extends JButton {

    public StartButton(final ActionListener actionListener){
        this.setText("Iniciar");
        this.addActionListener(actionListener);
    }
}
