package ru.algorithmist.jquant.signals;
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.*; 
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
public class StopLossAndTakeProfitCloseSignal implements IRangeCloseSignal { 
 
    private double stopLossRatio; 
    private double takeProfitRatio; 
    private TimeInterval interval; 
    private Map<Instant, Value> high = new HashMap<Instant, Value>(); 
    private Map<Instant, Value> low = new HashMap<Instant, Value>(); 
    private Security security; 
 
    public StopLossAndTakeProfitCloseSignal(double stopLossRatio, double takeProfitRatio, TimeInterval interval, Security security) { 
        this.stopLossRatio = stopLossRatio; 
        this.takeProfitRatio = takeProfitRatio; 
        this.interval = interval; 
        this.security = security; 
    } 
 
 
 
    @Override 
    public void setRange(Instant from, Instant to) { 
        DataQueryResult dqr = DataService.instance().values(from, to, new HighParameter(security, interval)); 
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
        List<SignalData> res = new ArrayList<SignalData>(); 
        for(Position pos : openPositions) { 
            Security sec = pos.getSecurity(); 
            if (!sec.equals(security)) continue; 
            double stopLossPrice, takeProfitPrice; 
            Value high = this.high.get(date); 
            Value low = this.low.get(date); 
            if (high.isOK() && low.isOK()) { 
                if (pos.getQuantity() > 0) { 
                    stopLossPrice = pos.getOpenPrice() * (1 - stopLossRatio); 
                    takeProfitPrice = pos.getOpenPrice() * (1 + takeProfitRatio); 
 
                    if (low.getValue() < stopLossPrice || high.getValue() > takeProfitPrice) { 
                        res.add(new BuySellSignalData(date, interval, sec, BuySellStatus.CLOSE_LONG)); 
                    } 
                } else { 
                    stopLossPrice = pos.getOpenPrice() * (1 + stopLossRatio); 
                    takeProfitPrice = pos.getOpenPrice() * (1 - takeProfitRatio); 
                    if (low.getValue() < takeProfitPrice || high.getValue() > stopLossPrice) { 
                        res.add(new BuySellSignalData(date, interval, sec, BuySellStatus.CLOSE_SHORT)); 
                    } 
                } 
            } 
        } 
        return res; 
    } 
}