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
import org.joda.time.DateTimeConstants; 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.*; 
import ru.algorithmist.jquant.indicators.*; 
import ru.algorithmist.jquant.infr.DateUtils; 
import ru.algorithmist.jquant.quotes.*; 
 
import java.io.File; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.util.ArrayList; 
import java.util.List; 
import java.util.Locale; 
 
/**
 * User: Sergey Edunov 
 * Date: 06.03.11 
 */ 
public class ExportForAnalysis { 
 
 
 
    public static void main(String[] args) throws IOException { 
        Locale.setDefault(Locale.ENGLISH); 
        Initializer.initialize(); 
        generate(11); 
        generate(12); 
        generate(13); 
        generate(14); 
        generate(15); 
        generate(16); 
        generate(17); 
        generate(18); 
        Initializer.dispose(); 
       } 
 
    private static void generate(int hour) throws IOException { 
        DateTime date = new DateTime(2011, 3, 5, 0, 0, 0, 0); 
        DateTime from = new DateTime(2009, 7, 14, 0, 0, 0, 0); 
 
        FileWriter fw = new FileWriter(new File("positions"+hour+" .txt")); 
        String security = "SPFB.RTS"; 
        String source = "FINAM"; 
        String[] constituents = new String[] {"RTS.ST.GAZP", "RTS.ST.LKOH", "RTS.ST.SBER", "SPFB.Si"}; 
//        DataService.instance().values(from.minusDays(50).toInstant(), date.toInstant(), new CloseParameter(new Security(security, source), TimeInterval.HOUR)); 
//        DataService.instance().values(from.minusDays(50).toInstant(), date.toInstant(), new CloseParameter(new Security(security, source), TimeInterval.DAY)); 
//        for(String constituent : constituents) { 
//            DataService.instance().values(from.minusDays(50).toInstant(), date.toInstant(), new CloseParameter(new Security(constituent, source), TimeInterval.HOUR)); 
//            DataService.instance().values(from.minusDays(50).toInstant(), date.toInstant(), new CloseParameter(new Security(constituent, source), TimeInterval.DAY)); 
//        } 
        boolean first = true; 
        while(date.isAfter(from)){ 
            if (date.dayOfWeek().get() > DateTimeConstants.MONDAY && 
                    date.dayOfWeek().get() < DateTimeConstants.FRIDAY) { 
                //Don't want to count fridays and mondays they are special 
 
                //Need to exclude national holidays as well 
                Value open = DataService.instance().value(date.plusHours(hour).toInstant(), new OpenParameter(new Security(security,source), TimeInterval.HOUR)); 
                Value high = DataService.instance().value(date.plusHours(hour).toInstant(), new HighParameter(new Security(security,source), TimeInterval.HOUR)); 
                Value low = DataService.instance().value(date.plusHours(hour).toInstant(), new LowParameter(new Security(security,source), TimeInterval.HOUR)); 
                Value close = DataService.instance().value(date.plusHours(hour).toInstant(), new CloseParameter(new Security(security,source), TimeInterval.HOUR)); 
                if (close.isOK()){ 
                    List<Double> parameters = new ArrayList<Double>(); 
                    parameters.add((close.getValue()-open.getValue())/open.getValue()); 
                    parameters.add((high.getValue()-open.getValue())/open.getValue()); 
                    parameters.add((low.getValue()-open.getValue())/open.getValue()); 
 
                    parameters.addAll(basicData(date, hour, new Security(security, source), first)); 
                    for(String constituent : constituents) { 
                        parameters.addAll(basicData(date, hour, new Security(constituent, source), first)); 
                    } 
                    boolean skip = false; 
                    for(double p : parameters){ 
                        if (Double.isNaN(p))  skip = true; 
                    } 
                    if (!skip){ 
                        if (first){ 
                            fw.write("ID\tClose\tHigh\tLow\t"); 
                            for(String name : COLUMN_NAMES){ 
                                fw.write(name + "\t"); 
                            } 
                            first = false; 
                            fw.write("\n"); 
                        } 
                        fw.write(date.year().get() + "_" + date.monthOfYear().get() + "_" + date.dayOfMonth().get() + "\t"); 
                        for(double p : parameters){ 
                            fw.write(String.format("%10.4f\t", p)); 
                        } 
                        fw.write("\n"); 
                        fw.flush(); 
                    } 
                } 
 
            } 
            date = date.minusDays(1); 
        } 
        fw.close(); 
        COLUMN_NAMES.clear(); 
    } 
 
