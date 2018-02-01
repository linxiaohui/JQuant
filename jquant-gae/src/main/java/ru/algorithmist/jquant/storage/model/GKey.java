package ru.algorithmist.jquant.storage.model;
 
import com.google.appengine.api.datastore.Key; 
 
import javax.jdo.annotations.IdGeneratorStrategy; 
import javax.jdo.annotations.PersistenceCapable; 
import javax.jdo.annotations.Persistent; 
import javax.jdo.annotations.PrimaryKey; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/26/11 
 */ 
@PersistenceCapable 
public class GKey { 
 
    @PrimaryKey 
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY) 
    private Long id; 
 
    @Persistent 
    private String key; 
 
    public GKey(String key) { 
        this.key = key; 
    } 
 
    public Long getId() { 
        return id; 
    } 
 
    public void setId(Long id) { 
        this.id = id; 
    } 
 
    public String getKey() { 
        return key; 
    } 
 
    public void setKey(String key) { 
        this.key = key; 
    } 
}