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
package yggdrasil.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import yggdrasil.util.Clipboard;

/**
 *
 * @author util2
 */
public class UserChatUID {
    private String username = "Anonymous";
    private ImageIcon userImage = new ImageIcon(getClass().getResource("/documents/images/bg48_boy.png"));
    private boolean boy = true;
    private String ipv4 = null;

    public UserChatUID() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ImageIcon getUserImage() {
        return userImage;
    }

    public void setUserImage(ImageIcon userImage) {
        this.userImage = userImage;
    }

    public boolean isBoy() {
        return boy;
    }

    public void setBoy(boolean boy) {
        this.boy = boy;
    }

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }
    
    public void resolveRemoteIPv4(){
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()))) {
                ipv4 = in.readLine(); //you get the IP as a String
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(UserChatUID.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserChatUID.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String toBase64(UserChatUID uid){
        String contact =
                uid.ipv4 + ";" + 
                Boolean.toString(uid.boy) + ";" +
                uid.username ;
        return Base64.getEncoder().encodeToString(contact.getBytes(StandardCharsets.UTF_16LE));
    }
    
    private static UserChatUID fromBase64(String str){
        UserChatUID uid = new UserChatUID();
        
        byte[] bytes = Base64.getDecoder().decode(str);
        String contact = StandardCharsets.UTF_16LE.decode(ByteBuffer.wrap(bytes)).toString();
        String[] t = contact.split(";", 3);
        uid.ipv4 = t[0];
        uid.boy = Boolean.parseBoolean(t[1]);
        uid.username = t[2];
        if(uid.boy == false){
            uid.userImage = new ImageIcon(uid.getClass().getResource("/documents/images/bg48_girl.png"));
        }
        
        return uid;
    }
    
    public static void encodeUID(UserChatUID uid){
        uid.resolveRemoteIPv4();        
        Clipboard.CCopy(toBase64(uid));
    }
    
    public static UserChatUID decodeUID() throws NullPointerException {
        String strFromCp = Clipboard.CPaste();
        if(strFromCp == null){
            throw new NullPointerException("Bad data!");
        }
        UserChatUID uid = fromBase64(strFromCp);
        return uid;
    }
    
    public static void save(List<UserChatUID> list, UserChatUID addUser){
        boolean add = true;
        for(UserChatUID uid : list){
            if(uid.username.equals(addUser.username) && uid.ipv4.equals(addUser.ipv4)){
                add = false;
                break;
            }
        }
        if(add == true){
            list.add(addUser);            
        }
        try(PrintWriter pw = new PrintWriter("db", StandardCharsets.UTF_16LE)){
            list.forEach(uid -> {
                pw.println(toBase64(uid));
            });
        } catch (IOException ex) {
            Logger.getLogger(UserChatUID.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static List<UserChatUID> load(){
        List<UserChatUID> list = new ArrayList<>();
        if((new File("db")).exists() == false) {return list;}
        try(FileReader fr = new FileReader("db", StandardCharsets.UTF_16LE);
                BufferedReader br = new BufferedReader(fr)){
            String line;
            while((line = br.readLine()) != null){
                UserChatUID uid = fromBase64(line);
                if(uid.ipv4 != null){
                    list.add(uid);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(UserChatUID.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
}
