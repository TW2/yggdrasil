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
package org.wingate.ygg.subs.srt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author util2
 */
public class SRT {
    
    // File
    private File srtFile = null;
    
    private List<SrtEvent> events = new ArrayList<>();
    
    public void clearData(){
        events.clear();
    }
    
    public static SRT NoFileToLoad(){
        return new SRT();
    }
    
    @SuppressWarnings("UnusedAssignment")
    public static SRT Read(String path){
        SRT srt = new SRT();
        srt.setSrtFile(new File(path));
        // Read file by line
        try(FileInputStream fis = new FileInputStream(path); 
                InputStreamReader isr = new InputStreamReader(fis, detectCharset(path)); 
                BufferedReader br = new BufferedReader(isr);){
            String line = "";
            while((line = br.readLine()) != null){
                if(line.isEmpty() == true){
                    srt.getEvents().add(SrtEvent.createFromSRT(line));
                    line = "";
                    continue;
                }
                line += line + "\n";
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SRT.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SRT.class.getName()).log(Level.SEVERE, null, ex);
        }
        return srt;
    }
    
    public static void Save(String path, SRT srt){
        try(PrintWriter pw = new PrintWriter(path, StandardCharsets.UTF_8)){
            srt.getEvents().forEach(ev -> {
                pw.println(SrtEvent.getSrtEventLine(ev));
            });
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SRT.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SRT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Try to get a correct charset
     * @param pathname A file name
     * @return A charset
     * @throws java.io.FileNotFoundException
     */
    public static Charset detectCharset(String pathname) throws FileNotFoundException{
        return detectCharset(new FileReader(pathname));
    }
    
    /**
     * Try to get a correct charset
     * Byte Order mark (Bytes >> Encoding Form):
     * - 00 00 FE FF >> UTF-32, big-endian
     * - FF FE 00 00 >> UTF-32, little-endian
     * - FE FF >> UTF-16, big-endian
     * - FF FE >> UTF-16, little-endian
     * - EF BB BF >> UTF-8
     * @param fr A stream
     * @return A charset
     */
    public static Charset detectCharset(FileReader fr){
        Charset cs = null;
        String newline;
        
        try(BufferedReader br = new BufferedReader(fr)){            
            // Scan for encoding marks
            while ((newline = br.readLine()) != null) {                
                if(newline.startsWith("[\u0000\u0000") | newline.startsWith("\u00FF\u00FE\u0000\u0000")){
                    cs = Charset.forName("UTF-32LE");
                }else if(newline.startsWith("\u0000\u0000[") | newline.startsWith("\u0000\u0000\u00FE\u00FF")){
                    cs = Charset.forName("UTF-32BE");
                }else if(newline.startsWith("[\u0000") | newline.startsWith("\u00FF\u00FE")){
                    cs = Charset.forName("UTF-16LE");
                }else if(newline.startsWith("\u0000[") | newline.startsWith("\u00FE\u00FF")){
                    cs = Charset.forName("UTF-16BE");
                }else if(newline.startsWith("\u00EF\u00BB\u00BF")){
                    cs = Charset.forName("UTF-8");
                }
                
                // If a charset was found then close the stream
                // and the return charset encoding.
                if (cs != null){
                    break;
                }
            }
            
            // If nothing was found then set the encoding to system default.
            if (cs == null){
                cs = Charset.forName(fr.getEncoding());
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        
        return cs;
    }

    public File getSrtFile() {
        return srtFile;
    }

    public void setSrtFile(File srtFile) {
        this.srtFile = srtFile;
    }

    public List<SrtEvent> getEvents() {
        return events;
    }

    public void setEvents(List<SrtEvent> events) {
        this.events = events;
    }
    
}
