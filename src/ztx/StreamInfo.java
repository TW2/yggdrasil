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
package ztx;

import org.bytedeco.ffmpeg.avformat.AVStream;

/**
 *
 * @author util2
 */
public class StreamInfo {
    
    private int streamIndex;
    private AVStream info;

    public StreamInfo() {
    }

    public StreamInfo(int streamIndex, AVStream info) {
        this.streamIndex = streamIndex;
        this.info = info;
    }

    public int getStreamIndex() {
        return streamIndex;
    }

    public void setStreamIndex(int streamIndex) {
        this.streamIndex = streamIndex;
    }

    public AVStream getInfo() {
        return info;
    }

    public void setInfo(AVStream info) {
        this.info = info;
    }
    
    
}
