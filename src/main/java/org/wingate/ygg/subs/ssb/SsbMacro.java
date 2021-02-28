/*
 * Copyright (C) 2021 util2
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
package org.wingate.ygg.subs.ssb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author util2
 */
public class SsbMacro {
    
    private String macroName = "Default";
    private List<String> macroContents = new ArrayList<>();
    private String text = "";

    public SsbMacro() {
        init();
    }
    
    private void init(){
        macroContents.add("color=000000");
    }

    public String getMacroName() {
        return macroName;
    }

    public void setMacroName(String macroName) {
        this.macroName = macroName;
    }

    public List<String> getMacroContents() {
        return macroContents;
    }

    public void setMacroContents(List<String> macroContents) {
        this.macroContents = macroContents;
    }
    
    public void clearMacroContents() {
        macroContents.clear();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public static String getMacro(SsbMacro macro){
        StringBuilder sb = new StringBuilder();
        
        for(int i=0; i<macro.macroContents.size(); i++){
            if(i == macro.macroContents.size() - 1){
                sb.append(macro.macroContents.get(i));
            }else{
                sb.append(macro.macroContents.get(i)).append(";");
            }            
        }
        
        return macro.macroName + ": [" + sb.toString() + "]" + macro.text;
    }
    
    public static SsbMacro setMacroFromLine(String line){
        SsbMacro macro = new SsbMacro();
        
        Pattern p = Pattern.compile("([^:]+): \\[([^\\]+])\\](.*)");
        Matcher m = p.matcher(line);
        
        if(m.find() == true){
            if(m.groupCount() > 3){
                macro.text = m.group(3);
            }
            macro.macroName = m.group(1);
            String[] t = m.group(2).split(";");
            macro.macroContents.addAll(Arrays.asList(t));
        }
        
        return macro;
    }
    
    public static SsbMacro getDefaultMacro(){
        return new SsbMacro();
    }
    
}
