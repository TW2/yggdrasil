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
package ygg.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author util2
 */
public class YggConf {
    
    private static String ipReplica = "127.0.0.1";

    public static String getIpReplica() {
        return ipReplica;
    }

    public static void setIpReplica(String ipReplica) {
        YggConf.ipReplica = ipReplica;
    }
    
    //--------------------------------------------------------
    
    public static void save(){
        File yggconf = new File("ygg.conf");
        try(PrintWriter pw = new PrintWriter(yggconf, StandardCharsets.UTF_16)){
            pw.println("Replica_Remote_Address " + ipReplica);
        } catch (IOException ex) {
            Logger.getLogger(YggConf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void load(){
        File yggconf = new File("ygg.conf");
        if(yggconf.exists() == false) return;
        try(FileInputStream fis = new FileInputStream(yggconf); 
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_16); 
                BufferedReader br = new BufferedReader(isr);){
            String line;
            while((line = br.readLine()) != null){
                if(line.startsWith("Replica_Remote_Address ")){
                    ipReplica = line.substring("Replica_Remote_Address ".length());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(YggConf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
