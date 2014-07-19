/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * Presence.java
 *
 */

package com.aaron.mmchat.core;

/**
 *
 * @Title: Presence.java
 * @Package: com.aaron.mmchat.core
 * @Description: xmpp presence
 * 
 * @Author: aaron
 * @Date: 2014-7-18
 *
 */

public class Presence {
    
    /**
     * No Presence, UI no need to care.
     * 
     * */
    public static final int NONE = -1;
    
    /**
     * Availabe
     * 
     * */
    public static final int AVAILABLE = 0;
    
    /**
     * Away
     * 
     * */
    public static final int AWAY = 1;
    
    /**
     * Do not disturb
     * 
     * */
    public static final int DND = 2;
    
    /**
     * Unavailable, offline
     * 
     * */
    public static final int UNAVAILABLE = 3;
    
    int presenceType;
    String presenceStatus;
    
    /**
     * return presenceType, like AVAILABLE, AWAY, DND, UNAVAILABLE, 
     * NONE will not be returned
     * 
     * */
    public int getPresenceType() {
        return presenceType;
    }
    
    /**
     * return presenceStatus or custom message
     * 
     * */
    public String getPresenceStatus() {
        return presenceStatus;
    }
}
