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
package ygg.io.audio;

import org.wingate.timelibrary.Time;

/**
 *
 * @author util2
 */
public class AudioWaveScale {
    double pixels = 400;
    double milliseconds = 1000;

    public AudioWaveScale() {
    }

    public double getPixels() {
        return pixels;
    }

    public void setPixels(double pixels) {
        this.pixels = pixels;
    }

    public double getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(double milliseconds) {
        this.milliseconds = milliseconds;
    }

    public int getWidthFrom(Time start, Time end){
        Time delta = Time.substract(start, end);
        double ratio = pixels / milliseconds;
        return (int)(Math.round(Time.toMillisecondsTime(delta) * ratio));
    }

    public int getXFrom(Time t){
        double ratio = pixels / milliseconds;
        return (int)(Math.round(Time.toMillisecondsTime(t) * ratio));
    }

    public Time getTimeFrom(int pixelsOnWaveForm){
        double ratio = pixels / milliseconds;
        return Time.fromMillisecondsTime(Math.round(pixelsOnWaveForm / ratio));
    }
}
