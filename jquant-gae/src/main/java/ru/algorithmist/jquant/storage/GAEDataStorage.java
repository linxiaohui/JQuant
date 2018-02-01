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
package ru.algorithmist.jquant.storage; 
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.DataQueryResult; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.storage.model.GData; 
import ru.algorithmist.jquant.storage.model.GKey; 
 
import javax.jdo.JDOHelper; 
import javax.jdo.PersistenceManager; 
import javax.jdo.PersistenceManagerFactory; 
import javax.jdo.Query; 
import java.util.Date; 
import java.util.HashMap; 
import java.util.List; 
import java.util.Map; 
import java.util.logging.Level; 
import java.util.logging.Logger; 
 
/**
 * @author "Sergey Edunov" 
 * @version 12/29/10 
 */ 
public class GAEDataStorage implements IDataStorage { 
 
    private static final Logger logger = Logger.getLogger(GAEDataStorage.class.getName()); 
 
    private PersistenceManagerFactory pmf; 
 
    private Map<String, Long> keyMap = new HashMap<String, Long>(); 
    private Map<Long, String> idMap = new HashMap<Long, String>(); 
 
    public GAEDataStorage(PersistenceManagerFactory pmf) { 
        this.pmf = pmf; 
    } 
 
    @Override 
    public void store(Key key, Instant date, Value value) { 
        PersistenceManager pm = null; 
        try { 
            pm = pmf.getPersistenceManager(); 
            List<GData> res = (List<GData>) lookup(pm).execute(key(key), date); 
            if (res == null || res.isEmpty()) { 
                pm.makePersistent(new GData(key, date.toDate(), value)); 
            } else { 
                if (res.size() > 1) { 
                    logger.log(Level.WARNING, "Query returned more than one instance " + key + ' ' + date); 
                } 
                res.get(0).setValue(value); 
            } 
        } finally { 
            if (pm != null) { 
                pm.close(); 
            } 
        } 
    } 
 
    @Override 
    public Value query(Key key, Instant date) { 
        PersistenceManager pm = null; 
        try { 
            pm = pmf.getPersistenceManager(); 
            List<GData> res = (List<GData>) lookup(pm).execute(key(key), date); 
            if (res == null || res.isEmpty()) { 
                return Value.TNA; 
            } 
            if (res.size() > 1) { 
                logger.log(Level.WARNING, "Query returned more than one instance " + key + ' ' + date); 
            } 
            return res.get(0).getValue(); 
        } finally { 
            if (pm != null) { 
                pm.close(); 
            } 
        } 
 
    } 
 
    @Override 
    public DataQueryResult query(Key key, Instant from, Instant to, TimeInterval interval) { 
        PersistenceManager pm = null; 
        try { 
            pm = pmf.getPersistenceManager(); 
            List<GData> res = (List<GData>) lookupRange(pm).execute(key(key), from, to); 
            DataQueryResult ret = new DataQueryResult(); 
            for(GData gd : res){ 
                ret.addResult(gd.getValue(), new Instant(gd.getDate())); 
            } 
            return ret; 
        } finally { 
            if (pm != null) { 
                pm.close(); 
            } 
        } 
    } 
 
    @Override 
    public void iterate(DataStorageWalker walker) { 
        PersistenceManager pm = null; 
        try { 
            pm = pmf.getPersistenceManager(); 
            List<GData> res = (List<GData>) pm.newQuery(GData.class).execute(); 
            for(GData data : res){ 
                walker.accept(from(data.getKey()), new Instant(data.getDate()), data.getValue()); 
            } 
        } finally { 
            if (pm != null) { 
                pm.close(); 
            } 
        } 
    } 
 
    private Key from(long id){ 
        String ks = idMap.get(id); 
        if (ks!=null){ 
            Key ret = Key.parseString(ks); 
            ret.setId(id); 
            return ret; 
        } 
        PersistenceManager pm = null; 
        try { 
            pm = pmf.getPersistenceManager(); 
            List<GKey> res = (List<GKey>) keyById(pm).execute(id); 
            GKey gkey = res.get(0); 
            Key key = Key.parseString(gkey.getKey()); 
            key.setId(id); 
            idMap.put(id, gkey.getKey()); 
            keyMap.put(gkey.getKey(), id); 
            return key; 
        } finally { 
            if (pm != null) { 
                pm.close(); 
            } 
        } 
    } 
 
    private long key(Key key){ 
        if (key.getId() > 0){ 
            return key.getId(); 
        } 
        String ks = key.toString(); 
        Long ret = keyMap.get(ks); 
        if (ret!=null){ 
            key.setId(ret); 
            return ret; 
        } 
        PersistenceManager pm = null; 
        try { 
            pm = pmf.getPersistenceManager(); 
            List<GKey> res = (List<GKey>) keyLookup(pm).execute(ks); 
            if (res == null || res.isEmpty()) { 
                return createKey(key); 
            } 
            if (res.size() > 1) { 
                logger.log(Level.WARNING, "Query returned more than one instance " + key); 
            } 
            long id = res.get(0).getId(); 
            key.setId(id); 
            keyMap.put(ks, id); 
            idMap.put(id, ks); 
            return id; 
        } finally { 
            if (pm != null) { 
                pm.close(); 
            } 
        } 
    } 
 
    private long createKey(Key key) { 
        GKey gkey = new GKey(key.toString()); 
        PersistenceManager pm = null; 
        try { 
            pm = pmf.getPersistenceManager(); 
            pm.makePersistent(gkey); 
            key.setId(gkey.getId()); 
            return gkey.getId(); 
        } finally { 
            if (pm != null) { 
                pm.close(); 
            } 
        } 
    } 
 
 
    private Query keyById(PersistenceManager manager) { 
        Query lookup = manager.newQuery(GKey.class); 
        lookup.setFilter("id == keyParam"); 
        lookup.declareParameters("String keyParam"); 
        return lookup; 
    } 
 
 
    private Query keyLookup(PersistenceManager manager) { 
        Query lookup = manager.newQuery(GKey.class); 
        lookup.setFilter("key == keyParam"); 
        lookup.declareParameters("String keyParam"); 
        return lookup; 
    } 
 
    private Query lookup(PersistenceManager manager) { 
        Query lookup = manager.newQuery(GData.class); 
        lookup.setFilter("key == keyParam && date == dateParam"); 
        lookup.declareImports("import java.util.Date"); 
        lookup.declareParameters("long keyParam, Date dateParam"); 
        return lookup; 
    } 
 
    private Query lookupRange(PersistenceManager manager) { 
        Query lookup = manager.newQuery(GData.class); 
        lookup.setFilter("key == keyParam && date >= dateFrom && date <= dateTo"); 
        lookup.declareImports("import java.util.Date"); 
        lookup.declareParameters("long keyParam, Date dateFrom, Date dateTo"); 
        lookup.setOrdering("date"); 
        return lookup; 
    } 
 
}