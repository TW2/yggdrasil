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
package org.wingate.ygg.chat;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 *
 * @author util2
 */
public class Chat {
    
    private final StyledDocument doc;
    private final Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
    private Style regular = null;
    private final Emoticons icons = new Emoticons();
    
    public Chat(StyledDocument doc){
        this.doc = doc;
        init(); 
    }
    
    private void init(){
        regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");
        
        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);
        
        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);
        
        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);
        
        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);
        
        for(String str : icons.getKeys()){
            s = doc.addStyle(str, regular);
            StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
            ImageIcon icon = icons.getIcon(str);
            
            if (icon != null) {
                StyleConstants.setIcon(s, icon);
            }
        }        
    }
    
    public StyledDocument getDocument(){
        return doc;
    }
    
    public void addToDoc(String s){
        try (Scanner scan = new Scanner(s)) {
            String word;
            while(scan.hasNext()){
                word = scan.next();
                boolean isSmiley = false;
                for(String icon : icons.getKeys()){
                    if(word.equals(icon)){
                        isSmiley = true;
                        break;
                    }
                }
                if(isSmiley == true){
                    try {
                        doc.insertString(doc.getLength(), word + " ", doc.getStyle(word));
                    } catch (BadLocationException ex) {
                        Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    try {
                        doc.insertString(doc.getLength(), word + " ", doc.getStyle("regular"));
                    } catch (BadLocationException ex) {
                        Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            try {
                doc.insertString(doc.getLength(), "\n", doc.getStyle("regular"));
            } catch (BadLocationException ex) {
                Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Style getRegular() {
        return regular;
    }
    
}
