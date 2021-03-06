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
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.storage.Key; 
 
 
/**
 * Calculate the (rate of) change of a series over n periods 
 * <p/> 
 * Author: Sergey Edunov 
 */ 
public class ROCParameter extends CalculatedParameter { 
 
    private IParameter base; 
    private int span; 
 
    public ROCParameter(IParameter base, int span) { 
        this.base = base; 
        this.span = span; 
    } 
 
    @Override 
    public Key createQueryKey() { 
        return Key.from(base.getQueryKey(), "ROC", String.valueOf(span)); 
    } 
 
    @Override 
    public TimeInterval getTimeInterval() { 
        return base.getTimeInterval(); 
    } 
 
    @Override 
    public IParameter[] declareDependencies() { 
        return new IParameter[]{base}; 
    } 
 
    @Override 
    public Value calculate(Instant date) { 
        Value v2 = DataService.instance().value(date, base); 
        Value v1 = DataService.instance().prevValue(date, base, -span); 
        if (v1.isOK() && v2.isOK()) { 
            return new Value(v2.getValue() / v1.getValue() - 1); 
        } 
        if (v1.isNA() || v2.isNA()) { 
            return Value.NA; 
        } 
        return Value.TNA; 
    } 
}