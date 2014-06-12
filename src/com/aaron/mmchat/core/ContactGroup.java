/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ContactGroup.java
 *
 */

package com.aaron.mmchat.core;

import com.aaron.mmchat.core.services.ContactManagerService;

import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import java.util.ArrayList;

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
    
    public ContactGroup(RosterGroup rosterGroup) {
        mRosterGroup = rosterGroup;
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
                    contactManager.notifyContactRemovedFailed(contact.getJid(), ContactManager.CONTACT_OPERATION_ERROR);
                } catch (XMPPErrorException e) {
                    e.printStackTrace();
                    contactManager.notifyContactRemovedFailed(contact.getJid(), ContactManager.CONTACT_OPERATION_ERROR);
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                    contactManager.notifyContactRemovedFailed(contact.getJid(), ContactManager.CONTACT_OPERATION_ERROR);
                } 
            }
        });   
    }
}
