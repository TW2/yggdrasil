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
package org.wingate.ygg.videoplayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.util.subtitle.YGGY;

/**
 *
 * @author util2
 */
public class VideoPlayer extends JPanel implements Runnable {

    private File audioVideoFile = null;
    private volatile Thread audioVideoThread = null;
    private volatile boolean running = false;
    private volatile BufferedImage videoImage = null;
    
    private boolean preserveRatio = true;
    private int videoWidth = -1;
    private int videoHeight = -1;
    
    private Color backgroundColor = Color.black;
    
    private volatile Time start = Time.create(0L);
    private volatile Time stop = Time.create(0L);
    
    private volatile long pause, offset, limit, microStart, microElapsed;    
    private volatile boolean paused = false;
    
    private YGGY yggy = YGGY.create();
    
    public VideoPlayer() {
        init();        
    }
    
    private void init(){
        audioVideoThread = new Thread(this);
        audioVideoThread.start();
    }
    
    @Override
    public void run() {
        while(true){
            if(running == true){
                process();
            }
        }
    }
    
    @Override
    public void paint(Graphics g) {
        if(videoImage != null){
            g.setColor(MainFrame.isDark() ? Color.black : Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());
            updateSize();
            int x = (getWidth()-videoImage.getWidth())/2;
            int y = (getHeight()-videoImage.getHeight())/2;
            g.drawImage(videoImage, x, y, null);
//            File file = new File(MainFrame.getTempFolder(), "temp.png");
//            Image img = makeColorTransparent(new ImageIcon(file.getPath()).getImage(), new Color(63, 63, 63));
//            g.drawImage(img, x, y, null);
        }else{
            super.paint(g);
        }
    }
    
    private void updateSize(){
        if(videoImage != null){
            int rw = getWidth();
            int rh = getHeight();
            
            if(preserveRatio == true){
                Dimension imgSize = new Dimension(videoImage.getWidth(), videoImage.getHeight());
                Dimension boundary = new Dimension(rw, rh);
                Dimension size = getScaledDimension(imgSize, boundary);
                rw = size.width;
                rh = size.height;
            }
            
            Image img = videoImage.getScaledInstance(rw, rh, Image.SCALE_DEFAULT);
            BufferedImage bi = new BufferedImage(rw, rh, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();
            videoImage = bi;
        }
    }
    
    public Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
    
    public Image makeColorTransparent(Image im, final Color color) {
        ImageFilter filter = new RGBImageFilter() {
            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            @Override
            public final int filterRGB(int x, int y, int rgb) {
                if (( rgb | 0xFF000000 ) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                }else{
                    // nothing to do
                    return rgb;
                }
            }
        }; 

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
    
    private void process(){
        boolean useStart = !Time.isEqual(Time.create(0L), start);
        boolean useStop = !Time.isEqual(Time.create(0L), stop);
        if(audioVideoFile != null){
            try {
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(audioVideoFile);
                grabber.start();
                
                Java2DFrameConverter converter = new Java2DFrameConverter();
                ExecutorService executor = Executors.newSingleThreadExecutor();
                
                offset = useStart == true ? TimeUnit.MILLISECONDS.toMicros(Time.toMillisecondsTime(start)) : 0L;
                limit = useStop == true ? TimeUnit.MILLISECONDS.toMicros(Time.toMillisecondsTime(stop)) : 0L;
                microStart = TimeUnit.NANOSECONDS.toMicros(System.nanoTime());
                microElapsed = 0L;
                
                while (!Thread.interrupted() && paused == false) {
                    // Start time when specified (action -> configure)
                    if(useStart == true && offset != 0L){
                        grabber.setVideoTimestamp(offset);
                        useStart = false;
                        System.out.println("1");
                    }

                    Frame frame = grabber.grab();
                    if (frame == null) {
                        break;
                    }
                    if (frame.image != null) {
                        long delay = frame.timestamp; // Microseconds

                        while(true){
                            microElapsed = TimeUnit.NANOSECONDS.toMicros(System.nanoTime()) - microStart;

                            if(microElapsed >= delay){
                                break;
                            }
                        }

                        // End time when specified (action -> stop video)
                        if(useStop == true && limit <= delay){                            
                            paused = true;
                            pause = TimeUnit.NANOSECONDS.toMicros(System.nanoTime());
                            useStop = false;
                            System.out.println("2");
                        }
                        
                        videoImage = converter.convert(frame);
                        
//                        Time assTime = Time.fromMillisecondsTime(TimeUnit.MICROSECONDS.toMillis(delay));
//                        yggy.getYggy().executor(
//                                MainFrame.getTempFolder() + File.separator + "temp.png",
//                                MainFrame.getTempFolder() + File.separator + "temp.ass",
//                                Double.toString(Time.getLengthInSeconds(assTime)),
//                                videoImage.getWidth(),
//                                videoImage.getHeight());
                                                
                        repaint();
                    }                    
                }
                executor.shutdownNow();
                executor.awaitTermination(10, TimeUnit.SECONDS);
                grabber.stop();
                grabber.release();
            } catch (FrameGrabber.Exception | InterruptedException ex) {
                Logger.getLogger(VideoPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
//    private double getMediaElapsedDuration(){
//        // We calculate the current time of the media
//        long systemTime = System.currentTimeMillis();
//        currentMediaTime = currentMediaTime + (systemTime - startSystemTime);
//        System.out.println("M: "+(currentMediaTime / 1000d));
//        return currentMediaTime / 1000d;
//    }
    
    private void setPauseTime(){
        if(paused == true){
            pause = TimeUnit.NANOSECONDS.toMicros(System.nanoTime());
        }
    }
    
    public void videoPlay(){
        running = true;
        paused = false;
    }
    
    public void videoPause(){
        paused = !paused;
        setPauseTime();
    }
    
    public void videoStop(){
        running = false;
        paused = true;
        setPauseTime();
    }

    public File getAudioVideoFile() {
        return audioVideoFile;
    }

    public void setAudioVideoFile(File audioVideoFile) {
        this.audioVideoFile = audioVideoFile;
    }

    public boolean isPreserveRatio() {
        return preserveRatio;
    }

    public void setPreserveRatio(boolean preserveRatio) {
        this.preserveRatio = preserveRatio;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public Time getStop() {
        return stop;
    }

    public void setStop(Time stop) {
        this.stop = stop;
    }
    
}
