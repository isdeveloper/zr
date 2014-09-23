package com.activity;

import com.example.defender2.R;
import com.notification.DefNotify;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class Main extends TabActivity {
	/**
	 * Tabhost添加三个界面
	 */
	private TabHost tabs;
	private DefNotify notify = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		//获取本页控件
		tabs = this.getTabHost();
		notify = new DefNotify(this);
    	notify.ClearNotification();
		
		//添加三个Tab
		Intent search_intent = new Intent();
		search_intent.setClass(this,Home.class);
		tabs.addTab(tabs.newTabSpec("home_tab")
				.setContent(search_intent)
				.setIndicator("首页")); 

		Intent about_intent = new Intent();
		about_intent.setClass(this,Log.class);
		tabs.addTab(tabs.newTabSpec("log_tab")
				.setContent(about_intent)
				.setIndicator("日志")); 
		
		Intent user_intent = new Intent();
		user_intent.setClass(this,Setting.class);
		tabs.addTab(tabs.newTabSpec("setting_tab")
				.setContent(user_intent)
				.setIndicator("设置"));
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0){
			notify.ShowNotification();
	    	finish();
	    }
         return false;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {//菜单键
		menu.add(0, 1, 1, "退出程序");
        return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {//菜单监听
		if(1 == item.getItemId()){
			if(notify != null){
				notify.ClearNotification();
				notify = null;
			}
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
