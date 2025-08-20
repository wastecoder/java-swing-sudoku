package br.com.sudoku.gui.custom.checkbox;

import javax.swing.*;
import java.awt.event.ItemListener;

public class ErrorCheckBox extends JCheckBox {

    public ErrorCheckBox(final ItemListener itemListener) {
        super("Verificar erros");
        this.setSelected(false);
        this.addItemListener(itemListener);
    }
}
