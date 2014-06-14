/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * BaseXmppObject.java
 *
 */

package com.aaron.mmchat.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @Title: BaseXmppObject.java
 * @Package: com.aaron.mmchat.core
 * @Description: All smack sync api, should extends from BaseXmppObject to do async call,
 * for example, @BaseManagerService, @ContactGroup 
 * 
 * @Author: aaron
 * @Date: 2014-6-12
 *
 */

public abstract class BaseXmppObject {
    
    private static ExecutorService sThreadPool;
    
    static {
        sThreadPool = Executors.newFixedThreadPool(3);
    }
    
    public void enqueneTask(Runnable runnable) {
        sThreadPool.submit(runnable);
    }
}
