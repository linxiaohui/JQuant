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
package ru.algorithmist.jquant; 
 
import org.joda.time.DateTime; 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.signals.*; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.IParameter; 
import ru.algorithmist.jquant.engine.DataService; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.quotes.CloseParameter; 
import ru.algorithmist.jquant.infr.DateUtils; 
import ru.algorithmist.jquant.strategy.LimitedCash; 
import ru.algorithmist.jquant.strategy.OpenPositions; 
 
import java.util.List; 
import java.util.ArrayList; 
import java.io.IOException; 
 
/**
 * Author: Sergey Edunov 
 */ 
public class RSICausalityAnalysis { 
 
    static Instant to = new DateTime(2010, 12, 28, 0, 0, 0, 0).toInstant(); 
    static Instant from = new DateTime(2009, 12, 28, 0, 0, 0, 0).toInstant(); 
            public static final int LAG = 10; 
 
    public static void main(String[] args) throws IOException { 
        Initializer.initialize(); 
 
        Security symbol = new Security("SBER", "FINAM"); 
        TimeInterval interval = TimeInterval.DAY; 
        RSISignal signal = new RSISignal(symbol, 14, interval); 
        DeTrendedRSISignal signal1 = new DeTrendedRSISignal(symbol, 14, 50, interval); 
 
 
        SignalProcessor processor = SignalProcessor.instance(); 
        RSICausalityCallback callback = new RSICausalityCallback(new CloseParameter(symbol, interval)); 
        processor.process(signal, from, to, callback, new OpenPositions(), new LimitedCash(1)); 
 
        for(int d = 1; d< LAG; d++) { 
            double[] y = y(callback.futureMoves(d)); 
            double[] x = x(callback.getSignalData()); 
            int cnt = 0; 
            for(int i=0; i<x.length; i++){ 
                if (x[i]*y[i] > 0){ 
                    cnt++; 
                } 
            } 
            System.out.println(d + " > " + cnt + "/" + x.length + " " + cnt/(double)x.length); 
        } 
        Initializer.dispose(); 
         
    } 
 
    static double[] y(List<Boolean> futureMoves){ 
        double[] res = new double[futureMoves.size()]; 
        for(int i=0; i<futureMoves.size(); i++){ 
            boolean move = futureMoves.get(i); 
            if (move){ 
                res[i] = 1; 
            }else{ 
                res[i] = -1; 
            } 
        } 
        return res; 
    } 
 
    static double[] x(List<BuySellSignalData> signalData){ 
        double[] res = new double[signalData.size()]; 
        for(int i=0; i<signalData.size(); i++){ 
            BuySellSignalData bssd = signalData.get(i); 
            if (bssd.getStatus() == BuySellStatus.BUY_LONG){ 
                res[i] = 1; 
            }else{ 
                res[i] = -1; 
            } 
        } 
        return res; 
    } 
 
    static class RSICausalityCallback implements SignalCallback{ 
 
        private List<BuySellSignalData> signalData = new ArrayList<BuySellSignalData>(); 
        private List<Boolean>[] moveData = new List[LAG]; 
        private IParameter closeValue; 
 
        RSICausalityCallback(IParameter closeValue) { 
            this.closeValue = closeValue; 
            for(int i=0; i<LAG; i++){ 
               moveData[i] = new ArrayList<Boolean>(); 
            } 
        } 
 
        @Override 
        public void signal(ISignal signal, SignalData data) { 
            if (data instanceof BuySellSignalData){ 
                System.out.print(data.getSignalDate() + " " + ((BuySellSignalData) data).getStatus()); 
                signalData.add((BuySellSignalData) data); 
                for(int i=1; i<LAG; i++){ 
                    Boolean r = realMarketMove(closeValue, data.getSignalDate(), i); 
                    if (r==null) return; 
                    System.out.print(" " + r); 
                    moveData[i].add(r); 
                } 
                System.out.println(); 
            } 
        } 
 
        private Boolean realMarketMove(IParameter closeValue, Instant signalDate, int delay) { 
            double currentClose = DataService.instance().value(signalDate, closeValue).getValue(); 
            double futureClose = 0; 
            while(delay > 0) { 
                signalDate = DateUtils.shift(signalDate, closeValue.getTimeInterval(), 1); 
                if (signalDate.isAfter(to)){ 
                    break; 
                } 
                Value value = DataService.instance().value(signalDate, closeValue); 
                if (value.isOK()){ 
                    delay--; 
                    futureClose = value.getValue(); 
                } 
            } 
            return futureClose > currentClose; 
 
        } 
 
        public List<BuySellSignalData> getSignalData() { 
            return signalData; 
        } 
 
        public List<Boolean> futureMoves(int days){ 
            return moveData[days]; 
        } 
    } 
}