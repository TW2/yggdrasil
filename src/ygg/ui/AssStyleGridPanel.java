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
package ygg.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import ygg.util.DrawColor;

/**
 *
 * @author util2
 */
public class AssStyleGridPanel extends JPanel {
    
    // [ALPHABET];[alphabet];[numbers];
    private String test = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\\N"
            + "abcdefghijklmnopqrstuvwxyz\\N"
            + "0123456789";
    private Color contrast = Color.gray;
    private int gridSize = 10;
    private BufferedImage img = null;
    
    public void setBufferedImage(BufferedImage img){
        this.img = img;
        repaint();
    }
    
    public AssStyleGridPanel(Color contrast) {
        setDoubleBuffered(true);
        if(contrast != null){
            this.contrast = contrast;
        }
    }

    public Color getContrast() {
        return contrast;
    }

    public void setContrast(Color contrast) {
        this.contrast = contrast;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        g.setColor(DrawColor.corn_flower_blue.getColor());
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(DrawColor.alice_blue.getColor());
        
        
        int x = 1, y = 1;
        boolean grid = false;
        while(y <= getHeight()){
            while(x <= getWidth()){
                g.fillRect(x, y, gridSize, gridSize);
                x += gridSize * 2;
            }
            y += gridSize;
            x = grid == false ? 1 + gridSize : 1;
            grid = !grid;
        }
        
        if(img != null){
            g.drawImage(img, 0, 0, null);
        }
    }
    
}
