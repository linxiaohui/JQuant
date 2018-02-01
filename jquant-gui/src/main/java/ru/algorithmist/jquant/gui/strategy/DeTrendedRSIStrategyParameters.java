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
import ru.algorithmist.jquant.signals.DeTrendedRSISignal; 
import ru.algorithmist.jquant.signals.LaggedAutoCloseSignal; 
import ru.algorithmist.jquant.signals.RSISignal; 
import ru.algorithmist.jquant.strategy.Strategy; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/19/11 
 */ 
public class DeTrendedRSIStrategyParameters implements StrategyParameters{ 
 
    private ParameterField symbol = new ParameterField("symbol", FieldType.TEXT, "MICEX"); 
    private ParameterField source = new ParameterField("source", FieldType.TEXT, "FINAM"); 
    private ParameterField span = new ParameterField("span", FieldType.INTEGER, 2); 
    private ParameterField trendAveragePeriod = new ParameterField("trend average", FieldType.INTEGER, 50); 
    private ParameterField longThreshold = new ParameterField("long treshold", FieldType.DECIMAL, 30.); 
    private ParameterField shortThreshold = new ParameterField("short treshold", FieldType.DECIMAL, 70.); 
 
    @Override 
    public ParameterField[] getFields() { 
        return new ParameterField[] {symbol, source, span, trendAveragePeriod, longThreshold, shortThreshold}; 
    } 
 
    @Override 
    public String getName() { 
        return symbol.getValue() + " De-Trended RSI " + span.getValue(); 
    } 
 
    @Override 
    public Strategy configure(CloseSignal closeSignal) { 
        Strategy strategy = new Strategy(); 
        Security security = new Security((String) symbol.getValue(), (String) source.getValue()); 
        DeTrendedRSISignal openSignal = new DeTrendedRSISignal(security, (Integer)span.getValue(), (Integer)trendAveragePeriod.getValue(),TimeInterval.DAY, (Double)longThreshold.getValue(), (Double)shortThreshold.getValue()); 
        strategy.addCloseSignal(closeSignal.getSignal(security, TimeInterval.DAY)); 
        strategy.addSignal(openSignal); 
        return strategy; 
    } 
 
 
    public String toString(){ 
        return "De-Trended RSI"; 
    } 
}
