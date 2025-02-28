package com.github.gkane1234.fluidsimulation.gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DropDown {
    private JComboBox<String> comboBox;
    private JLabel label;
    private String currentSelection;
    
    public DropDown(String forceName, String[] kernelNames, String initialKernel, Consumer<String> updateFunction) {
        this.currentSelection = initialKernel;
        this.comboBox = new JComboBox<>(kernelNames);
        this.comboBox.setSelectedItem(initialKernel);
        this.label = new JLabel(forceName + " Kernel: ");
        
        comboBox.addActionListener(e -> {
            currentSelection = (String) comboBox.getSelectedItem();
            updateFunction.accept(currentSelection);
        });
    }
    
    public JPanel getPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(label);
        panel.add(comboBox);
        return panel;
    }
    
    public String getCurrentSelection() {
        return currentSelection;
    }
    
    public void setSelection(String selection) {
        if (comboBox.getModel().getSize() > 0) {
            for (int i = 0; i < comboBox.getModel().getSize(); i++) {
                if (comboBox.getItemAt(i).equals(selection)) {
                    comboBox.setSelectedItem(selection);
                    currentSelection = selection;
                    break;
                }
            }
        }
    }
}
