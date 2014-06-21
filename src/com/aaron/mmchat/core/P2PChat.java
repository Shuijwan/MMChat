/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * P2PChat.java
 *
 */

package com.aaron.mmchat.core;

import com.aaron.mmchat.core.services.ContactManagerService;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;

/**
 *
 * @Title: P2PChat.java
 * @Package: com.aaron.mmchat.core
 * @Description: a P2P chat session
 * 
 * @Author: aaron
 * @Date: 2014-6-15
 *
 */

public class P2PChat extends BaseChat implements MessageListener {
    
    private Chat mChat;      //internal smack Chat object
    private Contact mContact;//peer contact, may be null, if it is not in our buddylist
    
    public P2PChat(String clientJid, Chat chat) {
        super(clientJid);
        mChat = chat;
        mChat.addMessageListener(this);
        ContactManagerService contactManager = (ContactManagerService) MMContext.peekInstance().getService(MMContext.CONTACT_SERVICE);
        mContact = contactManager.getContact(clientJid, chat.getParticipant());
    }
    
    /**
     * get Participant display name, if it is in buddylist, return Contact's name,
     * otherwise return the jid.
     * 
     * */
    public String getParticipantName() {
        if(mContact != null) {
            return mContact.getName();
        }
        
        if(mChat != null) {
            return mChat.getParticipant();
        }
        return null;
    }
    
    /**
     * return related Contact object of this participant, may be null if not in buddylist
     * 
     * */
    public Contact getParticipantContact() {
        return mContact;
    }
    
    /**
     * return the participant's jid
     * 
     * */
    public String getParticipantJid() {
        if(mChat != null) {
            return mChat.getParticipant();
        }
        return null;
    }
    
    /**
     * send IM message
     * @param text, the message content
     * 
     * */
    public void sendMessage(String text) {
        Message msg = new Message();
        msg.setBody(text);
        msg.addExtension(new DeliveryReceiptRequest());

        try {
            mChat.sendMessage(msg);            
            addMessage(new InstantMessage(msg, true));
            notifyMessageSent();
        } catch (NotConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            notifyMessageSentFailed();
        }
    }
    
    @Override
    public void processMessage(Chat chat, Message message) {
        if(message.getType() == Type.chat) {
            addMessage(new InstantMessage(message, false));
            notifyMessageReceived();
        }
        
    }

}
