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
import ru.algorithmist.jquant.quotes.HighParameter; 
import ru.algorithmist.jquant.quotes.LowParameter; 
import ru.algorithmist.jquant.storage.Key; 
 
import java.util.Date; 
 
/**
 * (Negative or positive) Directional Movement 
 * 
 * User: Sergey Edunov 
 * Date: 17.01.11 
 */ 
public class ADIParameter extends AveragedParameter implements SimpleValue{ 
 
    private IParameter high; 
    private IParameter low; 
    private Security security; 
    private TimeInterval interval; 
    private boolean positive; 
 
 
    public ADIParameter(Security security, TimeInterval interval, boolean positive, int span){ 
        this(security, interval, positive, new ExponentialMovingAverage(span)); 
    } 
 
    public ADIParameter(Security security, TimeInterval interval, boolean positive, MovingAverage ma){ 
        super(ma); 
        this.security = security; 
        this.interval = interval; 
        this.high = new HighParameter(security, interval); 
        this.low = new LowParameter(security, interval); 
        this.positive = positive; 
    } 
 
    @Override 
    public IParameter[] declareDependencies() { 
        return new IParameter[]{high, low}; 
    } 
 
    @Override 
    public Key createQueryKey() { 
        return Key.from(getMovingAverage().getKey(), positive? "+":"-", "ADI", security.getSymbol(), security.getSource(), interval.getKey()); 
    } 
 
    @Override 
    public TimeInterval getTimeInterval() { 
        return interval; 
    } 
 
    @Override 
    public Value simpleValue(Instant date, int shift) { 
        Value todayHigh = DataService.instance().value(date, high, shift); 
        Value todayLow = DataService.instance().value(date, low, shift); 
        if (todayHigh.isOK() && todayLow.isOK()){ 
            Value yesterdayHigh = DataService.instance().prevValue(date, high, shift); 
            Value yesterdayLow = DataService.instance().prevValue(date, low, shift); 
            if (yesterdayHigh.isOK() && yesterdayLow.isOK()){ 
                double upMove = todayHigh.getValue() - yesterdayHigh.getValue(); 
                double  downMove = yesterdayLow.getValue() - todayLow.getValue(); 
                double pdm = 0; 
                double mdm = 0; 
                if (upMove > downMove && upMove > 0) { 
                    pdm = upMove; 
                } 
                if (downMove > upMove && downMove > 0){ 
                    mdm = downMove; 
                } 
                return new Value(positive? pdm:mdm); 
            } 
        } 
        if (todayHigh.isNA() || todayLow.isNA()){ 
            return Value.NA; 
        } 
        return Value.TNA; 
    } 
}