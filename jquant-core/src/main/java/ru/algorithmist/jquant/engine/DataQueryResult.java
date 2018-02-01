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
 
import org.joda.time.Instant; 
 
import java.util.*; 
 
/**
 * User: Sergey Edunov 
 * Date: 24.02.11 
 */ 
public class DataQueryResult implements Iterable<DataQueryObject>{ 
 
    private List<DataQueryObject> result = new ArrayList<DataQueryObject>(); 
 
    public void addResult(Value value, Instant time){ 
        result.add(new DataQueryObject(value, time)); 
    } 
 
    @Override 
    public Iterator<DataQueryObject> iterator() { 
        return result.iterator(); 
    } 
 
    public Value query(Instant date){ 
        int a = 0; 
        int b = result.size()-1; 
        if (date.isBefore(result.get(a).getDate()) || date.isAfter(result.get(b).getDate())) return Value.TNA; 
        while(a<b){ 
            int c = (a+b)/2; 
            if (date.isBefore(c)){ 
                b = c; 
            }else if (date.isAfter(c)){ 
                a = c; 
            }else{ 
                return result.get(c).getValue(); 
            } 
        } 
        return Value.NA; 
    } 
 
    public int size(){ 
        return result.size(); 
    } 
 
    public DataQueryObject get(int i){ 
        return result.get(i); 
    } 
 
    public void merge(DataQueryResult other){ 
        result.addAll(other.result); 
        Collections.sort(result); 
    } 
}