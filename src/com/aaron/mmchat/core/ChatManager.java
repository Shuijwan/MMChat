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
    
    public static interface ChatListCallback {
        
        public void onP2PChatCreated(P2PChat chat);
        public void onP2PChatsRemoved(ArrayList<P2PChat> chats);
        
    }
    
    public void registerChatListCallback(ChatListCallback callback);
    
    public void unregisterChatListCallback(ChatListCallback callback);
    
    public ArrayList<P2PChat> getP2PChatList();
    
    public ArrayList<GroupChat> getGroupChatList();
    
    public ArrayList<PersistentGroupChat> getPersistentGroupChatList();
    
    public P2PChat getP2PChat(String clientJid, String jid);
    
    public P2PChat getOrCreateP2PChat(String clientJid, String jid);
    
    public void removeChat(BaseChat chat);
    
    public void createGroupChat(String[] jids);
     
    public void createPersistentGroupChat(String[] jids);
    
    public void removePersistentGroupChat(PersistentGroupChat chat);
    
    public void removeEmptyChats();
}
