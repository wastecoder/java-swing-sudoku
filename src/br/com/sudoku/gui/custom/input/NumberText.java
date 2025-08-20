package br.com.sudoku.gui.custom.input;

import br.com.sudoku.model.Space;
import br.com.sudoku.service.EventEnum;
import br.com.sudoku.service.EventListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.function.BooleanSupplier;

import static br.com.sudoku.service.EventEnum.CLEAR_SPACE;
import static java.awt.Font.PLAIN;

public class NumberText extends JTextField implements EventListener {

    public NumberText(final Space space, final BooleanSupplier errorCheckEnabled) {
        var dimension = new Dimension(50, 50);
        this.setSize(dimension);
        this.setPreferredSize(dimension);
        this.setVisible(true);
        this.setFont(new Font("Arial", PLAIN, 20));
        this.setHorizontalAlignment(CENTER);
        this.setDocument(new br.com.sudoku.gui.custom.input.NumberTextLimit());
        this.setEnabled(!space.isFixed());
        if (space.isFixed()){
            this.setText(space.getActual().toString());
        }
        this.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(final DocumentEvent e) {
                changeSpace();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                changeSpace();
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                changeSpace();
            }

            private void changeSpace(){
                if (getText().isEmpty()){
                    space.clearSpace();
                    setBorder(UIManager.getBorder("TextField.border"));
                    return;
                }

                int value = Integer.parseInt(getText());
                space.setActual(value);
                if (errorCheckEnabled.getAsBoolean() && !space.isCorrect()) {
                    setBorder(new LineBorder(Color.RED, 2));
                } else {
                    setBorder(UIManager.getBorder("TextField.border"));
                }
            }

        });
    }

    @Override
    public void update(final EventEnum eventType) {
        if (eventType.equals(CLEAR_SPACE) && (this.isEnabled())){
            this.setText("");
        }
    }
}
