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
package org.wingate.ygg.ui;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;

/**
 *
 * @author util2
 */
public class ContainersDesktopPane extends JDesktopPane {
    
    public enum ResizeType {
        Horizontal, Vertical;
    }
    
    private ImageIcon wallpaper = null;
    
    private final Matrix matrix;
    private final List<ContainerInternalFrame> containers = new ArrayList<>();

    public ContainersDesktopPane() {
        matrix = new Matrix();
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public List<ContainerInternalFrame> getContainers() {
        return containers;
    }
    
    public void setWallpaper(ImageIcon wallpaper){
        this.wallpaper = wallpaper;
        repaint();
    }
    
    public void addElementAbstract(ElementAbstract ea, String friendlyName){
        ContainerInternalFrame cp = new ContainerInternalFrame(this, ea);
        ea.setupMenu(friendlyName, cp);
        add(cp); // Add to desktop
        containers.add(cp); // Add to list (in order to remove it later)
        changeLocation(ea); // Set size and location according matrix
        cp.setVisible(true); // Set internal frame to visible
    }
    
    public void removeElementAbstract(ElementAbstract ea){
        int index = -1;
        for(int i=0; i<containers.size(); i++){
            // Get element abstract if present
            if(containers.get(i).getElementAbstract() == ea){
                index = i;
                break;
            }
        }
        if(index != -1){
            ContainerInternalFrame cp = containers.get(index); // Get internal frame to remove
            remove(cp); // Remove from desktop
            containers.remove(index); // Remove from list
            updateUI();
        }
    }
    
    public void changeLocation(ElementAbstract ea){
        for(ContainerInternalFrame container : containers){
            if(container.getElementAbstract().equals(ea)){
                int x, y, width = getWidth(), height = getHeight();

                int lastWCase = ea.getLastWCase();
                int firstWCase = ea.getFirstWCase();
                int caseWidth = matrix.getWCases();

                int lastHCase = ea.getLastHCase();
                int firstHCase = ea.getFirstHCase();
                int caseHeight = matrix.getHCases();

                int partWidth = width / caseWidth;
                x = firstWCase * partWidth;
                width = (lastWCase - firstWCase) * partWidth;

                int partHeight = height / caseHeight;
                y = firstHCase * partHeight;
                height = (lastHCase - firstHCase) * partHeight;

                container.setLocation(x, y);
                container.setSize(width, height);
                break;
            }
        }        
    }
    
    public void increaseBy(ElementAbstract ea, int unit, ResizeType rt){
        if(unit < 1 || unit > 35) return;
        int firstW = ea.getFirstWCase();
        int firstH = ea.getFirstHCase();
        int lastW = ea.getLastWCase();
        int lastH = ea.getLastHCase();
        switch(rt){
            case Horizontal -> {
                int w = lastW - firstW; w+=unit;
                if(firstW + w > matrix.getWCases()) w = 1;
                if(matrix.getWCases() >= firstW + w){
                    ea.setLastWCase(firstW + w);
                    changeLocation(ea);
                }
            }
            case Vertical -> {
                int h = lastH - firstH; h+=unit;
                if(firstH + h > matrix.getHCases()) h = 1;
                if(matrix.getHCases() >= firstH + h){
                    ea.setLastHCase(firstH + h);
                    changeLocation(ea);
                }
            }
            default -> {
                // Ignore
            }
        }
    }
    
    public void decreaseBy(ElementAbstract ea, int unit, ResizeType rt){
        if(unit < 1 || unit > 35) return;
        int firstW = ea.getFirstWCase();
        int firstH = ea.getFirstHCase();
        int lastW = ea.getLastWCase();
        int lastH = ea.getLastHCase();
        switch(rt){
            case Horizontal -> {
                int w = lastW - firstW; w-=unit;
                if(firstW - w < 1) w = matrix.getMaxWCases();
                if(matrix.getWCases() <= firstW - w){
                    ea.setLastWCase(firstW - w);
                    changeLocation(ea);
                }
            }
            case Vertical -> {
                int h = lastH - firstH; h-=unit;
                if(firstH - h < 1) h = matrix.getMaxHCases();
                if(matrix.getHCases() <= firstH - h){
                    ea.setLastHCase(firstH - h);
                    changeLocation(ea);
                }
            }
            default -> {
                // Ignore
            }
        }
    }
    
    public void toLeft(ElementAbstract ea){
        int firstW = ea.getFirstWCase();
        int lastW = ea.getLastWCase();
        int leftFirstW = firstW - 1 < 0 ? 0 : firstW - 1;
        int leftLastW = leftFirstW != firstW ? lastW - 1 : lastW;
        if(leftFirstW >= 0){
            ea.setFirstWCase(leftFirstW);
            ea.setLastWCase(leftLastW);
            changeLocation(ea);
        }
    }
    
    public void toRight(ElementAbstract ea){
        int firstW = ea.getFirstWCase();
        int lastW = ea.getLastWCase();
        int rightFirstW = firstW + 1 > matrix.getWCases() - 1 ? matrix.getWCases() - 1 : firstW + 1;
        int rightLastW = rightFirstW != firstW ? lastW + 1 : lastW;
        if(rightLastW <= matrix.getWCases()){
            ea.setFirstWCase(rightFirstW);
            ea.setLastWCase(rightLastW);
            changeLocation(ea);
        }
    }
    
    public void toUp(ElementAbstract ea){
        int firstH = ea.getFirstHCase();
        int lastH = ea.getLastHCase();
        int leftFirstH = firstH - 1 < 0 ? 0 : firstH - 1;
        int leftLastH = leftFirstH != firstH ? lastH - 1 : lastH;
        if(leftFirstH >= 0){
            ea.setFirstHCase(leftFirstH);
            ea.setLastHCase(leftLastH);
            changeLocation(ea);
        }
    }
    
    public void toDown(ElementAbstract ea){
        int firstH = ea.getFirstHCase();
        int lastH = ea.getLastHCase();
        int rightFirstH = firstH + 1 > matrix.getHCases() - 1 ? matrix.getHCases() - 1 : firstH + 1;
        int rightLastH = rightFirstH != firstH ? lastH + 1 : lastH;
        if(rightLastH <= matrix.getHCases()){
            ea.setFirstHCase(rightFirstH);
            ea.setLastHCase(rightLastH);
            changeLocation(ea);
        }
    }
    
    public void toCorner7(ElementAbstract ea){
        int lastWCase = ea.getLastWCase();
        int firstWCase = ea.getFirstWCase();
        int lastHCase = ea.getLastHCase();
        int firstHCase = ea.getFirstHCase();

        int eW = lastWCase - firstWCase;
        int eH = lastHCase - firstHCase;        
        
        ea.setFirstWCase(0); ea.setLastWCase(firstWCase + eW); // X
        ea.setFirstHCase(0); ea.setLastHCase(firstHCase + eH); // Y
        changeLocation(ea);
    }
    
    public void toCorner1(ElementAbstract ea){
        int lastWCase = ea.getLastWCase();
        int firstWCase = ea.getFirstWCase();
        int lastHCase = ea.getLastHCase();
        int firstHCase = ea.getFirstHCase();

        int eW = lastWCase - firstWCase;
        int eH = lastHCase - firstHCase;
        
        ea.setFirstWCase(0); ea.setLastWCase(firstWCase + eW); // X
        ea.setFirstHCase(lastHCase - eH); ea.setLastHCase(matrix.getHCases()); // Y
        changeLocation(ea);
    }
    
    public void toCorner9(ElementAbstract ea){
        int lastWCase = ea.getLastWCase();
        int firstWCase = ea.getFirstWCase();
        int lastHCase = ea.getLastHCase();
        int firstHCase = ea.getFirstHCase();

        int eW = lastWCase - firstWCase;
        int eH = lastHCase - firstHCase;

        ea.setFirstWCase(lastWCase - eW); ea.setLastWCase(matrix.getWCases()); // X
        ea.setFirstHCase(0); ea.setLastHCase(firstHCase + eH); // Y
        changeLocation(ea);
    }
    
    public void toCorner3(ElementAbstract ea){
        int lastWCase = ea.getLastWCase();
        int firstWCase = ea.getFirstWCase();
        int lastHCase = ea.getLastHCase();
        int firstHCase = ea.getFirstHCase();

        int eW = lastWCase - firstWCase;
        int eH = lastHCase - firstHCase;

        ea.setFirstWCase(lastWCase - eW); ea.setLastWCase(matrix.getWCases()); // X
        ea.setFirstHCase(lastHCase - eH); ea.setLastHCase(matrix.getHCases()); // Y
        changeLocation(ea);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if(wallpaper != null){
            g.drawImage(wallpaper.getImage(), 0, 0, getWidth(), getHeight(), null);
        }
    }
    
}
