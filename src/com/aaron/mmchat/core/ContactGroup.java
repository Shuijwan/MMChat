/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ContactGroup.java
 *
 */

package com.aaron.mmchat.core;

import android.util.Log;

import com.aaron.mmchat.core.services.ContactManagerService;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import java.util.ArrayList;
import java.util.Collection;
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

public class ContactGroup extends BaseXmppObject {
    
    private RosterGroup mRosterGroup;
    private ArrayList<Contact> mContacts;
    private HashMap<String, Contact> mContactMap;
    
    public ContactGroup(RosterGroup rosterGroup) {
        Log.i("TTT", "g:"+rosterGroup.getName());
        mRosterGroup = rosterGroup;
        mContacts = new ArrayList<Contact>();
        mContactMap = new HashMap<String, Contact>();
        Collection<RosterEntry> entries = mRosterGroup.getEntries();
        Contact contact;
        for(RosterEntry entry : entries) {
            contact = new Contact(entry);
            mContacts.add(contact);
            mContactMap.put(entry.getUser(), contact);
            Log.i("TTT", "c:"+entry.getUser());
        }
    }
    
    public String getName() {
        return mRosterGroup.getName();
    }
    
    public boolean addContactInternal(RosterEntry rosterEntry) {
        if(mContactMap.containsKey(rosterEntry.getUser())) {
            return false;
        }
        Contact contact = new Contact(rosterEntry);
        mContacts.add(contact);
        mContactMap.put(rosterEntry.getUser(), contact);
        return true;
    }
    
    public boolean removeContactInternal(String jid) {
        if(mContactMap.containsKey(jid)) {
            mContactMap.remove(jid);
            return true;
        }
        return false;
    }
    
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
                    contactManager.notifyContactRemovedFailed(contact.getJid(), ContactManager.CONTACT_OPERATION_ERROR);
                } catch (XMPPErrorException e) {
                    e.printStackTrace();
                    contactManager.removePendingRemoveContact(contact.getJid());
                    contactManager.notifyContactRemovedFailed(contact.getJid(), ContactManager.CONTACT_OPERATION_ERROR);
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                    contactManager.removePendingRemoveContact(contact.getJid());
                    contactManager.notifyContactRemovedFailed(contact.getJid(), ContactManager.CONTACT_OPERATION_ERROR);
                } 
            }
        });   
    }
}
