package uk.co.kring.ef396.data.components;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class ExecButton extends Button {

    public ExecButton(String label, Consumer<ActionEvent> onClick) {
        setLabel(label);
        if(onClick == null) {
            addActionListener(actionEvent -> {
                getParent().setVisible(false);//auto close exit
            });
        } else {
            addActionListener(actionEvent -> onClick.accept(actionEvent));
        }
    }

    public void onClick(Consumer<ActionEvent> onClick) {
        for (ActionListener al: getActionListeners()) {
            removeActionListener(al);
        }
        addActionListener(actionEvent -> onClick.accept(actionEvent));
    }
}
