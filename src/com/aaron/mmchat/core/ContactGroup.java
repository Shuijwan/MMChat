/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ContactGroup.java
 *
 */

package com.aaron.mmchat.core;

import android.preference.PreferenceManager;
import android.util.Log;

import com.aaron.mmchat.core.Contact.ContactCallback;
import com.aaron.mmchat.core.services.ContactManagerService;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @Title: ContactGroup.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-12
 *
 */

public class ContactGroup extends BaseXmppObject implements ContactCallback {
    
    public static final int ERROR_TIME_OUT = 1;
    
    public static interface ContactGroupCallback {
        /**
         * callback for @Contact is removed failed
         * @param contact, the failed removed Contact's jid
         * @param errorcode, fail reason
         * */
        public void onContactRemovedFailed(String contact, int errorcode);
        
        /**
         * callback for @Contact is removed successfully
         * @param contact, the removed Contact's jid
         * 
         * */
        public void onContactRemoved(String contact);
        
        /**
         * callback for @Contact is added successfully
         * @param contact, the added Contact's jid
         * 
         * */
        public void onContactAdded(String contact);
        
        /**
         * callback for @Contact is added failed
         * @param contact, the fail added Contact's jid
         * @param errorcode, fail reason
         * 
         * */
        public void onContactAddedFailed(String contact, int errorcode);
    }
    
