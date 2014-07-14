/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * Contact.java
 *
 */

package com.aaron.mmchat.core;

import android.text.TextUtils;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;

import java.util.ArrayList;

/**
 *
 * @Title: Contact.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-12
 *
 */

public class Contact extends BaseXmppObject {
    
    public static final int AVAILABLE = 0;
    public static final int AWAY = 1;
    public static final int DND = 2;
    public static final int UNAVAILABLE = 3;
    
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
    
    RosterEntry mRosterEntry;
    
    public Contact(RosterEntry rosterEntry) {
        mRosterEntry = rosterEntry;
        mCallbacks = new ArrayList<Contact.ContactCallback>();
    }
    
    public void setRosterEntry(RosterEntry entry) {
        mRosterEntry = entry;
        notifyContactUpdated();
    }
    
    public void registerContactCallback(ContactCallback callback) {
        if(!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }
    
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
    
    public void updatePresence() {
        mRosterEntry.updatePresence();
        notifyContactPresenceUpdated();
    }
    
    public String getJid() {
        return mRosterEntry.getUser();
    }
    
    public String getName() {
        String name = mRosterEntry.getName();
        if(TextUtils.isEmpty(name)) {
            name = getJid();
        }
        return name;
    }
    
    public int getPresence() {
        Presence.Mode mode = mRosterEntry.getPresence();
        if(mode == Mode.available || mode == Mode.chat) {
            return AVAILABLE;
        }
        if(mode == Mode.away || mode == Mode.xa) {
            return AWAY;
        }
        if(mode == Mode.dnd) {
            return DND;
        }
        return UNAVAILABLE;
    }
    
    public String getPresenceStatus() {
        return mRosterEntry.getPresenceStatus();
    }
}
