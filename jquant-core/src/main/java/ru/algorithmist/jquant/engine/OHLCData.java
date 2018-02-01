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
 
import java.util.ArrayList; 
import java.util.List; 
 
/**
 * User: Sergey Edunov 
 * Date: 04.03.11 
 */ 
public class OHLCData { 
 
    private List<OHLCEntry> ohlcEntries = new ArrayList<OHLCEntry>(); 
    private TimeInterval interval; 
 
    public OHLCData(TimeInterval interval) { 
        this.interval = interval; 
    } 
 
    public void add(Value open, Value high, Value low, Value close, Instant date){ 
        ohlcEntries.add(new OHLCEntry(open, high, low, close, date)); 
    } 
 
    public OHLCEntry get(Instant date){ 
        int a = 0; 
        int b = ohlcEntries.size()-1; 
        if (date.isBefore(date(a)) || date.isAfter(date(b))) return OHLCEntry.TNA(date); 
        while(a<b){ 
            int c = (a+b)/2; 
            if (date.isBefore(c)){ 
                b = c; 
            }else if (date.isAfter(c)){ 
                a = c; 
            }else{ 
                return ohlcEntries.get(c); 
            } 
        } 
        return OHLCEntry.NA(date); 
    } 
 
    private Instant date(int pos){ 
        return ohlcEntries.get(pos).getDate(); 
    } 
 
}