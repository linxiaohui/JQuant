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
 
import ru.algorithmist.jquant.storage.Key; 
 
/**
 * User: Sergey Edunov 
 * Date: 23.01.11 
 */ 
public abstract class AbstractParameter implements IParameter{ 
 
    private Key key; 
 
    @Override 
    public final Key getQueryKey() { 
        if (key==null){ 
            key = createQueryKey(); 
        } 
        return key; 
    } 
 
    protected abstract Key createQueryKey(); 
 
    @Override 
    public boolean equals(Object o) { 
        if (this == o) return true; 
        if (o == null || getClass() != o.getClass()) return false; 
 
        AbstractParameter that = (AbstractParameter) o; 
        return getQueryKey().equals(that.getQueryKey()); 
 
    } 
 
    @Override 
    public int hashCode() { 
        return getQueryKey().hashCode(); 
    } 
}