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
import ru.algorithmist.jquant.indicators.ma.MovingAverage; 
import ru.algorithmist.jquant.indicators.ma.SimpleValue; 
import ru.algorithmist.jquant.storage.Key; 
 
import java.util.Date; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/17/11 
 */ 
public abstract class AveragedParameter extends CalculatedParameter implements SimpleValue{ 
 
    private MovingAverage ma; 
 
    public AveragedParameter(MovingAverage ma){ 
        this.ma = ma; 
    } 
    @Override 
    public final Value calculate(Instant date) { 
        return ma.average(this, date); 
    } 
 
    protected MovingAverage getMovingAverage() { 
        return ma; 
    } 
 
    @Override 
    public boolean saveable() { 
        return true; 
    } 
 
    @Override 
    public IParameter getParameter() { 
        return this; 
    } 
}