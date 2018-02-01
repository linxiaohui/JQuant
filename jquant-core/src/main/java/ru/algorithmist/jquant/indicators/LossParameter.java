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
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.storage.Key; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.IParameter; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.engine.DataService; 
import ru.algorithmist.jquant.quotes.CloseParameter; 
 
 
/**
 * Author: Sergey Edunov 
 */ 
public class LossParameter extends CalculatedParameter { 
 
    private Security security; 
    private TimeInterval interval; 
    private IParameter close; 
 
    public LossParameter(Security security, TimeInterval interval){ 
        this.security = security; 
        close = new CloseParameter(security, interval); 
        this.interval = interval; 
    } 
 
    @Override 
    public Key createQueryKey() { 
        return Key.from("Loss", security.getSymbol(), security.getSource(), interval.getKey()); 
    } 
 
    @Override 
    public TimeInterval getTimeInterval() { 
        return interval; 
    } 
 
    @Override 
    public IParameter[] declareDependencies() { 
        return new IParameter[]{close}; 
    } 
 
    @Override 
    public Value calculate(Instant date) { 
        final Value v2 = DataService.instance().value(date, close); 
        if (v2.isOK()){ 
            return withPrev(new ParameterValue() { 
                @Override 
                public double calc(double... params) { 
                    double delta = params[0] - v2.getValue(); 
                    if (delta > 0){ 
                        return delta; 
                    } 
                    return 0; 
                } 
            }, date, close); 
        } 
        return  v2; 
    } 
 
}