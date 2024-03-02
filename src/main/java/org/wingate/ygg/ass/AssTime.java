/*
 * Copyright (C) 2022 util2
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
package org.wingate.ygg.ass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author util2
 */
public class AssTime {
    
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private int milliseconds = 0;

    /**
     * Create a new not initialized Time 
     */
    public AssTime(){

    }
    
    /**
     * Create a new Time
     * @param hours hours only
     * @param minutes minutes only
     * @param seconds seconds only
     * @param milliseconds milliseconds only
     * @return A Time object
     */
    public static AssTime create(int hours, int minutes, int seconds, int milliseconds){
        AssTime t = new AssTime();
        t.hours = hours;
        t.minutes = minutes;
        t.seconds = seconds;
        t.milliseconds = milliseconds;
        return t;
    }
    
    /**
     * Create a new Time
     * @param milliseconds milliseconds
     * @return A Time object
     */
    public static AssTime create(long milliseconds){
        AssTime t = AssTime.fromMillisecondsTime(milliseconds);
        return t;
    }
    
    /**
     * Create a new Time
     * @param strTime from ASS or SRT
     * @return A Time object
     */
    public static AssTime create(String strTime){
        Pattern p = Pattern.compile("(\\d+)[^\\d]+(\\d+)[^\\d]+(\\d+)[^\\d]+(\\d+)");
        Matcher m = p.matcher(strTime);
        if(m.matches()){            
            return AssTime.create(
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)),
                    Integer.parseInt(m.group(3)),
                    Integer.parseInt(m.group(4).length() == 2 ? m.group(4) + "0" : m.group(4)));
        }else{
            return AssTime.create(0L);
        }        
    }

    // <editor-fold defaultstate="collapsed" desc=" get/set Time components ">

    /**
     * Get the hours.
     * @return hours only
     */
    public int getHours(){
        return hours;
    }

    /**
     * Get the minutes.
     * @return minutes only
     */
    public int getMinutes(){
        return minutes;
    }

    /**
     * Get the seconds.
     * @return seconds only
     */
    public int getSeconds(){
        return seconds;
    }

    /**
     * Get the milliseconds.
     * @return milliseconds only
     */
    public int getMilliseconds(){
        return milliseconds;
    }

    /**
     * Set the hours.
     * @param hours hours only
     */
    public void setHours(int hours){
        this.hours = hours;
    }

    /**
     * Set the minutes.
     * @param minutes minutes only
     */
    public void setMinutes(int minutes){
        this.minutes = minutes;
    }

    /**
     * Set the seconds.
     * @param seconds seconds only
     */
    public void setSeconds(int seconds){
        this.seconds = seconds;
    }

    /**
     * Set the milliseconds.
     * @param milliseconds milliseconds only
     */
    public void setMilliseconds(int milliseconds){
        this.milliseconds = milliseconds;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Addition / Substraction ">

    /**
     * Add Time t1 to Time t2.
     * @param t1 A time object
     * @param t2 Another time object
     * @return Result as t1 + t2
     */
    public static AssTime addition(AssTime t1, AssTime t2){
        AssTime t;

        long lt1 = toMillisecondsTime(t1);
        long lt2 = toMillisecondsTime(t2);

        long lt = lt1 + lt2;

        t = fromMillisecondsTime(lt);

        return t;
    }

    /**
     * Substract Time t2 to Time t1.
     * @param t1 A time object
     * @param t2 Another time object
     * @return Result as t2 - t1
     */
    public static AssTime substract(AssTime t1, AssTime t2){
        AssTime t;

        long lt1 = toMillisecondsTime(t1);
        long lt2 = toMillisecondsTime(t2);

        long lt = Math.max(lt2, lt1) - Math.min(lt1, lt2);

        t = fromMillisecondsTime(lt);

        return t;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Multiply / Divide ">

    /**
     * Multiply Time t1 and Time t2.
     * @param t1 A time object
     * @param t2 Another time object
     * @return Result as t1 * t2
     */
    public static AssTime multiply(AssTime t1, AssTime t2){
        AssTime t;

        long lt1 = toMillisecondsTime(t1);
        long lt2 = toMillisecondsTime(t2);

        long lt = lt1 * lt2;

        t = fromMillisecondsTime(lt);

        return t;
    }

    /**
     * Divide Time t1 to Time t2.
     * @param t1 A time object
     * @param t2 Another time object
     * @return Result as t1 / t2 where t2 != 0 otherwise t1 is returned
     */
    public static AssTime divide(AssTime t1, AssTime t2){
        AssTime t;

        long lt1 = toMillisecondsTime(t1);
        long lt2 = toMillisecondsTime(t2);

        long lt = lt2 != 0 ? lt1 / lt2 : lt1;

        t = fromMillisecondsTime(lt);

        return t;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Conversion Time <> milliseconds ">

    /**
     * Convert Time object to milliseconds.
     * @param t A time object
     * @return milliseconds
     */
    public static long toMillisecondsTime(AssTime t){
        long mst;

        mst = t.getHours()*3600000
                + t.getMinutes()*60000
                + t.getSeconds()*1000
                + t.getMilliseconds();

        return mst;
    }

    /**
     * Convert milliseconds to Time object.
     * @param mst milliseconds
     * @return A time object
     */
    public static AssTime fromMillisecondsTime(long mst){
        AssTime t = new AssTime();

        int hour = (int)(mst / 3600000);
        int min = (int)((mst - 3600000 * hour) / 60000);
        int sec = (int)((mst - 3600000 * hour - 60000 * min) / 1000);
        int mSec = (int)(mst - 3600000 * hour - 60000 * min - 1000 * sec);

        t.setHours(hour);
        t.setMinutes(min);
        t.setSeconds(sec);
        t.setMilliseconds(mSec);

        return t;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Boolean ">
    
    public static boolean isSame(AssTime ref, AssTime... times){
        for (AssTime t : times) {
            if(ref.hours != t.hours) return false;
            if(ref.minutes != t.minutes) return false;
            if(ref.seconds != t.seconds) return false;
            if(ref.milliseconds != t.milliseconds) return false;
        }
        return true;
    }
    
    public static boolean isGreater(AssTime ref, AssTime t){
        return AssTime.toMillisecondsTime(AssTime.substract(ref, t)) > 0;
    }
    
    public static boolean isSmaller(AssTime ref, AssTime t){
        return AssTime.toMillisecondsTime(AssTime.substract(ref, t)) < 0;
    }
    
    public static boolean isEqual(AssTime ref, AssTime t){
        return AssTime.isSame(ref, t);
    }
    
    // </editor-fold>

    /**
     * Get Time.
     * @return A time object
     */
    public AssTime getTime(){
        return AssTime.create(getHours(), getMinutes(), getSeconds(), getMilliseconds());
    }

    /**
     * Set Time.
     * @param t A time object
     */
    public void setTime(AssTime t){
        this.hours = t.getHours();
        this.minutes = t.getMinutes();
        this.seconds = t.getSeconds();
        this.milliseconds = t.getMilliseconds();
    }

    /**
     * Get Time in ASS format.
     * @return A formatted time as "0:00:00.00"
     */
    public String toASSTime(){
        String Smin, Ssec, Scent;

        int hour = getHours();
        int min = getMinutes();
        int sec = getSeconds();
        int cSec = getMilliseconds()/10;

        if (min<10){Smin = "0"+min;}else{Smin = String.valueOf(min);}
        if (sec<10){Ssec = "0"+sec;}else{Ssec = String.valueOf(sec);}
        if (cSec<10){Scent = "0"+cSec;}else{Scent = String.valueOf(cSec);}

        return hour + ":" + Smin + ":" + Ssec + "." + Scent;
    }

    /**
     * Get Time in Program based ASS format.
     * @return A formatted time as "0.00.00.00"
     */
    public String toProgramBasedASSTime(){
        String Smin, Ssec, Scent;

        int hour = getHours();
        int min = getMinutes();
        int sec = getSeconds();
        int cSec = getMilliseconds()/10;

        if (min<10){Smin = "0"+min;}else{Smin = String.valueOf(min);}
        if (sec<10){Ssec = "0"+sec;}else{Ssec = String.valueOf(sec);}
        if (cSec<10){Scent = "0"+cSec;}else{Scent = String.valueOf(cSec);}

        return hour + "." + Smin + "." + Ssec + "." + Scent;
    }

    /**
     * Get Time in Program extended format.
     * @return A formatted time as "0.00.00.000"
     */
    public String toProgramExtendedTime(){
        String Smin, Ssec, Smilli;

        int hour = getHours();
        int min = getMinutes();
        int sec = getSeconds();
        int mSec = getMilliseconds();

        if (min<10){Smin = "0"+min;}else{Smin = String.valueOf(min);}
        if (sec<10){Ssec = "0"+sec;}else{Ssec = String.valueOf(sec);}
        if (mSec<10){
            Smilli = "00"+mSec;
        }else if (mSec<100){
            Smilli = "0"+mSec;
        }else{
            Smilli = String.valueOf(mSec);
        }

        return hour + "." + Smin + "." + Ssec + "." + Smilli;
    }
    
    /**
     * Get Time in Display format.
     * @return A formatted time as "0h00m00s000c"
     */
    public String toDisplayTime(){
        String Smin, Ssec, Smilli;

        int hour = getHours();
        int min = getMinutes();
        int sec = getSeconds();
        int mSec = getMilliseconds();

        if (min<10){Smin = "0"+min;}else{Smin = String.valueOf(min);}
        if (sec<10){Ssec = "0"+sec;}else{Ssec = String.valueOf(sec);}
        if (mSec<10){
            Smilli = "00"+mSec;
        }else if (mSec<100){
            Smilli = "0"+mSec;
        }else{
            Smilli = String.valueOf(mSec);
        }

        return hour + "h " + Smin + "m " + Ssec + "s " + Smilli + "ms";
    }
    
    /**
     * Get Time in Film Display format.
     * @return A formatted time as "00h00m00s"
     */
    public String toFilmDisplayTime(){
        String Shour, Smin, Ssec;

        int hour = getHours();
        int min = getMinutes();
        int sec = getSeconds();

        if (hour<10){Shour = "0"+hour;}else{Shour = String.valueOf(hour);}
        if (min<10){Smin = "0"+min;}else{Smin = String.valueOf(min);}
        if (sec<10){Ssec = "0"+sec;}else{Ssec = String.valueOf(sec);}

        return Shour + "h " + Smin + "m " + Ssec + "s";
    }
    
    /**
     * Get Time in FFMpeg format.
     * @return A formatted time as "00:00:00.000"
     */
    public String toFFMpegTime(){
        String Shour, Smin, Ssec, Smilli;

        int hour = getHours();
        int min = getMinutes();
        int sec = getSeconds();
        int mSec = getMilliseconds();

        if (hour<10){Shour = "0"+hour;}else{Shour = String.valueOf(hour);}
        if (min<10){Smin = "0"+min;}else{Smin = String.valueOf(min);}
        if (sec<10){Ssec = "0"+sec;}else{Ssec = String.valueOf(sec);}
        if (mSec<10){
            Smilli = "00"+mSec;
        }else if (mSec<100){
            Smilli = "0"+mSec;
        }else{
            Smilli = String.valueOf(mSec);
        }

        return Shour + ":" + Smin + ":" + Ssec + "." + Smilli;
    }
    
    /**
     * Get Time in SRT format.
     * @return A formatted time as "0:00:00,000"
     */
    public String toSRTTime(){
        String h, m, s, ms;

        int hour = getHours();
        int min = getMinutes();
        int sec = getSeconds();
        int mSec = getMilliseconds();

        // Hours 01 or 21 (2 digits)
        h = hour < 10 ? "0" + hour : Integer.toString(hour);
        // Minutes 01 or 21 (2 digits)
        m = min < 10 ? "0" + min : Integer.toString(min);
        // Seconds 01 or 21 (2 digits)
        s = sec < 10 ? "0" + sec : Integer.toString(sec);        
        // Milliseconds 001 or 021 or 721 (3 digits)
        ms = mSec < 10 ? "00" + mSec : (mSec < 100 ? "0" + mSec : Integer.toString(mSec));

        return h + ":" + m + ":" + s + "," + ms;
    }
    
    public static int getFrame(AssTime t, double fps){
        double timeInSeconds = AssTime.toMillisecondsTime(t) / 1000d;
        return (int)(timeInSeconds * fps);
    }
    
    public static AssTime getTimeFromFrame(int frame, double fps){
        double msec = frame / fps * 1000;
        return AssTime.create(Math.round(msec));
    }
    
    public static AssTime getTimeFromSamples(double samplerate, double samplesPerPixel, long pixels){
        // The formula is simple:
        // Milliseconds times the sample rate = # of samples    
        // example:
        // 17 times 44.1 = 749.7 samples
        
        // Get the number of samples
        // nbSamples = pixels * samplesPerPixel
        double nbSamples = pixels * samplesPerPixel;
        // Get time
        // ms = nbSamples / samplerate in kHz
        double msec = nbSamples / samplerate;
        // Return time
        return AssTime.create(Math.round(msec));
    }
    
    public static long getLengthInMilliseconds(AssTime t){
        return AssTime.toMillisecondsTime(t);
    }
    
    public static double getLengthInSeconds(AssTime t){
        long ms = AssTime.toMillisecondsTime(t);
        double s = ms/1000d;
        return s;
    }
    
    public static double getWordsPerMinutes(AssTime t, int words){
        long ms = AssTime.toMillisecondsTime(t);
        double min = ms/1000000d;
        return min <= 0 ? words : words / min;
    }
}
