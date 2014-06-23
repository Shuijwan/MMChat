/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * DialogUtils.java
 *
 */

package com.aaron.mmchat.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.aaron.mmchat.R;

/**
 *
 * @Title: DialogUtils.java
 * @Package: com.aaron.mmchat.utils
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-23
 *
 */

public class DialogUtils {
    
    public static Dialog showLoginingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.signing_in));
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

}
