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
 * @version 12/30/10 
 */ 
public enum ValueStatus{ 
    OK("OK"), 
    TEMPORARY_NOT_AVAILABLE("TNA"), 
    NOT_AVAILABLE("NA"); 
 
    private String key; 
 
    ValueStatus(String key) { 
        this.key = key; 
    } 
 
    @Override 
    public String toString() { 
        return key; 
    } 
 
    public static ValueStatus from(String from){ 
        if ("OK".equals(from)) return OK; 
        if ("TNA".equals(from)) return TEMPORARY_NOT_AVAILABLE; 
        if ("NA".equals(from))  return  NOT_AVAILABLE; 
        throw new RuntimeException("Value status " + from + " is not supported"); 
    } 
}