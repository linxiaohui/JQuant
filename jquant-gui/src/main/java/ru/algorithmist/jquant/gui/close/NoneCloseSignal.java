package ru.algorithmist.jquant.gui.close;
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.gui.strategy.ParameterField; 
import ru.algorithmist.jquant.signals.ICloseSignal; 
import ru.algorithmist.jquant.signals.ISignal; 
import ru.algorithmist.jquant.signals.SignalData; 
import ru.algorithmist.jquant.strategy.Cash; 
import ru.algorithmist.jquant.strategy.OpenPositions; 
 
import java.util.ArrayList; 
import java.util.List; 
 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/24/11 
 */ 
public class NoneCloseSignal implements CloseSignal{ 
 
 
    @Override 
    public String toString() { 
        return "None"; 
    } 
 
    @Override 
    public ParameterField[] getFields() { 
        return new ParameterField[] {}; 
    } 
 
    @Override 
    public ICloseSignal getSignal(Security security, TimeInterval interval) { 
        return new ICloseSignal() { 
            @Override 
            public List<SignalData> test(Instant date, OpenPositions openPositions, Cash cash) { 
                return new ArrayList<SignalData>(); 
            } 
 
            @Override 
            public TimeInterval timeInterval() { 
                return TimeInterval.DAY; 
            } 
        }; 
    } 
 
 
}