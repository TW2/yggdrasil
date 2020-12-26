/*
 * PressePapier.java
 *
 * Created on 28 octobre 2006, 20:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ygg.util;

import java.awt.Toolkit;
import java.awt.datatransfer.*;

public class Clipboard {
    
    public Clipboard() {
    }
    
    public static boolean CCopy(String s){
        try{
            StringSelection ss = new StringSelection(s);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss,null);
        }catch(IllegalStateException ise){
            /* Le presse-papier n'est pas disponible */
            return false;
        }
        return true;
    }
     
    public static String CPaste(){
        String s = "";
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            /* V�rification que le contenu est de type texte. */
            if( t!=null && t.isDataFlavorSupported(DataFlavor.stringFlavor) ) {
                s = (String)t.getTransferData(DataFlavor.stringFlavor);
            }
        }catch(UnsupportedFlavorException | java.io.IOException ex){
        }
        return s;
    }
    
}
