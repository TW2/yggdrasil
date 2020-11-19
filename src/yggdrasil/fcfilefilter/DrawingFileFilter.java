/*
 * Copyright (C) 2020 util2
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
package yggdrasil.fcfilefilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.filechooser.FileFilter;
import yggdrasil.languages.ISO_3166;
import yggdrasil.languages.Language;

/**
 *
 * @author util2
 */
public class DrawingFileFilter extends FileFilter {
    
    // Language (loading from properties of each component)
    private final ISO_3166 iso = ISO_3166.getISO_3166(Locale.getDefault().getISO3Country());
    private final Language chosen = new Language();
    
    private final List<String> extensions = new ArrayList<>();

    public DrawingFileFilter() {
        init();
    }
    
    private void init(){
        extensions.add("yds");
    }

    @Override
    public boolean accept(File f) {
        if(f.isDirectory()) return true;
        return extensions.contains(f.getName().substring(f.getName().lastIndexOf(".") + 1));
    }

    @Override
    public String getDescription() {
        return chosen.getTranslated("FileFilterDrawingFiles", iso, "Yggdrasil drawing scripts (*.yds)");
    }
}
