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
package org.wingate.ygg.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.Loader;

/**
 *
 * @author util2
 */
public class SignalData {
    
    public static void getWaveForm(File media, int width, int height, long msAreaStart, long msAreaStop){
        String start = Long.toString(msAreaStart) + "ms";
        String stop = Long.toString(msAreaStop) + "ms";
        
        File folder = new File("configuration" + File.separator + "w");
        if(folder.exists() == false) folder.mkdirs();
        File imgFile = new File(folder, Long.toString(msAreaStart) + ".png");
        if(imgFile.exists() == true) imgFile.delete();
        
        try{
            String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpeg,
                    "-ss", start,
                    "-to", stop, 
                    "-i", media.getPath(), 
                    "-filter_complex", "showwavespic=s=" + width + "x" + height + ":colors=Blue|BlueViolet",
                    "-frames:v", "1",
                    imgFile.getPath());
            Process p = pb.start();
            p.waitFor();
        }catch(IOException | InterruptedException ex){
            Logger.getLogger(SignalData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void getSpectrogram(File media, int width, int height, long msAreaStart, long msAreaStop){
        String start = Long.toString(msAreaStart) + "ms";
        String stop = Long.toString(msAreaStop) + "ms";
        
        File folder = new File("configuration" + File.separator + "s");
        if(folder.exists() == false) folder.mkdirs();
        File imgFile = new File(folder, Long.toString(msAreaStart) + ".png");
        if(imgFile.exists() == true) imgFile.delete();
        
        try{
            String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpeg,
                    "-ss", start,
                    "-to", stop, 
                    "-i", media.getPath(), 
                    "-lavfi",
                    "showspectrumpic=s=" + width + "x" + height + ":legend=0:scale=cbrt",
                    imgFile.getPath());
            Process p = pb.start();
            p.waitFor();
        }catch(IOException | InterruptedException ex){
            Logger.getLogger(SignalData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static BufferedImage getImage(long ms, boolean spectrum){
        BufferedImage image = null;
        
        File folder = new File("configuration" + File.separator + (spectrum == true ? "s" : "w"));
        File imgFile = new File(folder, Long.toString(ms) + ".png");
        
        try {
            image = ImageIO.read(imgFile);
        } catch (IOException ex) {
            Logger.getLogger(SignalData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return image;
    }
    
    public static boolean isImageExists(long ms, boolean spectrum){
        File folder = new File("configuration" + File.separator + (spectrum == true ? "s" : "w"));
        File imgFile = new File(folder, Long.toString(ms) + ".png");
        return imgFile.exists();
    }
    
    public static List<Integer> getKeyFrames(File media){
        List<Integer> keyframes = new ArrayList<>();
        
        Process p = null;
        
        try{
            String ffprobe = Loader.load(org.bytedeco.ffmpeg.ffprobe.class);
            ProcessBuilder pb = new ProcessBuilder(ffprobe,
                    "-i", media.getPath(),
                    "-select_streams", "v", 
                    "-show_frames",
                    "-show_entries", "frame=pict_type",
                    "-loglevel", "error");
            pb.redirectErrorStream(true);
            p = pb.start();
        }catch(IOException ex){
            Logger.getLogger(SignalData.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(p != null){
            int counter = 0;
            String line;
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))){
                
                while ((line = reader.readLine()) != null) {
                    if(line.contains("pict_type=I")){
                        keyframes.add(counter);
                    }
                    if(line.contains("pict_type=") == true){
                        counter++;
                    }
                }
            } catch (IOException ex) {        
                Logger.getLogger(SignalData.class.getName()).log(Level.SEVERE, null, ex);
            }
            p.destroy();
        }
        return keyframes;
    }
    
}
