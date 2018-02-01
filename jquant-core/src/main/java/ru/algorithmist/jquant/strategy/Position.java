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
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.engine.Security; 
import ru.algorithmist.jquant.engine.TimeInterval; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/18/11 
 */ 
public class Position { 
 
    private Security security; 
    private double quantity; 
    private double openPrice; 
    private double closePrice; 
    private Instant dateOpen; 
    private Instant dateClosed; 
 
    public Position(Security security, double quantity, double openPrice, Instant openDate) { 
        this.security = security; 
        this.quantity = quantity; 
        dateOpen = openDate; 
        this.openPrice = openPrice; 
    } 
 
    public Security getSecurity() { 
        return security; 
    } 
 
    public double getQuantity() { 
        return quantity; 
    } 
 
    public double getOpenPrice() { 
        return openPrice; 
    } 
 
    public Instant getOpenDate() { 
        return dateOpen; 
    } 
 
    public double getClosePrice() { 
        return closePrice; 
    } 
 
    public Instant getDateOpen() { 
        return dateOpen; 
    } 
 
    public Instant getDateClosed() { 
        return dateClosed; 
    } 
 
    public void setClosePrice(double closePrice) { 
        this.closePrice = closePrice; 
    } 
 
    public void setClosedDate(Instant dateClosed) { 
        this.dateClosed = dateClosed; 
    } 
 
    public boolean isClosed(){ 
        return dateClosed!=null; 
    } 
 
    @Override 
    public boolean equals(Object o) { 
        if (this == o) return true; 
        if (o == null || getClass() != o.getClass()) return false; 
 
        Position position = (Position) o; 
 
        if (Double.compare(position.quantity, quantity) != 0) return false; 
        if (security != null ? !security.equals(position.security) : position.security != null) return false; 
 
        return true; 
    } 
 
    @Override 
    public int hashCode() { 
        int result; 
        long temp; 
        result = security != null ? security.hashCode() : 0; 
        temp = quantity != +0.0d ? Double.doubleToLongBits(quantity) : 0L; 
        result = 31 * result + (int) (temp ^ (temp >>> 32)); 
        return result; 
    } 
 
    public String toString(){ 
        return dateOpen + " " + security + " " + quantity + " " + openPrice; 
    } 
}