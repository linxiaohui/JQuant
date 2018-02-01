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
package ru.algorithmist.jquant.signals; 
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
 
import java.util.Date; 
 
/**
 * Author: Sergey Edunov 
 */ 
public class BuySellSignalData extends SignalData { 
 
    private BuySellStatus status; 
 
    private Security security; 
 
    public BuySellSignalData(Instant signalDate, TimeInterval interval, Security security, BuySellStatus status) { 
        super(signalDate, interval); 
        this.status = status; 
        this.security = security; 
    } 
 
    public BuySellStatus getStatus() { 
        return status; 
    } 
 
    public Security getSecurity() { 
        return security; 
    } 
}