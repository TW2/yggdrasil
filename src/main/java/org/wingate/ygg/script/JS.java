/*
 * Copyright (C) 2024 util2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wingate.ygg.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graalvm.polyglot.*;

/**
 *
 * @author util2
 */
public class JS {
    
    private int lastValue;
    
    private JS(){
        lastValue = 0;
    }
    
    public static Value executeString(String script, String func, Object... args){
        JS js = new JS();
        return js.exec(script, func, args);
    }
    
    public static Value executeFile(String scriptPath, String func, Object... args){
        return executeFile(new File(scriptPath), func, args);
    }
    
    public static Value executeFile(File file, String func, Object... args){
        JS js = new JS();
        return js.exec(file, func, args);
    }
    
    private int getLastIndex(String line){
        int index = 0;
        for(char c : line.toCharArray()){            
            if(Character.toString(c).equals("{")){
                lastValue++;
            }
            if(Character.toString(c).equals("}")){
                lastValue--;
                if(lastValue == 0){
                    return index;
                }                
            }
            index++;
        }
        return -1;
    }
    
    private Value exec(String script, String func, Object... args){
        StringBuilder sb = new StringBuilder();
        
        try(StringReader sr = new StringReader(script);
                BufferedReader br = new BufferedReader(sr);){            
            String line;
            sb.append("(");
            boolean found = false;
            while((line = br.readLine()) != null){
                if(line.contains("function " + func)){
                    found = true;
                }
                if(found == true){
                    int index = getLastIndex(line);
                    if(index != -1){
                        sb.append(line.substring(0, index + 1));                    
                        break;
                    }else{
                        sb.append(line.trim());
                    }
                }                
            }
            sb.append(")");
        } catch (IOException ex) {
            Logger.getLogger(JS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(sb.length() <= 2) return null;
        
        try(Context context = Context.create()){
            Value value = context.eval("js", sb.toString());
            return value.execute(args);
        }
    }
    
    private Value exec(File file, String func, Object... args){
        StringBuilder sb = new StringBuilder();
        
        try(FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);){            
            String line;
            sb.append("(");
            boolean found = false;
            while((line = br.readLine()) != null){
                if(line.contains("function " + func)){
                    found = true;
                }
                if(found == true){
                    int index = getLastIndex(line);
                    if(index != -1){
                        sb.append(line.substring(0, index + 1));                    
                        break;
                    }else{
                        sb.append(line.trim());
                    }
                }                
            }
            sb.append(")");
        } catch (IOException ex) {
            Logger.getLogger(JS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(sb.length() <= 2) return null;
        
        try(Context context = Context.create()){
            Value value = context.eval("js", sb.toString());
            return value.execute(args);
        }
    }
}
