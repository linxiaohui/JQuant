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
 
import java.util.Date; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/14/11 
 */ 
public class DeTrendingParameter extends CalculatedParameter { 
    private IParameter base; 
    private int averagePeriod; 
    private IParameter ema; 
    double mean; 
 
    public DeTrendingParameter(IParameter base, int averagePeriod, double mean) { 
        this.base = base; 
        ema = new EMAParameter(base, averagePeriod); 
        this.mean = mean; 
        this.averagePeriod = averagePeriod; 
    } 
 
    @Override 
    public IParameter[] declareDependencies() { 
        return new IParameter[] {base, ema}; 
    } 
 
    @Override 
    public Value calculate(Instant date) { 
        return with(new ParameterValue() { 
            @Override 
            public double calc(double... params) { 
                double centerDeviation = params[1] - mean; 
                return params[0] - centerDeviation; 
            } 
        }, date, base, ema); 
    } 
 
    @Override 
    public Key createQueryKey() { 
        return Key.from(base.getQueryKey(), "DeTrending", String.valueOf(mean), String.valueOf(averagePeriod)); 
    } 
 
    @Override 
    public TimeInterval getTimeInterval() { 
        return base.getTimeInterval(); 
    } 
}