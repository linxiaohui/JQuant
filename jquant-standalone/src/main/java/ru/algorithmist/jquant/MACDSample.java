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
import ru.algorithmist.jquant.engine.DataService; 
import ru.algorithmist.jquant.engine.IParameter; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.indicators.EMAParameter; 
import ru.algorithmist.jquant.indicators.MACDParameter; 
import ru.algorithmist.jquant.indicators.MACDSignalParameter; 
import ru.algorithmist.jquant.indicators.RSIParameter; 
import ru.algorithmist.jquant.infr.DateUtils; 
import ru.algorithmist.jquant.quotes.CloseParameter; 
 
import java.io.IOException; 
import java.util.Date; 
 
/**
 * User: Sergey Edunov 
 * Date: 16.01.11 
 */ 
public class MACDSample { 
 
    public static void main(String[] args) throws IOException { 
           Initializer.initialize(); 
           Instant date = new DateTime(2011, 1, 14, 0, 0, 0, 0).toInstant(); 
           CloseParameter close = new CloseParameter(new Security("IBM", "YAHOO"), TimeInterval.DAY); 
           MACDParameter macd = new MACDParameter(close, 12, 26); 
           EMAParameter ema = new EMAParameter(close, 12); 
           EMAParameter ema26 = new EMAParameter(close, 26); 
           IParameter signalMacd = new MACDSignalParameter(macd, 9); 
           for(int i=100; i>=0; i--){ 
               Instant d = DateUtils.shift(date, TimeInterval.DAY, -i); 
               Value c = DataService.instance().value(d, close); 
               Value e = DataService.instance().value(d, ema); 
               Value e26 = DataService.instance().value(d, ema26); 
               Value v = DataService.instance().value(d, macd); 
               Value v1 = DataService.instance().value(d, signalMacd); 
               if (v.isOK()){ 
                   System.out.println(d + " " + c.getValue() + " " + e.getValue() + " " + e26.getValue() + " " + v.getValue() + " " + v1.getValue()); 
               } 
           } 
 
           Initializer.dispose(); 
       } 
 
 
}