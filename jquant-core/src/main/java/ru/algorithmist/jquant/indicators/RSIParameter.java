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
import ru.algorithmist.jquant.engine.IParameter; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.storage.Key; 
 
import java.util.Date; 
 
/**
 * @author "Sergey Edunov" 
 * @version 12/29/10 
 */ 
public class RSIParameter extends CalculatedParameter  { 
 
    private Security security; 
    private int span; 
    private TimeInterval interval; 
    private IParameter averageGain; 
    private IParameter averageLoss; 
 
    public RSIParameter(Security security, int span, TimeInterval interval) { 
        this.security = security; 
        this.span = span; 
        this.interval = interval; 
        averageGain = new EMAParameter(new GainParameter(security, interval), span); 
        averageLoss = new EMAParameter(new LossParameter(security, interval), span); 
    } 
 
    @Override 
    public IParameter[] declareDependencies() { 
        return new IParameter[]{averageGain, averageLoss}; 
    } 
 
    @Override 
    public Value calculate(Instant date) { 
        return with(new ParameterValue() { 
            @Override 
            public double calc(double... params) { 
                if (Math.abs(params[1]) < 1e-6){ 
                    return 100; 
                } 
                double rs = params[0]/params[1]; 
                return 100 - 100 / (1. + rs); 
            } 
        }, date, averageGain, averageLoss); 
 
    } 
 
    @Override 
    public Key createQueryKey() { 
        return Key.from("RSI", security.getSymbol(), security.getSource() , String.valueOf(span), interval.getKey()); 
    } 
 
    @Override 
    public boolean saveable() { 
        return true; 
    } 
 
    @Override 
    public TimeInterval getTimeInterval() { 
        return interval; 
    } 
}