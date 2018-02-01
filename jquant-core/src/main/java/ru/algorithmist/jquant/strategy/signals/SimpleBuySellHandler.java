package ru.algorithmist.jquant.strategy.signals;
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.DataService; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.quotes.CloseParameter; 
import ru.algorithmist.jquant.signals.BuySellSignalData; 
import ru.algorithmist.jquant.signals.SignalData; 
import ru.algorithmist.jquant.strategy.Cash; 
import ru.algorithmist.jquant.strategy.OpenPositions; 
import ru.algorithmist.jquant.strategy.Position; 
import ru.algorithmist.jquant.strategy.TransactionsCallback; 
 
import java.util.Date; 
import java.util.ListIterator; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/21/11 
 */ 
public class SimpleBuySellHandler implements SignalHandler { 
 
    @Override 
    public void handleSignal(SignalData sd, OpenPositions openPositions, Cash cash, TransactionsCallback callback) { 
        BuySellSignalData bsd = (BuySellSignalData) sd; 
        handleSignal0(bsd, openPositions, cash, callback); 
    } 
 
 
    private void handleSignal0(BuySellSignalData buySellSignalData, OpenPositions openPositions, Cash cash, TransactionsCallback callback) { 
        switch (buySellSignalData.getStatus()) { 
            case BUY_LONG: 
                buyLong(buySellSignalData.getSecurity(), buySellSignalData.getSignalDate(), openPositions, cash, buySellSignalData.getInterval(), callback); 
                break; 
            case SELL_SHORT: 
                sellShort(buySellSignalData.getSecurity(), buySellSignalData.getSignalDate(), openPositions, cash, buySellSignalData.getInterval(),  callback); 
                break; 
            case CLOSE_LONG: 
                closeLong(buySellSignalData.getSecurity(), buySellSignalData.getSignalDate(), openPositions, buySellSignalData.getInterval(), callback); 
                break; 
            case CLOSE_SHORT: 
                closeShort(buySellSignalData.getSecurity(), buySellSignalData.getSignalDate(), openPositions, buySellSignalData.getInterval(), callback); 
        } 
    } 
 
    private void closeShort(Security symbol, Instant date, OpenPositions openPositions, TimeInterval interval, TransactionsCallback callback) { 
        for (ListIterator<Position> it = openPositions.iterator(); it.hasNext();) { 
            Position pos = it.next(); 
            if (pos.getSecurity().equals(symbol) && pos.getQuantity() < 0) { 
                Value price = closePositionPrice(symbol, date, interval); 
                if (price.isOK()) { 
                    callback.closePosition(date, pos, pos.getQuantity(), price.getValue()); 
                } 
            } 
        } 
    } 
 
    private void closeLong(Security symbol, Instant date, OpenPositions openPositions, TimeInterval interval, TransactionsCallback callback) { 
        for (ListIterator<Position> it = openPositions.iterator(); it.hasNext();) { 
            Position pos = it.next(); 
            if (pos.getSecurity().equals(symbol) && pos.getQuantity() > 0) { 
                Value price = closePositionPrice(symbol, date, interval); 
                if (price.isOK()) { 
                    callback.closePosition(date, pos, pos.getQuantity(), price.getValue()); 
                } 
            } 
        } 
    } 
 
    private void sellShort(Security symbol, Instant date, OpenPositions openPositions, Cash cash, TimeInterval interval, TransactionsCallback callback) { 
        closeLong(symbol, date, openPositions, interval, callback); 
        double available = cash.available(); 
        if (available > 1e-6) { 
            Value price = openPositionPrice(symbol, date, interval); 
            if (price.isOK()) { 
                callback.openPosition(date, symbol, -available / price.getValue(), price.getValue()); 
            } 
        } 
    } 
 
    private void buyLong(Security symbol, Instant date, OpenPositions openPositions, Cash cash, TimeInterval interval, TransactionsCallback callback) { 
        closeShort(symbol, date, openPositions, interval, callback); 
        double available = cash.available(); 
        if (available > 1e-6) { 
            Value price = openPositionPrice(symbol, date, interval); 
            if (price.isOK()) { 
                callback.openPosition(date, symbol, available / price.getValue(), price.getValue()); 
            } 
        } 
    } 
 
    private Value closePositionPrice(Security security, Instant date, TimeInterval interval) { 
        Value close = DataService.instance().value(date, new CloseParameter(security, interval)); 
        return close; 
    } 
 
    private Value openPositionPrice(Security security, Instant date, TimeInterval interval) { 
        Value close = DataService.instance().value(date, new CloseParameter(security, interval)); 
        return close; 
    } 
 
}