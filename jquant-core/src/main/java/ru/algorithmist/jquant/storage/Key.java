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
package ru.algorithmist.jquant.storage; 
 
import java.util.Arrays; 
import java.util.regex.Pattern; 
 
/**
 * @author "Sergey Edunov" 
 * @version 12/29/10 
 */ 
public final class Key { 
 
    private static final Pattern QUOTE = Pattern.compile("\'"); 
    private String[] data; 
    private long id; 
 
    public static Key from(String... args){ 
        Key k = new Key(args); 
        return k; 
    } 
 
    public static Key from(Key other, String... args){ 
        Key k = new Key(args); 
        k.addKey(other); 
        return k; 
    } 
 
    public static Key from(Key o1, Key o2, String... args){ 
        Key k = new Key(args); 
        k.addKey(o1); 
        k.addKey(o2); 
        return k; 
    } 
 
    private void addKey(Key other){ 
        String[] newdata = new String[data.length + other.data.length]; 
        System.arraycopy(data, 0, newdata, 0, data.length); 
        System.arraycopy(other.data, 0, newdata, data.length, other.data.length); 
        data = newdata; 
    } 
 
    public Key(String[] data) { 
        this.data = data; 
    } 
 
    public Key(String[] data, Key... other) { 
        this.data = data; 
        for(Key k : other){ 
            addKey(k); 
        } 
    } 
 
    public String[] getData() { 
        return data; 
    } 
 
    public long getId() { 
        return id; 
    } 
 
    public void setId(long id) { 
        this.id = id; 
    } 
 
    public String toString(){ 
        StringBuilder res = new StringBuilder(); 
        for(int i=0; i<data.length; i++){ 
            if (i!=0) res.append('\''); 
            res.append(data[i]); 
        } 
        return res.toString(); 
    } 
 
    public static Key parseString(String value){ 
        String[] data = QUOTE.split(value); 
        return from(data); 
    } 
 
    @Override 
    public boolean equals(Object o) { 
        if (this == o) return true; 
        if (o == null || getClass() != o.getClass()) return false; 
 
        return Arrays.equals(data, ((Key) o).data); 
 
    } 
 
    @Override 
    public int hashCode() { 
        return data != null ? Arrays.hashCode(data) : 0; 
    } 
}