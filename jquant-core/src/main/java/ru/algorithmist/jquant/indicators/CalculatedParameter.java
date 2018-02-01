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
import ru.algorithmist.jquant.engine.*; 
import ru.algorithmist.jquant.infr.DateUtils; 
 
import java.util.Arrays; 
import java.util.List; 
 
/**
 * @author "Sergey Edunov" 
 * @version 12/29/10 
 */ 
public abstract class CalculatedParameter extends AbstractParameter implements ICalculator { 
 
 
    public CalculatedParameter(){ 
    } 
 
    @Override 
    public IUpdater getUpdater() { 
        return new CalculatorUpdater(this); 
    } 
 
 
    @Override 
    public DataQueryResult calculate(Instant from, Instant to) { 
        DataQueryResult res = new DataQueryResult(); 
        while(from.isBefore(to)){ 
            Value v = calculate(from); 
            if (!v.isTNA()){ 
                res.addResult(v, from); 
            } 
            from = DateUtils.shift(from, getTimeInterval(), 1); 
        } 
        return res; 
    } 
 
 
    @Override 
    public boolean equals(Object o) { 
        if (this == o) return true; 
        if (o == null || getClass() != o.getClass()) return false; 
 
        CalculatedParameter that = (CalculatedParameter) o; 
 
        return Arrays.equals(declareDependencies(), that.declareDependencies()); 
 
    } 
 
    @Override 
    public boolean saveable() { 
        return false; 
    } 
 
 
 
    @Override 
    public int hashCode() { 
        return declareDependencies() != null ? Arrays.hashCode(declareDependencies()) : 0; 
    } 
 
    public final Value withPrev(ParameterValue pv, Instant date, IParameter... params){ 
        double[] v = new double[params.length]; 
        boolean tna = false; 
        for(int i=0; i<params.length; i++){ 
            Value vl = DataService.instance().prevValue(date, params[i]); 
            if (vl.isOK()){ 
                v[i] = vl.getValue(); 
            }else if(vl.isNA()){ 
                return Value.NA; 
            }else{ 
                tna = true; 
            } 
        } 
        if (tna){ 
            return Value.TNA; 
        } 
        return new Value(pv.calc(v)); 
    } 
 
    public final Value with(ParameterValue pv, Instant date, IParameter... params){ 
        double[] v = new double[params.length]; 
        boolean tna = false; 
        for(int i=0; i<params.length; i++){ 
            Value vl = DataService.instance().value(date, params[i]); 
            if (vl.isOK()){ 
                v[i] = vl.getValue(); 
            }else if(vl.isNA()){ 
                return Value.NA; 
            }else{ 
                tna = true; 
            } 
        } 
        if (tna){ 
            return Value.TNA; 
        } 
        return new Value(pv.calc(v)); 
    } 
 
    private  static class CalculatorUpdater implements IUpdater{ 
 
        private CalculatedParameter parameter; 
 
        public CalculatorUpdater(CalculatedParameter parameter){ 
            this.parameter = parameter; 
 
        } 
 
        @Override 
        public Value update(Instant date) { 
            Value value = parameter.calculate(date); 
            if (parameter.saveable() && !value.isTNA()) { 
                DataService.instance().update(date, parameter, value); 
            } 
            return value; 
        } 
 
        @Override 
        public DataQueryResult update(Instant from, Instant to) { 
            DataQueryResult result = parameter.calculate(from, to); 
            if (parameter.saveable()){ 
                for(DataQueryObject dqo : result){ 
                     DataService.instance().update(dqo.getDate(), parameter, dqo.getValue()); 
                } 
            } 
            return result; 
        } 
    } 
 
    static interface ParameterValue{ 
 
        public double calc(double... params); 
 
    } 
}