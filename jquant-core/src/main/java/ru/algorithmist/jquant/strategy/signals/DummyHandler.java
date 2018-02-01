package ru.algorithmist.jquant.strategy.signals;
 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.signals.SignalData; 
import ru.algorithmist.jquant.strategy.Cash; 
import ru.algorithmist.jquant.strategy.OpenPositions; 
import ru.algorithmist.jquant.strategy.TransactionsCallback; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/21/11 
 */ 
public class DummyHandler implements SignalHandler{ 
    @Override 
    public void handleSignal(SignalData sd, OpenPositions openPositions, Cash cash,  TransactionsCallback callback) { 
 
    } 
}