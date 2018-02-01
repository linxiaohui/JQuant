package ru.algorithmist.jquant.signals;
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.*; 
import ru.algorithmist.jquant.infr.DateUtils; 
import ru.algorithmist.jquant.quotes.CloseParameter; 
import ru.algorithmist.jquant.quotes.HighParameter; 
import ru.algorithmist.jquant.quotes.LowParameter; 
import ru.algorithmist.jquant.strategy.Cash; 
import ru.algorithmist.jquant.strategy.OpenPositions; 
import ru.algorithmist.jquant.strategy.Position; 
 
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.List; 
import java.util.Map; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/24/11 
 */ 
public class TrailingStopCloseSignal implements IRangeCloseSignal { 
 
    private double stopLossRatio; 
    private TimeInterval interval; 
    private Security security; 
    private Map<Instant, Value> close = new HashMap<Instant, Value>(); 
    private Map<Instant, Value> high = new HashMap<Instant, Value>(); 
    private Map<Instant, Value> low = new HashMap<Instant, Value>(); 
 
    public TrailingStopCloseSignal(double stopLossRatio, TimeInterval interval, Security security) { 
        this.stopLossRatio = stopLossRatio; 
        this.security = security; 
        this.interval = interval; 
    } 
 
    @Override 
    public void setRange(Instant from, Instant to) { 
        DataQueryResult dqr = DataService.instance().values(from, to, new CloseParameter(security, interval)); 
        for(DataQueryObject dqo : dqr){ 
            close.put(dqo.getDate(), dqo.getValue()); 
        } 
        dqr = DataService.instance().values(from, to, new HighParameter(security, interval)); 
        for(DataQueryObject dqo : dqr){ 
            high.put(dqo.getDate(), dqo.getValue()); 
        } 
        dqr = DataService.instance().values(from, to, new LowParameter(security, interval)); 
        for(DataQueryObject dqo : dqr){ 
            low.put(dqo.getDate(), dqo.getValue()); 
        } 
    } 
 
    @Override 
    public TimeInterval timeInterval() { 
        return interval; 
    } 
 
    @Override 
    public List<SignalData> test(Instant date, OpenPositions openPositions, Cash cash) { 
        List<SignalData> result = new ArrayList<SignalData>(); 
        for (Position pos : openPositions) { 
            if (!date.isAfter(pos.getDateOpen())){ //Skip fresh positions 
                continue; 
            } 
            Security sec = pos.getSecurity(); 
            if (!sec.equals(security)) continue; 
            double stopLossPrice; 
            Value lastClose = lastClose(date); 
            Value high = this.high.get(date); 
            Value low = this.low.get(date); 
            if (lastClose.isOK() && high.isOK() && low.isOK()) { 
                if (pos.getQuantity() > 0) { 
                    stopLossPrice = lastClose.getValue() * (1 - stopLossRatio); 
                    if (low.getValue() < stopLossPrice) { 
                        result.add(new BuySellSignalData(date, interval, sec, BuySellStatus.CLOSE_LONG)); 
                    } 
                } else { 
                    stopLossPrice = lastClose.getValue() * (1 + stopLossRatio); 
                    if (high.getValue() > stopLossPrice) { 
                        result.add(new BuySellSignalData(date, interval, sec, BuySellStatus.CLOSE_SHORT)); 
                    } 
                } 
            } 
        } 
        return result; 
    } 
 
    private Value lastClose(Instant date) { 
        Value res = null; 
        do{ 
            date = DateUtils.shift(date, interval, -1); 
            res = close.get(date); 
        }while(res == null || res.isNA()); 
        return  res; 
    } 
}