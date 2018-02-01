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
 
package ru.algorithmist.jquant.gui; 
 
import org.jfree.chart.ChartFactory; 
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.ChartTheme; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.StandardChartTheme; 
import org.jfree.chart.axis.DateAxis; 
import org.jfree.chart.axis.LogarithmicAxis; 
import org.jfree.chart.axis.NumberAxis; 
import org.jfree.chart.axis.NumberTickUnit; 
import org.jfree.chart.axis.ValueAxis; 
import org.jfree.chart.labels.StandardXYToolTipGenerator; 
import org.jfree.chart.labels.XYToolTipGenerator; 
import org.jfree.chart.plot.XYPlot; 
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer; 
import org.jfree.chart.urls.StandardXYURLGenerator; 
import org.jfree.chart.urls.XYURLGenerator; 
import org.jfree.data.time.Day; 
import org.jfree.data.time.TimeSeries; 
import org.jfree.data.time.TimeSeriesCollection; 
import org.jfree.data.xy.XYDataset; 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.gui.close.CloseSignal; 
import ru.algorithmist.jquant.gui.strategy.StrategyParameters; 
import ru.algorithmist.jquant.strategy.LimitedCash; 
import ru.algorithmist.jquant.strategy.PortfolioState; 
import ru.algorithmist.jquant.strategy.Strategy; 
import ru.algorithmist.jquant.strategy.TransactionsCallback; 
 
import javax.swing.JPanel; 
import java.awt.Color; 
import java.util.Date; 
import java.util.List; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/19/11 
 */ 
public class MainPanel extends JPanel { 
 
    private TimeSeriesCollection onPlot = new TimeSeriesCollection(); 
    private ChartPanel chartPanel; 
 
    public MainPanel() { 
 
    } 
 
    public void addStrategy(StrategyParameters strategy, CloseSignal closeSignal, Instant from, Instant to) { 
        Strategy str = strategy.configure(closeSignal); 
        ChartTheme currentTheme = new StandardChartTheme("JFree"); 
 
        List<PortfolioState> portfolio = str.calculateReturn(from, to, new LimitedCash(1)).getStates(); 
        TimeSeries ts = new TimeSeries(strategy.getName()); 
 
        for (int i = 0; i < portfolio.size(); i++) { 
            PortfolioState ps = portfolio.get(i); 
//            System.out.println(ps); 
            ts.add(new Day(ps.getDate().toDate()), ps.getState()); 
        } 
        onPlot.addSeries(ts); 
 
        JFreeChart hitsPerDayChart = createTimeSeriesChart("", "Date", "", onPlot, false); 
 
        currentTheme.apply(hitsPerDayChart); 
        hitsPerDayChart.setBackgroundPaint(Color.white); 
        if (chartPanel == null) { 
            chartPanel = new ChartPanel(hitsPerDayChart); 
            add(chartPanel); 
        } else { 
            chartPanel.setChart(hitsPerDayChart); 
        } 
    } 
 
    public static JFreeChart createTimeSeriesChart(String title, 
                                                   String timeAxisLabel, 
                                                   String valueAxisLabel, 
                                                   XYDataset dataset, 
                                                   boolean logarithmic) { 
 
        ValueAxis timeAxis = new DateAxis(timeAxisLabel); 
        timeAxis.setLowerMargin(0.02);  // reduce the default margins 
        timeAxis.setUpperMargin(0.02); 
        timeAxis.setAutoRange(true); 
 
        NumberAxis valueAxis = logarithmic? new LogarithmicAxis(valueAxisLabel) : new NumberAxis(valueAxisLabel); 
        valueAxis.setAutoRange(true); 
 
        valueAxis.setAutoRangeIncludesZero(false);  // override default 
        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, null); 
 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false); 
 
        plot.setRenderer(renderer); 
 
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true); 
        return chart; 
 
    } 
 
}