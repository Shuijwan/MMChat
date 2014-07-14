/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * SettingFragment.java
 *
 */

package com.aaron.mmchat.home;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.mmchat.R;

/**
 *
 * @Title: SettingFragment.java
 * @Package: com.aaron.mmchat.home
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-14
 *
 */

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);
    }

    


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getActionBar().setTitle(R.string.setting);
        return super.onCreateView(inflater, container, savedInstanceState);
    }



    @Override
    public void onDetach() {
        super.onDetach();
       
    }
    
    

}
