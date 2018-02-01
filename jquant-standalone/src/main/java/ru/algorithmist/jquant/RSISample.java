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
import ru.algorithmist.jquant.indicators.RSIParameter; 
import ru.algorithmist.jquant.quotes.CloseParameter; 
import ru.algorithmist.jquant.infr.DateUtils; 
 
import java.io.IOException; 
 
/**
 * @author "Sergey Edunov" 
 * @version 12/29/10 
 */ 
public class RSISample { 
 
    public static void main(String[] args) throws IOException { 
        Initializer.initialize(); 
        Instant date = new DateTime(2010, 12, 28, 0, 0, 0, 0).toInstant(); 
        IParameter rsi = new RSIParameter(new Security("MICEX", "FINAM"), 14, TimeInterval.DAY); 
        IParameter ema = new EMAParameter(new CloseParameter(new Security("MICEX", "FINAM"), TimeInterval.DAY), 7); 
//        IParameter rsi = new EMAParameter(new CloseParameter("SBER", TimeInterval.DAY), 7); 
        for(int i=100; i>=0; i--){ 
            Instant d = DateUtils.shift(date, TimeInterval.DAY, -i); 
            Value v = DataService.instance().value(d, rsi); 
            Value v1 = DataService.instance().value(d, ema); 
            if (v.isOK()){ 
                System.out.println(d + " " + v.getValue() + " " + v1.getValue()); 
            } 
        } 
 
        Initializer.dispose(); 
    } 
 
 
}