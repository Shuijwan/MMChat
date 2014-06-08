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
    	 * add()�������ĸ������������ǣ� 1��������������Ļ���дMenu.NONE,
    	 * 2��Id���������Ҫ��Android�������Id��ȷ����ͬ�Ĳ˵� 3��˳���Ǹ��˵�������ǰ������������Ĵ�С����
    	 * 4���ı����˵�����ʾ�ı�
    	 */
    	menu.add(Menu.NONE, Menu.FIRST + 1, 5, "ɾ��").setIcon(android.R.drawable.ic_menu_delete);
    	// setIcon()����Ϊ�˵�����ͼ�꣬����ʹ�õ���ϵͳ�Դ���ͼ�꣬ͬѧ������һ��,��
    	// android.R��ͷ����Դ��ϵͳ�ṩ�ģ������Լ��ṩ����Դ����R��ͷ��
    	menu.add(Menu.NONE, Menu.FIRST + 2, 2, "����").setIcon(android.R.drawable.ic_menu_edit);
    	menu.add(Menu.NONE, Menu.FIRST + 3, 6, "����").setIcon(android.R.drawable.ic_menu_help);
    	menu.add(Menu.NONE, Menu.FIRST + 4, 1, "���").setIcon(android.R.drawable.ic_menu_add);
    	menu.add(Menu.NONE, Menu.FIRST + 5, 4, "��ϸ").setIcon(android.R.drawable.ic_menu_info_details);
    	menu.add(Menu.NONE, Menu.FIRST + 6, 3, "����").setIcon(android.R.drawable.ic_menu_send);
    	// return true�Ż�������
    	return true;

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title").setMessage("sssssss").show();
    }
    
    // �˵��ѡ���¼�
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case Menu.FIRST + 1:
    		Toast.makeText(this, "ɾ���˵��������", Toast.LENGTH_LONG).show();
    	    startActivity(new Intent(this, MenuTest2.class));
    		break;
    	case Menu.FIRST + 2:
    		Toast.makeText(this, "����˵��������", Toast.LENGTH_LONG).show();
    		break;
    	case Menu.FIRST + 3:
    		Toast.makeText(this, "�����˵��������", Toast.LENGTH_LONG).show();
    		break;
    	case Menu.FIRST + 4:
    		Toast.makeText(this, "��Ӳ˵��������", Toast.LENGTH_LONG).show();
    		break;
    	case Menu.FIRST + 5:
    		Toast.makeText(this, "��ϸ�˵��������", Toast.LENGTH_LONG).show();
    		break;
    	case Menu.FIRST + 6:
    		Toast.makeText(this, "���Ͳ˵��������", Toast.LENGTH_LONG).show();
    		break;
    	}

    	return false;
    }

    // ѡ��˵����ر��¼����˵����ر����������Σ�menu��ť���ٴε����back��ť����������û�ѡ����ĳһ���˵���
    @Override
    public void onOptionsMenuClosed(Menu menu) {
    	Toast.makeText(this, "ѡ��˵��ر���", Toast.LENGTH_LONG).show();
    }

    // �˵�����ʾ֮ǰ���¼�
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	Toast.makeText(this, "ѡ��˵���ʾ֮ǰonPrepareOptionsMenu�����ᱻ���ã�������ô˷��������ݴ�ʱ����������˵�", Toast.LENGTH_LONG).show();
    	// �������false���˷����Ͱ��û����menu�Ķ����������ˣ�onCreateOptionsMenu���������ᱻ����
    	return true;
    }
}