    private static List<String> COLUMN_NAMES = new ArrayList<String>(); 
 
    private static List<Double> basicData(DateTime date, int hour, Security security, boolean names) { 
        List<Double> result = new ArrayList<Double>(); 
 
        // 圾折快把忘扮扶快快 我戒技快扶快扶我快 志 扭把抉扯快扶找忘抒, 
        Value v = DataService.instance().prevValue(date.toInstant(), 
                new ROCParameter(new CloseParameter(security, TimeInterval.DAY), 0)); 
        result.add(v.getValue()); 
        if (names) COLUMN_NAMES.add("Yesterday_change" + security.getSymbol()); 
 
        Value closeYesterday = DataService.instance().prevValue(date.toInstant(), 
                new CloseParameter(security, TimeInterval.DAY)); 
 
        Value openToday = DataService.instance().value(date.plusHours(10).toInstant(), 
                new OpenParameter(security, TimeInterval.HOUR)); 
 
        // 忍改扭 扶忘 抉找抗把抑找我我 
        result.add((closeYesterday.getValue() - openToday.getValue())/closeYesterday.getValue()); 
        if (names) COLUMN_NAMES.add("Open_gap" + security.getSymbol()); 
 
        Value volumeYesterday = DataService.instance().prevValue(date.toInstant(), 
                new VolumeParameter(security, TimeInterval.DAY)); 
 
        //high, low, close, volume 扭把快忱抑忱批投我抒 折忘扼抉志 
        for(int i=10; i< hour; i++){ 
            Instant dt = date.plusHours(i).toInstant(); 
            Value open = DataService.instance().value(dt, new OpenParameter(security, TimeInterval.HOUR)); 
            Value high = DataService.instance().value(dt, new HighParameter(security, TimeInterval.HOUR)); 
            Value low = DataService.instance().value(dt, new LowParameter(security, TimeInterval.HOUR)); 
            Value volume = DataService.instance().value(dt, new VolumeParameter(security, TimeInterval.HOUR)); 
            Value close = DataService.instance().value(dt, new CloseParameter(security, TimeInterval.HOUR)); 
            result.add((high.getValue()-open.getValue())/open.getValue()); 
            if (names) COLUMN_NAMES.add("High" + i + security.getSymbol()); 
            result.add((low.getValue()-open.getValue())/open.getValue()); 
            if (names) COLUMN_NAMES.add("Low" + i + security.getSymbol()); 
            result.add((close.getValue()-open.getValue())/open.getValue()); 
            if (names) COLUMN_NAMES.add("Close" + i + security.getSymbol()); 
            result.add((volume.getValue())/volumeYesterday.getValue()); 
            if (names) COLUMN_NAMES.add("Volume" + i + security.getSymbol()); 
            result.add(DataService.instance().prevValue(dt, new RSIParameter(security, 2, TimeInterval.HOUR)).getValue()); 
            if (names) COLUMN_NAMES.add("RSI2_" + i + "_" + security.getSymbol()); 
            result.add(DataService.instance().prevValue(dt, new RSIParameter(security, 7, TimeInterval.HOUR)).getValue()); 
            if (names) COLUMN_NAMES.add("RSI7" + i + "_" + security.getSymbol()); 
            result.add(DataService.instance().prevValue(dt, new RSIParameter(security, 20, TimeInterval.HOUR)).getValue()); 
            if (names) COLUMN_NAMES.add("RSI20" + i + "_" + security.getSymbol()); 
        } 
 
 
        return result; 
    } 
}