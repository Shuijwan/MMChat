/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ContactManagerService.java
 *
 */

package com.aaron.mmchat.core.services;

import android.content.Context;
import android.util.Log;

import com.aaron.mmchat.core.Contact;
import com.aaron.mmchat.core.ContactGroup;
import com.aaron.mmchat.core.ContactManager;
import com.aaron.mmchat.core.LoginManager;
import com.aaron.mmchat.core.LoginManager.LoginCallback;
import com.aaron.mmchat.core.MMContext;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @Title: ContactManagerService.java
 * @Package: com.aaron.mmchat.core.services
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public class ContactManagerService extends BaseManagerService implements ContactManager, LoginCallback {

    private class ContactListListener implements RosterListener {

        private String clientJid;
        
        public ContactListListener(String clientJid) {
            this.clientJid = clientJid;
        }
        
        @Override
        public void entriesAdded(Collection<String> addresses) {
            if(mPendingAddContacts != null) {
                Iterator<String> iterator = mPendingAddContacts.iterator();
                String jid;
                while(iterator.hasNext()) {
                    jid = iterator.next();
                    if(addresses.contains(jid)) {
                        notifyContactAdded(jid);
                        iterator.remove();
                    }
                }
            }
            
            Roster roster = mRosters.get(clientJid);
            Collection<RosterGroup> groups = roster.getGroups();
            
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void presenceChanged(Presence presence) {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    private HashMap<String, ArrayList<ContactGroup>> mAllContactLists;
    private HashMap<String, Roster> mRosters;
    private ArrayList<ContactListCallback> mCallbacks;
    private LoginManager mLoginManager;
    private ArrayList<String> mPendingAddContacts;
    private ArrayList<String> mPendingRemoveContacts;
    
    public ContactManagerService(LoginManager manager) {
        mCallbacks = new ArrayList<ContactManager.ContactListCallback>();
        mRosters = new HashMap<String, Roster>();
        mAllContactLists = new HashMap<String, ArrayList<ContactGroup>>();
        mLoginManager = manager;
        mLoginManager.registerLoginCallback(this);
    }

    @Override
    public void registerContactListCallback(ContactListCallback callback) {
        if(!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterContactListCallback(ContactListCallback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public ArrayList<ContactGroup> getContactList(String clientJid) {
        return mAllContactLists.get(clientJid);
    }

    @Override
    public boolean refreshContactList(String clientJid) {
        Roster roster = mRosters.get(clientJid);
        try {
            roster.reload();
            return true;
        } catch (NotLoggedInException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (NotConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public void updateRoster(String clientJid) {
        
    }
    
    public void addPendingAddContact(String jid) {
        if(mPendingAddContacts == null) {
            mPendingAddContacts = new ArrayList<String>();
        }
        if(!mPendingAddContacts.contains(jid)) {
            mPendingAddContacts.add(jid);
        }
    }
    
    public void addPendingRemoveContact(String jid) {
        if(mPendingRemoveContacts == null) {
            mPendingRemoveContacts = new ArrayList<String>();
        }
        if(!mPendingRemoveContacts.contains(jid)) {
            mPendingRemoveContacts.add(jid);
        }
    }
    
    public void notifyContactAdded(String contact) {
        for(ContactListCallback callback : mCallbacks) {
            callback.onContactAdded(contact);
        }
    }
    
    public void notifyContactAddedFailed(String contact, int errorcode) {
        for(ContactListCallback callback : mCallbacks) {
            callback.onContactAddedFailed(contact, errorcode);
        }
    }
    
    public void notifyContactRemoved(String contact) {
        for(ContactListCallback callback : mCallbacks) {
            callback.onContactRemoved(contact);
        }
    }
    
    public void notifyContactRemovedFailed(String contact, int errorcode) {
        for(ContactListCallback callback : mCallbacks) {
            callback.onContactRemovedFailed(contact, errorcode);
        }
    }
    
//    public void notifyContactGroupAddedFailed(ContactGroup group, int errorcode) {
//        for(ContactListCallback callback : mCallbacks) {
//            callback.onContactGroupAddedFailed(group, errorcode);
//        }
//    }
//    
//    public void notifyContactGroupAdded(ContactGroup group) {
//        for(ContactListCallback callback : mCallbacks) {
//            callback.onContactGroupAdded(group);
//        }
//    }
//    
//    public void notifyContactGroupRemovedFailed(ContactGroup group, int errorcode) {
//        for(ContactListCallback callback : mCallbacks) {
//            callback.onContactGroupRemovedFailed(group, errorcode);
//        }
//    }
//    
//    public void notifyContactGroupRemoved(ContactGroup group) {
//        for(ContactListCallback callback : mCallbacks) {
//            callback.onContactGroupRemoved(group);
//        }
//    }

    @Override
    public void onLoginSuccessed(String clientJid) {
        XMPPConnection connection = getXmppConnection(clientJid);
        Roster roster = connection.getRoster();
        mRosters.put(clientJid, roster);
        roster.addRosterListener(new ContactListListener(clientJid));
    }

    @Override
    public void onLoginFailed(String clientJid, int errorcode) {
        
        
    }
}
