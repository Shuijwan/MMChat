/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * HomeActivity.java
 *
 */

package com.aaron.mmchat.home;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.View;

import com.aaron.mmchat.R;
import com.aaron.mmchat.core.AccountManager;
import com.aaron.mmchat.home.MenuFragment.ActiveMenuChangedCallback;
import com.aaron.mmchat.login.ChooseAccountTypeActivity;

/**
 * @Title: HomeActivity.java
 * @Package: com.aaron.mmchat.home
 * @Description:
 * @Author: aaron
 * @Date: 2014-6-14
 */

public class HomeActivity extends Activity implements ActiveMenuChangedCallback {

    public static void startHomeActivity(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    private MenuFragment menuFragment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerListener listener;
    private String mFragmentName = null;

    public void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        if (!hasValidAccount()) {
            ChooseAccountTypeActivity.startChooseAccountTypeActivity(this);
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        menuFragment = (MenuFragment) getFragmentManager().findFragmentById(R.id.menu_frame);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        initDrawerLayout();

        switchFragmentTo(MenuFragment.MENU_CONTACTLIST);
    }

    private void initDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // listener = (DrawerListener) menuFragment;
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
        drawerLayout, /* DrawerLayout object */
        R.drawable.title_nav, /* nav drawer image to replace 'Up' caret */
        R.string.drawer_open, /* "open drawer" description for accessibility */
        R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (listener != null) {
                    listener.onDrawerClosed(drawerView);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (listener != null) {
                    listener.onDrawerOpened(drawerView);
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (listener != null) {
                    listener.onDrawerSlide(drawerView, slideOffset);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (listener != null) {
                    listener.onDrawerStateChanged(newState);
                }
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void switchFragmentTo(int itemId) {
        Class<? extends Fragment> fragmentClass = null;

        switch (itemId) {
            case MenuFragment.MENU_CHATLIST:
                fragmentClass = ChatListFragment.class;
                break;
            case MenuFragment.MENU_CONTACTLIST:
                fragmentClass = ContactListFragment.class;
                break;
            case MenuFragment.MENU_SETTING:
                fragmentClass = SettingFragment.class;
                break;

        }

        FragmentManager fragmentManager = getFragmentManager();

        final String fragmentClassName = fragmentClass.getName();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;

        if (mFragmentName != null
                && (fragment = fragmentManager.findFragmentByTag(mFragmentName)) != null) {
            fragmentTransaction.detach(fragment);
        }

        mFragmentName = fragmentClassName;

        if ((fragment = fragmentManager.findFragmentByTag(mFragmentName)) == null) {
            fragmentTransaction.add(R.id.main_content,
                    Fragment.instantiate(this, fragmentClassName), mFragmentName);
        } else {
            fragmentTransaction.attach(fragment);
        }

        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onActiveMenuChanged(int menuId) {
        switchFragmentTo(menuId);
    }

    private boolean hasValidAccount() {
        AccountManager accountManager = AccountManager.getInstance(this);
        return accountManager.getAccounts().size() > 0;
    }
}
