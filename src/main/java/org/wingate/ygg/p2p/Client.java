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
package org.wingate.ygg.p2p;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wingate.ygglock.YggLock;

/**
 *
 * @author util2
 */
public class Client {
    public Socket socket;
    public YggLock.CryptObj cObj = null;
    
    private Client(){
        
    }
    
    private static Client connect(YggLock.CryptObj cObj){
        Client c = new Client();
        try{
            // On copie l'objet contenant les infos
            c.cObj = cObj;

            // On crée un socket pour la connexion avec le client distant
            c.socket = new Socket();

            // On prépare la connexion
            InetSocketAddress addr = new InetSocketAddress(c.cObj.getIp(), c.cObj.getPort());

            // Et on se connecte à l'aide du socket
            c.socket.connect(addr, 1500);
        }catch(IOException ex){
            c.closeSocket();
            return null;
        }
        
        return c;
    }
    
    public static void sendChatMessage(YggLock.CryptObj co, List<ChatMessage> cms){
        // Main protocol
        transmit(co, Protocol.Message.getProtocolMessage());
        for(ChatMessage cm : cms){
            // Protocol inner type (head)
            transmit(co, cm.getInnerChatType().getProtocol());
            // Message (body)
            transmit(co, cm.getObject());
        }
    }
    
    public static void sendProposalMessage(YggLock.CryptObj co, List<InSubsMessage> ims){
        // Main protocol
        transmit(co, Protocol.Proposal.getProtocolMessage());
        for(InSubsMessage im : ims){
            // Protocol inner type (head)
            transmit(co, im.getType().getProtocol());
            // Message (body)
            transmit(co, im.getObject());
        }
    }
    
    public static void sendRequestMessage(YggLock.CryptObj co, List<InSubsMessage> ims){
        // Main protocol
        transmit(co, Protocol.Request.getProtocolMessage());
        for(InSubsMessage im : ims){
            // Protocol inner type (head)
            transmit(co, im.getType().getProtocol());
            // Message (body)
            transmit(co, im.getObject());
        }
    }
    
    /**
     * Transmet un signal (connexion, transmission, déconnexion)
     * Transmits a signal (connection, transmission, deconnection)
     * @param co The crypted peer which contains IP:PORT:Surname and undecoded password
     * @param obj The object to transmit
     */
    private static void transmit(YggLock.CryptObj co, Object obj){
        // On se connecte au client par la pair
        // Connect to the peer
        Client client = Client.connect(co);
        
        // On vérifie que le client existe (s'il n'existe pas, on sort)
        // Check if peer is connected (or go out)
        if(client == null) return;
        
        // On crée des objets de communication
        // Create communication objects
        Charset ch = StandardCharsets.UTF_8;
        PrintWriter pw = null;
        
        // On crée un flux de sortie (entrée coté pair)
        // Output (Input to the other side in the peer)
        try(OutputStream out = client.socket.getOutputStream();){
            
            // Initialisation du type de flux
            // Initialization of the stream
            if(obj instanceof File){
                
            }else{
                pw = new PrintWriter(new OutputStreamWriter(out, ch));
            }
            
            // Envoi des objets
            // Send objects
            if(pw != null && obj instanceof String){
                pw.println((String)obj);
            }else if(pw != null && obj instanceof Integer){
                pw.println((Integer)obj);
            }else if(pw != null && obj instanceof Long){
                pw.println((Long)obj);
            }else if(pw != null && obj instanceof Boolean){
                pw.println((Boolean)obj);
            }else if(pw != null && obj instanceof Byte){
                pw.println((Byte)obj);
            }else if(obj instanceof Byte[]){
                byte[] z = (byte[])obj;                
                ByteArrayInputStream bais = new ByteArrayInputStream(z);
                bais.transferTo(out);
            }else if(pw != null && obj instanceof Character){
                pw.println((char)obj);
            }else if(pw != null && obj instanceof Character[]){
                pw.println(new String((char[])obj));
            }else if(pw != null && obj instanceof Double){
                pw.println((Double)obj);
            }else if(pw != null && obj instanceof Float){
                pw.println((Float)obj);
            }else if(pw != null && obj instanceof Short){
                pw.println((Short)obj);
            }else if(obj instanceof File){
//                File file = (File)obj;
//                PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, ch));
//                pw.println("File = " + file.getName());
//                int buffer = 4096;
//                byte[] data = new byte[buffer];                
//                try (
//                        FileInputStream fis = new FileInputStream(file);
//                        BufferedInputStream bis = new BufferedInputStream(fis)) 
//                {
//                    while(bis.available() > 0){
//                        bis.read(data, 0, buffer);
//                        out.write(data, 0, buffer);
//                        out.flush();
//                    }
//                } catch (IOException ex) {
//                    System.out.println("Send file aborted!");
//                }
//                pw.println("EndOfFile = " + file.getName());
            }else{
                System.err.println("Unknown object to send!");
            }
            
            // On force le flux à aller sur le réseau et on le ferme
            // Flush data and close peer
            if(pw != null){
                pw.flush();
                pw.close();
            }
            
        } catch (IOException ex) {
            System.err.println("Connection timed out!");
        }
        
        // On clôt la communication avec la pair
        // Close peer communication
        client.closeSocket();
    }
    
    private void closeSocket(){
        if(socket != null){
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
