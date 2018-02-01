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
package ru.algorithmist.jquant.engine; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/20/11 
 */ 
public class Security { 
 
    private String symbol; 
    private String source; 
 
    public Security(String symbol, String source) { 
        this.symbol = symbol; 
        this.source = source; 
    } 
 
    public String getSymbol() { 
        return symbol; 
    } 
 
    public String getSource() { 
        return source; 
    } 
 
    public String toString(){ 
        return symbol + "(" + source + ")"; 
    } 
 
    @Override 
    public boolean equals(Object o) { 
        if (this == o) return true; 
        if (o == null || getClass() != o.getClass()) return false; 
 
        Security security = (Security) o; 
        return symbol.equals(security.symbol) && source.equals(security.source); 
    } 
 
    @Override 
    public int hashCode() { 
        return 31 * symbol.hashCode() + source.hashCode(); 
    } 
}