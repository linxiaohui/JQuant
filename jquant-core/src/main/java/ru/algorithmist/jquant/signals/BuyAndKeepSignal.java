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
import ru.algorithmist.jquant.engine.DataService; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.infr.DateUtils; 
import ru.algorithmist.jquant.quotes.CloseParameter; 
import ru.algorithmist.jquant.strategy.Cash; 
import ru.algorithmist.jquant.strategy.OpenPositions; 
 
import java.util.ArrayList; 
import java.util.Date; 
import java.util.List; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/18/11 
 */ 
public class BuyAndKeepSignal extends AbstractSignal { 
 
    private TimeInterval interval; 
    private Security security; 
 
    public BuyAndKeepSignal(Security security,  TimeInterval interval) { 
        this.interval = interval; 
        this.security = security; 
    } 
 
 
 
    @Override 
    public List<SignalData> test(Instant from, Instant to) { 
        Value close = DataService.instance().value(from, new CloseParameter(security, interval)); 
        while (!close.isOK()){ 
            from = DateUtils.shift(from, interval, 1); 
            if (from.isAfter(to)){ 
                break; 
            } 
            close = DataService.instance().value(from, new CloseParameter(security, interval)); 
        } 
        if (from.isAfter(to)){ 
            return new ArrayList<SignalData>(); 
        } 
        Value eclose = DataService.instance().value(to, new CloseParameter(security, interval)); 
        while(!eclose.isOK()){ 
            to = DateUtils.shift(to, interval, -1); 
            if (from.isAfter(to)){ 
                break; 
            } 
            eclose = DataService.instance().value(to, new CloseParameter(security, interval)); 
        } 
        if (from.isAfter(to)){ 
            return new ArrayList<SignalData>(); 
        } 
        List<SignalData> ret = new ArrayList<SignalData>(); 
        ret.add(new BuySellSignalData(from, interval, security, BuySellStatus.BUY_LONG)); 
        ret.add(new BuySellSignalData(to, interval, security, BuySellStatus.CLOSE_LONG)); 
        return ret; 
    } 
 
    @Override 
    public TimeInterval timeInterval() { 
        return interval; 
    } 
}