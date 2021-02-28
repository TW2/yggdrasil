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
package org.wingate.ygg.io;

import java.io.File;

/**
 *
 * @author util2
 */
public class AssFileFilter extends MainFileFilter {

    public AssFileFilter() {
        extension = ".ass";
    }

    @Override
    public boolean accept(File file) {
        if(file.isDirectory()) return true;
        return file.getName().endsWith(".ass") | file.getName().endsWith(".ssa");
    }

    @Override
    public String getDescription() {
        return "Sub Station Alpha files & Advanced (*.ssa or *.ass)";
    }
    
}
