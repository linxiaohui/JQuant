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
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.infr.DateUtils; 
import ru.algorithmist.jquant.strategy.Cash; 
import ru.algorithmist.jquant.strategy.OpenPositions; 
import ru.algorithmist.jquant.strategy.Position; 
 
import java.util.ArrayList; 
import java.util.List; 
 
 
/**
 * This is a decorator-style signal. It decorates existing signal with automatic lagged close signals. 
 * Using this class you can automatically generate close signals for any buy/sell signal 
 * 
 * @author "Sergey Edunov" 
 * @version 1/18/11 
 */ 
public class LaggedAutoCloseSignal implements ICloseSignal{ 
 
    private int lag; 
    private TimeInterval interval; 
 
    public LaggedAutoCloseSignal(TimeInterval interval, int lag){ 
        this.lag = lag; 
        this.interval = interval; 
    } 
 
    @Override 
    public List<SignalData> test(Instant date, OpenPositions openPositions, Cash cash) { 
        Instant toCloseDate = DateUtils.shift(date, interval, -lag); 
        List<SignalData> data = new ArrayList<SignalData>(); 
        for(Position pos : openPositions){ 
            if (!pos.getDateOpen().isAfter(toCloseDate)){ 
                data.add(new BuySellSignalData(date, interval, pos.getSecurity(), pos.getQuantity()>0? BuySellStatus.CLOSE_LONG : BuySellStatus.CLOSE_SHORT)); 
            } 
        } 
        return data; 
    } 
 
    @Override 
    public TimeInterval timeInterval() { 
        return interval; 
    } 
 
 
}