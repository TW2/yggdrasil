/*
 * Copyright (C) 2024 util2
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
package org.wingate.ygg.bss;

import java.awt.image.BufferedImage;
import java.util.List;
import org.wingate.ygg.ass.ASS;
import org.wingate.ygg.ass.AssTime;

/**
 * ASS + BSS additions
 * @author util2
 */
public class BssRenderer {

    public BssRenderer() {
    }
    
    // ASS
    public static BufferedImage getAssImage(ASS ass, AssTime t, int imageWidth, int imageHeight){
        // On initialise un ensemble (plusieurs events) et on calcule la position
        // de chaque forme de type caractère ou dessin.
        BssEnsemble ensemble = new BssEnsemble(ass, t, imageWidth, imageHeight);
        List<BssShape> shapes = ensemble.getShapes();
        
        return applyASS(shapes, imageWidth, imageHeight);
    }
    
    // Treatment
    private static BufferedImage applyASS(List<BssShape> shapes, int imageWidth, int imageHeight){
        BssEngine3D be3D = BssEngine3D.createScene(imageWidth, imageHeight, false);
        be3D.apply(shapes);
        be3D.dispose();
        return be3D.getScene();
    }
        
    // BSS
    public static BufferedImage getBssImage(ASS ass, AssTime t, int imageWidth, int imageHeight){
        // On initialise un ensemble (plusieurs events) et on calcule la position
        // de chaque forme de type caractère ou dessin.
        BssEnsemble ensemble = new BssEnsemble(ass, t, imageWidth, imageHeight);
        List<BssShape> shapes = ensemble.getShapes();
        
        return applyBSS(shapes, imageWidth, imageHeight);
    }
    
    private static BufferedImage applyBSS(List<BssShape> shapes, int imageWidth, int imageHeight){
        BssEngine3D be3D = BssEngine3D.createScene(imageWidth, imageHeight, true);
        be3D.apply(shapes);
        be3D.dispose();
        return be3D.getScene();
    }
}
