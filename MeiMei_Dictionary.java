package com.company;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MeiMei_Dictionary {
    private JTextField textField1;
    private JButton browserButton;
    private JButton submitButton;
    private JTextField textField2;
    private JTextField textField3;
    private JButton addButton;
    private JButton deleteButton;
    private JRadioButton red_Black_TreeRadioButton;
    private JRadioButton bTreeRadioButton;
    private JTextField textField4;
    private JButton translateButton;
    private JTextField textField5;
    private JTextField textField6;

    public MeiMei_Dictionary() {
        addButton.addMouseListener(new MouseAdapter() {
        });
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
    }
}
