/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ChatManagerServices.java
 *
 */

package com.aaron.mmchat.core.services;

import com.aaron.mmchat.core.BaseChat;
import com.aaron.mmchat.core.ChatManager;
import com.aaron.mmchat.core.GroupChat;
import com.aaron.mmchat.core.LoginManager;
import com.aaron.mmchat.core.LoginManager.LoginCallback;
import com.aaron.mmchat.core.P2PChat;
import com.aaron.mmchat.core.PersistentGroupChat;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.carbons.CarbonManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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

public class ChatManagerService extends BaseManagerService implements ChatManager, LoginCallback {
    
    private ArrayList<P2PChat> mP2pChats;
    private ArrayList<GroupChat> mGroupChats;
    private ArrayList<PersistentGroupChat> mPersistentGroupChats;
    private Set<ChatListCallback> mCallbacks;
    
    private LoginManager mLoginManager;

    public ChatManagerService(LoginManager manager) {
        mP2pChats = new ArrayList<P2PChat>();
        mGroupChats = new ArrayList<GroupChat>();
        mPersistentGroupChats = new ArrayList<PersistentGroupChat>();
        mCallbacks = new HashSet<ChatManager.ChatListCallback>();
        mLoginManager = manager;
        mLoginManager.registerLoginCallback(this);
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
            if(clientJid.equals(chat.getClientJid()) && jid.equals(chat.getParticipantJid())) {
                return chat;
            }
        }
        return null;
    }
    
    @Override
    public P2PChat getOrCreateP2PChat(String clientJid, String jid) {
        
        P2PChat p2pChat = getP2PChat(clientJid, jid);
        if(p2pChat != null) {
            return p2pChat;
        }
        
        XMPPConnection connection = getXmppConnection(clientJid);
        org.jivesoftware.smack.ChatManager chatManager = org.jivesoftware.smack.ChatManager.getInstanceFor(connection);
   
        Chat chat = chatManager.createChat(jid, null);
        p2pChat = new P2PChat(clientJid, chat);
        mP2pChats.add(p2pChat);
        notifyP2PChatCreated(p2pChat);
        return p2pChat;
    }

    @Override
    public void removeChat(BaseChat chat) {
        if(chat instanceof P2PChat) {
            ArrayList<P2PChat> chats = new ArrayList<P2PChat>();
            chats.add((P2PChat)chat);
            mP2pChats.remove(chat);
            notifyP2PChatsRemoved(chats);
        }
    }

    @Override
    public void createGroupChat(String[] jids) {
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

    public void updateMessageDeliverStatus() {
        
    }
    
    private void notifyP2PChatCreated(P2PChat chat) {
        for(ChatListCallback callback : mCallbacks) {
            callback.onP2PChatCreated(chat);
        }
    }
    
    private void notifyP2PChatsRemoved(ArrayList<P2PChat> chats) {
        for(ChatListCallback callback : mCallbacks) {
            callback.onP2PChatsRemoved(chats);
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

    @Override
    public void onLoginSuccessed(String clientJid) {
        XMPPConnection connection = getXmppConnection(clientJid);
        
        enableCarbons(connection);
        
        org.jivesoftware.smack.ChatManager chatManager = org.jivesoftware.smack.ChatManager.getInstanceFor(connection);
        chatManager.addChatListener(new ChatCreatedListener(clientJid));
    }

    private void enableCarbons(final XMPPConnection connection) {
        enqueneTask(new Runnable() {
            
            @Override
            public void run() {
                try {
                    if(CarbonManager.getInstanceFor(connection).isSupportedByServer()) {
                        CarbonManager.getInstanceFor(connection).sendCarbonsEnabled(true);
                    }
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                }
                
            }
        });
        
    }
    
    @Override
    public void onLoginFailed(String clientJid, int errorcode) {
        
    }

    @Override
    public void removeEmptyChats() {
        Iterator<P2PChat> iterator = mP2pChats.iterator();
        P2PChat p2pChat;
        ArrayList<P2PChat> chats = null;
        while(iterator.hasNext()) {
            p2pChat = iterator.next();
            if(p2pChat.getLastMessage() == null) {
                iterator.remove();
                if(chats == null) {
                    chats = new ArrayList<P2PChat>();
                }
                chats.add(p2pChat);
            }
        }
        if(chats != null) {
            notifyP2PChatsRemoved(chats);
        }
    }

    @Override
    public void onLogoutFinished(String clientJid, boolean remove) {
        if(remove) {
           ArrayList<P2PChat> chats = null;
           Iterator<P2PChat> iterator = mP2pChats.iterator();
           P2PChat p2pChat;

           while(iterator.hasNext()) {
               p2pChat = iterator.next();
               if(p2pChat.getClientJid().equals(clientJid)) {
                   iterator.remove();
                   if(chats == null) {
                       chats = new ArrayList<P2PChat>();
                   }
                   chats.add(p2pChat);
               }
           }
           if(chats != null) {
               notifyP2PChatsRemoved(chats);
           }
        }
        
    }
}
