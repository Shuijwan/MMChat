/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * BaseManagerService.java
 *
 */

package com.aaron.mmchat.core.services;

import android.graphics.drawable.Drawable.Callback;

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

public abstract class BaseManagerService {
    
    private static ExecutorService sThreadPool;
    
    
    public void init() {
        if(sThreadPool == null) {
            sThreadPool = Executors.newFixedThreadPool(5);
        }
    }

    public void enqueneTask(Runnable runnable) {
        init();
        sThreadPool.execute(runnable);
    }
    
    
}
