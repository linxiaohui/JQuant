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
 
package ru.algorithmist.jquant.strategy; 
 
import org.joda.time.DateTime; 
import org.joda.time.DateTimeZone; 
import org.junit.Before; 
import org.junit.Test; 
import ru.algorithmist.jquant.connectors.ConnectorProcess; 
import ru.algorithmist.jquant.connectors.TestConnector; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.signals.*; 
 
import java.text.ParseException; 
import java.util.Date; 
import java.util.List; 
 
import static org.hamcrest.Matchers.closeTo; 
import static org.hamcrest.core.Is.is; 
import static org.junit.Assert.assertThat; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/18/11 
 */ 
public class RSIStrategyTest { 
 
    @Before 
    public void setUp() throws ParseException { 
        TestConnector testConnector = new TestConnector(); 
        ConnectorProcess cp = ConnectorProcess.getInstance(); 
        cp.register(testConnector); 
    } 
 
    @Test 
    public void testRSIStrategy() { 
        DateTime to = new DateTime(2010, 12, 28, 0, 0, 0, 0, DateTimeZone.UTC); 
        DateTime from = new DateTime(2009, 3, 24, 0, 0, 0, 0, DateTimeZone.UTC); 
        AbstractSignal openSignal = new RSISignal(new Security("IBM", "TEST"), 2, TimeInterval.DAY, 10, 90); 
        ICloseSignal closeSignal = new LaggedAutoCloseSignal(TimeInterval.DAY, 1); 
        Strategy strategy = new Strategy(); 
        strategy.addCloseSignal(closeSignal); 
        strategy.addSignal(openSignal); 
 
        List<PortfolioState> portfolio = strategy.calculateReturn(from.toInstant(), to.toInstant(), new LimitedCash(1000)).getStates(); 
        PortfolioState last = portfolio.get(portfolio.size()-1); 
        assertThat(last.getState(), is(closeTo(1278.4981925829, 1e-6))); 
 
    } 
 
}