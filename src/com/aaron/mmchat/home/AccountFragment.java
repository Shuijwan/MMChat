/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * AccountFragment.java
 *
 */

package com.aaron.mmchat.home;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.aaron.mmchat.R;
import com.aaron.mmchat.core.AccountManager;
import com.aaron.mmchat.core.LoginManager;
import com.aaron.mmchat.core.ReconnectManager;
import com.aaron.mmchat.core.LoginManager.LoginCallback;
import com.aaron.mmchat.core.MMContext;
import com.aaron.mmchat.core.AccountManager.Account;
import com.aaron.mmchat.core.ReconnectManager.ReconnectCallback;
import com.aaron.mmchat.utils.DialogUtils;

/**
 *
 * @Title: AccountFragment.java
 * @Package: com.aaron.mmchat.home
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-15
 *
 */

public class AccountFragment extends Fragment implements OnClickListener, LoginCallback, ReconnectCallback {

    
    private View mContentView;
    private EditText mUsername;
    private EditText mPassword;
    private Button mLogin;
    private Button mDelete;
    
    private Spinner mPresenceStatus;
    private EditText mPresenceMessage;
    
    private LoginManager mLoginManager;
    private Account mAccount;
    private Dialog mSigninDialog;
    private ReconnectManager mReconnectManager;
    
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mLoginManager = (LoginManager) MMContext.peekInstance().getService(MMContext.LOGIN_SERVICE);
        mReconnectManager = ReconnectManager.getInstance();
        mReconnectManager.registerReconnectCallback(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        mContentView = inflater.inflate(R.layout.fragment_account, container, false);
     
        mPresenceStatus = (Spinner) mContentView.findViewById(R.id.presence_status);
        mPresenceMessage = (EditText) mContentView.findViewById(R.id.presence_message);
        
        mUsername = (EditText) mContentView.findViewById(R.id.account_name);    
        mPassword = (EditText) mContentView.findViewById(R.id.password);
        
        mLogin = (Button) mContentView.findViewById(R.id.login);      
        mLogin.setOnClickListener(this);
        
        mDelete = (Button) mContentView.findViewById(R.id.delete);
        mDelete.setOnClickListener(this);

        String jid = getArguments().getString("jid");
        AccountManager accountManager = AccountManager.getInstance(getActivity());
        Account account = accountManager.getAccount(jid);
        getActivity().getActionBar().setTitle(account.username);
        
        mUsername.setText(account.username);
        
        if(mLoginManager.isSignedIn(account.jid)) {
            mLogin.setText(R.string.logout);
        } else {
            mLogin.setText(R.string.login);
        }
               
        mAccount = account;
        
        return mContentView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mReconnectManager.unregisterReconnectCallback(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mLogin) {
            if(mLoginManager.isSignedIn(mAccount.jid)) {
                mLoginManager.logout(mAccount, false);
            } else {
                mLoginManager.relogin(mAccount);   
            }
            mLoginManager.registerLoginCallback(this);
            mSigninDialog = DialogUtils.showLoginingDialog(getActivity());
            
        } else if(v == mDelete) {
            mLoginManager.logout(mAccount, true);
            mLoginManager.registerLoginCallback(this);
            mSigninDialog = DialogUtils.showLoginingDialog(getActivity());
        }
        
    }

    @Override
    public void onLoginSuccessed(String clientJid) {
        mLogin.setText(R.string.logout);
        mLoginManager.unregisterLoginCallback(this);
        mSigninDialog.dismiss();
    }

    @Override
    public void onLoginFailed(String clientJid, int errorcode) {
        mLoginManager.unregisterLoginCallback(this);
        mSigninDialog.dismiss();
        
    }

    @Override
    public void onLogoutFinished(String clientJid, boolean remove) {
        mLoginManager.unregisterLoginCallback(this);
        mSigninDialog.dismiss();
        mLogin.setText(R.string.login);
        if(remove) {
            if(AccountManager.getInstance(getActivity()).getAccounts().size() == 0) {
                getActivity().finish();
            } else {
                ((HomeActivity)getActivity()).onAccountDelete();
            }
        }
    }

    @Override
    public void onConnected(String clientJid) {
        if(clientJid.equals(mAccount.jid)) {
            mLogin.setText(R.string.logout);
        }
        
    }

    @Override
    public void onDisconnected(String clientJid) {
        if(clientJid.equals(mAccount.jid)) {
            mLogin.setText(R.string.login);
        }
    }

}
