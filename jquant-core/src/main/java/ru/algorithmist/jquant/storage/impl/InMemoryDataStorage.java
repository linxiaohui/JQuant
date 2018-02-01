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
package ru.algorithmist.jquant.storage.impl; 
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.DataQueryResult; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.infr.DateUtils; 
import ru.algorithmist.jquant.storage.DataStorageWalker; 
import ru.algorithmist.jquant.storage.IDataStorage; 
import ru.algorithmist.jquant.storage.Key; 
 
import java.util.HashMap; 
import java.util.Map; 
 
/**
 * @author "Sergey Edunov" 
 * @version 12/29/10 
 */ 
public class InMemoryDataStorage implements IDataStorage { 
 
    private Map<Key, Map<Instant, Value>> storage = new HashMap<Key, Map<Instant, Value>>(); 
 
    @Override 
    public void store(Key key, Instant date, Value value) { 
        Map<Instant, Value> map = storage.get(key); 
        if (map == null){ 
            map = new HashMap<Instant, Value>(); 
            storage.put(key, map); 
        } 
        map.put(date, value); 
    } 
 
    @Override 
    public Value query(Key key, Instant date) { 
        Map<Instant, Value> map = storage.get(key); 
        if (map != null){ 
            Value value = map.get(date); 
            if (value != null){ 
                return value; 
            } 
        } 
        return Value.TNA; 
    } 
 
    @Override 
    public DataQueryResult query(Key key, Instant from, Instant to, TimeInterval interval) { 
        DataQueryResult result = new DataQueryResult(); 
        while(from.isBefore(to)){ 
            Value v = query(key, from); 
            if (!v.isTNA()){ 
                result.addResult(v, from); 
            } 
            from = DateUtils.shift(from, interval, 1); 
        } 
        return result; 
    } 
 
    @Override 
    public void iterate(DataStorageWalker walker) { 
        for(Map.Entry<Key, Map<Instant, Value>> entry : storage.entrySet()){ 
            Key key = entry.getKey(); 
            Map<Instant, Value> map = entry.getValue(); 
            for(Map.Entry<Instant, Value> dd : map.entrySet()){ 
                walker.accept(key, dd.getKey(), dd.getValue()); 
            } 
        } 
    } 
}