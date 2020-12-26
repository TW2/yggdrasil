/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ztx;

/**
 *
 * @author util2
 */
public interface IAudioVideoListener {
    
    public void audioEnded();
    public void videoEnded();
    public void videoImageChanged(ImageEvent event);
    public void audioStopReached();
    public void videoStopReached();
    
    public void videoTimeElapsed(double ms);
}
