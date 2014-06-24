/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * LoginManagerService.java
 *
 */

package com.aaron.mmchat.core.services;

import android.R.anim;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.aaron.mmchat.core.AccountManager;
import com.aaron.mmchat.core.AccountType;
import com.aaron.mmchat.core.MMContext;
import com.aaron.mmchat.core.AccountManager.Account;
import com.aaron.mmchat.core.LoginManager;

import de.duenndns.ssl.MemorizingTrustManager;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import org.jivesoftware.smackx.caps.EntityCapsManager;
import org.jivesoftware.smackx.caps.cache.SimpleDirectoryPersistentCache;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    private static final String TAG = LoginManagerService.class.getSimpleName();
    
    private static final int DEFAULT_PORT = 5222;
    
    private static final int MSG_LOGIN_SUCCESS = 0;
    private static final int MSG_LOGIN_FAIL = 1;
    private static final int MSG_SIGNOUT_FINISHED = 2;
    
    private boolean mEntityCapInited = false;
    private Context mContext;
    
    private ArrayList<LoginCallback> mCallbacks;
    
    private Handler mUIHandler = new Handler(Looper.getMainLooper()) {
        
        @Override
        public void handleMessage(android.os.Message msg) {
            String jid = (String) msg.obj;
            switch (msg.what) {
                case MSG_LOGIN_SUCCESS:
                    Log.d(TAG, "login success:"+jid);
                    notifyLoginSuccess(jid);
                    break;
                case MSG_LOGIN_FAIL:
                    Log.d(TAG, "login failed:"+jid);
                    notifyLoginFailed(jid, msg.arg1);
                    break;
                case MSG_SIGNOUT_FINISHED:
                    boolean remove = msg.arg1 == 1;
                    if(remove) {
                        AccountManager.getInstance(mContext).deleteAccount(jid);
                        sConnections.remove(jid);
                    }
                    notifyLogoutFinished(jid, remove);
                    break;
                default:
                    break;
            }
        }
    };
    
    public LoginManagerService(Context context) {
        mContext = context;
        mCallbacks = new ArrayList<LoginManager.LoginCallback>();
    }

    @Override
    public void login(String username, String password, String server) {
        login(username, password, server, DEFAULT_PORT);   
    }

    @Override
    public void login(String email, String password) {
        int index = email.lastIndexOf("@");
        String domain = email.substring(index+1);
        
        ConnectionConfiguration configuration = initConfiguration(domain);
        XMPPConnection connection = new XMPPTCPConnection(configuration);
        
        Connection con = new Connection();
        con.connection = connection;
        con.configuration = configuration;
        
        initServiceDiscovery(connection);
        doLoginByDisCovery(con, email, password);
      
    }

    @Override
    public void login(String username, String password, String server, int port) {
        ConnectionConfiguration configuration = initConfiguration(server, port);
        XMPPConnection connection = new XMPPTCPConnection(configuration);
        
        Connection con = new Connection();
        con.connection = connection;
        con.configuration = configuration;

        initServiceDiscovery(connection);
        doLogin(con, username, password, server);
        
    }
    
    private ConnectionConfiguration initConfiguration(String domain) {
        ConnectionConfiguration configuration = new ConnectionConfiguration(domain);
        initConfiguration(configuration);
        return configuration;
    }
    
    private ConnectionConfiguration initConfiguration(String server, int port) {
        
        ConnectionConfiguration configuration = new ConnectionConfiguration(server, port);
        initConfiguration(configuration);
        return configuration;
    }

    private void initConfiguration(ConnectionConfiguration configuration) {
        configuration.setReconnectionAllowed(false);
        configuration.setSendPresence(false);
        configuration.setCompressionEnabled(true);
        configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        configuration.setRosterLoadedAtLogin(false);
        
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new X509TrustManager[] { new MemorizingTrustManager(mContext) },
                    new java.security.SecureRandom());
            configuration.setCustomSSLContext(sc);
        } catch (java.security.GeneralSecurityException e) {
            e.printStackTrace();
        }   
    }
      
    private void initServiceDiscovery(XMPPConnection connection) {
        // register connection features
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        
        if(!mEntityCapInited) {
            // init Entity Caps manager with storage in app's cache dir
            try {
                File capsCacheDir = new File(mContext.getCacheDir(), "entity-caps-cache");
                capsCacheDir.mkdirs();
                EntityCapsManager.setPersistentCache(new SimpleDirectoryPersistentCache(capsCacheDir));
                
            } catch (java.io.IOException e) {
                Log.e("TTT", "Could not init Entity Caps cache: " + e.getLocalizedMessage());
            }
            mEntityCapInited = true;
        }
        // reference PingManager, set ping flood protection to 10s
        PingManager.setDefaultPingInterval(10*1000);
        PingManager.getInstanceFor(connection).setPingInterval(10*1000);
        

        // reference DeliveryReceiptManager, add listener
        DeliveryReceiptManager dm = DeliveryReceiptManager.getInstanceFor(connection);
        dm.enableAutoReceipts();
        
        dm.addReceiptReceivedListener(new ReceiptReceivedListener() { // DOES NOT WORK IN CARBONS
            public void onReceiptReceived(String fromJid, String toJid, String receiptId) {
                Log.i("TTT","from:"+fromJid+"   to:"+toJid+"   receiptId:"+receiptId);
                ChatManagerService chatManagerService = (ChatManagerService) MMContext.peekInstance().getService(MMContext.CHAT_SERVICE);
                chatManagerService.updateMessageDeliverStatus();
            }});
    }
    
    private void doLoginByDisCovery(final Connection connection, final String email, final String password) {
        enqueneTask(new Runnable() {
            
            @Override
            public void run() {
                String rawJid = null;
                int index = email.lastIndexOf("@");
                String username = email.substring(0, index);
                String domain = email.substring(index+1);
                try {
                    connection.connection.connect();
                    connection.connection.login(username, password, "MMChat");
                    rawJid = connection.connection.getUser();
                    int resIndex = rawJid.indexOf("/");
                    String bareJid = rawJid.substring(0, resIndex);
                    sConnections.put(bareJid, connection);
                    AccountManager.getInstance(mContext).addAccount(bareJid, domain, email, password);
                    
                    sendLoginSuccessMsg(bareJid);
                } catch (SmackException e) {
                    e.printStackTrace();
                    sendLoginFailMsg(email, LOGIN_ERROR_OTHER);
                } catch (IOException e) {
                    e.printStackTrace();
                    sendLoginFailMsg(email, LOGIN_ERROR_OTHER);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    sendLoginFailMsg(email, LOGIN_ERROR_OTHER);
                }
                
            }
        });
    }
    
    private void doLogin(final Connection connection, final String username, final String password, final String domain) {
        enqueneTask(new Runnable() {
            
            @Override
            public void run() {
                String rawjid = null;
                String id = username+"@"+domain;
                try {
                    connection.connection.connect();
                    connection.connection.login(username, password, "MMChat");
                    rawjid = connection.connection.getUser();
                    int index = rawjid.indexOf("/");
                    String bareJid = rawjid.substring(0, index);
                    sConnections.put(bareJid, connection);
                    AccountManager.getInstance(mContext).addAccount(bareJid, domain, username, password);
                    
                    sendLoginSuccessMsg(bareJid);
                } catch (SmackException e) {
                    e.printStackTrace();
                    sendLoginFailMsg(id, LOGIN_ERROR_OTHER);
                } catch (IOException e) {
                    e.printStackTrace();
                    sendLoginFailMsg(id, LOGIN_ERROR_OTHER);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    sendLoginFailMsg(id, LOGIN_ERROR_OTHER);
                }
                
            }
        });
    }

    private void sendLoginSuccessMsg(String jid) {
       android.os.Message msg = android.os.Message.obtain(mUIHandler, MSG_LOGIN_SUCCESS, jid);
       msg.sendToTarget();
    }
    
    private void sendLoginFailMsg(String jid, int reason) {
        android.os.Message msg = android.os.Message.obtain(mUIHandler, MSG_LOGIN_FAIL, reason, 0 , jid);
        msg.sendToTarget();
     }
    
    private void sendLogoutFinishedMsg(String jid, boolean remove) {
        android.os.Message msg = android.os.Message.obtain(mUIHandler, MSG_SIGNOUT_FINISHED, remove ? 1 : 0, 0, jid);
        msg.sendToTarget();
    }
    
    @Override
    public void registerLoginCallback(LoginCallback callback) {
        if(!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterLoginCallback(LoginCallback callback) {
        mCallbacks.remove(callback);
    }  
    
    private void notifyLogoutFinished(String clientJid, boolean remove) {
        for(LoginCallback callback : mCallbacks) {
            callback.onLogoutFinished(clientJid, remove);
        }
    }
    
    private void notifyLoginSuccess(String clientJid) {
        for(LoginCallback callback : mCallbacks) {
            callback.onLoginSuccessed(clientJid);
        }
    }
    
    private void notifyLoginFailed(String clientJid, int errorcode) {
        for(LoginCallback callback : mCallbacks) {
            callback.onLoginFailed(clientJid, errorcode);
        }
    }

    @Override
    public void logout(final Account account, final boolean remove) {
        enqueneTask(new Runnable() {
            
            @Override
            public void run() {
                Connection connection = getConnection(account.jid);
                try {
                    connection.configuration.setReconnectionAllowed(false);
                    connection.connection.disconnect();
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                }
                
                sendLogoutFinishedMsg(account.jid, remove);
            }
        });

    }

    @Override
    public boolean isSignedIn(String clientJid) {
        Connection connection = sConnections.get(clientJid);
        if(connection != null) {
            return connection.connection.isConnected() && connection.connection.isAuthenticated();
        }
        return false;
    }

    @Override
    public void relogin(final Account account) {
        final Connection connection = sConnections.get(account.jid);
        if(connection != null) {
            enqueneTask(new Runnable() {
                
                @Override
                public void run() {
                    connection.configuration.setReconnectionAllowed(true);
                    try {
                        connection.connection.connect();
                        sendLoginSuccessMsg(account.jid);
                    } catch (SmackException e) {
                        e.printStackTrace();
                        sendLoginFailMsg(account.jid, LOGIN_ERROR_OTHER);
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendLoginFailMsg(account.jid, LOGIN_ERROR_OTHER);
                    } catch (XMPPException e) {
                        e.printStackTrace();
                        sendLoginFailMsg(account.jid, LOGIN_ERROR_OTHER);
                    }
                    
                }
            });
        } else {
            AccountType accountType = AccountType.getAccountTypeById(account.accountTypeId);
            if(accountType.needSrv) {
                login(account.username, account.password);
            } else {
                login(account.username , account.password, accountType.domain, accountType.port);
            } 
        }
        
    }

}
