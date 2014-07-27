/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ChatManager.java
 *
 */

package com.aaron.mmchat.core;

import java.util.ArrayList;

/**
 *
 * @Title: ChatManager.java
 * @Package: com.aaron.mmchat.core
 * @Description: chat interface, get ChatManager through {@link MMContext.getInstance(context).getService(MMContext.CHAT_SERVICE)}
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public interface ChatManager {
    
    /**
     * callback for chat list
     * 
     * */
    public static interface ChatListCallback {
       
        /**
         * callback for a new P2PChat is created
         * 
         * */
        public void onP2PChatCreated(P2PChat chat);
        
        /**
         * callback for P2PChats are removed
         * 
         * */
        public void onP2PChatsRemoved(ArrayList<P2PChat> chats);
        
    }
    
    /**
     * register a ChatListCallback
     * 
     * */
    public void registerChatListCallback(ChatListCallback callback);
    
    /**
     * unregister a ChatListCallback
     * 
     * */
    public void unregisterChatListCallback(ChatListCallback callback);
    
    /**
     * return all P2PChats
     * 
     * */
    public ArrayList<P2PChat> getP2PChatList();
    
    /**
     * return all GroupChats
     * 
     * */
    public ArrayList<GroupChat> getGroupChatList();
    
    /**
     * return all PersitentGroupChats
     * 
     * */
    public ArrayList<PersistentGroupChat> getPersistentGroupChatList();
    
    /**
     * return P2PChat by clientJid & participant's jid
     * 
     * */
    public P2PChat getP2PChat(String clientJid, String jid);
    
    /**
     * return P2PChat by clientJid & participant's jid, if not exist, create a new one
     * 
     * */
    public P2PChat getOrCreateP2PChat(String clientJid, String jid);
    
    /**
     * remove a Chat
     * 
     * */
    public void removeChat(BaseChat chat);
    
    /**
     * remove chats
     * 
     * */
    public void removeChats(ArrayList<BaseChat> chats);
    
    /**
     * create a GroupChat
     * @param jids, the participant's jids
     * 
     * */
    public void createGroupChat(String[] jids);
     
    /**
     * create a PersistentGroupChat
     * @param jids, the participant's jids
     * 
     * */
    public void createPersistentGroupChat(String[] jids);
    
    /**
     * remove all empty chats
     * 
     * */
    public void removeEmptyChats();
}
