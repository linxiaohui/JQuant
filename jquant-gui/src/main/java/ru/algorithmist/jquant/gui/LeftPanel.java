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
 
import org.joda.time.DateTimeZone; 
import org.joda.time.Instant; 
import org.joda.time.format.DateTimeFormat; 
import org.joda.time.format.DateTimeFormatter; 
import ru.algorithmist.jquant.gui.close.CloseSignal; 
import ru.algorithmist.jquant.gui.strategy.BuyAndKeepStrategyParameters; 
import ru.algorithmist.jquant.gui.strategy.DeTrendedRSIStrategyParameters; 
import ru.algorithmist.jquant.gui.strategy.RSIBreakStrategyParameters; 
import ru.algorithmist.jquant.gui.strategy.RSIStrategyParameters; 
import ru.algorithmist.jquant.gui.strategy.StrategyParameters; 
 
import javax.swing.JButton; 
import javax.swing.JComboBox; 
import javax.swing.JLabel; 
import javax.swing.JList; 
import javax.swing.JPanel; 
import javax.swing.JTextField; 
import java.awt.FlowLayout; 
import java.awt.GridLayout; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.text.DateFormat; 
import java.text.ParseException; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
import java.util.Vector; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/19/11 
 */ 
public class LeftPanel extends JPanel { 
 
    private Object[] knownStrategies = new Object[] { 
            "Select strategy", 
            new BuyAndKeepStrategyParameters(), 
            new RSIStrategyParameters(), 
            new DeTrendedRSIStrategyParameters(), 
            new RSIBreakStrategyParameters() 
    }; 
    private Vector<StrategyParameters> selectedStrategies = new Vector<StrategyParameters>(); 
    private JTextField dateFrom = new JTextField("2010-01-01"); 
    private JTextField dateTo = new JTextField("2011-01-01"); 
    private JComboBox strategies = new JComboBox(knownStrategies); 
//    private JList selectedStrategiesL = new JList(selectedStrategies); 
    private StrategySelectedCallback callback0; 
    private static DateTimeFormatter DF = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC); 
 
 
    public LeftPanel(StrategySelectedCallback callback){ 
        setLayout(new GridLayout(20, 2)); 
        add(new JLabel("Date from:")); 
        add(dateFrom); 
        add(new JLabel("Date to:")); 
        add(dateTo); 
        add(new JLabel("Strategy:")); 
        add(strategies); 
//        add(new JLabel("Selected:")); 
//        add(selectedStrategiesL); 
 
        this.callback0 = callback; 
 
        strategies.addActionListener(new ActionListener() { 
            @Override 
            public void actionPerformed(ActionEvent e) { 
                Object selected = strategies.getSelectedItem(); 
                if (selected instanceof  StrategyParameters){ 
                    ConfigurationPanel cp = new ConfigurationPanel((StrategyParameters) selected, new StrategySelectedCallback() { 
                        @Override 
                        public void selected(StrategyParameters sp, CloseSignal closeSignal, Instant a, Instant b) { 
                            selectedStrategies.add(sp); 
//                            selectedStrategiesL.updateUI(); 
                            try { 
                                Instant from = getDate(dateFrom.getText()); 
                                Instant to = getDate(dateTo.getText()); 
                                callback0.selected(sp, closeSignal, from, to); 
                            } catch (ParseException e1) { 
 
                                e1.printStackTrace(); 
                            } 
 
                        } 
                    }); 
                    cp.pack(); 
                    cp.setVisible(true); 
                } 
            } 
        }); 
 
 
    } 
 
    private Instant getDate(String str) throws ParseException { 
         return DF.parseDateTime(str).toInstant(); 
    } 
}