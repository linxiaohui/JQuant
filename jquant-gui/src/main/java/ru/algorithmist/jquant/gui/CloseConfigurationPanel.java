/*
 * Copyright (c) 2011, Sergey Edunov. All Rights Reserved. 
 * 
 * This file is part of JQuant library. 
 * 
 * JQuant library is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version. 
 * 
 * JQuant is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU Lesser General Public License for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with JQuant. If not, see <http://www.gnu.org/licenses/>. 
 */
 
package ru.algorithmist.jquant.gui; 
 
import ru.algorithmist.jquant.gui.close.CloseSignal; 
import ru.algorithmist.jquant.gui.strategy.FieldType; 
import ru.algorithmist.jquant.gui.strategy.ParameterField; 
import ru.algorithmist.jquant.gui.strategy.StrategyParameters; 
 
import javax.swing.JButton; 
import javax.swing.JFrame; 
import javax.swing.JLabel; 
import javax.swing.JPanel; 
import javax.swing.JTextField; 
import java.awt.BorderLayout; 
import java.awt.GridLayout; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/19/11 
 */ 
public class CloseConfigurationPanel extends JFrame { 
 
    private Object[] closeSignals = new Object[] { 
            "Select close type", 
    }; 
 
    private CloseSignal parameters; 
    //TODO: replace with generic field type for particular fields 
    private JTextField[] inputs; 
    private CloseSignalCallback callback; 
 
    public CloseConfigurationPanel(CloseSignal parameters, CloseSignalCallback callback){ 
        this.parameters = parameters; 
        this.callback = callback; 
        setLayout(new BorderLayout()); 
        add(new JLabel(parameters.toString()), BorderLayout.NORTH); 
        add(createOkCancel(), BorderLayout.SOUTH); 
        add(createControls(), BorderLayout.CENTER); 
    } 
 
    private JPanel createControls() { 
        JPanel p = new JPanel(); 
        ParameterField[] fields = parameters.getFields(); 
        inputs = new JTextField[fields.length]; 
        p.setLayout(new GridLayout(fields.length, 2)); 
        for(int i=0; i<fields.length; i++){ 
            p.add(new JLabel(fields[i].getFieldName())); 
            inputs[i] = new JTextField(fields[i].getDefaultValue().toString()); 
            p.add(inputs[i]); 
        } 
        return p; 
    } 
 
    private JPanel createOkCancel() { 
        JPanel p = new JPanel(); 
        JButton ok = new JButton("OK"); 
        JButton cancel = new JButton("Cancel"); 
        p.add(ok); 
        p.add(cancel); 
        ok.addActionListener(new ActionListener() { 
            @Override 
            public void actionPerformed(ActionEvent e) { 
                ok(); 
            } 
        }); 
        cancel.addActionListener(new ActionListener() { 
            @Override 
            public void actionPerformed(ActionEvent e) { 
                cancel(); 
            } 
        }); 
        return p; 
    } 
 
    private void cancel() { 
        dispose(); 
    } 
 
    private void ok() { 
        ParameterField[] fields = parameters.getFields(); 
        for(int i=0; i<inputs.length; i++){ 
            String value = inputs[i].getText(); 
            if (fields[i].getType() == FieldType.TEXT){ 
                fields[i].setValue(value); 
            } else if (fields[i].getType() == FieldType.INTEGER) { 
                fields[i].setValue(Integer.valueOf(value)); 
            } else if (fields[i].getType() == FieldType.DECIMAL) { 
                fields[i].setValue(Double.valueOf(value)); 
            } 
 
        } 
        callback.selected(parameters); 
        dispose(); 
    } 
} 
 
interface CloseSignalCallback{ 
 
    void selected(CloseSignal parameters); 
}