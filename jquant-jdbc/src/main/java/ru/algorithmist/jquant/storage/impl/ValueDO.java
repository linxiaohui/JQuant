package ru.algorithmist.jquant.storage.impl;
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.storage.Key; 
 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/20/11 
 */ 
public class ValueDO { 
 
    private Key key; 
    private Instant date; 
    private String status; 
    private double value; 
 
    public ValueDO(Key key, Instant date, String status, double value) { 
        this.key = key; 
        this.status = status; 
        this.value = value; 
        this.date = date; 
    } 
 
    public Instant getDate() { 
        return date; 
    } 
 
    public void setDate(Instant date) { 
        this.date = date; 
    } 
 
    public Key getKey() { 
        return key; 
    } 
 
    public void setKey(Key key) { 
        this.key = key; 
    } 
 
    public String getStatus() { 
        return status; 
    } 
 
    public void setStatus(String status) { 
        this.status = status; 
    } 
 
    public double getValue() { 
        return value; 
    } 
 
    public void setValue(double value) { 
        this.value = value; 
    } 
}