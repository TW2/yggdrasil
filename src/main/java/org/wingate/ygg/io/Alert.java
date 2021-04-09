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
package org.wingate.ygg.io;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import org.bytedeco.javacv.FrameGrabber;
import org.wingate.ygg.audiovideo.PlayAudio;

/**
 *
 * @author util2
 */
public class Alert {
    private static PlayAudio player = new PlayAudio();
    
    private static void play(final File file) {
        try {            
            player.setAudio(file);
            player.playStopAudio();
        } catch (FrameGrabber.Exception | LineUnavailableException ex) {
            Logger.getLogger(Alert.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void messageReceived(){
        try {
            play(new File(Alert.class.getResource("/sounds/NewMessage.opus").toURI()));            
        } catch (URISyntaxException ex) {
            Logger.getLogger(Alert.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void downloadFinished(){
        try {
            play(new File(Alert.class.getResource("/sounds/DownloadFinished.opus").toURI()));            
        } catch (URISyntaxException ex) {
            Logger.getLogger(Alert.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void requestGot(){
        try {
            play(new File(Alert.class.getResource("/sounds/Request.opus").toURI()));            
        } catch (URISyntaxException ex) {
            Logger.getLogger(Alert.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void proposalGot(){
        try {
            play(new File(Alert.class.getResource("/sounds/Proposal.opus").toURI()));            
        } catch (URISyntaxException ex) {
            Logger.getLogger(Alert.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
