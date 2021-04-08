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
package org.wingate.ygg.subs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.wingate.ygg.subs.ass.ASS;
import org.wingate.ygg.subs.ssb.SSB;
import org.wingate.ygg.ui.synctable.AssLinkPanel;
import org.wingate.ygg.ui.synctable.AssSynchroTable;
import org.wingate.ygg.ui.synctable.SsbLinkPanel;
import org.wingate.ygg.ui.synctable.SsbSynchroTable;

/**
 *
 * @author util2
 */
public class SubsData {
    
    private List<Object> subs = new ArrayList<>();

    public SubsData() {
    }

    public List<Object> getSubs() {
        return subs;
    }

    public void setSubs(List<Object> subs) {
        this.subs = subs;
    }
    
    public Object forName(String filename){
        Object o = null;
        
        for (Object obj : subs){
            if(obj instanceof ASS){
                ASS ass = (ASS)obj;
                if(ass.getAssFile().getName().equalsIgnoreCase(filename)){
                    o = obj;
                    break;
                }
            }else if(obj instanceof SSB){
                SSB ssb = (SSB)obj;
                if(ssb.getSsbFile().getName().equalsIgnoreCase(filename)){
                    o = obj;
                    break;
                }
            }
        }
        return o;
    }
    
    public void newASS(File file, AssLinkPanel tableLink, AssSynchroTable table){
        ASS ass = ASS.NoFileToLoad();
        ass.setAssFile(file);
    }
    
    public void newSSB(File file, SsbLinkPanel tableLink, SsbSynchroTable table){
        
    }
    
    public void oldASS(ASS ass, AssLinkPanel tableLink, AssSynchroTable table){
        
    }
    
    public void oldSSB(SSB ssb, SsbLinkPanel tableLink, SsbSynchroTable table){
        
    }
}
