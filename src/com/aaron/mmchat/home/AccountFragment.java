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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.aaron.mmchat.R;
import com.aaron.mmchat.core.AccountManager;
import com.aaron.mmchat.core.ClientUser;
import com.aaron.mmchat.core.ContactManager;
import com.aaron.mmchat.core.LoginManager;
import com.aaron.mmchat.core.Presence;
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

public class AccountFragment extends Fragment implements OnClickListener, LoginCallback, ReconnectCallback, OnItemSelectedListener {

    
    private View mContentView;
    private EditText mUsername;
    private EditText mPassword;
   
    private Button mDelete;
    
    private Spinner mPresenceType;
    private EditText mPresenceStatus;
    
    private LoginManager mLoginManager;
    private Account mAccount;
 
    private ReconnectManager mReconnectManager;
    
    private ContactManager mContactManager;
    private ClientUser mClientUser;
    
    private Dialog mDialog;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mLoginManager = (LoginManager) MMContext.getInstance().getService(MMContext.LOGIN_SERVICE);
        mLoginManager.registerLoginCallback(this);
        mReconnectManager = ReconnectManager.getInstance();
        mReconnectManager.registerReconnectCallback(this);
        mContactManager = (ContactManager) MMContext.getInstance().getService(MMContext.CONTACT_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        mContentView = inflater.inflate(R.layout.fragment_account, container, false);
     
        mPresenceType = (Spinner) mContentView.findViewById(R.id.presence_type);
        mPresenceStatus = (EditText) mContentView.findViewById(R.id.presence_status);
        
        mUsername = (EditText) mContentView.findViewById(R.id.account_name);    
        mPassword = (EditText) mContentView.findViewById(R.id.password);
        
        
        mDelete = (Button) mContentView.findViewById(R.id.delete);
        mDelete.setOnClickListener(this);

        String jid = getArguments().getString("jid");
        AccountManager accountManager = AccountManager.getInstance(getActivity());
        mAccount = accountManager.getAccount(jid);
        getActivity().getActionBar().setTitle(mAccount.username);
        
        mUsername.setText(mAccount.username);
        mClientUser = mContactManager.getClientUser(jid);
        
        updatePresenceStatus(jid);
        mPresenceType.setOnItemSelectedListener(this);
        
        
        
        return mContentView;
    }

    private void updatePresenceStatus(String clientUser) {
        if(clientUser.equals(mAccount.jid)) {
            Presence presence = mClientUser.getPresence();
            mPresenceType.setSelection(presence.getPresenceType());
            mPresenceStatus.setText(presence.getPresenceStatus());
        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mLoginManager.unregisterLoginCallback(this);
        mReconnectManager.unregisterReconnectCallback(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mDelete) {
            mLoginManager.logout(mAccount, true);
            mDialog = DialogUtils.showProgressDialog(getActivity(), "deleting");
        }
    }

    @Override
    public void onLoginSuccessed(String clientJid) {
        updatePresenceStatus(clientJid);
    }

    @Override
    public void onLoginFailed(String clientJid, int errorcode) {
        updatePresenceStatus(clientJid);
    }

    @Override
    public void onLogoutFinished(String clientJid, boolean remove) {
        updatePresenceStatus(clientJid);
        if(remove) {
            mDialog.dismiss();
            if(AccountManager.getInstance(getActivity()).getAccounts().size() == 0) {
                getActivity().finish();
            } else {
                ((HomeActivity)getActivity()).onAccountDelete();
            }
        }
    }

    @Override
    public void onConnected(String clientJid) {
        updatePresenceStatus(clientJid);
    }

    @Override
    public void onDisconnected(String clientJid) {
        updatePresenceStatus(clientJid);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        mClientUser.setPresence(mPresenceType.getSelectedItemPosition(), mPresenceStatus.getText().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        
    }

}
