/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * Contact.java
 *
 */

package com.aaron.mmchat.core;

import android.text.TextUtils;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence.Mode;

import java.util.ArrayList;

/**
 *
 * @Title: Contact.java
 * @Package: com.aaron.mmchat.core
 * @Description: represent a xmpp contact object.
 * 
 * @Author: aaron
 * @Date: 2014-6-12
 *
 */

public class Contact extends BaseXmppObject {
    
    public static interface ContactCallback {
        /**
         * callback for @Contact' info changed
         * */
        public void onContactUpdated(Contact contact);
        
        /**
         * callback for @Contact's presence changed
         * 
         * */
        public void onContactPresenceUpdated(Contact contact);
    }

    private ArrayList<ContactCallback> mCallbacks;
    private Presence mPresence;
    
    RosterEntry mRosterEntry;
    
    public Contact(RosterEntry rosterEntry) {
        mRosterEntry = rosterEntry;
        mCallbacks = new ArrayList<Contact.ContactCallback>();
        mPresence = new Presence();
        
        updatePresence();
    }
    
    private void updatePresence() {
        mPresence.presenceType = getPresenceType();
        mPresence.presenceStatus = getPresenceStatus();
    }
    
    /**
     * set the internal RosterEntry, it is called by core, not for UI
     * 
     * */
    public void setRosterEntry(RosterEntry entry) {
        mRosterEntry = entry;
        updatePresence();
        notifyContactUpdated();
    }
    
    /**
     * register a ContactCallback
     * 
     * */
    public void registerContactCallback(ContactCallback callback) {
        if(!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }
    
    /**
     * unregister a ContactCallback
     * 
     * */
    public void unregisterContactCallback(ContactCallback callback) {
        mCallbacks.remove(callback);
    }
    
    private void notifyContactUpdated() {
        for(ContactCallback callback : mCallbacks) {
            callback.onContactUpdated(this);
        }
    }
    
    private void notifyContactPresenceUpdated() {
        for(ContactCallback callback : mCallbacks) {
            callback.onContactPresenceUpdated(this);
        }
    }
    
    /**
     * update the internal RosterEntry's presence, this is called by core, not for UI
     * 
     * */
    public void updateRosterPresence() {
        mRosterEntry.updatePresence();
        updatePresence();
        notifyContactPresenceUpdated();
    }
    
    /**
     * return the Jid of this Contact
     * 
     * */
    public String getJid() {
        return mRosterEntry.getUser();
    }
    
    /**
     * return the Name of this Contact
     * 
     * */
    public String getName() {
        String name = mRosterEntry.getName();
        if(TextUtils.isEmpty(name)) {
            name = getJid();
        }
        return name;
    }
    
    /**
     * return the Presence of this Contact
     * 
     * */
    public Presence getPresence() {
        return mPresence;
    }
    
    private int getPresenceType() {
        Mode mode = mRosterEntry.getPresence();
        if(mode == Mode.available || mode == Mode.chat) {
            return Presence.AVAILABLE;
        }
        if(mode == Mode.away || mode == Mode.xa) {
            return Presence.AWAY;
        }
        if(mode == Mode.dnd) {
            return Presence.DND;
        }
        return Presence.UNAVAILABLE;
    }
    
    private String getPresenceStatus() {
        return mRosterEntry.getPresenceStatus();
    }
}
