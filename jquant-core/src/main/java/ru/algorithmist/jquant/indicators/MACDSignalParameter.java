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
 * User: Sergey Edunov 
 * Date: 16.01.11 
 */ 
public class MACDSignalParameter extends CalculatedParameter { 
 
    private MACDParameter base; 
    private IParameter ema; 
    private int nSignal; 
 
    public MACDSignalParameter(MACDParameter base, int nSignal){ 
        this.base = base; 
        this.ema = new EMAParameter(base, nSignal); 
        this.nSignal = nSignal; 
    } 
 
    @Override 
    public IParameter[] declareDependencies() { 
        return new IParameter[] {ema}; 
    } 
 
    @Override 
    public Value calculate(Instant date) { 
        return DataService.instance().value(date, ema); 
    } 
 
    @Override 
    public Key createQueryKey() { 
        return Key.from(base.getQueryKey(), "Signal", String.valueOf(nSignal)); 
    } 
 
    @Override 
    public TimeInterval getTimeInterval() { 
        return base.getTimeInterval(); 
    } 
}