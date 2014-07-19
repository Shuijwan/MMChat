/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ClientUser.java
 *
 */

package com.aaron.mmchat.core;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;

import com.aaron.mmchat.core.AccountManager.Account;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;

/**
 *
 * @Title: ClientUser.java
 * @Package: com.aaron.mmchat.core
 * @Description: represent a xmpp level's account info. for non-xmpp Account info, see {@link Account}
 * 
 * @Author: aaron
 * @Date: 2014-7-18
 *
 */

public class ClientUser extends BaseXmppObject {

    private String mClientJid;
    private XMPPConnection mConnection;
    private com.aaron.mmchat.core.Presence mPresence;
    
    private int mPendingPresenceType = com.aaron.mmchat.core.Presence.NONE;
    
    public ClientUser(String clientJid, XMPPConnection connection) {
        mClientJid = clientJid;
        mConnection = connection;
        
        mPresence = new com.aaron.mmchat.core.Presence();
        mPresence.presenceType = com.aaron.mmchat.core.Presence.UNAVAILABLE;
        mPresence.presenceStatus = getPresenceStatus(com.aaron.mmchat.core.Presence.UNAVAILABLE);
        
    }
    
    /**
     * refresh self's presence after login successfully.
     * this should be called by core.
     * 
     * */
    public void refreshSelfPresence() {
        String presenceStatus = null;
        if(mPendingPresenceType != com.aaron.mmchat.core.Presence.NONE) {
            presenceStatus = getPresenceStatus(mPendingPresenceType);
        } else {
            presenceStatus = getPresenceStatus(com.aaron.mmchat.core.Presence.AVAILABLE);
        }

        Presence presence = new Presence(Presence.Type.available);
        mapPresenceMode(mPendingPresenceType == com.aaron.mmchat.core.Presence.NONE ? com.aaron.mmchat.core.Presence.AVAILABLE : mPendingPresenceType , presence);
        presence.setStatus(presenceStatus);
        presence.setPriority(127);
        try {
            mConnection.sendPacket(presence);
        } catch (NotConnectedException e) {
            e.printStackTrace();
            mPresence.presenceType = com.aaron.mmchat.core.Presence.UNAVAILABLE;
            presenceStatus = getPresenceStatus(com.aaron.mmchat.core.Presence.UNAVAILABLE);
            mPresence.presenceStatus = presenceStatus;
            return;
        }   
        
        mPresence.presenceType = mPendingPresenceType == com.aaron.mmchat.core.Presence.NONE ? com.aaron.mmchat.core.Presence.AVAILABLE : mPendingPresenceType ;
        mPresence.presenceStatus = presenceStatus;
        
        mPendingPresenceType = com.aaron.mmchat.core.Presence.NONE;
    }
    
    /**
     * return current {@link com.aaron.mmchat.core.Presence}
     * 
     * */
    public com.aaron.mmchat.core.Presence getPresence() {
        return mPresence;
    }
    
    /**
     * manual setup new Presence
     * @param presenceType, {@link com.aaron.mmchat.core.Presence}, AVAILABLE, AWAY, DND,  .etc
     * @param presenceStatus, the presenceStatus
     * 
     * */
    public void setPresence(int presenceType, String presenceStatus) {
        if(presenceType == mPresence.presenceType && presenceStatus.equals(mPresence.presenceStatus)) {
            return;
        }
        
        Account account = AccountManager.peekInstance().getAccount(mClientJid);
        LoginManager loginManager = (LoginManager) MMContext.getInstance().getService(MMContext.LOGIN_SERVICE);
        if(presenceType == com.aaron.mmchat.core.Presence.UNAVAILABLE) {    
            loginManager.logout(account, false);
            return;
        } 
        
        if(presenceStatus == null) {
            presenceStatus = getPresenceStatus(presenceType);
        } else {
            savePresenceStatus(presenceStatus);
        }

        if(mConnection == null) {
            mPresence.presenceType = com.aaron.mmchat.core.Presence.UNAVAILABLE;
            presenceStatus = getPresenceStatus(com.aaron.mmchat.core.Presence.UNAVAILABLE);
            mPresence.presenceStatus = presenceStatus;
            
            mPendingPresenceType = presenceType;
            return;
        }
        
        if(!loginManager.isSignedIn(mClientJid)) {
            loginManager.relogin(account);
        }
        
        Presence presence = new Presence(Presence.Type.available);
        mapPresenceMode(presenceType, presence);
        presence.setStatus(presenceStatus);
        presence.setPriority(127);
        try {
            mConnection.sendPacket(presence);
        } catch (NotConnectedException e) {
            e.printStackTrace();
            mPresence.presenceType = com.aaron.mmchat.core.Presence.UNAVAILABLE;
            presenceStatus = getPresenceStatus(com.aaron.mmchat.core.Presence.UNAVAILABLE);
            mPresence.presenceStatus = presenceStatus;
            
            mPendingPresenceType = presenceType;
            return;
        }   
        
        mPresence.presenceType = presenceType;
        mPresence.presenceStatus = presenceStatus;
    }
    
    private void mapPresenceMode(int presenceType, Presence presence) {
        switch (presenceType) {
            case com.aaron.mmchat.core.Presence.AVAILABLE:
                presence.setMode(Mode.available);
                break;
            case com.aaron.mmchat.core.Presence.AWAY:
                presence.setMode(Mode.away);
                break;
            case com.aaron.mmchat.core.Presence.DND:
                presence.setMode(Mode.dnd);
                break;
            default:
                presence.setMode(Mode.available);
                break;
        }
    }
    
    private String getPresenceStatus(int presenceType) {
        String presenceStatus = null;
        SharedPreferences preferences = MMContext.getAppContext().getSharedPreferences(mClientJid, 0);
        if(preferences.contains("presenceStatus")) {
            presenceStatus = preferences.getString("presenceStatus", "");
        } else {
            Resources resources = MMContext.getAppContext().getResources();
            presenceStatus = resources.getStringArray(com.aaron.mmchat.R.array.presence_status_array)[presenceType];
        }
        
        return presenceStatus;
    }
    
    private void savePresenceStatus(String presenceStatus) {
        SharedPreferences preferences = MMContext.getAppContext().getSharedPreferences(mClientJid, 0);
        Editor editor = preferences.edit();
        editor.putString("presenceStatus", presenceStatus);
        editor.commit();
    }
    
    /**
     * update the XmppConnection after being connected, this should be set by core.
     * @param connection, the XmppConnection
     * 
     * */
    public void setConnection(XMPPConnection connection) {
        mConnection = connection;
    }
    
    /**
     * delete this ClientUser after user delete relative Account
     * this should be called by core.
     * 
     * */
    public void delete() {
        SharedPreferences preferences = MMContext.getAppContext().getSharedPreferences(mClientJid, 0);
        preferences.edit().clear().commit();
    }
    
    void updatePresenceTypeInternal(int presenceType) {
        mPresence.presenceType = presenceType;
    }
}
