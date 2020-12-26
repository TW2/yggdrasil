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
public class AudioVideoAdapter extends AudioVideoListener {

    @Override
    public void audioEnded() { }

    @Override
    public void videoEnded() { }

    @Override
    public void videoImageChanged(ImageEvent event) { }

    @Override
    public void audioStopReached() { }

    @Override
    public void videoStopReached() { }

    @Override
    public void videoTimeElapsed(double ms) { }
    
}
