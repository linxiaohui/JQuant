package ru.algorithmist.jquant.storage.impl;
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.DataQueryResult; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.Value; 
import ru.algorithmist.jquant.storage.DataStorageWalker; 
import ru.algorithmist.jquant.storage.IDataStorage; 
import ru.algorithmist.jquant.storage.Key; 
 
import java.util.*; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/21/11 
 */ 
public class TransientMemoryCache implements IDataStorage { 
 
    private IDataStorage base; 
    private Map<Long,WeakHashMap<Instant, Value>> cache = new HashMap<Long, WeakHashMap<Instant, Value>>(); 
    public static final boolean DEBUG = true; 
    private int hits; 
    private int puts; 
    private int misses; 
 
    public TransientMemoryCache(IDataStorage base){ 
         this.base = base; 
    } 
 
    @Override 
    public void store(Key key, Instant date, Value value) { 
        base.store(key, date, value); 
        long id = key.getId(); 
        if (id==0) throw new RuntimeException("Id is 0 " + key + " " + date); 
        WeakHashMap<Instant, Value> subcache = cache.get(id); 
        if (subcache==null){ 
            subcache = new WeakHashMap<Instant, Value>(); 
            cache.put(id, subcache); 
        } 
        if(DEBUG) puts++; 
        subcache.put(date, value); 
    } 
 
    @Override 
    public Value query(Key key, Instant date) { 
        long id = key.getId(); 
        if (DEBUG && (hits+misses)%1000 == 0){ 
            int cacheSize = 0; 
            for(WeakHashMap<Instant, Value> subCache : cache.values()){ 
                cacheSize+=subCache.size(); 
            } 
            System.out.println("Cache stats hits " + hits + " misses " + misses + " cache size " + cacheSize + " puts " +puts); 
        } 
        if (id > 0){ 
            WeakHashMap<Instant, Value> subcache = cache.get(id); 
            Value value = null; 
            if (subcache==null){ 
                subcache = new WeakHashMap<Instant, Value>(); 
                cache.put(id, subcache); 
            }else{ 
                value = subcache.get(date); 
            } 
            if (value == null){ 
                if (DEBUG) misses++; 
                value = base.query(key, date); 
                if (value!=null && !value.isTNA()) { 
                    if(DEBUG) puts++; 
                    subcache.put(date, value); 
                } 
            }else if(DEBUG){ 
                hits++; 
            } 
            return value; 
        } 
        if (DEBUG) misses++; 
        Value value = base.query(key, date); 
        id = key.getId(); 
        if (value!=null && id>0 && !value.isTNA()){ 
            WeakHashMap<Instant, Value> subcache = cache.get(id); 
            if (subcache==null){ 
                subcache = new WeakHashMap<Instant, Value>(); 
                cache.put(id, subcache); 
            } 
            if(DEBUG) puts++; 
            subcache.put(date, value); 
        } 
        return value; 
    } 
 
    @Override 
    public DataQueryResult query(Key key, Instant from, Instant to, TimeInterval interval) { 
        return base.query(key, from, to, interval); 
    } 
 
    @Override 
    public void iterate(DataStorageWalker walker) { 
        base.iterate(walker); 
    } 
}