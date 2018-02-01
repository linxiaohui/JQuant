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
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.signals.*; 
import ru.algorithmist.jquant.strategy.*; 
 
import java.io.File; 
import java.io.IOException; 
import java.io.PrintWriter; 
import java.text.DateFormat; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
import java.util.List; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/18/11 
 */ 
public class RSIStrategySample { 
 
    public static void main(String[] args) throws IOException { 
        Initializer.initialize(); 
        Instant to = new DateTime(2011, 1, 28, 0, 0, 0, 0).toInstant(); 
        Instant from1 = new DateTime(2010, 11, 28, 0, 0, 0, 0).toInstant(); 
        Instant from3 = new DateTime(2010, 10, 28, 0, 0, 0, 0).toInstant(); 
        Instant fromY = new DateTime(2010, 1, 28, 0, 0, 0, 0).toInstant(); 
        File file = new File("report2.txt"); 
        if (!file.exists()) { 
            file.createNewFile(); 
        } 
        out = new PrintWriter(file); 
//        String[] symbols = new String[]{"RTSI", "MICEX", "SBER", "GAZP", "AFLT", "GMKN", "VTBR", "LKOH", "ROSN", "PLZL", "SNGS", "USDJPY", "EURUSD", "GBPUSD"}; 
        String[] symbols = new String[]{"PLZL"}; 
//        double[] stopGaps = new double[]{0.1, 0.05, 0.03, 0.01, 0.005, 0.001}; 
        double[] stopGaps = new double[]{0.03}; 
//        int[] days = new int[]{2, 5, 7, 14, 20}; 
        int[] days = new int[]{2}; 
        int[] min = new int[]{20}; 
//        int[] min = new int[]{5, 10, 20, 30}; 
        int[] max = new int[]{80}; 
//        int[] max = new int[]{95, 90, 80, 70}; 
        boolean[] tf = new boolean[]{true, false}; 
        Instant[] froms = new Instant[]{from1, from3, fromY}; 
        String[] spans = new String[]{"1"}; 
//        String[] spans = new String[]{"1", "3", "Y"}; 
        for (int day : days) { 
            for (int i = 0; i < min.length; i++) { 
                for (double sg : stopGaps) { 
                    for (boolean t : tf) { 
                        double res = 0; 
                        for (int sp = 0; sp < spans.length; sp++) { 
                            for (String s : symbols) { 
                                res += process(to, froms[sp], s, sg, day, min[i], max[i], t, spans[sp]); 
                            } 
                        } 
                        out.println((t ? "BREAK" : "SIMPLE") + "\t" + sg + "\t" + day + "\t" + min[i] + "\t" + max[i] + "\t" + res / (symbols.length * 3)); 
                        out.flush(); 
                    } 
                } 
            } 
        } 
        out.close(); 
        Initializer.dispose(); 
    } 
 
    private static PrintWriter out; 
    private static DateFormat dfRus = new SimpleDateFormat("dd.MM.yy"); 
 
 
    private static double process(Instant to, Instant from, String s, double stopGap, int days, int min, int max, boolean breakSignal, String span) { 
        Security symbol = new Security(s, "FINAM"); 
 
        AbstractSignal openSignal = breakSignal ? new RSIBreakSignal(symbol, days, TimeInterval.DAY, min, max) : new RSISignal(symbol, days, TimeInterval.DAY, min, max); 
//        AbstractSignal openSignal = new RSIBreakSignal(symbol, 7, TimeInterval.DAY, 20, 80); 
//        ISignal openSignal = new DeTrendedRSISignal(symbol, 2, 7, TimeInterval.DAY, 10, 90); 
//        ISignal closeSignal = new LaggedAutoCloseSignal(openSignal, 2); 
//        ISignal closeSignal = new StopLossAndTakeProfitCloseSignal(0.001, 0.05); 
        ICloseSignal closeSignal = new TrailingStopCloseSignal(stopGap, TimeInterval.DAY, symbol); 
        Strategy strategy = new Strategy(); 
 
        strategy.addCloseSignal(closeSignal); 
        strategy.addSignal(openSignal); 
        StrategyResult result = strategy.calculateReturn(from, to, new LimitedCash(1)); 
        List<PortfolioState> portfolio = result.getStates(); 
 
        System.out.println((breakSignal ? "BREAK" : "SIMPLE") + "\t" + s + "\t" + span + "\t" + stopGap + "\t" + days + "\t" + min + "\t" + max + "\t" + portfolio.get(portfolio.size() - 1).getState()); 
        for(int i=0; i<portfolio.size(); i++){ 
            PortfolioState ps = portfolio.get(i); 
//            PortfolioState bnkPs = bnkPortfolio.get(j++); 
//            while(!bnkPs.getDate().equals(ps.getDate())){ 
//               bnkPs = bnkPortfolio.get(j++); 
//            } 
            System.out.println(ps.getDate() + " " + ps.getState()); 
        } 
        for(Position pos : result.getPositions()){ 
            System.out.println(pos.getDateOpen() + " " + pos.getDateClosed() + " " + pos.getOpenPrice() + " " + pos.getClosePrice()); 
        } 
        return portfolio.get(portfolio.size() - 1).getState(); 
    } 
}