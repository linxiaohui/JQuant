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
 
package ru.algorithmist.jquant.indicators; 
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.DataService; 
import ru.algorithmist.jquant.engine.IParameter; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.indicators.ma.ExponentialMovingAverage; 
import ru.algorithmist.jquant.indicators.ma.MovingAverage; 
import ru.algorithmist.jquant.indicators.ma.SimpleValue; 
import ru.algorithmist.jquant.quotes.CloseParameter; 
import ru.algorithmist.jquant.quotes.HighParameter; 
import ru.algorithmist.jquant.quotes.LowParameter; 
import ru.algorithmist.jquant.storage.Key; 
 
import java.util.Date; 
 
/**
 * Average True Range (ATR) is a technical analysis volatility indicator originally developed by J. Welles Wilder, Jr. 
 * for commodities. The indicator does not provide an indication of price trend, simply the degree of price volatility. 
 * The average true range is an N-day exponential moving average of the true range values. 
 * Wilder recommended a 14-period smoothing. 
 * 
 * User: Sergey Edunov 
 * Date: 17.01.11 
 */ 
public class ATRParameter extends AveragedParameter implements SimpleValue { 
 
    private Security security; 
    private TimeInterval interval; 
    private IParameter high; 
    private IParameter low; 
    private IParameter close; 
 
    public ATRParameter(Security security, TimeInterval timeInterval, int span){ 
        this(security, new ExponentialMovingAverage(span), timeInterval); 
    } 
 
    public ATRParameter(Security security, MovingAverage ma, TimeInterval timeInterval){ 
        super(ma); 
        this.security = security; 
        this.interval = timeInterval; 
        this.high = new HighParameter(security, timeInterval); 
        this.low = new LowParameter(security, timeInterval); 
        this.close = new CloseParameter(security, timeInterval); 
    } 
 
    @Override 
    public IParameter[] declareDependencies() { 
        return new IParameter[] {high, low, close}; 
    } 
 
    @Override 
    public Key createQueryKey() { 
        return Key.from("ATR", security.getSymbol(), security.getSource(), interval.getKey()); 
    } 
 
    @Override 
    public TimeInterval getTimeInterval() { 
        return interval; 
    } 
 
    @Override 
    public Value simpleValue(Instant date, int shift) { 
        Value h = DataService.instance().value(date, high, shift); 
        Value l = DataService.instance().value(date, low, shift); 
        if (h.isOK() && l.isOK()){ 
            Value closePrev = DataService.instance().prevValue(date, close, shift); 
            if (closePrev.isOK()){ 
                return new Value(Math.max(h.getValue(), closePrev.getValue()) - Math.min(l.getValue(), closePrev.getValue())); 
            }else{ 
                return  closePrev; 
            } 
 
        }else if (h.isNA() || l.isNA()){ 
            return Value.NA; 
        } 
        return Value.TNA; 
    } 
}