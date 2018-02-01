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
 
/**
 * @author "Sergey Edunov" 
 * @version 1/18/11 
 */ 
public class LimitedCash implements Cash { 
 
    private double deposit; 
 
    public LimitedCash(double deposit) { 
        this.deposit = deposit; 
    } 
 
    @Override 
    public void change(double amount) { 
        if (deposit + amount < -1e-6){ 
            throw new RuntimeException("No more cash"); 
        } 
        deposit+= amount; 
 
    } 
 
    @Override 
    public double available() { 
        return deposit; 
    } 
 
    @Override 
    public double estimate() { 
        return deposit; 
    } 
}