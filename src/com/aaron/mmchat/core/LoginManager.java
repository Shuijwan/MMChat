/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * LoginManager.java
 *
 */

package com.aaron.mmchat.core;

import android.R.interpolator;

/**
 *
 * @Title: LoginManager.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public interface LoginManager {
    
    public static final int LOGIN_ERROR_OTHER = 1;
    
    public static interface LoginCallback {
        public void onLoginSuccessed();
        public void onLoginFailed(int errorcode);
    }
    
    
    public void login(String email, String password, LoginCallback callback);
    
    public void login(String username, String password, String server, LoginCallback callback);
    
    public void login(String username, String password, String server, int port, LoginCallback callback);

}
