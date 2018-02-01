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
package ru.algorithmist.jquant.infr; 
 
import org.joda.time.DateTimeFieldType; 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.TimeInterval; 
 
import java.util.Calendar; 
import java.util.Date; 
import java.util.GregorianCalendar; 
 
/**
 * User: Sergey Edunov 
 * Date: 09.12.10 
 */ 
public class DateUtils { 
 
    public static boolean isTheSameDay(Instant a, Instant b) { 
        return a.get(DateTimeFieldType.dayOfYear()) == b.get(DateTimeFieldType.dayOfYear())  && a.get(DateTimeFieldType.yearOfEra()) == b.get(DateTimeFieldType.yearOfEra()); 
    } 
 
 
    public static Instant shift(Instant from, TimeInterval interval, int shift){ 
        return from.plus(interval.getTimeSpan().getMillis() * shift); 
    } 
 
    public static Instant shiftDays(Instant date, int days){ 
        return date.plus(days*TimeInterval.DAY.getTimeSpan().getMillis()); 
    } 
 
    public static Instant yesterday(Instant today){ 
        return shiftDays(today, -1); 
    } 
 
    public static Instant tomorrow(Instant today){ 
        return shiftDays(today, 1); 
    } 
 
 
}