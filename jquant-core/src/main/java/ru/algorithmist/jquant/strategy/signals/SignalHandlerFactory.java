package ru.algorithmist.jquant.strategy.signals;
 
import ru.algorithmist.jquant.signals.BuySellSignalData; 
import ru.algorithmist.jquant.signals.SignalData; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/21/11 
 */ 
public class SignalHandlerFactory { 
 
    private SimpleBuySellHandler simpleBuySellHandler = new SimpleBuySellHandler(); 
    private DummyHandler dummyHandler = new DummyHandler(); 
 
    public SignalHandler getHandler(SignalData signal){ 
        if (signal instanceof BuySellSignalData){ 
            return  simpleBuySellHandler; 
        } 
        return dummyHandler; 
    } 
}