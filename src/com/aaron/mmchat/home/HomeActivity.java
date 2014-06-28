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
import android.view.MenuItem;

import com.aaron.mmchat.R;
import com.aaron.mmchat.core.AccountManager;
import com.aaron.mmchat.home.MenuFragment.MenuCallback;
import com.aaron.mmchat.login.ChooseAccountTypeActivity;

/**
 * @Title: HomeActivity.java
 * @Package: com.aaron.mmchat.home
 * @Description:
 * @Author: aaron
 * @Date: 2014-6-14
 */

public class HomeActivity extends Activity implements MenuCallback {

    public static void startHomeActivity(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mFragmentName = null;

    public void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        if (!hasValidAccount()) {
            ChooseAccountTypeActivity.startChooseAccountTypeActivity(this);
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        initDrawerLayout();

        switchFragmentTo(MenuFragment.MENU_CHATLIST);
    }

    private void initDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                    drawerLayout, /* DrawerLayout object */
                    R.drawable.title_nav, /* nav drawer image to replace 'Up' caret */
                    R.string.drawer_open, /* "open drawer" description for accessibility */
                    R.string.drawer_close /* "close drawer" description for accessibility */
                    );
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
            default:
                fragmentClass = AccountFragment.class;
                break;

        }

        FragmentManager fragmentManager = getFragmentManager();

        final String fragmentClassName = fragmentClass.getName();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;

        if (mFragmentName != null
                && (fragment = fragmentManager.findFragmentByTag(mFragmentName)) != null) {
            if(mFragmentName.equals(AccountFragment.class.getName())) {
                fragmentTransaction.hide(fragment);
            } else {
                fragmentTransaction.detach(fragment);
            }
        }

        mFragmentName = fragmentClassName;

        if ((fragment = fragmentManager.findFragmentByTag(mFragmentName)) == null) {
            if(itemId >= MenuFragment.MENU_ACCOUNT) {
                int accountIndex = itemId - MenuFragment.MENU_ACCOUNT;
                Bundle bundle = new Bundle();
                bundle.putInt("accountIndex", accountIndex);
                fragment = Fragment.instantiate(this, fragmentClassName, bundle);
            } else {
                fragment = Fragment.instantiate(this, fragmentClassName);
            }
            fragmentTransaction.add(R.id.main_content, fragment , mFragmentName);
        } else {
            Bundle bundle = fragment.getArguments();
            if(itemId >= MenuFragment.MENU_ACCOUNT) {
                int accountIndex = itemId - MenuFragment.MENU_ACCOUNT;
                bundle.putInt("accountIndex", accountIndex);
                fragmentTransaction.show(fragment);
            } else {
                fragmentTransaction.attach(fragment);
            }
        }
        
        fragmentTransaction.commit();
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
    
    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            mDrawerToggle.onOptionsItemSelected(item);
        }
        return true;
    }
    
    @Override
    public void onActiveMenuChanged(int menuId) {
        switchFragmentTo(menuId);
    }

    private boolean hasValidAccount() {
        AccountManager accountManager = AccountManager.getInstance(this);
        return accountManager.getAccounts().size() > 0;
    }

    @Override
    public void onMenuClicked() {
        drawerLayout.closeDrawers();
    }
    
    public void onAccountDelete() {
        MenuFragment fragment = (MenuFragment) getFragmentManager().findFragmentById(R.id.menu_frame);
        fragment.onAccountDeleted();
    }
}
