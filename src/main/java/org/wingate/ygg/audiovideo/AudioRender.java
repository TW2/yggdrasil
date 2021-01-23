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
import org.wingate.timelibrary.Time;
import org.wingate.ygg.util.SignalData;

/**
 *
 * @author util2
 */
public class AudioRender {
    
    private boolean showKeyFrame = false;

    public AudioRender() {
    }
    
    public void renderWaveform(File media, int w, int h, Time areaStart, Time areaStop){
        if(media == null | w == 0 | h == 0) return;
        SignalData.getWaveForm(
                media,                              // File
                w,                                  // Rendering width
                h,                                  // Rendering height
                Time.toMillisecondsTime(areaStart), // Start time
                Time.toMillisecondsTime(areaStop)); // Stop time
    }
    
    public void renderSpectrogram(File media, int w, int h, Time areaStart, Time areaStop){
        if(media == null | w == 0 | h == 0) return;
        SignalData.getSpectrogram(
                media,                              // File
                w,                                  // Rendering width
                h,                                  // Rendering height
                Time.toMillisecondsTime(areaStart), // Start time
                Time.toMillisecondsTime(areaStop)); // Stop time
    }

    public boolean isShowKeyFrame() {
        return showKeyFrame;
    }

    public void setShowKeyFrame(boolean showKeyFrame) {
        this.showKeyFrame = showKeyFrame;
    }
    
    
}
