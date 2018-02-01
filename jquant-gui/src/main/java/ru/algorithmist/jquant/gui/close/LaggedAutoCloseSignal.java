package ru.algorithmist.jquant.gui.close;
 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.gui.strategy.FieldType; 
import ru.algorithmist.jquant.gui.strategy.ParameterField; 
import ru.algorithmist.jquant.signals.AbstractSignal; 
import ru.algorithmist.jquant.signals.ICloseSignal; 
import ru.algorithmist.jquant.signals.ISignal; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/24/11 
 */ 
public class LaggedAutoCloseSignal implements CloseSignal{ 
 
    private ParameterField lag = new ParameterField("lag", FieldType.INTEGER, 1); 
 
    public String toString() { 
        return "Auto"; 
    } 
 
    @Override 
    public ParameterField[] getFields() { 
        return new ParameterField[] {lag}; 
    } 
 
    @Override 
    public ICloseSignal getSignal(Security security, TimeInterval interval) { 
        return new ru.algorithmist.jquant.signals.LaggedAutoCloseSignal(interval, (Integer)lag.getValue()); 
    } 
 
}