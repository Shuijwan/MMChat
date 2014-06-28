/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * LoginActivity.java
 *
 */

package com.aaron.mmchat.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.mmchat.R;
import com.aaron.mmchat.core.AccountType;
import com.aaron.mmchat.core.LoginManager;
import com.aaron.mmchat.core.LoginManager.LoginCallback;
import com.aaron.mmchat.core.MMContext;
import com.aaron.mmchat.home.HomeActivity;
import com.aaron.mmchat.utils.DialogUtils;

/**
 *
 * @Title: LoginActivity.java
 * @Package: com.aaron.mmchat.login
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-13
 *
 */

public class LoginActivity extends Activity implements OnClickListener, LoginCallback {

    public static void startLoginActivity(Context context, AccountType account) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("account", account);
        context.startActivity(intent);
    }
    
    private ImageView mAccountTypeIcon;
    private TextView mAccountTypeName;
    private EditText mUserName, mPassword, mServer, mPort;
    private Button mLogin;
    private View mAdvancedSetting, mAdvancedTitle;
    private CheckBox mSSL;
    
    private AccountType mAccount;
    private LoginManager mLoginManager;
    private Dialog mSigninDialog;
    
    public void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        setTitle(R.string.login);
        setContentView(R.layout.activity_login);
        
        mAccount = getIntent().getParcelableExtra("account"); 
        
        mAccountTypeIcon = (ImageView) findViewById(R.id.account_type_icon);
        mAccountTypeName = (TextView) findViewById(R.id.account_type_name);
        mUserName = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mServer = (EditText) findViewById(R.id.server);
        mPort = (EditText) findViewById(R.id.port);
        mSSL = (CheckBox) findViewById(R.id.ssl);
        mAdvancedTitle = findViewById(R.id.advanced_title);
        mAdvancedSetting = findViewById(R.id.advanced_wrapper);
        mLogin = (Button) findViewById(R.id.login);
        
        if(mAccount == null) {
            setupCustomAccount();
        } else {
            setupKnownAccount();
        }
        
        mLogin.setOnClickListener(this);
        
        mLoginManager = (LoginManager) MMContext.getInstance(this).getService(MMContext.LOGIN_SERVICE);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupCustomAccount() {
        mAccountTypeIcon.setVisibility(View.GONE);
        mAccountTypeName.setVisibility(View.GONE);
        
        mServer.setVisibility(View.VISIBLE);
        mAdvancedTitle.setVisibility(View.VISIBLE);
        findViewById(R.id.advanced_dash).setVisibility(View.VISIBLE);
        mAdvancedTitle.setOnClickListener(this);
    }

    private void setupKnownAccount() {
        mAccountTypeIcon.setImageResource(mAccount.icon);
        mAccountTypeName.setText(mAccount.name);
        if(!TextUtils.isEmpty(mAccount.description)) {
            mUserName.setHint(mAccount.description);
        }
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
    
    private boolean validateField() {
        String username = mUserName.getText().toString();
        String password = mPassword.getText().toString();
        
        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if(mAccount == null) {
            String server = mServer.getText().toString();
            if(server.isEmpty()) {
                Toast.makeText(this, "Server不能为空", Toast.LENGTH_SHORT).show();
                return false;
            }
            int port = 0;
            try {
                port = Integer.parseInt(mPort.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Port格式不正确", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(port <= 0) {
                Toast.makeText(this, "Port格式不正确", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void onClick(View v) {
        if(v == mLogin) {
            boolean validated = validateField();
            if(validated) {
                if(mAccount == null) {
                    mLoginManager.login(mUserName.getText().toString() , mPassword.getText().toString(), mServer.getText().toString(), Integer.parseInt(mPort.getText().toString()));
                } else {
                    if(mAccount.needSrv) {
                        mLoginManager.login(mUserName.getText().toString() , mPassword.getText().toString());
                    } else {
                        mLoginManager.login(mUserName.getText().toString() , mPassword.getText().toString(), mAccount.domain, mAccount.port);
                    } 
                }
                mLoginManager.registerLoginCallback(this);
                mSigninDialog = DialogUtils.showLoginingDialog(this);
            }
            
        } else if(v == mAdvancedTitle) {
            if(mAdvancedSetting.getVisibility() == View.GONE) {
                mAdvancedSetting.setVisibility(View.VISIBLE);
            } else {
                mAdvancedSetting.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoginSuccessed(String clientJid) {
      
        Toast.makeText(LoginActivity.this, "login success", Toast.LENGTH_LONG).show();
        mSigninDialog.dismiss();
        mLoginManager.unregisterLoginCallback(this);
        finish();
        HomeActivity.startHomeActivity(LoginActivity.this);
    }

    @Override
    public void onLoginFailed(String clientJid, int errorcode) {
      
        Toast.makeText(LoginActivity.this, "login faild", Toast.LENGTH_LONG).show();
        mSigninDialog.dismiss();
        mLoginManager.unregisterLoginCallback(this);
    }
    
    public void onBackPressed() {
        ChooseAccountTypeActivity.startChooseAccountTypeActivity(this);
        finish();
    }

    @Override
    public void onLogoutFinished(String clientJid, boolean remove) {
        // TODO Auto-generated method stub
        
    }
}
