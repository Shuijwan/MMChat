/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * BaseManagerService.java
 *
 */

package com.aaron.mmchat.core.services;

import com.aaron.mmchat.core.BaseXmppObject;
import com.aaron.mmchat.core.ReconnectManager;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import java.util.HashMap;

/**
 *
 * @Title: BaseManagerService.java
 * @Package: com.aaron.mmchat.core.services
 * @Description: Base class for services handle the core logical, all services share
 * the underly XmppConnection, see @LoginManagerService, @ContactManagerService and @ChatManagerService.
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
    
    private static HashMap<String, Connection> sConnections;
    
    static {
        sConnections = new HashMap<String, Connection>();
    }
    
    public XMPPConnection getXmppConnection(String clientJid) {
        return sConnections.get(clientJid).connection;
    }
    
    public Connection getConnection(String clientJid) {
        return sConnections.get(clientJid);
    }
    
    public void addConnection(String clientJid, Connection connection) {
        sConnections.put(clientJid, connection);
        connection.connection.addConnectionListener(ReconnectManager.getInstance().getReconnectListener(clientJid));
    }
    
    public void removeConnection(String clientJid) {
        Connection connection = sConnections.remove(clientJid);
        if(connection != null) {
            connection.connection.removeConnectionListener(ReconnectManager.getInstance().getReconnectListener(clientJid));
        }
    }
}
