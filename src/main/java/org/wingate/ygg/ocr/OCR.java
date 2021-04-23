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
package org.wingate.ygg.ocr;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.LeptonicaFrameConverter;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;
import static org.bytedeco.leptonica.global.lept.*;

/**
 *
 * @author util2
 */
public class OCR {
    
    // Convertisseurs
    private static final Java2DFrameConverter converter = new Java2DFrameConverter();
    private static final LeptonicaFrameConverter converter2 = new LeptonicaFrameConverter();
    
    private File video = null;
    
    public static void search(String path){
        search(new ImageIcon(path));
    }
    
    public static void search(ImageIcon img){
        BufferedImage bi = new BufferedImage(
                img.getIconWidth(),
                img.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = bi.createGraphics();
        
        g2d.drawImage(bi, 0, 0, null);
        
        g2d.dispose();
        search(bi);
    }
    
    public static void search(BufferedImage img){
        BytePointer outText;

        TessBaseAPI api = new TessBaseAPI();
        // Initialize tesseract-ocr with English, without specifying tessdata path
        if (api.Init("C:\\Users\\util2\\Desktop\\tessdata", "fra") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }
        
        // Open input image with leptonica library
        PIX image = converter2.convert(converter.convert(img));
        api.SetImage(image);
        // Get OCR result
        outText = api.GetUTF8Text();
        System.out.println("OCR output:\n" + outText.getString());

        // Destroy used object and release memory
        api.End();
        outText.deallocate();
        pixDestroy(image);
    }
}
