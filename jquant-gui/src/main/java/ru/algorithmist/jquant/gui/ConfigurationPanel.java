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
import ru.algorithmist.jquant.gui.close.LaggedAutoCloseSignal; 
import ru.algorithmist.jquant.gui.close.NoneCloseSignal; 
import ru.algorithmist.jquant.gui.close.StopLossAndTakeProfitCloseSignal; 
import ru.algorithmist.jquant.gui.close.TrailingCloseSignal; 
import ru.algorithmist.jquant.gui.strategy.BuyAndKeepStrategyParameters; 
import ru.algorithmist.jquant.gui.strategy.DeTrendedRSIStrategyParameters; 
import ru.algorithmist.jquant.gui.strategy.FieldType; 
import ru.algorithmist.jquant.gui.strategy.ParameterField; 
import ru.algorithmist.jquant.gui.strategy.RSIBreakStrategyParameters; 
import ru.algorithmist.jquant.gui.strategy.RSIStrategyParameters; 
import ru.algorithmist.jquant.gui.strategy.StrategyParameters; 
 
import javax.swing.JButton; 
import javax.swing.JComboBox; 
import javax.swing.JFrame; 
import javax.swing.JLabel; 
import javax.swing.JPanel; 
import javax.swing.JTextField; 
import java.awt.BorderLayout; 
import java.awt.Component; 
import java.awt.GridLayout; 
import java.awt.Label; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.text.ParseException; 
import java.util.Date; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/19/11 
 */ 
public class ConfigurationPanel extends JFrame { 
 
 
    private CloseSignal defaultCloseSignal = new LaggedAutoCloseSignal(); 
 
    private Object[] closeSignals = new Object[]{ 
            defaultCloseSignal, 
            new NoneCloseSignal(), 
            new StopLossAndTakeProfitCloseSignal(), 
            new TrailingCloseSignal() 
    }; 
    private JComboBox closeStrategies = new JComboBox(closeSignals); 
 
 
    private StrategyParameters parameters; 
    //TODO: replace with generic field type for particular fields 
    private JTextField[] inputs; 
    private StrategySelectedCallback callback; 
 
    public ConfigurationPanel(StrategyParameters parameters, StrategySelectedCallback callback) { 
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
        p.setLayout(new GridLayout(fields.length + 1, 2)); 
        for (int i = 0; i < fields.length; i++) { 
            p.add(new JLabel(fields[i].getFieldName())); 
            inputs[i] = new JTextField(fields[i].getDefaultValue().toString()); 
            p.add(inputs[i]); 
        } 
        p.add(new JLabel("Close type")); 
        p.add(closeStrategies); 
        closeStrategies.addActionListener(new ActionListener() { 
            @Override 
            public void actionPerformed(ActionEvent e) { 
                Object selected = closeStrategies.getSelectedItem(); 
                if (selected instanceof CloseSignal){ 
                    CloseConfigurationPanel cp = new CloseConfigurationPanel((CloseSignal) selected, new CloseSignalCallback() { 
                        @Override 
                        public void selected(CloseSignal parameters) { 
                           defaultCloseSignal = parameters; 
                        } 
                    }); 
                    cp.pack(); 
                    cp.setVisible(true); 
                } 
            } 
        }); 
        return p; 
    } 
 
    private Component createSignalFields() { 
        JPanel p = new JPanel(); 
 
 
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
        for (int i = 0; i < inputs.length; i++) { 
            String value = inputs[i].getText(); 
            if (fields[i].getType() == FieldType.TEXT) { 
                fields[i].setValue(value); 
            } else if (fields[i].getType() == FieldType.INTEGER) { 
                fields[i].setValue(Integer.valueOf(value)); 
            } else if (fields[i].getType() == FieldType.DECIMAL) { 
                fields[i].setValue(Double.valueOf(value)); 
            } 
 
        } 
        callback.selected(parameters, defaultCloseSignal, null, null); 
        dispose(); 
    } 
}