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
 
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.Initializer; 
import ru.algorithmist.jquant.gui.close.CloseSignal; 
import ru.algorithmist.jquant.gui.strategy.StrategyParameters; 
 
import javax.swing.JFrame; 
import java.awt.BorderLayout; 
import java.io.IOException; 
import java.util.Date; 
 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/19/11 
 */ 
public class Main { 
 
 
    public static void main(String[] args) throws IOException { 
        Main main = new Main(); 
        main.run(); 
    } 
 
    private void run() throws IOException { 
        Initializer.initialize(); 
        final JFrame frame = new JFrame("JQuant"); 
        frame.setSize(700, 500); 
        frame.setLayout(new BorderLayout()); 
        final MainPanel mp = new MainPanel(); 
        LeftPanel lp = new LeftPanel(new StrategySelectedCallback() { 
            @Override 
            public void selected(StrategyParameters sp, CloseSignal closeSignal, Instant from, Instant to) { 
                mp.addStrategy(sp, closeSignal, from, to); 
                frame.pack(); 
            } 
        }); 
        frame.add(lp, BorderLayout.WEST); 
 
        frame.add(mp, BorderLayout.CENTER); 
        frame.setVisible(true); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    } 
 
 
 
}