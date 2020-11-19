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
package yggdrasil.drawing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author util2
 */
public class MouseHelpers {
    
    // Mouse helpers
    private final ImageIcon ADD_POINT = new ImageIcon(
            getClass().getResource("/documents/images/48 mouse_helper_plus addpoint.png"));
    private final ImageIcon MOVE_POINT = new ImageIcon(
            getClass().getResource("/documents/images/48 mouse_helper_plus move.png"));
    private final ImageIcon SELECT_ORIGIN = new ImageIcon(
            getClass().getResource("/documents/images/48 mouse_helper_plus origin.png"));
    private final ImageIcon SELECT_LAST = new ImageIcon(
            getClass().getResource("/documents/images/48 mouse_helper_plus last.png"));

    public MouseHelpers() {
    }
    
    private static BufferedImage getImage(ImageIcon icon){
        BufferedImage img = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = img.createGraphics();
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2d.drawImage(icon.getImage(), 0, 0, null);
        
        g2d.dispose();
        
        return img;
    }
    
    public static BufferedImage getAddPoint(){
        MouseHelpers mh = new MouseHelpers();
        return getImage(mh.ADD_POINT);
    }
    
    public static BufferedImage getMovePoint(){
        MouseHelpers mh = new MouseHelpers();
        return getImage(mh.MOVE_POINT);
    }
    
    public static BufferedImage getSelectOrigin(){
        MouseHelpers mh = new MouseHelpers();
        return getImage(mh.SELECT_ORIGIN);
    }
    
    public static BufferedImage getSelectLast(){
        MouseHelpers mh = new MouseHelpers();
        return getImage(mh.SELECT_LAST);
    }
}
