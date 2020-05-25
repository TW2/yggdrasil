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
package org.wingate.ygg.base;

import java.io.File;
import org.wingate.ygg.ass.ASS;
import org.wingate.ygg.ass.Event;
import org.wingate.ygg.ifrm.*;
import org.wingate.ygg.ui.AudioWave;
import org.wingate.ygg.ui.FramesPanel;
import org.wingate.ygg.util.FFStuffs;
import org.wingate.ygg.util.Time;

/**
 *
 * @author util2
 */
public class AVStudio {
    
    // Video
    FramesPanel fp = new FramesPanel();
    private File video = null;
    private ASS ass = null;
    
    // Wave
    FFStuffs ffss = null;
    AudioWave aw = null;
    
    private FrmWave frmWave;
    private FrmVideo frmVideo;
    private FrmTable frmTable;
    private FrmSubCommand frmSC;
    
    boolean dark;
    
    public AVStudio(boolean dark){
        this.dark = dark;
        init();
    }
    
    private void init(){
        frmWave = new FrmWave(this);
        frmVideo = new FrmVideo(this);
        frmTable = new FrmTable(this);
        frmSC = new FrmSubCommand(this);
    }

    public FrmWave getFrmWave() {
        return frmWave;
    }

    public void setFrmWave(FrmWave frmWave) {
        this.frmWave = frmWave;
    }

    public FrmVideo getFrmVideo() {
        return frmVideo;
    }

    public void setFrmVideo(FrmVideo frmVideo) {
        this.frmVideo = frmVideo;
    }

    public FrmTable getFrmTable() {
        return frmTable;
    }

    public void setFrmTable(FrmTable frmTable) {
        this.frmTable = frmTable;
    }

    public FrmSubCommand getFrmSC() {
        return frmSC;
    }

    public void setFrmSC(FrmSubCommand frmSC) {
        this.frmSC = frmSC;
    }
    
    public boolean isDark() {
        return dark;
    }

    public FramesPanel getFp() {
        return fp;
    }

    public void setFp(FramesPanel fp) {
        this.fp = fp;
    }

    public File getVideo() {
        return video;
    }

    public void setVideo(File video) {
        this.video = video;
    }

    public ASS getAss() {
        return ass;
    }

    public void setAss(ASS ass) {
        this.ass = ass;
    }

    public FFStuffs getFfss() {
        return ffss;
    }

    public void setFfss(FFStuffs ffss) {
        this.ffss = ffss;
    }

    public AudioWave getAw() {
        return aw;
    }

    public void setAw(AudioWave aw) {
        this.aw = aw;
    }
    
    //==========================================================================
    // Méthodes ----------------------------------------------------------------
    //--------------------------------------------------------------------------

    public void configure(File video, File assFile){
        // On ouvre
        frmTable.loadASSTable(assFile);
        frmVideo.openAudioVideo(video);
        frmWave.setFile(video);
        frmSC.reinit();
    }
    
    public void tableToCommand(Event ev){
        frmSC.alter(ev);
    }
    
    public void commandToTable(Event ev, boolean alter){
        if(alter == true){
            frmTable.alter(ev);
        }else{
            frmTable.add(ev);
        }
    }
    
    public void waveToCommand(Time start, Time end, boolean alter){
        frmSC.changeTime(start, end, !alter);
    }
    
    public void updateASS(File assFile){
        frmVideo.openASS(assFile);
    }
}
