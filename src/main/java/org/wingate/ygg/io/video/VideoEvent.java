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
package org.wingate.ygg.io.video;

import java.awt.image.BufferedImage;

/**
 *
 * @author util2
 */
public class VideoEvent {
    
    private final BufferedImage image;
    private final double milliseconds;
    private final int frameNumber;

    public VideoEvent(BufferedImage image, double milliseconds, int frameNumber) {
        this.image = image;
        this.milliseconds = milliseconds;
        this.frameNumber = frameNumber;
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getMilliseconds() {
        return milliseconds;
    }

    public int getFrameNumber() {
        return frameNumber;
    }
    
}
