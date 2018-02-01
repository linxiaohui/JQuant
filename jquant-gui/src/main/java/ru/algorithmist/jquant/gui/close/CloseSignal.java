package ru.algorithmist.jquant.gui.close;
 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.gui.strategy.ParameterField; 
import ru.algorithmist.jquant.signals.ICloseSignal; 
import ru.algorithmist.jquant.signals.ISignal; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/24/11 
 */ 
public interface CloseSignal { 
 
    public ParameterField[] getFields(); 
 
    public ICloseSignal getSignal(Security security, TimeInterval interval); 
}