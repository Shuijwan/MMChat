package com.aaron.mmchat.core;

import com.aaron.mmchat.core.AccountManager.Account;

/**
 *
 * @Title: LoginManager.java
 * @Package: com.aaron.mmchat.core
 * @Description: login interface, get LoginManager through {@link MMContext.getInstance(context).getService(MMContext.LOGIN_SERVICE)}
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public interface LoginManager {
    
    public static final int LOGIN_ERROR_OTHER = 1;
    
    public static interface LoginCallback {
        public void onLoginSuccessed(String clientJid);
        public void onLoginFailed(String clientJid, int errorcode);
    }
    
    /**
     * register login result callback
     * @param callback
     */
    public void registerLoginCallback(LoginCallback callback);
    
    /**
     * unregister login result callback
     * @param callback
     * 
     * */
    public void unregisterLoginCallback(LoginCallback callback);
    
    /**
     * login through DNS SRV
     * @param email
     * @param password
     * 
     * */
    public void login(String email, String password);
    
    /**
     * login through custom server using default port(52222)
     * @param username
     * @param password
     * @param server
     * 
     * */
    public void login(String username, String password, String server);
    
    /**
     * login through custom server and port
     * @param username
     * @param password
     * @param server
     * @param port
     * 
     * */
    public void login(String username, String password, String server, int port);
    
    /**
     * logout the client
     * @param clientJid
     * 
     * */
    public void logout(Account account, boolean remove);
    
    /**
     * is clientJid signed in
     * @param clientJid
     * 
     * */
    public boolean isSignedIn(String clientJid);

}
