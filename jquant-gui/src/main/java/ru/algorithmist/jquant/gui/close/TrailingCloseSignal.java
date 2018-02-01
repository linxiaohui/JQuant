package ru.algorithmist.jquant.gui.close;
 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.gui.strategy.FieldType; 
import ru.algorithmist.jquant.gui.strategy.ParameterField; 
import ru.algorithmist.jquant.signals.ICloseSignal; 
import ru.algorithmist.jquant.signals.TrailingStopCloseSignal; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/24/11 
 */ 
public class TrailingCloseSignal implements CloseSignal{ 
 
    private ParameterField stopLoss = new ParameterField("stop loss", FieldType.DECIMAL, 0.01); 
 
    @Override 
    public String toString() { 
        return "Trailing"; 
    } 
 
    @Override 
    public ParameterField[] getFields() { 
        return new ParameterField[] {stopLoss}; 
    } 
 
    @Override 
    public ICloseSignal getSignal(Security security, TimeInterval interval) { 
         return new TrailingStopCloseSignal((Double) stopLoss.getValue(), interval, security); 
    } 
 
}