    private static Comparator<Contact> sComparator = new Comparator<Contact>() {
        
        @Override
        public int compare(Contact lhs, Contact rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    };
    
    private ArrayList<ContactGroupCallback> mCallbacks;
    private RosterGroup mRosterGroup;
    private ArrayList<Contact> mContacts;
    private ArrayList<Contact> mOnlineContacts;
    private HashMap<String, Contact> mContactMap;
    private String mClientJid;
    
    public ContactGroup(String clientJid, RosterGroup rosterGroup) {
        Log.i("TTT", "g:"+rosterGroup.getName());
        mClientJid = clientJid;
        mRosterGroup = rosterGroup;
        mContacts = new ArrayList<Contact>();
        mContactMap = new HashMap<String, Contact>();
        mCallbacks = new ArrayList<ContactGroup.ContactGroupCallback>();
        Collection<RosterEntry> entries = mRosterGroup.getEntries();
        Contact contact;
        for(RosterEntry entry : entries) {
            ContactManagerService contactManager = (ContactManagerService) MMContext.peekInstance().getService(MMContext.CONTACT_SERVICE);
            contact = contactManager.getOrCreateContact(mClientJid, entry);
            contact.registerContactCallback(this);
            mContacts.add(contact);
            mContactMap.put(entry.getUser(), contact);
            Log.i("TTT", "c:"+entry.getUser());
        }
        sortContacts();
    }
    
    public String getClientJid() {
        return mClientJid;
    }
    
    /**
     * register callback to listen to ContactGroup changed
     * 
     * */
    public void registerContactGroupCallback(ContactGroupCallback callback) {
        if(!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }
    
    /**
     * unregister ContactGroup callback
     * 
     * */
    public void unregisterContactGroupCallback(ContactGroupCallback callback) {
        mCallbacks.remove(callback);
    }
    
    private void notifyContactAdded(String jid) {
        for(ContactGroupCallback callback : mCallbacks) {
            callback.onContactAdded(jid);
        }
    }
    
    private void notifyContactAddedFailed(String jid, int errorcode) {
        for(ContactGroupCallback callback : mCallbacks) {
            callback.onContactAddedFailed(jid, errorcode);
        }
    }
    
    private void notifyContactRemoved(String jid) {
        for(ContactGroupCallback callback : mCallbacks) {
            callback.onContactRemoved(jid);
        }
    }
    
    private void notifyContactRemovedFailed(String jid, int errorcode) {
        for(ContactGroupCallback callback : mCallbacks) {
            callback.onContactRemovedFailed(jid, errorcode);
        }
    }
    
    public ArrayList<Contact> getContacts() {
        return mContacts;
    }
    
    public ArrayList<Contact> getOnlineContacts() {
        return mOnlineContacts;
    }
    
    public Contact getContact(String jid) {
        return mContactMap.get(jid);
    }
    
    public String getName() {
        return mRosterGroup.getName();
    }
    
    /**
     * add contact to ContactGroup, it is internal method called by contactmanagerservice 
     * to update the internal data
     * 
     * */
    public boolean addContactInternal(RosterEntry rosterEntry) {
        if(mContactMap.containsKey(rosterEntry.getUser())) {
            return false;
        }
        
        ContactManagerService contactManager = (ContactManagerService) MMContext.peekInstance().getService(MMContext.CONTACT_SERVICE);
        Contact contact = contactManager.getOrCreateContact(mClientJid, rosterEntry);
        mContacts.add(contact);
        mContactMap.put(rosterEntry.getUser(), contact);
        sortContacts();
        notifyContactAdded(rosterEntry.getUser());
        return true;
    }
    
    
    /**
     * remove contact from ContactGroup, it is internal method called by contactmanagerservice 
     * to update the internal data
     * 
     * */
    public boolean removeContactInternal(String jid) {
        if(mContactMap.containsKey(jid)) {
            Contact contact = mContactMap.remove(jid);
            mContacts.remove(contact);
            notifyContactRemoved(jid);
            return true;
        }
        return false;
    }
    
    /**
     * update contact's RosterEntry, it is internal method called by contactmanagerservice 
     * when contact's info is changed.
     * 
     * */
    public void updateContactRosterEntry(String jid, RosterEntry entry) {
        if(mContactMap.containsKey(jid)) {
            Contact contact = mContactMap.get(jid);
            contact.setRosterEntry(entry);
        }
    }
    
    /**
     * remove a contact from this group
     * @param contact, contact to be removed
     * 
     * */
    public void removeContact(final Contact contact) {
        
        enqueneTask(new Runnable() {
            
            @Override
            public void run() {
                ContactManagerService contactManager = (ContactManagerService) MMContext.peekInstance().getService(MMContext.CONTACT_SERVICE);
                try {
                    mRosterGroup.removeEntry(contact.mRosterEntry);
                    contactManager.addPendingRemoveContact(contact.getJid());
                } catch (NoResponseException e) {
                    e.printStackTrace();
                    contactManager.removePendingRemoveContact(contact.getJid());
                    notifyContactRemovedFailed(contact.getJid(), ERROR_TIME_OUT);
                } catch (XMPPErrorException e) {
                    e.printStackTrace();
                    contactManager.removePendingRemoveContact(contact.getJid());
                    notifyContactRemovedFailed(contact.getJid(), ERROR_TIME_OUT);
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                    contactManager.removePendingRemoveContact(contact.getJid());
                    notifyContactRemovedFailed(contact.getJid(), ERROR_TIME_OUT);
                } 
            }
        });   
    }
    
    /**
     * add a contact to this group
     * @param contact, contact to be added
     * 
     * */
    public void addContact(final String jid) {
        
        enqueneTask(new Runnable() {
            
            @Override
            public void run() {
                ContactManagerService contactManager = (ContactManagerService) MMContext.peekInstance().getService(MMContext.CONTACT_SERVICE);
                try {
                    Roster roster = contactManager.getRoster(mClientJid);
                    roster.createEntry(jid, jid.substring(jid.lastIndexOf("@")+1), new String[] {getName()});
                    contactManager.addPendingAddContact(jid);
                } catch (NoResponseException e) {
                    e.printStackTrace();
                    contactManager.removePendingAddContact(jid);
                    notifyContactAddedFailed(jid, ERROR_TIME_OUT);
                } catch (XMPPErrorException e) {
                    e.printStackTrace();
                    contactManager.removePendingAddContact(jid);
                    notifyContactAddedFailed(jid, ERROR_TIME_OUT);
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                    contactManager.removePendingAddContact(jid);
                    notifyContactAddedFailed(jid, ERROR_TIME_OUT);
                } catch (NotLoggedInException e) {
                    e.printStackTrace();
                    contactManager.removePendingAddContact(jid);
                    notifyContactAddedFailed(jid, ERROR_TIME_OUT);
                } 
            }
        });   
    }
    
    private void sortContacts() {
        Collections.sort(mContacts, sComparator);
    }

    @Override
    public void onContactUpdated(Contact contact) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onContactPresenceUpdated(Contact contact) {
        
        
    }
}
