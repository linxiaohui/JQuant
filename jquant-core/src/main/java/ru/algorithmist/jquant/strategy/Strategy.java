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
 
package ru.algorithmist.jquant.strategy; 
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.*; 
import ru.algorithmist.jquant.infr.DateUtils; 
import ru.algorithmist.jquant.quotes.CloseParameter; 
import ru.algorithmist.jquant.signals.*; 
import ru.algorithmist.jquant.strategy.signals.SignalHandler; 
import ru.algorithmist.jquant.strategy.signals.SignalHandlerFactory; 
 
import java.util.*; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/18/11 
 */ 
public class Strategy { 
 
    private List<ISignal> signals = new ArrayList<ISignal>(); 
    private List<ICloseSignal> closeSignals = new ArrayList<ICloseSignal>(); 
    private TimeInterval interval; 
 
    public void addSignal(ISignal signal){ 
        if (interval == null){ 
            interval = signal.timeInterval(); 
        }else if (interval != signal.timeInterval()){ 
            throw new IllegalArgumentException("Can't work on different time intervals"); 
        } 
        signals.add(signal); 
    } 
 
    public void addCloseSignal(ICloseSignal signal){ 
        if (interval == null){ 
            interval = signal.timeInterval(); 
        }else if (interval != signal.timeInterval()){ 
            throw new IllegalArgumentException("Can't work on different time intervals"); 
        } 
        closeSignals.add(signal); 
    } 
 
 
    public StrategyResult calculateReturn(Instant from, Instant to, Cash cash){ 
        StrategyResult result = new StrategyResult(); 
        OpenPositions openPositions = new OpenPositions(); 
        SignalHandlerFactory sfFactory = new SignalHandlerFactory(); 
        TransactionsCallback simpleTransactionsCallback = new SimpleTransactionsCallback(openPositions, cash); 
 
        for(ICloseSignal closeSignal : closeSignals){ 
            if (closeSignal instanceof IRangeCloseSignal){ 
                ((IRangeCloseSignal)closeSignal).setRange(from, to); 
            } 
        } 
 
        List<SignalData> allSignals = new ArrayList<SignalData>(); 
        for(ISignal signal : signals){ 
            List<SignalData> sdl = signal.test(from, to); 
            allSignals.addAll(sdl); 
        } 
        Collections.sort(allSignals); 
 
        Instant date = from; 
        for(SignalData sd : allSignals) { 
            date = checkClose(cash, result, openPositions, sfFactory, simpleTransactionsCallback, date, sd.getSignalDate()); 
            SignalHandler handler = sfFactory.getHandler(sd); 
            handler.handleSignal(sd, openPositions, cash, simpleTransactionsCallback); 
            postProcessClosedPositions(openPositions, result); 
        } 
        date = checkClose(cash, result, openPositions, sfFactory, simpleTransactionsCallback, date, to); 
 
        for(Position p : openPositions){ 
            result.getPositions().add(p); 
        } 
        return result; 
    } 
 
    private Instant checkClose(Cash cash, StrategyResult result, OpenPositions openPositions, SignalHandlerFactory sfFactory, TransactionsCallback simpleTransactionsCallback, Instant date, Instant to) { 
        while (!to.equals(date)){ 
            generateCloseSignals(cash, openPositions, sfFactory, simpleTransactionsCallback, date, result); 
            double closePrice = estimateOpenPositions(date, openPositions); 
            if (!Double.isNaN(closePrice)){ 
                result.getStates().add(new PortfolioState(cash.estimate(), closePrice, date)); 
            } 
            date = DateUtils.shift(date, interval, 1); 
        } 
        return date; 
    } 
 
