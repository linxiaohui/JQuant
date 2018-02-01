package ru.algorithmist.jquant.connectors;
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.DataQueryResult; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.infr.DateUtils; 
 
import java.net.URLEncoder; 
import java.util.logging.Logger; 
import java.util.logging.Level; 
 
/**
 * Author: Sergey Edunov 
 */ 
public class YahooDayTradesConnector  implements IConnector{ 
 
    private YahooDataExporter exporter = new YahooDataExporter(); 
    private static final Logger logger = Logger.getLogger(YahooDayTradesConnector.class.getName()); 
 
 
    @Override 
    public void load(String name, String symbol, Instant date, TimeInterval interval) { 
        System.out.println("Yahoo query for " + symbol + " for " + date); 
        Instant to = new Instant(); 
        Instant toS = DateUtils.shiftDays(date, 60); 
        if (to.isAfter(toS)){ 
            to = toS; 
        } 
        if (date.isAfter(to)){ 
            throw new IllegalArgumentException("Date is not available " + date); 
        } 
        Instant from = DateUtils.shiftDays(date, -60); 
        try { 
            exporter.parseDaily(URLEncoder.encode(symbol, "UTF-8"), from, to, new StoreQuoteCallback(new Security(symbol, "YAHOO"))); 
        } catch (Exception e) { 
            logger.log(Level.SEVERE, e.getMessage(), e); 
        } 
    } 
 
    @Override 
    public boolean canLoad(String source, TimeInterval interval) { 
        return interval == TimeInterval.DAY && "YAHOO".equals(source); 
    } 
 
    @Override 
    public DataQueryResult load(String name, String symbol, Instant from, Instant to, TimeInterval interval) { 
        System.out.println("Query for " + symbol + " from " + from + " to " + to); 
        DataQueryResult ret = new DataQueryResult(); 
        try { 
            StoreQuoteCallback sqc = new StoreQuoteCallback(new ru.algorithmist.jquant.engine.Security(symbol, "YAHOO")); 
            sqc.setQueryParameter(name); 
            sqc.setQueryResult(ret); 
            exporter.parseDaily(URLEncoder.encode(symbol, "UTF-8"), from, to, sqc); 
        } catch (Exception e) { 
            logger.log(Level.SEVERE, e.getMessage(), e); 
        } 
        return ret; 
    } 
}