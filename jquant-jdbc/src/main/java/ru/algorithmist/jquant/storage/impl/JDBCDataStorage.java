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
import ru.algorithmist.jquant.engine.ValueStatus; 
import ru.algorithmist.jquant.storage.DataStorageWalker; 
import ru.algorithmist.jquant.storage.IDataStorage; 
import ru.algorithmist.jquant.storage.Key; 
 
import java.sql.SQLException; 
import java.util.Date; 
import java.util.List; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/20/11 
 */ 
public class JDBCDataStorage implements IDataStorage { 
 
    private JDBCDao dao; 
 
    public JDBCDataStorage(String url){ 
        this.dao = new JDBCDao(); 
        try { 
            dao.init(url); 
        } catch (SQLException e) { 
            throw new RuntimeException(e); 
        } 
    } 
 
    @Override 
    public void store(Key key, Instant date, Value value) { 
        ValueDO existing = dao.find(key, date); 
        if (existing==null){ 
            dao.insert(new ValueDO(key, date, value.getStatus().toString(), value.getValue())); 
        } else { 
            dao.update(new ValueDO(key, date, value.getStatus().toString(), value.getValue())); 
        } 
 
    } 
 
    @Override 
    public Value query(Key key, Instant date) { 
        ValueDO dto = dao.find(key, date); 
        if (dto==null){ 
            return Value.TNA; 
        } 
        return createValue(dto.getStatus(), dto.getValue()); 
 
    } 
 
    @Override 
    public DataQueryResult query(Key key, Instant from, Instant to, TimeInterval interval) { 
        List<ValueDO> res = dao.find(key, from, to); 
        DataQueryResult ret = new DataQueryResult(); 
        for(ValueDO vdo : res){ 
            ret.addResult(createValue(vdo.getStatus(), vdo.getValue()), vdo.getDate()); 
        } 
        return ret; 
    } 
 
    private Value createValue(String st, double value) { 
        ValueStatus status = ValueStatus.from(st); 
        if (status == ValueStatus.OK){ 
            return new Value(value); 
        } else if (status == ValueStatus.NOT_AVAILABLE){ 
            return Value.NA; 
        } 
        return Value.TNA; 
    } 
 
    @Override 
    public void iterate(final DataStorageWalker walker) { 
        dao.iterate(new JDBCWalker() { 
            @Override 
            public void accept(String key, Instant date, String status, double value) { 
                walker.accept(Key.parseString(key), date, createValue(status, value)); 
            } 
        }); 
    } 
 
}