/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * P2PChat.java
 *
 */

package com.aaron.mmchat.core;

import android.util.Log;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

/**
 *
 * @Title: P2PChat.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-15
 *
 */

public class P2PChat implements MessageListener {
    
    public static interface P2PChatCallback {
        public void onMessageSent();
        public void onMessageSentFailed();
    }
    
    private Chat mChat;
    private ArrayList<P2PChatCallback> mCallbacks;
    private String mClientJid; 
    
    public P2PChat(String clientJid, Chat chat) {
        mClientJid = clientJid;
        mChat = chat;
        mChat.addMessageListener(this);
    }
    
    private void notifyMessageSentFailed() {
        
    }
    
    public String getParticipant() {
        if(mChat != null) {
            return mChat.getParticipant();
        }
        return null;
    }
    
    public String getClientJid() {
        return mClientJid;
    }
    
    public void sendMessage(String text) {
        Message msg = new Message();
        msg.setBody(text);
        try {
            mChat.sendMessage(msg);
        } catch (NotConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        Log.i("TTT", "msg:"+message.toString());
        
    }

}
