package com.aaron.mmchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.mmchat.core.LoginManager;
import com.aaron.mmchat.core.LoginManager.LoginCallback;
import com.aaron.mmchat.core.MMContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MenuTestActivity extends Activity implements OnClickListener, LoginCallback {
    
    Button text2;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text2 = (Button) findViewById(R.id.text1);
        text2.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        MMContext context = MMContext.getInstance(this);
        LoginManager manager = (LoginManager) context.getService(MMContext.LOGIN_SERVICE);
        manager.login("flyalizee@gmail.com", "45397254", this);
    }
    @Override
    public void onLoginSuccessed() {
        // TODO Auto-generated method stub
        Log.i("TTT","success");
    }
    @Override
    public void onLoginFailed(int errorcode) {
        // TODO Auto-generated method stub
        Log.i("TTT","fail");
    }
}