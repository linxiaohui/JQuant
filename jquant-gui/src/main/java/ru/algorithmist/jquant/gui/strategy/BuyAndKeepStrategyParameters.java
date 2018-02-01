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
 
package ru.algorithmist.jquant.gui.strategy; 
 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.gui.close.CloseSignal; 
import ru.algorithmist.jquant.signals.BuyAndKeepSignal; 
import ru.algorithmist.jquant.signals.RSISignal; 
import ru.algorithmist.jquant.strategy.Strategy; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/19/11 
 */ 
public class BuyAndKeepStrategyParameters implements StrategyParameters{ 
 
    private ParameterField symbol = new ParameterField("symbol", FieldType.TEXT, "MICEX"); 
    private ParameterField source = new ParameterField("source", FieldType.TEXT, "FINAM"); 
 
 
    @Override 
    public ParameterField[] getFields() { 
        return new ParameterField[] {symbol, source}; 
    } 
 
    public String getName() { 
        return "" + symbol.getValue(); 
    } 
 
    @Override 
    public Strategy configure(CloseSignal closeSignal) { 
        Strategy strategy = new Strategy(); 
        Security security = new Security((String) symbol.getValue(), (String) source.getValue()); 
        strategy.addSignal(new BuyAndKeepSignal(security, TimeInterval.DAY)); 
        return strategy; 
    } 
 
 
    public String toString(){ 
        return "Buy&Keep"; 
    } 
}