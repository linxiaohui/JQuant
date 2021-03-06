/*
 * Copyright (c) 2011, Sergey Edunov. All Rights Reserved. 
 * 
 * This file is part of JQuant library. 
 * 
 * JQuant library is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version. 
 * 
 * JQuant is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU Lesser General Public License for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with JQuant. If not, see <http://www.gnu.org/licenses/>. 
 */
package ru.algorithmist.jquant.connectors; 
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.DataQueryResult; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
 
import java.util.ArrayList; 
import java.util.Date; 
import java.util.List; 
 
/**
 * @author "Sergey Edunov" 
 * @version 12/29/10 
 */ 
public class ConnectorProcess { 
 
    private static ConnectorProcess instance = new ConnectorProcess(); 
    private List<IConnector> connectors = new ArrayList<IConnector>(); 
 
    public static ConnectorProcess getInstance(){ 
        return instance; 
    } 
 
    private ConnectorProcess(){} 
 
    public void update(String name, Security symbol, Instant date, TimeInterval interval){ 
        for(IConnector connector : connectors){ 
            if (connector.canLoad(symbol.getSource(), interval)){ 
                connector.load(name, symbol.getSymbol(), date, interval); 
            } 
        } 
    } 
 
    public void register(IConnector connector){ 
        connectors.add(connector); 
    } 
 
    public DataQueryResult update(String name, Security symbol, Instant from, Instant to, TimeInterval interval) { 
        for(IConnector connector : connectors){ 
            if (connector.canLoad(symbol.getSource(), interval)){ 
                return connector.load(name, symbol.getSymbol(), from, to, interval); 
            } 
        } 
        return new DataQueryResult(); 
    } 
}