/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * GroupChat.java
 *
 */

package com.aaron.mmchat.core;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.muc.UserStatusListener;

/**
 *
 * @Title: GroupChat.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-15
 *
 */

public class GroupChat extends BaseChat implements PacketListener, InvitationRejectionListener, ParticipantStatusListener, SubjectUpdatedListener, UserStatusListener {

    private MultiUserChat mChat;
    
    public GroupChat(String clientJid, MultiUserChat chat) {
        super(clientJid);
        mChat = chat;
        mChat.addMessageListener(this);
        mChat.addInvitationRejectionListener(this);
        mChat.addParticipantListener(this);
        mChat.addParticipantStatusListener(this);
        mChat.addSubjectUpdatedListener(this);
        mChat.addUserStatusListener(this);
        
    }
    
    @Override
    public void sendMessage(String text) {
        org.jivesoftware.smack.packet.Message msg = mChat.createMessage();
        msg.setBody(text);
        try {
            mChat.sendMessage(msg);
            notifyMessageSent();
        } catch (NotConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            notifyMessageSentFailed();
        } catch (XMPPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            notifyMessageSentFailed();
        }
    }

    @Override
    public void processPacket(Packet packet) throws NotConnectedException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void invitationDeclined(String invitee, String reason) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void joined(String participant) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void left(String participant) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void kicked(String participant, String actor, String reason) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void voiceGranted(String participant) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void voiceRevoked(String participant) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void banned(String participant, String actor, String reason) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void membershipGranted(String participant) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void membershipRevoked(String participant) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void moderatorGranted(String participant) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void moderatorRevoked(String participant) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void ownershipGranted(String participant) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void ownershipRevoked(String participant) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void adminGranted(String participant) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void adminRevoked(String participant) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void nicknameChanged(String participant, String newNickname) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void subjectUpdated(String subject, String from) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void kicked(String actor, String reason) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void voiceGranted() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void voiceRevoked() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void banned(String actor, String reason) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void membershipGranted() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void membershipRevoked() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void moderatorGranted() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void moderatorRevoked() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void ownershipGranted() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void ownershipRevoked() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void adminGranted() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void adminRevoked() {
        // TODO Auto-generated method stub
        
    }

}
