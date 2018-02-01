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
 
import org.joda.time.DateTimeZone; 
import org.joda.time.Instant; 
import org.joda.time.format.DateTimeFormat; 
import org.joda.time.format.DateTimeFormatter; 
import org.junit.Ignore; 
import ru.algorithmist.jquant.engine.DataQueryResult; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.infr.DateUtils; 
 
import java.io.InputStream; 
import java.text.DateFormat; 
import java.text.ParseException; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
import java.util.Scanner; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/17/11 
 */ 
@Ignore("Helper class. Not a test. Need to ignore it because maven tests fail otherwise.") 
public class TestConnector implements IConnector { 
 
    private static DateTimeFormatter DF = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC); 
    private static Instant from; 
    private static Instant to; 
    private static boolean isLoaded = false; 
 
    public TestConnector() throws ParseException { 
        if (!isLoaded) { 
            InputStream is = TestConnector.class.getResourceAsStream("ibm.txt"); 
            Scanner sc = new Scanner(is); 
            QuoteCallback callback = new StoreQuoteCallback(new Security("IBM", "TEST")); 
            callback.setTimeInterval(TimeInterval.DAY); 
            while (sc.hasNextLine()) { 
                String line = sc.nextLine(); 
                to = parse(to, line, callback); 
                if (from==null){ 
                    from = to; 
                } 
            } 
            isLoaded = true; 
        } 
    } 
 
    @Override 
    public void load(String name, String symbol, Instant date, TimeInterval interval) { 
    } 
 
    @Override 
    public boolean canLoad(String source, TimeInterval interval) { 
        return "TEST".equals(source) && interval == TimeInterval.DAY; 
    } 
 
    @Override 
    public DataQueryResult load(String name, String symbol, Instant from, Instant to, TimeInterval interval) { 
        throw new IllegalArgumentException(); 
    } 
 
    private static Instant parse(Instant prev, String in, QuoteCallback callback) throws ParseException { 
        try { 
            String[] items = in.split("\\s+"); 
            Instant date = DF.parseDateTime(items[0]).toInstant(); 
            if (prev != null) { 
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
        } catch (Exception e) { 
            throw new RuntimeException(e); 
        } 
    } 
 
    private static void fillTheGap(Instant prev, Instant date, QuoteCallback callback) { 
        Instant yesterday = DateUtils.yesterday(date); 
        while (!DateUtils.isTheSameDay(prev, yesterday)){ 
            callback.setDate(yesterday); 
            callback.setClose(Double.NaN); 
            callback.setOpen(Double.NaN); 
            callback.setHigh(Double.NaN); 
            callback.setLow(Double.NaN); 
            callback.setVolume(Double.NaN); 
            callback.commit(); 
            yesterday = DateUtils.yesterday(yesterday); 
        } 
    } 
}