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
package ru.algorithmist.jquant.engine; 
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.infr.DateUtils; 
import ru.algorithmist.jquant.storage.IDataStorage; 
import ru.algorithmist.jquant.storage.Key; 
import ru.algorithmist.jquant.storage.StorageFactory; 
 
import java.util.Date; 
 
/**
 * @author "Sergey Edunov" 
 * @version 12/29/10 
 */ 
public class DataService { 
 
    private IDataStorage storage = StorageFactory.getInstance().getDataStorage(); 
    private static final DataService instance = new DataService(); 
 
    public static DataService instance() { 
        return instance; 
    } 
 
    public DataQueryResult values(Instant from, Instant to, IParameter parameter) { 
        DataQueryResult storedValues = storage.query(parameter.getQueryKey(), from, to, parameter.getTimeInterval()); 
        int i = 0; 
        for (; i < storedValues.size(); i++) { 
            if (!from.equals(storedValues.get(i).getDate())) { 
                break; 
            } 
            from = DateUtils.shift(from, parameter.getTimeInterval(), 1); 
        } 
        int j = storedValues.size() - 1; 
        for (; j > i; j--) { 
            if (!to.equals(storedValues.get(j).getDate())) { 
                break; 
            } 
            to = DateUtils.shift(to, parameter.getTimeInterval(), -1); 
        } 
        if (j == i) { //Happy you are, 100% hit 
            return storedValues; 
        } 
        DataQueryResult other = parameter.getUpdater().update(from,  to); 
        storedValues.merge(other); 
        return storedValues; 
    } 
 
    public Value value(Instant date, IParameter parameter) { 
        return value(date, parameter, 0); 
    } 
 
    public Value value(Instant date, IParameter parameter, int shift) { 
        date = DateUtils.shift(date, parameter.getTimeInterval(), shift); 
        Value value = storage.query(parameter.getQueryKey(), date); 
        if (value.isNA() || value.isOK()) { 
            return value; 
        } 
        value = parameter.getUpdater().update(date); 
        if (value.isTNA()) { 
            value = storage.query(parameter.getQueryKey(), date); 
        } 
        return value; 
    } 
 
    /**
     * Returns value _only_ if it has been calculated already. 
     */ 
    public Value lookup(Instant date, IParameter parameter) { 
        return lookup(date, parameter, 0); 
    } 
 
    /**
     * Returns value _only_ if it has been calculated already. 
     */ 
    public Value lookup(Instant date, IParameter parameter, int shift) { 
        date = DateUtils.shift(date, parameter.getTimeInterval(), shift); 
        Value value = storage.query(parameter.getQueryKey(), date); 
        if (value.isNA() || value.isOK()) { 
            return value; 
        } 
        return Value.TNA; 
    } 
 
 
    public Value prevValue(Instant date, IParameter param) { 
        return prevValue(date, param, 0); 
    } 
 
    public Value prevValue(Instant date, IParameter param, int shift) { 
        shift--; 
        Value closePrev = DataService.instance().value(date, param, shift); 
        while (closePrev.isNA()) { 
            shift--; 
            closePrev = DataService.instance().value(date, param, shift); 
        } 
        return closePrev; 
    } 
 
    public void update(Instant date, IParameter parameter, Value value) { 
        storage.store(parameter.getQueryKey(), date, value); 
    } 
}