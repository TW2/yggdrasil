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

/**
 *
 * @author util2
 */
public class AudioLink {
    
    private long msDuration;
    private int imageSize;
    private int scrollBarTotal;
    
    private AudioLink(){
    }
    
    /**
     * Create a new AudioLink, a tool to know position of image with offset
     * @param scrollBarTotal The maximum value of the scrollbar by reducing its minimum
     * @param imageSize The size of one complete image
     * @param msDuration The duration of the track in millisconds
     * @return An initialized AudioLink
     */
    public static AudioLink create(int scrollBarTotal, int imageSize, long msDuration){
        AudioLink audioLink = new AudioLink();
        
        audioLink.scrollBarTotal = scrollBarTotal;
        audioLink.imageSize = imageSize;
        audioLink.msDuration = msDuration;
        
        return audioLink;
    }
    
    /**
     * Get the offset for the current ScrollBar value with scale
     * @param scrollBarCurrent Current ScrollBar value
     * @param msPerImage Milliseconds for one image
     * @return Offset with scale (caution: this number is negative by default)
     */
    public long getOffset(int scrollBarCurrent, long msPerImage){
        // On calcule le nombre de pixels de l'offset
        // sbTotal <> msDur
        // sbCur <> msCur
        //--
        // msCur = sbCur * msDur / sbTotal
        long msCur = scrollBarCurrent * msDuration / scrollBarTotal;
        // imgSize <> 2000ms
        // a <> msCur
        //--
        // a = msCur * imgSize / 2000ms où 2000 = 8000 * 0.25
        long a = msCur * imageSize / msPerImage;
        
        return -a;
    }
}
