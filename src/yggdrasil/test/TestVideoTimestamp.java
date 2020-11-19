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
package yggdrasil.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.wingate.timelibrary.Time;
import yggdrasil.MainFrame;

/**
 *
 * @author util2
 */
public class TestVideoTimestamp extends JFrame implements Runnable {
    
    //========================================
    // Test delayed video
    //========================================
    
    private final File media = new File(getClass().getResource("/fortest/testfile.mp4").getPath());
    private final VideoPanel videoPane = new VideoPanel();
    
    private volatile Thread audioVideoThread = null;
//    private volatile boolean running = false;
    private volatile BufferedImage videoImage = null;
    
    
    private volatile Time start = Time.create(10000L);
    private volatile Time stop = Time.create(20000L);
    
    private volatile long pause = 0L, microStart, microElapsed;    
//    private volatile boolean paused = false;
    
    public enum TimeStatus {
        Stop, Play, Playing, Pause, Resume;
    }
    
    private volatile TimeStatus state = TimeStatus.Stop;

    public TestVideoTimestamp() {
        init();
    }
    
    private void init(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().add(videoPane, BorderLayout.CENTER);
        audioVideoThread = new Thread(this);
        audioVideoThread.start();
//        running = true;
        state = TimeStatus.Play;
    }

    @Override
    public void run() {
        while(true){
            if(state == TimeStatus.Play | state == TimeStatus.Resume){
                process();
            }
        }
    }
    
    private void process(){        
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(media);
            grabber.start();
            
            Java2DFrameConverter converter = new Java2DFrameConverter();
            
            microStart = TimeUnit.NANOSECONDS.toMicros(System.nanoTime());
            microElapsed = 0L;

            while (!Thread.interrupted()) {
                // Start time when specified (action -> configure)
                startAt(grabber);                

                Frame frame = grabber.grab();
                if (frame == null) {
                    System.out.println("-");
                    break;
                }
                if (frame.image != null) {
                    System.out.println("+");
                    long delay = frame.timestamp; // Microseconds                    

                    while(true){
                        microElapsed = TimeUnit.NANOSECONDS.toMicros(System.nanoTime()) - microStart;

                        if(microElapsed >= delay){
                            break;
                        }
                    }

                    // End time when specified (action -> stop video)
                    boolean requireStop = endAt(stop, delay);
                    if(requireStop == true){
                        state = TimeStatus.Stop;
                        System.out.println("----------");
                        break;
                    }else if(state == TimeStatus.Pause){
                        pause = delay;
                        System.out.println("Pause = delay");
                        break;
                    }

                    videoImage = converter.convert(frame);

                    repaint();
                }                    
            }
            grabber.stop();
            grabber.release();
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(TestVideoTimestamp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void startAt(FFmpegFrameGrabber grabber) throws FrameGrabber.Exception {
        switch(state){
            case Stop, Play -> {
                long micro = TimeUnit.MILLISECONDS.toMicros(Time.toMillisecondsTime(start));
                if(micro > 0){
                    grabber.setVideoTimestamp(micro);
                    state = TimeStatus.Playing;
                }
                break;
            }
            case Resume -> {
                if(pause > 0){
                    grabber.setVideoTimestamp(pause);
                }
                break;
            }
        }   
    }
    
    private boolean endAt(Time limit, long delay){
        long ref = TimeUnit.MILLISECONDS.toMicros(Time.toMillisecondsTime(limit));
        return ref <= delay;
    }
    
    private class VideoPanel extends JPanel {

        private final boolean preserveRatio = true;
        
        public VideoPanel() {
            init();
        }
        
        private void init(){
            setDoubleBuffered(true);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getButton() == MouseEvent.BUTTON1){
                        switch(state){
                            case Stop, Play, Playing, Resume -> {
                                state = TimeStatus.Pause;
                                System.out.println("Pause");
                                break;
                            }
                            case Pause ->{
                                state = TimeStatus.Resume;
                                System.out.println("Resume");
                                break;
                            }
                        }
                    }
                }
            });
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
    }
}
