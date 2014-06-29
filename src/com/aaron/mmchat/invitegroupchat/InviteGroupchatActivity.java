/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * InviteGroupchatActivity.java
 *
 */

package com.aaron.mmchat.invitegroupchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.aaron.mmchat.core.AccountManager.Account;

/**
 *
 * @Title: InviteGroupchatActivity.java
 * @Package: com.aaron.mmchat.invitegroupchat
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-29
 *
 */

public class InviteGroupchatActivity extends Activity {
    
    public static void startInviteGroupchatActivity(Context context, String clientJid) {
        Intent intent = new Intent(context, InviteGroupchatActivity.class);
        intent.putExtra("clientJid", clientJid);
    }
    
    private Account mAccount;

}
