
package ygg;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

/**
 *
 * @author util2
 */
public class Ygg {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("----- YGGDRASIL -----");
        System.out.println("--- FUNSUB PROJECT --");
        System.out.println("------- 2020 --------");
        System.out.println("-------- TW2 --------");        
        
        boolean dark = args.length > 0 && args[0].equalsIgnoreCase("dark");
        
        if(dark){
            FlatDarkLaf.install();
        }else{
            FlatLightLaf.install();
        }
        
        MainFrame mf = new MainFrame(dark);
        mf.setVisible(true);
        
        if(dark){
            System.out.println("---- HAPPY RUMBA ----");
        }else{
            System.out.println("---- HAPPY FESTA ----");
        }
        //======================================================================
        // TEST (activate or deactivate)
        //======================================================================
//        org.wingate.ygg.test.TestVideoTimestamp frame = new org.wingate.ygg.test.TestVideoTimestamp();
//        frame.setVisible(true);
    }
    
}
