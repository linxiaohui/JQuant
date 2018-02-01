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
 
import java.util.Date; 
 
/**
 * Author: Sergey Edunov 
 */ 
public class SignalData implements Comparable<SignalData>{ 
 
    private Instant signalDate; 
    private TimeInterval interval; 
 
    public SignalData(Instant signalDate, TimeInterval interval) { 
        this.signalDate = signalDate; 
        this.interval = interval; 
    } 
 
    public Instant getSignalDate() { 
        return signalDate; 
    } 
 
    public TimeInterval getInterval() { 
        return interval; 
    } 
 
    @Override 
    public int compareTo(SignalData o) { 
        return signalDate.compareTo(o.signalDate); 
    } 
}