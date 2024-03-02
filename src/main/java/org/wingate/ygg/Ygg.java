/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.wingate.ygg;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.EventQueue;

/**
 *
 * @author util2
 */
public class Ygg {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            System.out.println("Thanks for running yggdrasil!");
            FlatDarkLaf.setup();
            MainFrame mf = new MainFrame();
            mf.setSize(1900, 1000);
            mf.setTitle("yggdrasil");
            mf.setLocationRelativeTo(null);
            mf.setVisible(true);
        });
    }
}
