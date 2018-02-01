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
 * @version Jan 9, 2011 
 */ 
public class YahooDataExporter { 
 
    private static DateTimeFormatter DF = DateTimeFormat.forPattern("yyyy-MM-dd"); 
    private IHTTPClient httpClient = HTTPClientFactory.getInstance().client(); 
 
    public void parseDaily(String ticker, Instant from, Instant to, QuoteCallback callback) throws IOException, ParseException { 
        String content = httpClient.getContent("http://ichart.finance.yahoo.com/table.csv?s=" 
                + ticker + 
                "&a=" + from.get(DateTimeFieldType.monthOfYear()) + 
                "&b=" + from.get(DateTimeFieldType.dayOfMonth()) + 
                "&c=" + from.get(DateTimeFieldType.year()) + 
                "&d=" + to.get(DateTimeFieldType.monthOfYear()) + 
                "&e=" + to.get(DateTimeFieldType.dayOfMonth()) + 
                "&f=" + to.get(DateTimeFieldType.year()) + 
                "&g=d&ignore=.csv" 
        ); 
        String[] lines = content.split("\n"); 
        parse(lines, callback); 
    } 
 
 
    private static void parse(String[] lines, QuoteCallback callback) throws ParseException { 
        Instant prev = null; 
        if (lines[1]!=null && lines[1].contains("Yahoo! - 404 Not Found")) return; 
        for(int i=1; i<lines.length; i++){ 
            String line = lines[i]; 
            prev = parse(prev, line, callback); 
        } 
    } 
 
 
 
    private static Instant parse(Instant prev, String in, QuoteCallback callback) throws ParseException { 
        try{ 
            String[] items = in.split(","); 
            Instant date = DF.parseDateTime(items[0]).toInstant(); 
            if (prev!=null) { 
                fillTheGap(prev, date, callback); 
            } 
            callback.setDate(date); 
            callback.setOpen(Double.parseDouble(items[1])); 
            callback.setHigh(Double.parseDouble(items[2])); 
            callback.setLow(Double.parseDouble(items[3])); 
            callback.setClose(Double.parseDouble(items[4])); 
            callback.setVolume(Long.parseLong(items[5])); 
            callback.setTimeInterval(TimeInterval.DAY); 
            callback.commit(); 
            return date; 
        }catch(Exception e){ 
            throw new RuntimeException(e); 
        } 
    } 
 
    private static void fillTheGap(Instant prev, Instant date, QuoteCallback callback) { 
        Instant tomorrow = DateUtils.tomorrow(date); 
        while (!DateUtils.isTheSameDay(prev, tomorrow)){ 
            callback.setDate(tomorrow); 
            callback.setClose(Double.NaN); 
            callback.setOpen(Double.NaN); 
            callback.setHigh(Double.NaN); 
            callback.setLow(Double.NaN); 
            callback.setVolume(Double.NaN); 
            callback.commit(); 
            tomorrow = DateUtils.tomorrow(tomorrow); 
        } 
    } 
}