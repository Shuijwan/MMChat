/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * LoginManagerService.java
 *
 */

package com.aaron.mmchat.core.services;

import android.R.interpolator;
import android.content.Context;
import android.util.Log;

import com.aaron.mmchat.core.LoginManager;

import de.duenndns.ssl.MemorizingTrustManager;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import org.jivesoftware.smackx.caps.EntityCapsManager;
import org.jivesoftware.smackx.caps.cache.SimpleDirectoryPersistentCache;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.iqversion.packet.Version;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @Title: LoginManagerService.java
 * @Package: com.aaron.mmchat.core.services
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public class LoginManagerService extends BaseManagerService implements LoginManager {

    private static final int DEFAULT_PORT = 5222;
    
    
    
    static class Connection {
        XMPPConnection connection;
        ConnectionConfiguration configuration;
    }
    
    private Context mContext;
    
    private HashMap<String, Connection> mConnections;
   
    public LoginManagerService(Context context) {
        mContext = context;
        mConnections = new HashMap<String, LoginManagerService.Connection>();
    }

    @Override
    public void login(String username, String password, String server, LoginCallback callback) {
        login(username, password, server, DEFAULT_PORT, callback);   
    }

    @Override
    public void login(String email, String password, LoginCallback callback) {
        int index = email.lastIndexOf("@");
        String username = email.substring(0, index);
        String domain = email.substring(index+1);
        
        ConnectionConfiguration configuration = initConfiguration(domain);
        XMPPConnection connection = new XMPPTCPConnection(configuration);
        
        Connection con = new Connection();
        con.connection = connection;
        con.configuration = configuration;
        
        mConnections.put(email, con);
        initServiceDiscovery(connection);
        doLogin(connection, username, password, callback);
      
    }

    @Override
    public void login(String username, String password, String server, int port,
            LoginCallback callback) {
        ConnectionConfiguration configuration = new ConnectionConfiguration(server, port);
        
    }
    
    private ConnectionConfiguration initConfiguration(String domain) {
        
        ConnectionConfiguration configuration = new ConnectionConfiguration(domain);
        configuration.setReconnectionAllowed(false);
        configuration.setSendPresence(false);
        configuration.setCompressionEnabled(false); // disable for now
        configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new X509TrustManager[] { new MemorizingTrustManager(mContext) },
                    new java.security.SecureRandom());
            configuration.setCustomSSLContext(sc);
        } catch (java.security.GeneralSecurityException e) {
            e.printStackTrace();
        }   
        
        return configuration;
    }
    
    private ConnectionConfiguration initConfiguration(String server, int port) {
        
        ConnectionConfiguration configuration = new ConnectionConfiguration(server, port);
        configuration.setReconnectionAllowed(false);
        configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new X509TrustManager[] { new MemorizingTrustManager(mContext) },
                    new java.security.SecureRandom());
            configuration.setCustomSSLContext(sc);
        } catch (java.security.GeneralSecurityException e) {
            e.printStackTrace();
        }   
        
        return configuration;
    }
    
    private void initServiceDiscovery(XMPPConnection connection) {
        // register connection features
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        File capsCacheDir = null;
        // init Entity Caps manager with storage in app's cache dir
        try {
            if (capsCacheDir == null) {
                capsCacheDir = new File(mContext.getCacheDir(), "entity-caps-cache");
                capsCacheDir.mkdirs();
                EntityCapsManager.setPersistentCache(new SimpleDirectoryPersistentCache(capsCacheDir));
            }
        } catch (java.io.IOException e) {
            Log.e("TTT", "Could not init Entity Caps cache: " + e.getLocalizedMessage());
        }

        // reference PingManager, set ping flood protection to 10s
        PingManager.setDefaultPingInterval(10*1000);
        PingManager.getInstanceFor(connection).setPingInterval(10*1000);
        

        // reference DeliveryReceiptManager, add listener
        DeliveryReceiptManager dm = DeliveryReceiptManager.getInstanceFor(connection);
        dm.enableAutoReceipts();
        
        dm.addReceiptReceivedListener(new ReceiptReceivedListener() { // DOES NOT WORK IN CARBONS
            public void onReceiptReceived(String fromJid, String toJid, String receiptId) {
                Log.d("TTT", "got delivery receipt for " + receiptId);
//                changeMessageDeliveryStatus(receiptId, ChatConstants.DS_ACKED);
            }});
    }
    
    private void doLogin(final XMPPConnection connection, final String username, final String password, final LoginCallback callback) {
        enqueneTask(new Runnable() {
            
            @Override
            public void run() {
                try {
                    connection.connect();
                    connection.login(username, password, "MMChat");
                    callback.onLoginSuccessed();
                } catch (SmackException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    callback.onLoginFailed(LOGIN_ERROR_OTHER);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    callback.onLoginFailed(LOGIN_ERROR_OTHER);
                } catch (XMPPException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    callback.onLoginFailed(LOGIN_ERROR_OTHER);
                }
                
            }
        });
    }  

}
