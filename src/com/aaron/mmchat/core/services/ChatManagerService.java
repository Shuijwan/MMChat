/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ChatManagerServices.java
 *
 */

package com.aaron.mmchat.core.services;

import com.aaron.mmchat.core.ChatManager;
import com.aaron.mmchat.core.GroupChat;
import com.aaron.mmchat.core.P2PChat;
import com.aaron.mmchat.core.PersistentGroupChat;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @Title: ChatManagerServices.java
 * @Package: com.aaron.mmchat.core.services
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public class ChatManagerService extends BaseManagerService implements ChatManager {
    
    private ArrayList<P2PChat> mP2pChats;
    private ArrayList<GroupChat> mGroupChats;
    private ArrayList<PersistentGroupChat> mPersistentGroupChats;
    private Set<ChatListCallback> mCallbacks;

    public ChatManagerService() {
        mP2pChats = new ArrayList<P2PChat>();
        mGroupChats = new ArrayList<GroupChat>();
        mPersistentGroupChats = new ArrayList<PersistentGroupChat>();
        mCallbacks = new HashSet<ChatManager.ChatListCallback>();
    }

    @Override
    public void registerChatListCallback(ChatListCallback callback) {
        mCallbacks.add(callback);
    }

    @Override
    public void unregisterChatListCallback(ChatListCallback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public ArrayList<P2PChat> getP2PChatList() {
        return mP2pChats;
    }

    @Override
    public ArrayList<GroupChat> getGroupChatList() {
        return mGroupChats;
    }

    @Override
    public ArrayList<PersistentGroupChat> getPersistentGroupChatList() {
        return mPersistentGroupChats;
    }

    @Override
    public P2PChat getP2PChat(String clientJid, String jid) {
        for(P2PChat chat : mP2pChats) {
            if(clientJid.equals(chat.getClientJid()) && jid.equals(chat.getParticipant())) {
                return chat;
            }
        }
        return null;
    }
    
    @Override
    public P2PChat getOrCreateP2PChat(String clientJid, String jid) {
        
        for(P2PChat chat : mP2pChats) {
            if(clientJid.equals(chat.getClientJid()) && jid.equals(chat.getParticipant())) {
                return chat;
            }
        }
        
        XMPPConnection connection = getXmppConnection(clientJid);
        org.jivesoftware.smack.ChatManager chatManager = org.jivesoftware.smack.ChatManager.getInstanceFor(connection);
        chatManager.addChatListener(new ChatCreatedListener(clientJid));
        Chat chat = chatManager.createChat(jid, null);
        P2PChat p2pChat = new P2PChat(clientJid, chat);
        mP2pChats.add(p2pChat);
        notifyP2PChatCreated(p2pChat);
        return p2pChat;
    }

    @Override
    public void removeP2PChat(P2PChat chat) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createGroupChat(String[] jids) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeGroupChat(GroupChat chat) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createPersistentGroupChat(String[] jids) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removePersistentGroupChat(PersistentGroupChat chat) {
        // TODO Auto-generated method stub
        
    }

    private void notifyP2PChatCreated(P2PChat chat) {
        for(ChatListCallback callback : mCallbacks) {
            callback.onP2PChatCreated(chat);
        }
    }
    
    class ChatCreatedListener implements ChatManagerListener {

        private String mClientJid;
        
        public ChatCreatedListener(String clientJid) {
            mClientJid = clientJid;
        }
        
        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {
            if(createdLocally) {
                return;
            }
            P2PChat p2pChat = new P2PChat(mClientJid, chat);
            mP2pChats.add(p2pChat);
            notifyP2PChatCreated(p2pChat);
        }
        
    }
}
