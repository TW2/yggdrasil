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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author util2
 */
public class YggSystem {
    
    public static String resolveRemoteIPv4(){
        String ipv4 = "";
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()))) {
                ipv4 = in.readLine(); //you get the IP as a String
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(YggSystem.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(YggSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ipv4;
    }
    
    public static String resolveLocalIPv4(){
        String ipv4;
        
        try {
            ipv4 = InetAddress.getLocalHost().getHostAddress();                    
        } catch (UnknownHostException ex) {
            ipv4 = resolveRemoteIPv4();
        }
        
        return ipv4;
    }
    
}
