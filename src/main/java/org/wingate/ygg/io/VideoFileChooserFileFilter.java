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
package org.wingate.ygg.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author util2
 */
public class VideoFileChooserFileFilter extends FileFilter {
    
    private List<String> extensions = new ArrayList<>(Arrays.asList(new String[]{
        "avi",
        "wav",
        "wmv",
        "asf",
        "rm",
        "mpeg",
        "m2ts",
        "ts",
        "mp2",
        "mp3",
        "mp4",
        "m4v",
        "m4a",
        "aac",
        "ogm",
        "ogv",
        "oga",
        "mkv",
        "mka",
        "tta",
        "flv",
        "vob",
        "opus",
        "vp8",
        "vp9",
        "xvid",
        "divx",
        "flac",
        "mov",
    }));

    public VideoFileChooserFileFilter() {
    }    
    
    @Override
    public boolean accept(File f) {
        if(f.isDirectory()) return true;
        String name = f.getName();
        String ext = name.substring(name.lastIndexOf(".")+1);
        return extensions.contains(ext.toLowerCase());
    }

    @Override
    public String getDescription() {
        return "Media files";
    }
    
}
