/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * BaseManagerService.java
 *
 */

package com.aaron.mmchat.core.services;

import android.graphics.drawable.Drawable.Callback;

import com.aaron.mmchat.core.BaseXmppObject;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @Title: BaseManagerService.java
 * @Package: com.aaron.mmchat.core.services
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public abstract class BaseManagerService extends BaseXmppObject {
    
    
    
    static class Connection {
        XMPPConnection connection;
        ConnectionConfiguration configuration;
    }
    
    protected static HashMap<String, Connection> sConnections;
    
    static {
        sConnections = new HashMap<String, Connection>();
    }
    
    public XMPPConnection getXmppConnection(String clientJid) {
        return sConnections.get(clientJid).connection;
    }
}
