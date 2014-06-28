/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ReconnectManager.java
 *
 */

package com.aaron.mmchat.core;

import android.os.Handler;
import android.os.Looper;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @Title: ReconnectManager.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-28
 *
 */

public class ReconnectManager {
    
    public class ReconnectListener implements ConnectionListener {

        private String mClientJid;
        
        public ReconnectListener(String clientJid) {
            mClientJid = clientJid;
        }
        
        @Override
        public void connected(XMPPConnection connection) {
          notifyConnected(mClientJid);  
            
        }

        @Override
        public void authenticated(XMPPConnection connection) {
            
            
        }

        @Override
        public void connectionClosed() {
            notifyDisConnected(mClientJid);
            
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            notifyDisConnected(mClientJid);
        }

        @Override
        public void reconnectingIn(int seconds) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void reconnectionSuccessful() {
            // TODO Auto-generated method stub
            notifyConnected(mClientJid); 
        }

        @Override
        public void reconnectionFailed(Exception e) {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    public static interface ReconnectCallback {
        public void onConnected(String clientJid);
        public void onDisconnected(String clientJid);
    }
    
    private static ReconnectManager sReconnectManager;
    
    private ArrayList<ReconnectCallback> mCallbacks;
    private HashMap<String, ReconnectListener> mReconnectListeners;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    
    private ReconnectManager() {
        mCallbacks = new ArrayList<ReconnectManager.ReconnectCallback>();
        mReconnectListeners = new HashMap<String, ReconnectManager.ReconnectListener>();
    }
    
    public static synchronized ReconnectManager getInstance() {
        if(sReconnectManager == null) {
            sReconnectManager = new ReconnectManager();
        }
        
        return sReconnectManager;
    }
    
    public void registerReconnectCallback(ReconnectCallback callback) {
        if(!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }
    
    public void unregisterReconnectCallback(ReconnectCallback callback) {
        mCallbacks.remove(callback);
    }
    
    private void notifyConnected(final String clientJid) {
        mUIHandler.post(new Runnable() {
            
            @Override
            public void run() {
                for(ReconnectCallback callback : mCallbacks) {
                    callback.onConnected(clientJid);
                }
                
            }
        });
    }
    
    private void notifyDisConnected(final String clientJid) {
        mUIHandler.post(new Runnable() {
            
            @Override
            public void run() {
                for(ReconnectCallback callback : mCallbacks) {
                    callback.onDisconnected(clientJid);
                }
                
            }
        });
    }

    public ReconnectListener getReconnectListener(String clientJid) {
        if(mReconnectListeners.containsKey(clientJid)) {
            return mReconnectListeners.get(clientJid);
        }
        ReconnectListener listener =  new ReconnectListener(clientJid);
        mReconnectListeners.put(clientJid, listener);
        
        return listener;
    }
}
