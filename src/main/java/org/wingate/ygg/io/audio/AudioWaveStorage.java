/*
 * Copyright (C) 2020 util2
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
package org.wingate.ygg.io.audio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author util2
 */
public class AudioWaveStorage {
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    
    private PrintWriter printWriter;
    
    public AudioWaveStorage() {
    }
    
    //=====
    // SAVE
    //-----
    
    public void startSave(File videoFile) throws IOException{        
        File temp = new File(videoFile.getPath().substring(0, videoFile.getPath().lastIndexOf(".")) + ".wst");
        if(temp.exists() == true) { temp.delete(); }
        printWriter = new PrintWriter(temp);
    }
    
    public void saveOneValue(int x, int y){
        printWriter.println(x + "," + y);
    }
    
    public void stopSave(){
        printWriter.flush();
        printWriter.close();
    }
    
    //=====
    // LOAD
    //-----
    
    public void startConsult(File videoFile) throws IOException{
        File temp = new File(videoFile.getPath().substring(0, videoFile.getPath().lastIndexOf(".")) + ".wst");
        fileReader = new FileReader(temp);
        bufferedReader = new BufferedReader(fileReader);
    }
    
    public int consultOneValue(int x) throws IOException{
        String line; int y = 0;
        while((line = bufferedReader.readLine()) != null){
            if(line.contains(Integer.toString(x)) == true){
                y = Integer.parseInt(line.substring(line.lastIndexOf(",") + 1));
                break;
            }
        }
        return y;
    }
    
    public Map<Integer, Integer> consultMultiValues(int xFROM, int xTO) throws IOException{
        String line; Map<Integer, Integer> ys = new HashMap<>(); boolean copy = false;
        int count = 0;
        while((line = bufferedReader.readLine()) != null){            
            if(line.contains(Integer.toString(xFROM)) == true){
                ys.put(count, Integer.parseInt(line.substring(line.lastIndexOf(",") + 1)));
                copy = true;
            }else if(line.contains(Integer.toString(xTO)) == true){
                ys.put(count, Integer.parseInt(line.substring(line.lastIndexOf(",") + 1)));
                break;
            }else if(copy == true){
                ys.put(count, Integer.parseInt(line.substring(line.lastIndexOf(",") + 1)));
            }
            count++;
        }
        return ys;
    }
    
    public void stopConsult() throws IOException{
        bufferedReader.close();
        fileReader.close();
    }
}
