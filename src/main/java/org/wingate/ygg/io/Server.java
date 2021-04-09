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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.subs.ass.AssEvent;
import org.wingate.ygg.ui.synctable.SelectedFormat;
import org.wingate.ygglock.YggLock;

/**
 *
 * @author util2
 */
public class Server implements Runnable {
    
    private Thread execTh = null;
    private volatile boolean running = false;
    
    private final YggLock.CryptObj cObj;
    private Protocol lastProtocol = Protocol.Unknown;
    private ByteBuffer output;
    
    private Selector selector;
    private final Map<SocketChannel, List> dataMapper;
    private final InetSocketAddress listenAddress;
    
    public Server(YggLock.CryptObj cObj) throws IOException {
        this.cObj = cObj;
    	listenAddress = new InetSocketAddress(cObj.getIp(), cObj.getPort());
        dataMapper = new HashMap<>();
    }
    
    public void startThread(){
        stopThread();
        running = true;
        execTh = new Thread(this);
        execTh.start();
    }
    
    public void stopThread(){
        if(execTh != null && (execTh.isInterrupted() == false | execTh.isAlive() == true)){
            execTh.interrupt();
            execTh = null;
            running = false;
        }        
    }

    // create server channel	
    public void startServer() throws IOException {
        selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        // retrieve server socket and bind to port
        serverChannel.socket().bind(listenAddress);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started with " + cObj.getSurname());

        startThread();
    }

    //accept a connection made to this channel's socket
    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel sserverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = sserverChannel.accept();
        channel.configureBlocking(false);
        System.out.println("Connected to a client!");

        // register channel with selector for further IO
        dataMapper.put(channel, new ArrayList());
        channel.register(this.selector, SelectionKey.OP_READ);
    }
    
    //read from the socket channel
    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        int numRead = channel.read(buffer);

        if (numRead == -1) {
            free(channel, key);
        }else if(numRead != -1){
            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);
            System.out.println("Got a chunk!");
            String testData = new String(data);
            if(testData.startsWith(Protocol.Message.getProtocolMessage())){
                lastProtocol = Protocol.Message;
                setTextToChat(channel, testData.substring(3));
                Alert.messageReceived();
            }else if(testData.startsWith(Protocol.Download.getProtocolMessage())){
                lastProtocol = Protocol.Download;
            }else if(testData.equalsIgnoreCase("end-of-file")){
                lastProtocol = Protocol.DownLoadFinished;
                Alert.downloadFinished();
            }else if(lastProtocol == Protocol.Download){
                
            }else if(testData.startsWith(Protocol.Proposal.getProtocolMessage())){
                lastProtocol = Protocol.Proposal;
                setProposalToChat(channel, testData.substring(3));
                Alert.proposalGot();
            }else if(testData.startsWith(Protocol.Request.getProtocolMessage())){
                lastProtocol = Protocol.Request;
                setRequestToChat(channel, testData.substring(3));
                Alert.requestGot();
            }
            
        }
    }
    
    //free
    private void free(SocketChannel channel, SelectionKey key){
        try (channel) {
            dataMapper.remove(channel);
            System.out.println("Connection closed by client!");
        }catch(Exception ex){
            
        }
        key.cancel();
    }
    
    private void waiter() throws IOException{
        // wait for events
            selector.select();

        //work on selected keys
        Iterator keys = selector.selectedKeys().iterator();
        while (keys.hasNext()) {
            SelectionKey key = (SelectionKey) keys.next();

            // this is necessary to prevent the same key from coming up 
            // again the next time around.
            keys.remove();

            if (!key.isValid()) {
                continue;
            }

            if (key.isAcceptable()) {
                accept(key);
            }else if (key.isReadable()) {
                read(key);
            }
        }
    }

    @Override
    public void run() {
        while(true){
            if(running == true){
                try {
                    waiter();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    stopThread();
                }
            }
        }
    }
    
    private void setTextToChat(SocketChannel channel, String message) throws IOException{
        String author = "?";
        for(YggLock.CryptObj co : MainFrame.getCryptObjs()){
            if(co.getIp().equalsIgnoreCase(channel.socket().getInetAddress().getHostAddress())){
                author = co.getSurname();
                break;
            }
        }
        MainFrame.setMessageToChat(author, message);
    }
    
    private void setProposalToChat(SocketChannel channel, String message) throws IOException{
        String author = "?";
        for(YggLock.CryptObj co : MainFrame.getCryptObjs()){
            if(co.getIp().equalsIgnoreCase(channel.socket().getInetAddress().getHostAddress())){
                author = co.getSurname();
                break;
            }
        }
        String[] t = message.split(";", 2);
        SelectedFormat sf = SelectedFormat.from(t[0]);
        switch(sf){
            case ASS -> {
                AssEvent ev = AssEvent.createFromASS(t[1]);
                message = "(ASS) See one event starting at " + ev.getStartTime().toASSTime() +
                        " and stopping at " + ev.getEndTime().toASSTime() +
                        " for the following proposal:\n" + ev.getText();
            }
            default -> { message = "An unknown format has been proposed and has been ignored!"; }
        }
        MainFrame.setProposalToChat(author, message);
    }
    
    private void setRequestToChat(SocketChannel channel, String message) throws IOException{
        String author = "?";
        for(YggLock.CryptObj co : MainFrame.getCryptObjs()){
            if(co.getIp().equalsIgnoreCase(channel.socket().getInetAddress().getHostAddress())){
                author = co.getSurname();
                break;
            }
        }
        String[] t = message.split(";", 2);
        SelectedFormat sf = SelectedFormat.from(t[0]);
        switch(sf){
            case ASS -> {
                AssEvent ev = AssEvent.createFromASS(t[1]);
                message = "(ASS) See one event starting at " + ev.getStartTime().toASSTime() +
                        " and stopping at " + ev.getEndTime().toASSTime() +
                        " for the following request:\n" + ev.getText();
            }
            default -> { message = "An unknown format has been requested and has been ignored!"; }
        }
        MainFrame.setRequestToChat(author, message);
    }
}
