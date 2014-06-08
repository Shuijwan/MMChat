package com.aaron.menu;

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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MenuTestActivity extends Activity {
    
    TextView text2;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text2 = (TextView) findViewById(R.id.text2);
//        DisplayMetrics DM = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(DM);
//        Method m;
//        FileOutputStream stream;
//        try {
//            m = Surface.class.getMethod("screenshot", int.class, int.class);
//            Bitmap b = (Bitmap) m.invoke(null, DM.widthPixels,DM.heightPixels);
//            File f = new File("/sdcard/screenshot.png");
//            f.createNewFile();
//            stream = new FileOutputStream(f);
//            b.compress(CompressFormat.PNG, 100, stream);
//        } catch (NoSuchMethodException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } finally {
//            if(stream != null) {
//                stream.close();
//            }
//        }
//       
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Log.i("TTT", "onStart");
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.i("TTT","onResume");
    }
    
    @Override
    public void onPause() {
        super.onPause();
        Log.i("TTT","onPause");
    }
    
    public void onWindowFocusChanged(boolean foucs) {
        if(foucs) {
            int[] location = new int[2];
            text2.getLocationInWindow(location);
            Log.i("TTT","location:"+location[0]+"   "+location[1]+"   "+text2.getVisibility());
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	/*
    	 * add()方法的四个参数，依次是： 1、组别，如果不分组的话就写Menu.NONE,
    	 * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单 3、顺序，那个菜单现在在前面由这个参数的大小决定
    	 * 4、文本，菜单的显示文本
    	 */
    	menu.add(Menu.NONE, Menu.FIRST + 1, 5, "删除").setIcon(android.R.drawable.ic_menu_delete);
    	// setIcon()方法为菜单设置图标，这里使用的是系统自带的图标，同学们留意一下,以
    	// android.R开头的资源是系统提供的，我们自己提供的资源是以R开头的
    	menu.add(Menu.NONE, Menu.FIRST + 2, 2, "保存").setIcon(android.R.drawable.ic_menu_edit);
    	menu.add(Menu.NONE, Menu.FIRST + 3, 6, "帮助").setIcon(android.R.drawable.ic_menu_help);
    	menu.add(Menu.NONE, Menu.FIRST + 4, 1, "添加").setIcon(android.R.drawable.ic_menu_add);
    	menu.add(Menu.NONE, Menu.FIRST + 5, 4, "详细").setIcon(android.R.drawable.ic_menu_info_details);
    	menu.add(Menu.NONE, Menu.FIRST + 6, 3, "发送").setIcon(android.R.drawable.ic_menu_send);
    	// return true才会起作用
    	return true;

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title").setMessage("sssssss").show();
    }
    
    // 菜单项被选择事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case Menu.FIRST + 1:
    		Toast.makeText(this, "删除菜单被点击了", Toast.LENGTH_LONG).show();
    	    startActivity(new Intent(this, MenuTest2.class));
    		break;
    	case Menu.FIRST + 2:
    		Toast.makeText(this, "保存菜单被点击了", Toast.LENGTH_LONG).show();
    		break;
    	case Menu.FIRST + 3:
    		Toast.makeText(this, "帮助菜单被点击了", Toast.LENGTH_LONG).show();
    		break;
    	case Menu.FIRST + 4:
    		Toast.makeText(this, "添加菜单被点击了", Toast.LENGTH_LONG).show();
    		break;
    	case Menu.FIRST + 5:
    		Toast.makeText(this, "详细菜单被点击了", Toast.LENGTH_LONG).show();
    		break;
    	case Menu.FIRST + 6:
    		Toast.makeText(this, "发送菜单被点击了", Toast.LENGTH_LONG).show();
    		break;
    	}

    	return false;
    }

    // 选项菜单被关闭事件，菜单被关闭有三种情形，menu按钮被再次点击、back按钮被点击或者用户选择了某一个菜单项
    @Override
    public void onOptionsMenuClosed(Menu menu) {
    	Toast.makeText(this, "选项菜单关闭了", Toast.LENGTH_LONG).show();
    }

    // 菜单被显示之前的事件
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	Toast.makeText(this, "选项菜单显示之前onPrepareOptionsMenu方法会被调用，你可以用此方法来根据打当时的情况调整菜单", Toast.LENGTH_LONG).show();
    	// 如果返回false，此方法就把用户点击menu的动作给消费了，onCreateOptionsMenu方法将不会被调用
    	return true;
    }
}