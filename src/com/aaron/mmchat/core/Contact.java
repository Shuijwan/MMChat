/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * Contact.java
 *
 */

package com.aaron.mmchat.core;

import org.jivesoftware.smack.RosterEntry;

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

    RosterEntry mRosterEntry;
    
    public Contact(RosterEntry rosterEntry) {
        mRosterEntry = rosterEntry;
    }
    
    public String getJid() {
        return mRosterEntry.getUser();
    }
}
