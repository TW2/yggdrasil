/*
 * Copyright (C) 2018 util2
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
package org.wingate.ygg.renderer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.wingate.ygg.ass.Event;
import org.wingate.ygg.ass.Style;
import org.wingate.ygg.model.AssEventTableModel;
import org.wingate.ygg.util.DrawColor;
import org.wingate.ygg.util.Time;

/**
 *
 * @author util2
 */
public class AssEventTableRenderer extends JLabel implements TableCellRenderer {

    private Color lineNumberColor = DrawColor.khaki.getColor();
    private Color dialogueColor = Color.white;
    private Color commentColor = DrawColor.pale_green.getColor();
    private Color proposalColor = DrawColor.gold.getColor();
    private Color requestColor = DrawColor.light_sky_blue.getColor();
    
    public enum TextType{
        StripAll, Normal, WithItems;
    }
    
    TextType texttype = TextType.Normal;
    
    public AssEventTableRenderer() {
        init();
    }
    
    private void init(){
        setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        setForeground(Color.black);
        
        // Get linetype (index 1)
        if(table.getModel() instanceof AssEventTableModel){
            AssEventTableModel model = (AssEventTableModel)table.getModel();
            Event.LineType linetype = model.getLineType(row);
            
            switch(linetype){
                case Dialogue: setBackground(dialogueColor); break;
                case Comment: setBackground(commentColor); break;
                case Proposal: setBackground(proposalColor); break;
                case Request: setBackground(requestColor); break;
            }
        }
        
        if(value instanceof Event.LineType){
            setText(((Event.LineType)value).toString());
        }
        
        if(value instanceof Integer && column == 0){
            // Fill the number color and display the number
            setBackground(lineNumberColor);
            setText(Integer.toString(row + 1));
        }
        
        if(value instanceof String){
            setText(value.toString());
        }
        
        if(value instanceof Integer){
            setText(Integer.toString((Integer)value));
        }
        
        if(value instanceof Time){
            setText(((Time)value).toASSTime());
        }
        
        if(value instanceof Style){
            setText(((Style)value).getName());
        }
        
        if(value instanceof String && column == 13){
            String content = (String)value;
            switch(texttype){
                case StripAll:
                    // Strip text if the text contains edit marks.
                    if(content.contains("{\\")){
                        try{
                            setText(content.replaceAll("\\{[^\\}]+\\}", ""));
                        }catch(Exception e){
                            setText(content);
                        }
                    }else{
                        setText(content);
                    }
                    break;
                case WithItems:
                    // Replace tags by items if the text contains edit marks.
                    if(content.contains("{\\")){
                        try{
                            setText(content.replaceAll("\\{[^\\}]+\\}", "◆"));
                        }catch(Exception e){
                            setText(content);
                        }
                    }else{
                        setText(content);
                    }
                    break;
                case Normal:
                    // Do nothing.
                    setText(content);
                    break;
            }
        }
        
        // Filler when selected
        if(isSelected == true){
            setBackground(DrawColor.alice_blue.getColor());            
        }
        
        // CPL
        if(value instanceof Integer && column == 11){
            int v = (Integer)value;
            setBackground(DrawColor.red.getColor(Math.min(50f, (float)v)/100f));
        }
        
        // CPS
        if(value instanceof Integer && column == 12){
            int v = (Integer)value;
            setBackground(DrawColor.red.getColor(Math.min(25f, (float)v)/100f));
        }
        
        return this;
    }

    public void setLineNumberColor(Color lineNumberColor) {
        this.lineNumberColor = lineNumberColor;
    }

    public Color getLineNumberColor() {
        return lineNumberColor;
    }

    public void setDialogueColor(Color dialogueColor) {
        this.dialogueColor = dialogueColor;
    }

    public Color getDialogueColor() {
        return dialogueColor;
    }

    public void setCommentColor(Color commentColor) {
        this.commentColor = commentColor;
    }

    public Color getCommentColor() {
        return commentColor;
    }

    public void setProposalColor(Color proposalColor) {
        this.proposalColor = proposalColor;
    }

    public Color getProposalColor() {
        return proposalColor;
    }

    public void setRequestColor(Color requestColor) {
        this.requestColor = requestColor;
    }

    public Color getRequestColor() {
        return requestColor;
    }

    public TextType getTexttype() {
        return texttype;
    }

    public void setTexttype(TextType texttype) {
        this.texttype = texttype;
    }
    
}
