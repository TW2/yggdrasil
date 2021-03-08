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
package org.wingate.ygg.subs.ssb.tool;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.subs.ssb.SsbEvent;
import org.wingate.ygg.subs.ssb.SsbEventType;
import org.wingate.ygg.util.DrawColor;

/**
 *
 * @author util2
 */
public class SsbEventTableRenderer extends JPanel implements TableCellRenderer {
    
    private JLabel content_1 = new JLabel();
    private JLabel content_2 = new JLabel();

    private boolean dark = false;
    
    public enum Colors{
        Background("Background", Color.white, new Color(71, 75, 76)),
        Foreground("Foreground", Color.black, Color.white),
        Line("Line number", DrawColor.khaki.getColor(), DrawColor.blue_violet.getColor()),
        Dialogue("Dialogue", Color.white, new Color(71, 75, 76)),
        Comment("Comment", DrawColor.pale_green.getColor(), DrawColor.dark_olive_green.getColor()),
        Proposal("Proposal", DrawColor.gold.getColor(), DrawColor.chocolate.getColor()),
        Request("Request", DrawColor.light_sky_blue.getColor(), DrawColor.dark_blue.getColor()),
        Picture("Picture", Color.white, new Color(71, 75, 76)),
        Movie("Movie", Color.white, new Color(71, 75, 76)),
        Sound("Sound", Color.white, new Color(71, 75, 76)),
        Commands("Commands", Color.white, new Color(71, 75, 76)),
        Selected("Selected", DrawColor.alice_blue.getColor(), Color.black.brighter());
        
        
        String name;
        Color light;
        Color dark;
        
        private Colors(String name, Color light, Color dark){
            this.name = name;
            this.light = light;
            this.dark = dark;
        }

        public String getName() {
            return name;
        }

        public Color getColor(boolean isDark) {
            return isDark == false ? light : dark;
        }
    }
    
    public enum TextType{
        StripAll, Normal, WithItems;
    }
    
    TextType texttype = TextType.Normal;
    
    public SsbEventTableRenderer(boolean dark) {
        this.dark = dark;
        init();
    }
    
    private void init(){
        setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        setForeground(Colors.Foreground.getColor(dark));
        
        // Get linetype (index 1)
        if(table.getModel() instanceof SsbEventTableModel){
            SsbEventTableModel model = (SsbEventTableModel)table.getModel();
            SsbEventType type = model.getLineType(row);
            
            switch(type){
                case Dialogue -> setBackground(Colors.Dialogue.getColor(dark));
                case Comment -> setBackground(Colors.Comment.getColor(dark));
//                case Proposal -> setBackground(Colors.Proposal.getColor(dark));
//                case Request -> setBackground(Colors.Request.getColor(dark));
                default -> setBackground(Colors.Dialogue.getColor(dark));             }
        }
        
        if(value instanceof SsbEventType){
            content_1.setText(((SsbEventType)value).toString());
        }
        
        if(value instanceof Integer && column == 0){
            // Fill the number color and display the number
            setBackground(Colors.Line.getColor(dark));
            content_1.setText(Integer.toString(row + 1));
        }
        
        if(value instanceof String){
            content_1.setText(value.toString());
        }
        
        if(value instanceof Integer){
            content_1.setText(Integer.toString((Integer)value));
        }
        
//        if(value instanceof Time){
//            setText(((Time)value).toASSTime());
//        }
        
//        if(value instanceof AssStyle){
//            setText(((AssStyle)value).getName());
//        }
        
        if(value instanceof String && column == 7){
            String content = (String)value;
            switch(texttype){
                case StripAll -> {
                    // Strip text if the text contains edit marks.
                    if(content.contains("{\\")){
                        try{
                            content_1.setText(content.replaceAll("\\{[^\\}]+\\}", ""));
                        }catch(Exception e){
                            content_1.setText(content);
                        }
                    }else{
                        content_1.setText(content);
                    }
                }
                case WithItems -> {
                    // Replace tags by items if the text contains edit marks.
                    if(content.contains("{\\")){
                        try{
                            content_1.setText(content.replaceAll("\\{[^\\}]+\\}", "◆"));
                        }catch(Exception e){
                            content_1.setText(content);
                        }
                    }else{
                        content_1.setText(content);
                    }
                }
                case Normal -> // Do nothing.
                    content_1.setText(content);
            }
        }
        
        // Filler when selected
        if(isSelected == true){
            setBackground(Colors.Selected.getColor(dark));            
        }
        
        // CPL
        if(value instanceof Integer && column == 5){
            int v = (Integer)value;
            setBackground(DrawColor.red.getColor(Math.min(50f, (float)v)/100f));
        }
        
        // CPS
        if(value instanceof Integer && column == 6){
            int v = (Integer)value;
            setBackground(DrawColor.red.getColor(Math.min(25f, (float)v)/100f));
        }
        
        return this;
    }

    public TextType getTexttype() {
        return texttype;
    }

    public void setTexttype(TextType texttype) {
        this.texttype = texttype;
    }
}
