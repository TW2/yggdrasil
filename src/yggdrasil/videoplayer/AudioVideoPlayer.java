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
package yggdrasil.videoplayer;

import java.awt.Color;
import java.io.File;
import javax.swing.JPanel;
import org.wingate.timelibrary.Time;

/**
 *
 * @author util2
 */
public class AudioVideoPlayer {

    private final AudioPlayer audioPlayer = new AudioPlayer();
    private final VideoPlayer videoPlayer = new VideoPlayer();
    
    public AudioVideoPlayer() {
        
    }
    
    public JPanel getVideoPanel(){
        return videoPlayer;
    }
    
    public void audioVideoPlay(){
        audioPlayer.audioPlay();
        videoPlayer.videoPlay();
    }
    
    public void audioVideoPause(){
        audioPlayer.audioPause();
        videoPlayer.videoPause();
    }
    
    public void audioVideoStop(){
        audioPlayer.audioStop();
        videoPlayer.videoStop();
    }

    public File getAudioVideoFile() {
        return audioPlayer.getAudioVideoFile();
    }

    public void setAudioVideoFile(File audioVideoFile) {
        audioPlayer.setAudioVideoFile(audioVideoFile);
        videoPlayer.setAudioVideoFile(audioVideoFile);
    }

    public boolean isPreserveRatio() {
        return videoPlayer.isPreserveRatio();
    }

    public void setPreserveRatio(boolean preserveRatio) {
        videoPlayer.setPreserveRatio(preserveRatio);
    }

    public int getVideoWidth() {
        return videoPlayer.getVideoWidth();
    }

    public void setVideoWidth(int videoWidth) {
        videoPlayer.setVideoWidth(videoWidth);
    }

    public int getVideoHeight() {
        return videoPlayer.getVideoHeight();
    }

    public void setVideoHeight(int videoHeight) {
        videoPlayer.setVideoHeight(videoHeight);
    }

    public Color getBackgroundColor() {
        return videoPlayer.getBackgroundColor();
    }

    public void setBackgroundColor(Color backgroundColor) {
        videoPlayer.setBackgroundColor(backgroundColor);
    }

    public Time getStart() {
        return audioPlayer.getStart();
    }

    public void setStart(Time start) {
        audioPlayer.setStart(start);
        videoPlayer.setStart(start);
    }

    public Time getStop() {
        return audioPlayer.getStop();
    }

    public void setStop(Time stop) {
        audioPlayer.setStop(stop);
        videoPlayer.setStop(stop);
    }
    
}
