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
 
package ru.algorithmist.jquant.indicators.ma; 
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.DataService; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.storage.Key; 
 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/17/11 
 */ 
public class WilderMovingAverage implements MovingAverage { 
 
    private int span; 
 
    public WilderMovingAverage(int span) { 
        if (span < 2) { 
            throw new IllegalArgumentException("EMA span is too small " + span); 
        } 
        this.span = span; 
    } 
 
    @Override 
    public Value average(SimpleValue sv, Instant date) { 
        Value value = sv.simpleValue(date, 0); 
        if (value.isOK()) { 
            double alpha = 1. / span; 
            double remAlpha = (1 - alpha); 
            double delta = alpha * value.getValue(); 
            double res = delta; 
            int shift = -1; 
            while (Math.abs(alpha*res) > 1e-6 || Math.abs(alpha) > 1e-6) { 
                do { 
                    Value avValue = DataService.instance().lookup(date, sv.getParameter(), shift); 
                    if (avValue.isOK()){ 
                        return new Value(res + remAlpha*avValue.getValue()); 
                    } 
                    value = sv.simpleValue(date, shift--); 
                } while (value.isNA()); 
                if (value.isTNA()) { 
                    return Value.TNA; 
                } 
                alpha *= (1 - 1. / span); 
                remAlpha *= (1 - 1. / span); 
                delta = alpha * value.getValue(); 
                res += delta; 
            } 
            return new Value(res); 
        } 
        return value; 
    } 
 
    @Override 
    public Key getKey() { 
        return Key.from("EMA", String.valueOf(span)); 
    } 
}