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
package org.wingate.ygg.audiovideo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

/**
 *
 * @author util2
 */
public class VideoKeyFrames implements Runnable {

    private final Map<Integer, Long> keyframes = new HashMap<>();
    
    private volatile boolean doAct = false;
    private Thread thAct = null;
    private File video = null;
    
    public VideoKeyFrames() {
        
    }
    
    public void startSearch(File video){
        stopThread();
        this.video = video;
        doAct = true;
        thAct = new Thread(this);
        thAct.start();
    }
    
    public void stopThread(){
        if(thAct != null){
            thAct.interrupt();
            thAct = null;
            doAct = false;
        }
    }
    
    private void findKeyFrames(File video){        
        try {
            FFmpegFrameGrabber g = new FFmpegFrameGrabber(video);
            g.start();
            
            Frame frame;
            while((frame = g.grabKeyFrame()) != null){
                keyframes.put(g.getFrameNumber(), frame.timestamp);
            }
            
            g.stop();
            g.release();
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(VideoKeyFrames.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void register(File video){
        File folder = new File("configuration" + File.separator + "k");
        File keyframesFile = new File(folder, video.getName() + ".kfs");
        
        if(folder.exists() == false){
            folder.mkdirs();
        }
        
        Map<Integer, Long> sorted = new TreeMap<>(keyframes);

        try(PrintWriter pw = new PrintWriter(keyframesFile, StandardCharsets.UTF_16LE);){
            sorted.entrySet().forEach(entry -> {
                pw.println(entry.getKey() + "," + entry.getValue());
            });
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VideoKeyFrames.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VideoKeyFrames.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        fireKeyFrames(keyframes);
    }
    
    private void extract(File video){
        File folder = new File("configuration" + File.separator + "k");
        File keyframesFile = new File(folder, video.getName() + ".kfs");
        
        if(keyframesFile.exists() == false){
            findKeyFrames(video);
            register(video);
            return;
        }
        
        try(FileReader fr = new FileReader(keyframesFile, StandardCharsets.UTF_16LE);
                BufferedReader br = new BufferedReader(fr);){
            String line;
            while((line = br.readLine()) != null){
                String[] t = line.split(",");
                keyframes.put(Integer.parseInt(t[0]), Long.parseLong(t[1]));
            }
        } catch (IOException ex) {
            Logger.getLogger(VideoKeyFrames.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        fireKeyFrames(keyframes);
    }

    @Override
    public void run() {
        if(doAct){
            keyframes.clear();
            extract(video);
            fireKeyFrames(keyframes);
            stopThread();
        }
    }

    public Map<Integer, Long> getKeyframes() {
        return keyframes;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Evénements">
    
    private final EventListenerList listeners = new EventListenerList();
    
    public void addKeyFrameListener(IKeyFrame listener) {
        listeners.add(KeyFrameListener.class, (KeyFrameListener)listener);
    }

    public void removeKeyFrameListener(IKeyFrame listener) {
        listeners.remove(KeyFrameListener.class, (KeyFrameListener)listener);
    }

    public Object[] getListeners() {
        return listeners.getListenerList();
    }
    
    protected void fireKeyFrames(Map<Integer, Long> keyframes) {
        for(Object o : getListeners()){
            if(o instanceof KeyFrameListener){
                KeyFrameListener listen = (KeyFrameListener)o;
                listen.getKeyFrames(keyframes);
                break;
            }
        }
    }
    
    // </editor-fold>
}
