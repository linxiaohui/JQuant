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
package ru.algorithmist.jquant.engine; 
 
import org.joda.time.Duration; 
 
/**
 * User: Sergey Edunov 
 * Date: 29.12.10 
 */ 
public enum TimeInterval { 
 
    TICK("tick", Duration.ZERO), 
    MIN("min", Duration.standardMinutes(1)), 
    HOUR("hour", Duration.standardHours(1)), 
    DAY("day", Duration.standardDays(1)), 
    FOREVER("forever", Duration.standardDays(1000000)); 
//    WEEK("week"), 
//    MONTH("month"); 
 
 
    private String key; 
    private Duration timeSpan; 
 
    TimeInterval(String key, Duration timeSpan){ 
        this.key = key; 
        this.timeSpan = timeSpan; 
    } 
 
    public String getKey() { 
        return key; 
    } 
 
    public TimeInterval up(TimeInterval i){ 
        switch (i){ 
            case DAY: return FOREVER; 
            case HOUR: return DAY; 
            case MIN: return HOUR; 
        } 
        throw new RuntimeException("Unsupported interval " + i); 
    } 
 
    public TimeInterval down(TimeInterval i){ 
        switch (i){ 
            case DAY: return MIN; 
            case HOUR: return MIN; 
            case MIN: return TICK; 
        } 
        throw new RuntimeException("Unsupported interval " + i); 
    } 
 
    public Duration getTimeSpan() { 
        return timeSpan; 
    } 
}