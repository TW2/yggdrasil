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
package org.wingate.ygg.subs.ass.tool;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 *
 * @author util2
 */
public class AssYGGY {
    
    private YggyLibrary yggy = null;
    private String toolsPath = null;
    
    public interface YggyLibrary extends Library {
        YggyLibrary INSTANCE = (YggyLibrary)Native.load("yggy", YggyLibrary.class);
        
        int executor(String pngpath, String asspath, String asstime, int width, int height);
    }
    
    private AssYGGY(){
        init();
    }
    
    private void init(){
        toolsPath =  getClass().getResource("/ass").getPath();
    }
    
    public static AssYGGY create(){
        AssYGGY y = new AssYGGY();
        
        System.setProperty("jna.library.path", y.toolsPath);
        y.yggy = YggyLibrary.INSTANCE;
        
        return y;
    }

    public YggyLibrary getYggy() {
        return yggy;
    }
}
