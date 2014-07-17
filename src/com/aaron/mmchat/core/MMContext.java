package com.aaron.mmchat.core;

import android.content.Context;

import com.aaron.mmchat.core.services.BaseManagerService;
import com.aaron.mmchat.core.services.ChatManagerService;
import com.aaron.mmchat.core.services.ContactManagerService;
import com.aaron.mmchat.core.services.LoginManagerService;


import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackConfiguration;

import java.util.HashMap;

/**
 *
 * @Title: MMContext.java
 * @Package: com.aaron.mmchat.core
 * @Description: Entry for retrieving the core services,see {@link LoginManager},{@link ContactManager},{@link ChatManager}
 * use getService(String service).
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public class MMContext {
    
    public static final String LOGIN_SERVICE = "login";
    public static final String CONTACT_SERVICE = "contact";
    public static final String CHAT_SERVICE = "chat";
    public static final String CONFIG_SERVICE = "config";
    
    private static MMContext sInstance; 
    
    private static Context sAppContext;
    
    private CoreThreadPool mThreadPool;
    
    private HashMap<String, BaseManagerService> mServices;
      
    private MMContext() {
        Context appContext = sAppContext;
        
        mThreadPool = new CoreThreadPool();
        
        mServices = new HashMap<String, BaseManagerService>();
        LoginManagerService loginManagerService = new LoginManagerService(appContext);
        mServices.put(LOGIN_SERVICE, loginManagerService);
        mServices.put(CONTACT_SERVICE, new ContactManagerService(loginManagerService));
        mServices.put(CHAT_SERVICE, new ChatManagerService(loginManagerService));
        
        SmackAndroid.init(appContext);
        SmackConfiguration.DEBUG_ENABLED = true;
    }
    
    /**
     * get instance of MMContext
     * 
     * */
    public synchronized static MMContext getInstance() {
        if(sInstance == null) {
            sInstance = new MMContext();
        }
        return sInstance;
    }
    
    /**
     * get core service according to given service name.
     * @param service, service name, MMContext.LOGIN_SERVICE, MMContext.CONTACT_SERVICE, .etc
     * 
     * */
    public BaseManagerService getService(String service) {
        return mServices.get(service);
    }

    /**
     * init MMContext's global info, should be inited in the beginning of app
     * 
     * */
    public static void init(Context context) {
        sAppContext = context;
    }
    
    static Context getAppContext() {
        return sAppContext;
    }
    
    /**
     * free resources
     * 
     * */
    public void cleanup() {
        mThreadPool.stopThreadPool();
    }
    
    CoreThreadPool getCoreThreadPool() {
        return mThreadPool;
    }
}
