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
import ru.algorithmist.jquant.engine.ValueStatus; 
import ru.algorithmist.jquant.indicators.ma.ExponentialMovingAverage; 
import ru.algorithmist.jquant.infr.DateUtils; 
import ru.algorithmist.jquant.storage.Key; 
 
import java.util.Date; 
 
/**
 * <b>Exponential moving average</b> calculated parameter. 
 * Calculates EMA for specified parameter with specified time span. 
 * Usually in finance people are interested in EMA(Close, 11) 
 * 
 * @author "Sergey Edunov" 
 * @version 12/29/10 
 */ 
public class EMAParameter extends AveragedParameter { 
 
    private IParameter base; 
    private int span; 
 
    public EMAParameter(IParameter base, int span) { 
        super(new ExponentialMovingAverage(span)); 
        this.base = base; 
        this.span = span; 
    } 
 
    @Override 
    public IParameter[] declareDependencies() { 
        return new IParameter[]{base}; 
    } 
 
 
    @Override 
    public Key createQueryKey() { 
        return Key.from(base.getQueryKey(), "EMA", String.valueOf(span)); 
    } 
 
    @Override 
    public boolean saveable() { 
        return true; 
    } 
 
    @Override 
    public TimeInterval getTimeInterval() { 
        return base.getTimeInterval(); 
    } 
 
    @Override 
    public Value simpleValue(Instant date, int shift) { 
        return DataService.instance().value(date, base, shift); 
    } 
 
    @Override 
    public IParameter getParameter() { 
        return this; 
    } 
}