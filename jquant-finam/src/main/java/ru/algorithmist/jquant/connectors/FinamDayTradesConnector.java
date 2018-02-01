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
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.*; 
import ru.algorithmist.jquant.infr.DateUtils; 
 
import java.util.logging.Level; 
import java.util.logging.Logger; 
 
/**
 * @author "Sergey Edunov" 
 * @version 12/29/10 
 */ 
public class FinamDayTradesConnector implements IConnector{ 
 
    private FinamDataExporter exporter = new FinamDataExporter(); 
 
    private static final Logger logger = Logger.getLogger(FinamDayTradesConnector.class.getName()); 
 
 
    public static final int DOWNLOAD_GAP_DAYS = 30; 
    public static final int DOWNLOAD_GAP_HOURS = 24*30; 
 
 
    @Override 
    public void load(String name, String symbol, Instant date, TimeInterval interval) { 
        System.out.println("Query for " + symbol + " for " + date + " interval " + interval); 
        Instant to = new Instant(); 
        Instant toS = shift(date, interval, 1); 
        if (to.isAfter(toS)){ 
            to = toS; 
        } 
        if (date.isAfter(to)){ 
            throw new IllegalArgumentException("Date is not available " + date); 
        } 
        Instant from = shift(date, interval, -1); 
        try { 
            StoreQuoteCallback sqc = new StoreQuoteCallback(new ru.algorithmist.jquant.engine.Security(symbol, "FINAM")); 
            exporter.parseDaily(symbol, from, to, sqc, interval); 
        } catch (Exception e) { 
            logger.log(Level.SEVERE, e.getMessage(), e); 
        } 
    } 
 
    private Instant shift(Instant date, TimeInterval interval, int sign) { 
        if (interval == TimeInterval.DAY){ 
            return DateUtils.shift(date, interval, sign*DOWNLOAD_GAP_DAYS); 
        } else if (interval == TimeInterval.HOUR){ 
            return DateUtils.shift(date, interval, sign*DOWNLOAD_GAP_HOURS); 
        } 
        return date; 
    } 
 
    @Override 
    public boolean canLoad(String source, TimeInterval interval) { 
        return "FINAM".equals(source); 
    } 
 
    @Override 
    public DataQueryResult load(String name, String symbol, Instant from, Instant to, TimeInterval interval) { 
        System.out.println("Query for " + symbol + " from " + from + " to " + to); 
        DataQueryResult ret = new DataQueryResult(); 
        try { 
            StoreQuoteCallback sqc = new StoreQuoteCallback(new ru.algorithmist.jquant.engine.Security(symbol, "FINAM")); 
            sqc.setQueryParameter(name); 
            sqc.setQueryResult(ret); 
            exporter.parseDaily(symbol, from, to, sqc, interval); 
        } catch (Exception e) { 
            logger.log(Level.SEVERE, e.getMessage(), e); 
        } 
        return ret; 
    } 
 
}