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
 
package ru.algorithmist.jquant.indicators; 
 
import org.joda.time.DateTime; 
import org.joda.time.DateTimeZone; 
import org.junit.Before; 
import org.junit.Test; 
import ru.algorithmist.jquant.connectors.ConnectorProcess; 
import ru.algorithmist.jquant.connectors.TestConnector; 
import ru.algorithmist.jquant.engine.DataService; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
import ru.algorithmist.jquant.engine.Value; 
 
import java.text.ParseException; 
 
import static java.lang.Boolean.TRUE; 
import static org.hamcrest.CoreMatchers.equalTo; 
import static org.hamcrest.Matchers.closeTo; 
import static org.hamcrest.core.Is.is; 
import static org.junit.Assert.assertThat; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/17/11 
 */ 
public class DeTrendingRSIParameterTest { 
 
    @Before 
    public void setUp() throws ParseException { 
        TestConnector testConnector = new TestConnector(); 
        ConnectorProcess cp = ConnectorProcess.getInstance(); 
        cp.register(testConnector); 
    } 
 
    @Test 
    public void testParameter(){ 
        DateTime dt2 = new DateTime(2010, 12, 28, 0, 0, 0, 0, DateTimeZone.UTC); 
        RSIParameter rsi = new RSIParameter(new Security("IBM", "TEST"), 7, TimeInterval.DAY); 
        DeTrendingParameter dtp = new DeTrendingParameter(rsi, 9, 50); 
        Value value = DataService.instance().value(dt2.toInstant(), dtp); 
        assertThat(value.isOK(), is(equalTo(TRUE))); 
        assertThat(value.getValue(), is(closeTo(51.57278534203, 1e-6))); 
    } 
 
    @Test 
    public void testParameterNA(){ 
        DateTime dt1 = new DateTime(2010, 12, 26, 0, 0, 0, 0, DateTimeZone.UTC); 
        RSIParameter rsi = new RSIParameter(new Security("IBM", "TEST"), 7, TimeInterval.DAY); 
        DeTrendingParameter dtp = new DeTrendingParameter(rsi, 9, 50); 
        Value value = DataService.instance().value(dt1.toInstant(), dtp); 
        assertThat(value.isNA(), is(equalTo(TRUE))); 
    } 
}