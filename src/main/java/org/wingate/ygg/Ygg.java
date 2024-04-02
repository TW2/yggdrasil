/*
 * Copyright (C) 2023 util2
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
package org.wingate.ygg;

import java.awt.EventQueue;
import org.wingate.ygg.helper.DialogResult;
import org.wingate.ygg.theme.Dark;
import org.wingate.ygg.theme.Theme;
import org.wingate.ygg.ui.ThemeDialog;

/**
 *
 * @author util2
 */
public class Ygg {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            System.out.println("Thanks for running yggdrasil!");
            
            Theme theme = new Dark();
            
            ThemeDialog dialog = new ThemeDialog(new javax.swing.JFrame(), true);
            dialog.showDialog();
            
            if(dialog.getDialogResult() == DialogResult.Ok){
                theme = dialog.getChosenTheme();
            }
            
            MainFrame mf = new MainFrame(theme);
            mf.setSize(1900, 1000);
            mf.setTitle("yggdrasil");
            mf.setLocationRelativeTo(null);
            mf.setVisible(true);
            
            theme.apply(mf);
        });
    }
}
