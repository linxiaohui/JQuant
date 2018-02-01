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
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.storage.Key; 
 
 
/**
 * User: Sergey Edunov 
 * Date: 16.01.11 
 */ 
public class MACDParameter extends CalculatedParameter { 
 
    private IParameter emaFast; 
    private IParameter emaSlow; 
    private IParameter base; 
    private int nFast; 
    private int nSlow; 
    private boolean percent; 
 
    public MACDParameter(IParameter base, int nFast, int nSlow, boolean percent){ 
        emaFast = new EMAParameter(base, nFast); 
        emaSlow = new EMAParameter(base, nSlow); 
        this.nFast = nFast; 
        this.nSlow = nSlow; 
        this.base = base; 
        this.percent = percent; 
    } 
 
    public MACDParameter(IParameter base, int nFast, int nSlow){ 
        this(base,nFast, nSlow, true); 
    } 
    @Override 
    public IParameter[] declareDependencies() { 
        return new IParameter[]{emaFast, emaSlow}; 
    } 
 
    @Override 
    public Value calculate(Instant date) { 
        return with(new ParameterValue() { 
            @Override 
            public double calc(double... params) { 
                if (percent){ 
                    return 100 * (params[0]/params[1] - 1); 
                } 
                return params[0] - params[1]; 
            } 
        }, date, emaFast, emaSlow); 
    } 
 
    @Override 
    public Key createQueryKey() { 
        return Key.from(base.getQueryKey(), "MACD", percent?"PERCENT":"FLAT", String.valueOf(nFast), String.valueOf(nSlow)); 
    } 
 
    @Override 
    public TimeInterval getTimeInterval() { 
        return base.getTimeInterval(); 
    } 
}