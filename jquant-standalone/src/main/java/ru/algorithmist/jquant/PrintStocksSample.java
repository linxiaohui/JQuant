
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
import ru.algorithmist.jquant.indicators.EMAParameter; 
import ru.algorithmist.jquant.quotes.CloseParameter; 
 
import java.io.IOException; 
 
/**
 * @author "Sergey Edunov" 
 * @version 12/29/10 
 */ 
public class PrintStocksSample { 
 
    public static void main(String[] args) throws IOException { 
        Initializer.initialize(); 
        Instant date = new DateTime(2010, 12, 28, 12, 0, 0, 0).toInstant(); 
        IParameter close = new CloseParameter(new Security("SBER", "FINAM"), TimeInterval.HOUR); 
        System.out.println(DataService.instance().value(date, close)); 
        IParameter ema = new EMAParameter(close, 10); 
        System.out.println(DataService.instance().value(date, ema)); 
 
        Initializer.dispose(); 
    } 
}