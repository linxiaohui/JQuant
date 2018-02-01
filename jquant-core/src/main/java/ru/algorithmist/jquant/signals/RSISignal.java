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
package ru.algorithmist.jquant.signals; 
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.*; 
import ru.algorithmist.jquant.indicators.RSIParameter; 
import ru.algorithmist.jquant.strategy.Cash; 
import ru.algorithmist.jquant.strategy.OpenPositions; 
 
import java.util.ArrayList; 
import java.util.List; 
 
/**
 * Author: Sergey Edunov 
 */ 
public class RSISignal extends AbstractSignal{ 
 
    private IParameter rsi; 
    private Security security; 
    private double shortValue; 
    private double longValue; 
 
    public RSISignal(Security security, int span, TimeInterval interval){ 
        this(security, span, interval, 30, 70); 
    } 
 
    public RSISignal(Security security, int span, TimeInterval interval, double longValue, double shortValue){ 
        rsi = new RSIParameter(security, span, interval); 
        this.security = security; 
        this.shortValue = shortValue; 
        this.longValue = longValue; 
    } 
 
    @Override 
    public List<SignalData> test(Instant from, Instant to) { 
        DataQueryResult res = DataService.instance().values(from, to, rsi); 
        List<SignalData> ret = new ArrayList<SignalData>(); 
        for(DataQueryObject dqo : res){ 
            Value val = dqo.getValue(); 
            if (val.isOK()){ 
                if (val.getValue() < longValue){ 
                    ret.add(new BuySellSignalData(dqo.getDate(), timeInterval(), security, BuySellStatus.BUY_LONG)); 
                }else if (val.getValue() > shortValue){ 
                    ret.add(new BuySellSignalData(dqo.getDate(), timeInterval(), security, BuySellStatus.SELL_SHORT)); 
                } 
            } 
        } 
        return ret; 
    } 
 
    @Override 
    public TimeInterval timeInterval() { 
        return rsi.getTimeInterval(); 
    } 
}