    private void generateCloseSignals(Cash cash, OpenPositions openPositions, SignalHandlerFactory sfFactory, TransactionsCallback simpleTransactionsCallback, Instant date, StrategyResult result) { 
        for(ICloseSignal closeSignal : closeSignals){ 
            List<SignalData> sdl = closeSignal.test(date, openPositions, cash); 
            for(SignalData ssd : sdl){ 
                SignalHandler handler = sfFactory.getHandler(ssd); 
                handler.handleSignal(ssd, openPositions, cash, simpleTransactionsCallback); 
                postProcessClosedPositions(openPositions, result); 
            } 
        } 
    } 
 
 
    private Value closePositionPrice(Security security, Instant date){ 
        Value close = DataService.instance().value(date, new CloseParameter(security, interval)); 
        return close; 
    } 
 
    private double estimateOpenPositions(Instant date, OpenPositions openPositions){ 
        double res = 0; 
        boolean isOk = false; 
        for(Position pos : openPositions){ 
            Value close = closePositionPrice(pos.getSecurity(), date); 
            if (close.isOK()){ 
                isOk = true; 
                if (pos.getQuantity() > 0){ 
                    res += close.getValue() * pos.getQuantity(); 
                } else { 
                    res += (close.getValue() - 2 * pos.getOpenPrice()) * pos.getQuantity(); 
                } 
 
            } 
        } 
        return isOk? res:Double.NaN; 
    } 
 
    private void postProcessClosedPositions(OpenPositions openPositions, StrategyResult result){ 
        //TODO: some kind of reporting can be done here 
        for (ListIterator<Position> it = openPositions.iterator(); it.hasNext();) { 
            Position p = it.next(); 
            if (p.isClosed()){ 
//                System.out.println(p.getDateOpen() + " " + p.getOpenPrice() + " " + p.getDateClosed() + " " + p.getClosePrice()); 
                result.getPositions().add(p); 
                it.remove(); 
            } 
        } 
    } 
 
 
    static class SimpleTransactionsCallback implements TransactionsCallback{ 
 
        private OpenPositions openPositions; 
        private Cash cash; 
 
        SimpleTransactionsCallback(OpenPositions openPositions, Cash cash){ 
            this.openPositions = openPositions; 
            this.cash = cash; 
        } 
 
 
        @Override 
        public void openPosition(Instant date, Security security, double quantity, double price) { 
            if (quantity > 0){  //Long 
                openPositions.openPosition(new Position(security, quantity, price, date)); 
                cash.change(-quantity*price); 
            } else { //Short 
                openPositions.openPosition(new Position(security, quantity, price, date)); 
                cash.change(quantity*price); 
            } 
        } 
 
        @Override 
        public void closePosition(Instant date, Position position, double quantity, double price) { 
            if (position.isClosed()){ 
                throw new RuntimeException("Can't close already closed position " + position); 
            } 
            if(position.getQuantity() > 0){ //Long 
                if (quantity > 0 && quantity <= position.getQuantity()+1e-6) { 
                    cash.change(quantity * price); 
                    double remQ = position.getQuantity() - quantity; 
                    position.setClosedDate(date); 
                    position.setClosePrice(price); 
                    if (Math.abs(remQ)< 1e-6){ //Fully closed 
 
                    }else{ //Partially closed 
                        openPositions.openPosition(new Position(position.getSecurity(), remQ, position.getOpenPrice(), date)); 
                    } 
                } else { 
                    throw new RuntimeException("Can't close position " + position + " with " + quantity); 
                } 
            } else { //Short 
                if (quantity < 0 && quantity >= position.getQuantity()-1e-6){ 
                    cash.change((price - 2*position.getOpenPrice())*quantity); 
                    double remQ = position.getQuantity() - quantity; 
                    position.setClosedDate(date); 
                    position.setClosePrice(price); 
                    if (Math.abs(remQ)< 1e-6){ //Fully closed 
 
                    }else{ //Partially closed 
                        openPositions.openPosition(new Position(position.getSecurity(), remQ, position.getOpenPrice(), date)); 
                    } 
                } else { 
                    throw new RuntimeException("Can't close position " + position + " with " + quantity); 
                } 
            } 
        } 
    } 
}