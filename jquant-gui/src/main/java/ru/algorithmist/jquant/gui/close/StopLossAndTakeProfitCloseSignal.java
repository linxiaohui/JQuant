package ru.algorithmist.jquant.gui.close;
 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.gui.strategy.FieldType; 
import ru.algorithmist.jquant.gui.strategy.ParameterField; 
import ru.algorithmist.jquant.signals.ICloseSignal; 
import ru.algorithmist.jquant.signals.ISignal; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/24/11 
 */ 
public class StopLossAndTakeProfitCloseSignal implements CloseSignal{ 
 
    private ParameterField stopLoss = new ParameterField("stop loss", FieldType.DECIMAL, 0.01); 
    private ParameterField takeProfit = new ParameterField("take profit", FieldType.DECIMAL, 0.05); 
 
    @Override 
    public String toString() { 
        return "Stop loss & Take Profit"; 
    } 
 
    @Override 
    public ParameterField[] getFields() { 
        return new ParameterField[] {stopLoss, takeProfit}; 
    } 
 
    @Override 
    public ICloseSignal getSignal(Security security, TimeInterval interval) { 
        return new ru.algorithmist.jquant.signals.StopLossAndTakeProfitCloseSignal((Double)stopLoss.getValue(), (Double)takeProfit.getValue(), interval, security); 
    } 
 
}
