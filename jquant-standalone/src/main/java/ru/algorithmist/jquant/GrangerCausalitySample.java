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
import ru.algorithmist.jquant.indicators.ADXParameter; 
import ru.algorithmist.jquant.indicators.EMAParameter; 
import ru.algorithmist.jquant.indicators.ROCParameter; 
import ru.algorithmist.jquant.indicators.RSIParameter; 
import ru.algorithmist.jquant.infr.DateUtils; 
import ru.algorithmist.jquant.math.GrangerTest; 
import ru.algorithmist.jquant.math.GrangerTestResult; 
import ru.algorithmist.jquant.quotes.CloseParameter; 
 
import java.io.IOException; 
import java.util.ArrayList; 
import java.util.Date; 
import java.util.LinkedList; 
import java.util.List; 
 
/**
 * User: Sergey Edunov 
 * Date: 06.01.11 
 */ 
public class GrangerCausalitySample { 
 
 
    public static void main(String[] args) throws IOException { 
        Initializer.initialize(); 
 
        IParameter close = new ROCParameter(new CloseParameter(new Security("GAZP", "FINAM"), TimeInterval.DAY), 1); 
        IParameter rsi = new RSIParameter(new Security("GAZP", "FINAM"), 2, TimeInterval.DAY); 
        IParameter adx = new ADXParameter(new Security("GAZP", "FINAM"), TimeInterval.DAY, 7); 
 
        for(int l = 1; l<10; l++) { 
            GrangerTestResult res = GrangerTest.granger(tod(values(close)), tod(values(adx)), l); 
            System.out.println(l+ ":\t" + res.getFStat() + "\t" + res.getPValue() + "\t" + res.getR2()); 
        } 
        Initializer.dispose(); 
    } 
 
    private static List<Double> values(IParameter param){ 
        List<Double> res = new LinkedList<Double>(); 
        Instant to = new DateTime(2010, 12, 30, 0, 0, 0, 0).toInstant(); 
        Instant from = new DateTime(2008, 12, 30, 0, 0, 0, 0).toInstant(); 
        while(from.isBefore(to)){ 
 
            Value v = DataService.instance().value(from, param); 
            if (v.isOK()){ 
                res.add(v.getValue()); 
            } 
            from = DateUtils.tomorrow(from); 
        } 
        return res; 
    } 
 
 
 
    public static double[] tod(List<Double> data){ 
        double[] res = new double[data.size()]; 
        for(int i=0; i<res.length; i++){ 
            res[i] = data.get(i); 
        } 
        return res; 
    } 
 
    public static List<Double> delta(List<Double> in){ 
        List<Double> res = new ArrayList<Double>(); 
        for(int i=1; i<in.size(); i++){ 
            res.add(in.get(i)-in.get(i-1)); 
        } 
        return res; 
    } 
 
}