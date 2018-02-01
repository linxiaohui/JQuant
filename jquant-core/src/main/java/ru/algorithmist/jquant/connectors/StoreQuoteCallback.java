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
package ru.algorithmist.jquant.connectors; 
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.*; 
import ru.algorithmist.jquant.quotes.CloseParameter; 
import ru.algorithmist.jquant.quotes.HighParameter; 
import ru.algorithmist.jquant.quotes.LowParameter; 
import ru.algorithmist.jquant.quotes.OpenParameter; 
import ru.algorithmist.jquant.quotes.VolumeParameter; 
 
import java.util.Date; 
 
/**
 * @author "Sergey Edunov" 
 * @version 12/29/10 
 */ 
public class StoreQuoteCallback implements QuoteCallback { 
 
    private DataService dataService = DataService.instance(); 
    private Security security; 
    private double open; 
    private double close; 
    private double high; 
    private double low; 
    private double volume; 
    private Instant date; 
    private TimeInterval interval; 
    private DataQueryResult queryResult; 
    private String queryParameter; 
 
 
    public StoreQuoteCallback(Security security) { 
        this.security = security; 
    } 
 
    public void setQueryResult(DataQueryResult queryResult) { 
        this.queryResult = queryResult; 
    } 
 
    public void setQueryParameter(String queryParameter) { 
        this.queryParameter = queryParameter; 
    } 
 
    @Override 
    public void setOpen(double value) { 
        open = value; 
    } 
 
    @Override 
    public void setClose(double value) { 
        close = value; 
    } 
 
    @Override 
    public void setHigh(double value) { 
        high = value; 
    } 
 
    @Override 
    public void setLow(double value) { 
        low = value; 
    } 
 
    @Override 
    public void setVolume(double value) { 
        volume = value; 
    } 
 
    @Override 
    public void setDate(Instant date) { 
         this.date = date; 
    } 
 
    @Override 
    public void setTimeInterval(TimeInterval interval) { 
        this.interval = interval; 
    } 
 
    @Override 
    public void commit() { 
        store(new CloseParameter(security, interval), close); 
        store(new OpenParameter(security, interval), open); 
        store(new HighParameter(security, interval), high); 
        store(new LowParameter(security, interval), low); 
        store(new VolumeParameter(security, interval), volume); 
    } 
 
    public void store(StockQuoteParameter parameter, double  value){ 
        Value val = Double.isNaN(value) ? Value.NA : new Value(value); 
        dataService.update(date, parameter, val); 
        if (parameter.getName().equals(queryParameter) && queryResult!=null){ 
            queryResult.addResult(val, date); 
        } 
    } 
}