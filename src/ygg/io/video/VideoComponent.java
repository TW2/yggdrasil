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
package ygg.io.video;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import ygg.util.DrawColor;

/**
 *
 * @author util2
 */
public class VideoComponent extends JPanel {

    private BufferedImage img = null;
    
    public VideoComponent() {
        setDoubleBuffered(true);
    }
    
    public void updateDrawing(BufferedImage img){
        this.img = img;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(img != null){
            g.setColor(DrawColor.alice_blue.getColor());
            g.fillRect(0, 0, getWidth(), getHeight());
            
            Dimension dim = getScaledDimension(
                    new Dimension(img.getWidth(), img.getHeight()), 
                    new Dimension(getWidth(), getHeight())
            );
            
            int x = (getWidth() - dim.width) / 2;
            int y = (getHeight() - dim.height) / 2;
            g.drawImage(img, x, y, dim.width, dim.height, null);
        }
    }
    
    private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
    
}
