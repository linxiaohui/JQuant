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
package ru.algorithmist.jquant.signals; 
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.strategy.Cash; 
import ru.algorithmist.jquant.strategy.OpenPositions; 
 
import java.util.Date; 
import java.util.List; 
 
/**
 * Author: Sergey Edunov 
 */ 
public interface ISignal { 
 
    public List<SignalData> test(Instant from, Instant to); 
 
    public TimeInterval timeInterval(); 
     
}