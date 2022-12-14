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
package org.wingate.ygg.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.bytedeco.javacpp.Loader;
import org.wingate.timelibrary.Time;

/**
 *
 * @author util2
 */
public class FFStuffs {
    
    private final Map<Integer, Time> IFrames = new HashMap<>();
    private final Map<Integer, Time> PFrames = new HashMap<>();
    private final Map<Integer, Time> BFrames = new HashMap<>();
    
    private int videoWidth = 0, videoHeight = 0;
    private VideoRatio ratio = null;
    private double fps = 1d;
    private int frameCount = 0;
    private Time duration = null;
    private String sampleFormat = null;
    private double sampleRate = 0d;
    
    private boolean hasAudio = false;
    private boolean hasVideo = false;
    
    public FFStuffs() {
        
    }
    
    public static FFStuffs create(File video){
        FFStuffs fs = new FFStuffs();
        
        String file = video.getPath();
        String videoReport = "", audioReport = "";
        Process p = null; ProcessBuilder pb;
        String line; int countCheck;
        
        
        
        // On se renseigne sur la nature du fichier
        // Fichier vid??o >> Obtenir la partie son en la convertissant en un fichier WAV
        // Fichier audio >> Convertir en un fichier WAV
        //----------------------------------------------------------------------
        // V??rification de la pr??sence de vid??o :
        try{
            String ffprobe = Loader.load(org.bytedeco.ffmpeg.ffprobe.class);
            pb = new ProcessBuilder(ffprobe,
                    "-i", file, 
                    "-show_streams", 
                    "-select_streams", "v", 
                    "-loglevel", "error");
            pb.redirectErrorStream(true);
            p = pb.start();
        }catch(IOException ex){
            Logger.getLogger(FFStuffs.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        countCheck = 0;
        if(p != null){
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))){
                while ((line = reader.readLine()) != null) {
                    countCheck++;
                    videoReport = videoReport + line + "\n";
                }
            } catch (IOException ex) {        
                Logger.getLogger(FFStuffs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        if(countCheck > 0){
            // On a un flux vid??o
            fs.hasVideo = true;
        }
        if(p != null){
            p.destroy();
        }
        
        // V??rification de la pr??sence d'audio :
        try{
            String ffprobe = Loader.load(org.bytedeco.ffmpeg.ffprobe.class);
            pb = new ProcessBuilder(ffprobe, "-i", file, "-show_streams", "-select_streams", "a", "-loglevel", "error");
            pb.redirectErrorStream(true);
            p = pb.start();
        }catch(IOException ex){
            Logger.getLogger(FFStuffs.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        countCheck = 0;
        if(p != null){
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))){
                while ((line = reader.readLine()) != null) {
                    countCheck++;
                    audioReport = audioReport + line + "\n";
                }
            } catch (IOException ex) {        
                Logger.getLogger(FFStuffs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        if(countCheck > 0){
            // On a un flux vid??o
            fs.hasAudio = true;
        }
        if(p != null){
            p.destroy();
        }
        
        // Obtention des informations du fichier vid??o
        //----------------------------------------------------------------------
        
        StringBuilder sb = new StringBuilder(videoReport);
        sb.append("\n");
        sb.append(audioReport);
        String report = sb.toString();
        
        try(StringReader sr = new StringReader(report); BufferedReader br = new BufferedReader(sr)){
            while((line = br.readLine()) != null){
                if(line.startsWith("width=") == true) {
                    fs.videoWidth = Integer.parseInt(line.substring("width=".length())); 
                }
                if(line.startsWith("height=") == true) { 
                    fs.videoHeight = Integer.parseInt(line.substring("height=".length()));
                }
                if(line.startsWith("display_aspect_ratio=") == true) {
                    String[] ratio = line.substring("display_aspect_ratio=".length()).split(":");
                    fs.ratio = new VideoRatio(Integer.parseInt(ratio[0]), Integer.parseInt(ratio[1]));
                }
                if(fs.fps == 0d && line.startsWith("avg_frame_rate=") == true){
                    String[] framerate = line.substring("avg_frame_rate=".length()).split("/");
                    fs.fps = Double.parseDouble(framerate[0]) / Double.parseDouble(framerate[1]);
                }
                if(fs.frameCount == 0){
                    if(line.startsWith("TAG:NUMBER_OF_FRAMES=") == true){
                        fs.frameCount = Integer.parseInt(line.substring("TAG:NUMBER_OF_FRAMES=".length()));
                    }else if(line.startsWith("nb_frames=") == true){
                        try{
                            fs.frameCount = Integer.parseInt(line.substring("nb_frames=".length()));
                        }catch(NumberFormatException ex){
                            fs.frameCount = 0;
                        }                        
                    }                    
                }
                if(fs.duration == null){
                    if(line.startsWith("TAG:DURATION=") == true){
                        fs.duration = Time.create(line.substring("TAG:DURATION=".length(), "TAG:DURATION=".length() + 12));
                    }else if(line.startsWith("duration=") == true){                        
                        try{
                            fs.duration = Time.create(Math.round(Double.parseDouble(line.substring("duration=".length())) * 1000d));
                        }catch(NumberFormatException ex){
                            fs.duration = null;
                        }
                    }
                }
                if(line.startsWith("sample_fmt=") == true){
                    fs.sampleFormat = line.substring("sample_fmt=".length());
                }
                if(line.startsWith("sample_rate=") == true){
                    fs.sampleRate = Double.parseDouble(line.substring("sample_rate=".length()))/1000d;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FFStuffs.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        waiterFrame.updateStatus(0, "Retrieving frames");
        
        // On cr??e ou r??cup??re un fichier
        // Int??rieur d'un fichier 'frames'
        // fps
        // frameType
        // frameType
        // ...
        File framesInFile = new File(file.substring(0, file.lastIndexOf(".")) + ".frames");
        countCheck = 0;
        if(framesInFile.exists() == true){
            try(FileReader fr = new FileReader(framesInFile, StandardCharsets.UTF_8); 
                    BufferedReader br = new BufferedReader(fr)){
                String myLine;
                double myFps = -1d;
                while((myLine = br.readLine()) != null){
                    if(myFps == -1d) myFps = Double.parseDouble(myLine);
                    if(myLine.startsWith("I")){
                        fs.IFrames.put(countCheck, Time.getTimeFromFrame(countCheck, myFps));
                        countCheck++;
                    }else if(myLine.startsWith("P")){
                        fs.PFrames.put(countCheck, Time.getTimeFromFrame(countCheck, myFps));
                        countCheck++;
                    }else if(myLine.startsWith("B")){
                        fs.BFrames.put(countCheck, Time.getTimeFromFrame(countCheck, myFps));
                        countCheck++;
                    }
//                    waiterFrame.updateStatus(
//                            countCheck * 100 / fs.frameCount, 
//                            "Populate a collection of frames"
//                    );
                }                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FFStuffs.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FFStuffs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            // On obtient les keyframes
            if(fs.hasVideo == true){
                try{
                    String ffprobe = Loader.load(org.bytedeco.ffmpeg.ffprobe.class);
                    pb = new ProcessBuilder(ffprobe, "-i", file, "-select_streams", "v", "-show_frames",
                            "-show_entries", "frame=pict_type", "-loglevel", "error");
                    pb.redirectErrorStream(true);
                    p = pb.start();
                }catch(IOException ex){
                    Logger.getLogger(FFStuffs.class.getName()).log(Level.SEVERE, null, ex);
                }

                if(p != null){
                    countCheck = 0;                    
                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                            PrintWriter pw = new PrintWriter(framesInFile, StandardCharsets.UTF_8)){
                        pw.println(fs.fps);
                        while ((line = reader.readLine()) != null) {
                            if(line.contains("pict_type=I")){
                                fs.IFrames.put(countCheck, Time.getTimeFromFrame(countCheck, fs.fps));
                                pw.println("I");
                            }else if(line.contains("pict_type=P")){
                                fs.PFrames.put(countCheck, Time.getTimeFromFrame(countCheck, fs.fps));
                                pw.println("P");
                            }else if(line.contains("pict_type=B")){
                                fs.BFrames.put(countCheck, Time.getTimeFromFrame(countCheck, fs.fps));
                                pw.println("B");
                            }
                            if(line.contains("pict_type=") == true){
                                countCheck++;
                            }
//                            waiterFrame.updateStatus(
//                                    countCheck * 100 / fs.frameCount, 
//                                    "Populate a collection of frames"
//                            );
                        }
                    } catch (IOException ex) {        
                        Logger.getLogger(FFStuffs.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    p.destroy();
                }
            }
        }
        
//        waiterFrame.close();
        
        return fs;
    }
    
    public ImageIcon getImageAtTime(String videoPath, Time time){
        
        try{
            String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
            ProcessBuilder pb = new ProcessBuilder(ffmpeg, 
                    "-y",                           // YES for force overwrite
                    "-ss", time.toFFMpegTime(),     // Time
                    "-i", videoPath,                // Input
                    "-vframes", "1",                // Number of frames (here one image)
                    "-q:v", "2",                    // JPEG 
                    "eye.jpg");                     // Output
            pb.start();
        }catch(IOException ex){
            return null;
        }
        
        return new ImageIcon("eye.jpg");
    }

    public Map<Integer, Time> getIFrames() {
        return IFrames;
    }
    
    public Map<Integer, Time> getPFrames() {
        return PFrames;
    }
    
    public Map<Integer, Time> getBFrames() {
        return BFrames;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public VideoRatio getRatio() {
        return ratio;
    }

    public double getFps() {
        return fps;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public Time getDuration() {
        return duration;
    }

    public String getSampleFormat() {
        return sampleFormat;
    }

    public double getSampleRate() {
        return sampleRate;
    }
    
    public boolean hasVideo(){
        return hasVideo;
    }
    
    public boolean hasAudio(){
        return hasAudio;
    }
}
