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
 
import java.util.ArrayList; 
import java.util.List; 
 
/**
 * User: Sergey Edunov 
 * Date: 30.01.11 
 */ 
public class StrategyResult { 
 
    List<PortfolioState> states = new ArrayList<PortfolioState>(); 
    List<Position> positions = new ArrayList<Position>(); 
 
    public List<PortfolioState> getStates() { 
        return states; 
    } 
 
    public List<Position> getPositions() { 
        return positions; 
    } 
}