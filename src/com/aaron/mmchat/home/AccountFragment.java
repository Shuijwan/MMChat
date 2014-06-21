/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * AccountFragment.java
 *
 */

package com.aaron.mmchat.home;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.mmchat.R;
import com.aaron.mmchat.core.AccountManager;
import com.aaron.mmchat.core.AccountManager.Account;

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

public class AccountFragment extends Fragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        int index = getArguments().getInt("accountIndex");
        AccountManager accountManager = AccountManager.getInstance(getActivity());
        Account account = accountManager.getAccounts().get(index);
        getActivity().getActionBar().setTitle(account.username);
        
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
    }

}
