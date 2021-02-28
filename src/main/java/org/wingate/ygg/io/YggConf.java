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
    
    private String language = "Nothing";
    private String ipReplica = "192.168.1.11";
    private String surname = "Newbie";
    private String team = "Battle Royale";

    public YggConf() {
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getIpReplica() {
        return ipReplica;
    }

    public void setIpReplica(String ipReplica) {
        this.ipReplica = ipReplica;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
    
    //--------------------------------------------------------
    
    public void save(){
        File folder = new File("configuration");
        if(folder.exists() == false) folder.mkdirs();
        File yggconf = new File(folder, "ygg.conf");
        try(PrintWriter pw = new PrintWriter(yggconf, StandardCharsets.UTF_16)){
            pw.println("Language " + language);
            pw.println("Replica_Remote_Address " + ipReplica);
            pw.println("Replica_Surname " + surname);
            pw.println("Replica_Team " + team);
        } catch (IOException ex) {
            Logger.getLogger(YggConf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void load(){
        File folder = new File("configuration");
        if(folder.exists() == false) return;
        File yggconf = new File(folder, "ygg.conf");
        if(yggconf.exists() == false) return;
        try(FileInputStream fis = new FileInputStream(yggconf); 
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_16); 
                BufferedReader br = new BufferedReader(isr);){
            String line;
            while((line = br.readLine()) != null){
                if(line.startsWith("Language ")){
                    language = line.substring("Language ".length());
                }else if(line.startsWith("Replica_Remote_Address ")){
                    ipReplica = line.substring("Replica_Remote_Address ".length());
                }else if(line.startsWith("Replica_Surname ")){
                    surname = line.substring("Replica_Surname ".length());
                }else if(line.startsWith("Replica_Team ")){
                    team = line.substring("Replica_Team ".length());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(YggConf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
