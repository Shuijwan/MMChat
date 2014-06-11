/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * LoginManagerService.java
 *
 */

package com.aaron.mmchat.core.services;

import android.R.interpolator;
import android.content.Context;

import com.aaron.mmchat.core.LoginManager;

import de.duenndns.ssl.MemorizingTrustManager;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

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
        String domain = email.substring(index);
        
        ConnectionConfiguration configuration = initConfiguration(domain);
        XMPPConnection connection = new XMPPTCPConnection(configuration);
        
        Connection con = new Connection();
        con.connection = connection;
        con.configuration = configuration;
        
        mConnections.put(email, con);
        
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
