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
package org.wingate.ygg;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

/**
 *
 * @author util2
 */
public class Yggdrasil {
    public static void main(String[] args) {
        System.out.println("----- YGGDRASIL -----");
        System.out.println("--- FUNSUB PROJECT --");
        System.out.println("------- 2020 --------");
        System.out.println("-------- TW2 --------");        
        
        boolean dark = args.length > 0 && args[0].equalsIgnoreCase("dark");
        
        if(dark){
            FlatDarkLaf.install();
        }else{
            FlatLightLaf.install();
        }
        
        MainFrame mf = new MainFrame(dark);
        mf.setVisible(true);
        
        if(dark){
            System.out.println("---- HAPPY RUMBA ----");
        }else{
            System.out.println("---- HAPPY FESTA ----");
        }
        //======================================================================
        // TEST (activate or deactivate)
        //======================================================================
//        org.wingate.ygg.test.TestVideoTimestamp frame = new org.wingate.ygg.test.TestVideoTimestamp();
//        frame.setVisible(true);
    }
}
