/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * NetWorkUtils.java
 *
 */

package com.aaron.mmchat.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *
 * @Title: NetWorkUtils.java
 * @Package: com.aaron.mmchat.utils
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-29
 *
 */

public class NetWorkUtils {
    
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }

}
