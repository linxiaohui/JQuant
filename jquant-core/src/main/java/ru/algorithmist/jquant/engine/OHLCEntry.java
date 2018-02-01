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
 
import org.joda.time.Instant; 
 
/**
 * User: Sergey Edunov 
 * Date: 04.03.11 
 */ 
class OHLCEntry implements Comparable<OHLCEntry>{ 
 
    private Value open; 
    private Value high; 
    private Value low; 
    private Value close; 
    private Instant date; 
 
    static OHLCEntry NA(Instant date){ 
        return new OHLCEntry(Value.NA, Value.NA, Value.NA, Value.NA, date); 
    } 
 
    static OHLCEntry TNA(Instant date){ 
        return new OHLCEntry(Value.TNA, Value.TNA, Value.TNA, Value.TNA, date); 
    } 
 
 
    OHLCEntry(Value open, Value high, Value low, Value close, Instant date) { 
        this.open = open; 
        this.high = high; 
        this.low = low; 
        this.close = close; 
        this.date = date; 
    } 
 
    Value getOpen() { 
        return open; 
    } 
 
    Value getHigh() { 
        return high; 
    } 
 
    Value getLow() { 
        return low; 
    } 
 
    Value getClose() { 
        return close; 
    } 
 
    Instant getDate() { 
        return date; 
    } 
 
    @Override 
    public int compareTo(OHLCEntry o) { 
        return date.compareTo(o.date); 
    } 
}