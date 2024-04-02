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
package org.wingate.ygg.ui.table;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.wingate.ygg.ass.ASS;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.theme.Theme;
import org.wingate.ygg.ui.ContainerPanel;
import org.wingate.ygg.ui.ElementAbstract;

/**
 *
 * @author util2
 */
public class AssTableElement extends ElementAbstract<AssTablePanel> {
    
    private String lastSavePath = null;
    
    public AssTableElement(Theme theme) {
        name = "ASSA Table";
        panel = new AssTablePanel(theme);
        panel.resetColumnWidth();
    }

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public void setupMenu(String friendlyName, ContainerPanel cp){
        ImageIcon iiNewDoc = new ImageIcon(getClass().getResource("/images/16 newdocument.png"));
        ImageIcon iiOpenDoc = new ImageIcon(getClass().getResource("/images/16 folder.png"));
        ImageIcon iiSaveDoc = new ImageIcon(getClass().getResource("/images/16 floppydisk.png"));
        ImageIcon iiCorner1 = new ImageIcon(getClass().getResource("/images/16 corner 1.png"));
        ImageIcon iiCorner3 = new ImageIcon(getClass().getResource("/images/16 corner 3.png"));
        ImageIcon iiCorner9 = new ImageIcon(getClass().getResource("/images/16 corner 9.png"));
        ImageIcon iiCorner7 = new ImageIcon(getClass().getResource("/images/16 corner 7.png"));
        ImageIcon iiClose = new ImageIcon(getClass().getResource("/images/16 cross-small.png"));
        
        menu = new JMenu();
        menu.setText(String.format("%s (%s)", name, friendlyName));
        
        JMenuItem miNewDoc = new JMenuItem("New empty document");
        miNewDoc.addActionListener((listener)->{
            panel.getModel().createNew();           // Setup
            panel.getTable().updateUI();
        });
        miNewDoc.setIcon(iiNewDoc);
        menu.add(miNewDoc);
        
        JMenuItem miOpenDoc = new JMenuItem("Open document...");
        miOpenDoc.addActionListener((listener)->{
            panel.getModel().clear();               // Clean
            int z = panel.getOpenFileChooser().showOpenDialog(panel);
            if(z == JFileChooser.APPROVE_OPTION){
                String path = panel.getOpenFileChooser().getSelectedFile().getPath();
                ASS ass = ASS.Read(path);
                panel.getModel().setAss(ass);
                panel.getModel().setLanguages(ass.getLanguages());
                panel.getTable().updateUI();
            }
        });
        miOpenDoc.setIcon(iiOpenDoc);
        menu.add(miOpenDoc);
        
        JMenuItem miSaveDocAs = new JMenuItem("Save document as...");
        miSaveDocAs.addActionListener((listener)->{
            int z = panel.getSaveFileChooser().showSaveDialog(panel);
            if(z == JFileChooser.APPROVE_OPTION){
                String path = panel.getSaveFileChooser().getSelectedFile().getPath();
                if(path.endsWith(".ass") == false) path += ".ass";
                lastSavePath = path;
                ASS ass = panel.getModel().getAss();
                ass.setLanguages(panel.getModel().getLanguages());
                ASS.Save(path, ass);
            }
        });
        miSaveDocAs.setIcon(iiSaveDoc);
        menu.add(miSaveDocAs);
        
        JMenuItem miSaveDoc = new JMenuItem("Save document");
        miSaveDoc.addActionListener((listener)->{
            if(lastSavePath == null){
                int z = panel.getSaveFileChooser().showSaveDialog(panel);
                if(z == JFileChooser.APPROVE_OPTION){
                    String path = panel.getSaveFileChooser().getSelectedFile().getPath();
                    if(path.endsWith(".ass") == false) path += ".ass";
                    lastSavePath = path;
                    ASS ass = panel.getModel().getAss();
                    ass.setLanguages(panel.getModel().getLanguages());
                    ASS.Save(path, ass);
                }
            }else{                
                ASS ass = panel.getModel().getAss();
                ass.setLanguages(panel.getModel().getLanguages());
                ASS.Save(lastSavePath, ass);
            }            
        });
        miSaveDoc.setIcon(iiSaveDoc);
        menu.add(miSaveDoc);
        
        menu.add(new JSeparator());
        
        JMenuItem miToLeftTop = new JMenuItem("Put to the corner 7");
        miToLeftTop.addActionListener((listener)->{
            cp.getMainFrame().shiftMax(7, cp);
            cp.getElementAbstract().setCorner(7);
        });
        miToLeftTop.setIcon(iiCorner7);
        menu.add(miToLeftTop);
        
        JMenuItem miToRightTop = new JMenuItem("Put to the corner 9");
        miToRightTop.addActionListener((listener)->{
            cp.getMainFrame().shiftMax(9, cp);
            cp.getElementAbstract().setCorner(9);
        });
        miToRightTop.setIcon(iiCorner9);
        menu.add(miToRightTop);
        
        JMenuItem miToRightBottom = new JMenuItem("Put to the corner 3");
        miToRightBottom.addActionListener((listener)->{
            cp.getMainFrame().shiftMax(3, cp);
            cp.getElementAbstract().setCorner(3);
        });
        miToRightBottom.setIcon(iiCorner3);
        menu.add(miToRightBottom);
        
        JMenuItem miToLeftBottom = new JMenuItem("Put to the corner 1");
        miToLeftBottom.addActionListener((listener)->{
            cp.getMainFrame().shiftMax(1, cp);
            cp.getElementAbstract().setCorner(1);
        });
        miToLeftBottom.setIcon(iiCorner1);
        menu.add(miToLeftBottom);
        
        menu.add(new JSeparator());
        
        JMenuItem miToLeft = new JMenuItem("Left by 1");
        miToLeft.addActionListener((listener)->{
            cp.getMainFrame().shift(MainFrame.ShiftDirection.Left, cp);
        });
        menu.add(miToLeft);
        
        JMenuItem miToRight = new JMenuItem("Right by 1");
        miToRight.addActionListener((listener)->{
            cp.getMainFrame().shift(MainFrame.ShiftDirection.Right, cp);
        });
        menu.add(miToRight);
        
        JMenuItem miToTop = new JMenuItem("Top by 1");
        miToTop.addActionListener((listener)->{
            cp.getMainFrame().shift(MainFrame.ShiftDirection.Top, cp);
        });
        menu.add(miToTop);
        
        JMenuItem miToBottom = new JMenuItem("Bottom by 1");
        miToBottom.addActionListener((listener)->{
            cp.getMainFrame().shift(MainFrame.ShiftDirection.Bottom, cp);
        });
        menu.add(miToBottom);
        
        menu.add(new JSeparator());
        
        JCheckBoxMenuItem miHide = new JCheckBoxMenuItem("Hide");
        miHide.setSelected(false);
        miHide.addActionListener((listener)->{
            cp.setVisible(!miHide.isSelected());
        });
        menu.add(miHide);
        
        menu.add(new JSeparator());
        
        JMenuItem miClose = new JMenuItem("Close element");
        miClose.addActionListener((listener)->{
            cp.setVisible(false);
            cp.getMainFrame().removeContainerPanel(cp);
        });
        miClose.setIcon(iiClose);
        menu.add(miClose);
    }
}
