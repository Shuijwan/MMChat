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
import android.widget.TextView;

import com.aaron.mmchat.R;
import com.aaron.mmchat.core.AccountManager;
import com.aaron.mmchat.core.AccountType;
import com.aaron.mmchat.core.LoginManager;
import com.aaron.mmchat.core.LoginManager.LoginCallback;
import com.aaron.mmchat.core.MMContext;
import com.aaron.mmchat.core.AccountManager.Account;
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

public class AccountFragment extends Fragment implements OnClickListener, LoginCallback {

    private EditText mPassword;
    private Button mLogin;
    private Button mDelete;
    
    private Spinner mPresenceStatus;
    private EditText mPresenceMessage;
    
    private LoginManager mLoginManager;
    private Account mAccount;
    private Dialog mSigninDialog;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mLoginManager = (LoginManager) MMContext.peekInstance().getService(MMContext.LOGIN_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        int index = getArguments().getInt("accountIndex");
        AccountManager accountManager = AccountManager.getInstance(getActivity());
        Account account = accountManager.getAccounts().get(index);
        getActivity().getActionBar().setTitle(account.username);
        mAccount = account;
        
        View view = inflater.inflate(R.layout.fragment_account, null);
        
        mPresenceStatus = (Spinner) view.findViewById(R.id.presence_status);
        mPresenceMessage = (EditText) view.findViewById(R.id.presence_message);
        
        TextView username = (TextView) view.findViewById(R.id.username);
        username.setText(mAccount.username);
        
        mPassword = (EditText) view.findViewById(R.id.password);
        
        mLogin = (Button) view.findViewById(R.id.login);
        
        if(mLoginManager.isSignedIn(account.jid)) {
            mLogin.setText(R.string.logout);
        } else {
            mLogin.setText(R.string.login);
        }
        
        mLogin.setOnClickListener(this);
        
        mDelete = (Button) view.findViewById(R.id.delete);
        return view;
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if(v == mLogin) {
            if(mLoginManager.isSignedIn(mAccount.jid)) {
                
            } else {
                AccountType accountType = AccountType.getAccountTypeById(mAccount.accountTypeId);
                
                if(accountType.needSrv) {
                    mLoginManager.login(mAccount.username, mAccount.password);
                } else {
                    mLoginManager.login(mAccount.username , mAccount.password, accountType.domain, accountType.port);
                } 
                
                mLoginManager.registerLoginCallback(this);
                mSigninDialog = DialogUtils.showLoginingDialog(getActivity());
            }
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

}
