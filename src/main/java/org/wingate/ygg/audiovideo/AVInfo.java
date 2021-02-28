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

import java.io.File;
import java.util.Map;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;

/**
 *
 * @author util2
 */
public class AVInfo {
    
    private double aspectRatio;
    private int audioBitrate;
    private int audioChannels;
    private int audioCodec;
    private String audioCodecName;
    private double audioFrameRate;
    private Map<String, String> audioMetadata;
    private Map<String, String> audioOptions;
    private int audioStream;
    private int bitsPerPixel;
    private double frameRate;
    private double gamma;
    private int imageHeight;
    private int imageWidth;
    private long duration;
    private int pixelFormat;
    private int sampleFormat;
    private int sampleRate;
    private int videoBitrate;
    private int videoCodec;
    private String videoCodecName;
    private double fps;
    private Map<String, String> videoMetadata;
    private Map<String, String> videoOptions;
    private int videoStream;

    public AVInfo(File media) throws FrameGrabber.Exception {
        init(media);
    }
    
    private void init(File media) throws FrameGrabber.Exception{
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(media);
        grabber.start();
        
        aspectRatio = grabber.getAspectRatio();
        audioBitrate = grabber.getAudioBitrate();
        audioChannels = grabber.getAudioChannels();
        audioCodec = grabber.getAudioCodec();
        audioCodecName = grabber.getAudioCodecName();
        audioFrameRate = grabber.getAudioFrameRate();
        audioMetadata = grabber.getAudioMetadata();
        audioOptions = grabber.getAudioOptions();
        audioStream = grabber.getAudioStream();
        bitsPerPixel = grabber.getBitsPerPixel();
        frameRate = grabber.getFrameRate();
        gamma = grabber.getGamma();
        imageHeight = grabber.getImageHeight();
        imageWidth = grabber.getImageWidth();
        duration = grabber.getLengthInTime();
        pixelFormat = grabber.getPixelFormat();
        sampleFormat = grabber.getSampleFormat();
        sampleRate = grabber.getSampleRate();
        videoBitrate = grabber.getVideoBitrate();
        videoCodec = grabber.getVideoCodec();
        videoCodecName = grabber.getVideoCodecName();
        fps = grabber.getVideoFrameRate();
        videoMetadata = grabber.getVideoMetadata();
        videoOptions = grabber.getVideoOptions();
        videoStream = grabber.getVideoStream();
        
        grabber.stop();
        grabber.release();
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public int getAudioBitrate() {
        return audioBitrate;
    }

    public int getAudioChannels() {
        return audioChannels;
    }

    public int getAudioCodec() {
        return audioCodec;
    }

    public String getAudioCodecName() {
        return audioCodecName;
    }

    public double getAudioFrameRate() {
        return audioFrameRate;
    }

    public Map<String, String> getAudioMetadata() {
        return audioMetadata;
    }

    public Map<String, String> getAudioOptions() {
        return audioOptions;
    }

    public int getAudioStream() {
        return audioStream;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public double getFrameRate() {
        return frameRate;
    }

    public double getGamma() {
        return gamma;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public long getDuration() {
        return duration;
    }

    public int getPixelFormat() {
        return pixelFormat;
    }

    public int getSampleFormat() {
        return sampleFormat;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public int getVideoCodec() {
        return videoCodec;
    }

    public String getVideoCodecName() {
        return videoCodecName;
    }

    public double getFps() {
        return fps;
    }

    public Map<String, String> getVideoMetadata() {
        return videoMetadata;
    }

    public Map<String, String> getVideoOptions() {
        return videoOptions;
    }

    public int getVideoStream() {
        return videoStream;
    }
    
    
}
