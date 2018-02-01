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
package ru.algorithmist.jquant.connectors; 
 
 
 
import org.joda.time.DateTimeFieldType; 
import org.joda.time.DateTimeZone; 
import org.joda.time.Instant; 
import org.joda.time.format.DateTimeFormat; 
import org.joda.time.format.DateTimeFormatter; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.infr.DateUtils; 
import ru.algorithmist.jquant.infr.HTTPClientFactory; 
import ru.algorithmist.jquant.infr.IHTTPClient; 
 
import java.io.IOException; 
import java.text.ParseException; 
 
/**
 * @author "Sergey Edunov" 
 * @version Nov 18, 2010 
 */ 
public class FinamDataExporter { 
 
    private static DateTimeFormatter DF = DateTimeFormat.forPattern("yyyyMMddHHmmss").withZone(DateTimeZone.UTC); 
    private IHTTPClient httpClient = HTTPClientFactory.getInstance().client(); 
    private FinamStaticData fstd = new FinamStaticData(); 
 
    public void parseDaily(String ticker, Instant from, Instant to, QuoteCallback callback, TimeInterval interval) throws IOException, ParseException { 
        int id = getId(ticker); 
        String content = httpClient.getContent("http://195.128.78.52/file.txt?d=d&market=1&em=" 
                +id+ 
                "&df=" + from.get(DateTimeFieldType.dayOfMonth()) + 
                "&mf="+ (from.get(DateTimeFieldType.monthOfYear()) - 1) + 
                "&yf="+ (from.get(DateTimeFieldType.year())) + 
                "&dt=" + to.get(DateTimeFieldType.dayOfMonth()) + 
                "&mt=" + (to.get(DateTimeFieldType.monthOfYear()) - 1) + 
                "&yt="+ (to.get(DateTimeFieldType.year())) + 
                "&p=" + interval(interval) + 
                "&f=file&e=.txt&cn="+ticker+ 
                "&dtf=1&tmf=1&MSOR=0&sep=1&sep2=1&datf=1&at=1" 
        ); 
        String[] lines = content.split("\n"); 
        parse(lines, callback, from); 
    } 
 
    private String interval(TimeInterval interval) { 
        switch (interval){ 
            case DAY: return "8"; 
            case HOUR: return "7"; 
            case MIN: return "2"; 
        } 
        throw new IllegalArgumentException("Unsupported time interval " + interval); 
    } 
 
    private static void parse(String[] lines, QuoteCallback callback, Instant prev) throws ParseException { 
        Instant now = new Instant(); 
        for(int i=1; i<lines.length; i++){ 
            String line = lines[i]; 
            prev = parse(prev, line, callback, now); 
        } 
    } 
 
 
    private int getId(String ticker) { 
        return fstd.get(ticker); 
    } 
 
 
 
    private static Instant parse(Instant prev, String in, QuoteCallback callback, Instant now) throws ParseException { 
        try{ 
            String[] items = in.split(","); 
            Instant date = DF.parseDateTime(items[2] + items[3]).toInstant(); 
            TimeInterval interval; 
            if ("D".equals(items[1])){ 
                interval = TimeInterval.DAY; 
            }else if ("60".equals(items[1])){ 
                interval = TimeInterval.HOUR; 
            } else if ("1".equals(items[1])){ 
                interval = TimeInterval.MIN; 
            }else{ 
                throw new IllegalArgumentException("Unsupported time interval from Finam " + items[1]); 
            } 
            if (prev!=null) { 
                fillTheGap(prev, date, callback, interval); 
            } 
            callback.setDate(date); 
            callback.setOpen(Double.parseDouble(items[4])); 
            callback.setHigh(Double.parseDouble(items[5])); 
            callback.setLow(Double.parseDouble(items[6])); 
            callback.setClose(Double.parseDouble(items[7])); 
            if (items.length>8) { 
                callback.setVolume(Long.parseLong(items[8].trim())); 
            } else{ 
                callback.setVolume(Double.NaN); 
            } 
 
 
            callback.setTimeInterval(interval); 
            Instant realDateTo = DateUtils.shift(date, interval, 1); 
            if (realDateTo.isBefore(now)){ 
                callback.setTimeInterval(interval); 
                callback.commit(); 
            } 
            return date; 
        }catch(Exception e){ 
            throw new RuntimeException(e); 
        } 
    } 
 
    private static void fillTheGap(Instant prev, Instant date, QuoteCallback callback, TimeInterval interval) { 
        Instant yesterday = DateUtils.shift(date, interval, -1); 
        while (prev.isBefore(yesterday)){ 
            callback.setDate(yesterday); 
            callback.setClose(Double.NaN); 
            callback.setOpen(Double.NaN); 
            callback.setHigh(Double.NaN); 
            callback.setLow(Double.NaN); 
            callback.setVolume(Double.NaN); 
            callback.setTimeInterval(interval); 
            callback.commit(); 
            yesterday = DateUtils.shift(yesterday, interval, -1); 
        } 
    } 
}