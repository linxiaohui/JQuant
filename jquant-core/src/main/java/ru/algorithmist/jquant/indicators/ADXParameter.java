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
import ru.algorithmist.jquant.storage.Key; 
 
/**
 * The Average Directional Index (ADX) was developed in 1978 by J. Welles Wilder as an indicator of trend strength 
 * in a series of prices of a financial instrument. ADX has become a widely used indicator for technical analysts, 
 * and is provided as a standard in collections of indicators offered by various trading platforms. 
 * 
 * User: Sergey Edunov 
 * Date: 17.01.11 
 */ 
public class ADXParameter extends AveragedParameter implements SimpleValue{ 
 
    private IParameter emaPlusDM; 
    private IParameter emaMinusDM; 
    private Security security; 
    private TimeInterval interval; 
 
    public ADXParameter(Security security, TimeInterval interval, int span){ 
        this(security, interval, new ExponentialMovingAverage(span)); 
    } 
 
    public ADXParameter(Security security, TimeInterval interval, MovingAverage ma){ 
        super(ma); 
        this.security = security; 
        this.interval = interval; 
        emaMinusDM = new ADIParameter(security, interval, false, ma); 
        emaPlusDM = new ADIParameter(security, interval, true, ma); 
    } 
 
    @Override 
    public IParameter[] declareDependencies() { 
        return new IParameter[] {emaMinusDM, emaPlusDM}; 
    } 
 
    @Override 
    public Key createQueryKey() { 
        return Key.from(getMovingAverage().getKey(), "ADX", security.getSymbol(), security.getSource(), interval.getKey()); 
    } 
 
    @Override 
    public TimeInterval getTimeInterval() { 
        return interval; 
    } 
 
    @Override 
    public Value simpleValue(Instant date, int shift) { 
        Value pDi = DataService.instance().value(date, emaPlusDM, shift); 
        Value mDi = DataService.instance().value(date, emaMinusDM, shift); 
        if (pDi.isOK() && mDi.isOK()){ 
            return new Value( 100 * Math.abs(pDi.getValue() - mDi.getValue()) / (pDi.getValue() + mDi.getValue())); 
        } 
        if (pDi.isNA() || mDi.isNA()){ 
            return Value.NA; 
        } 
        return Value.TNA; 
    } 
}