package com.activity;

import com.example.defender2.R;
import com.phone.MyPhoneBroadcastReceiver;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

public class Home extends Activity {
	/**
	 * Home页面处理
	 */
	private SharedPreferences sp = null;
	private Editor editor = null;
	private Switch switch_button;
	private ImageView logo;
	private MyPhoneBroadcastReceiver mBroadcastReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home);
		sp = getSharedPreferences("DEFENDER", MODE_PRIVATE);
		editor = sp.edit();
		editor.putString("FLAG", "hang");
		editor.commit();
		
		//获取本页控件
		switch_button = (Switch)findViewById(R.id.home_switch);
		logo = (ImageView)findViewById(R.id.home_logo);
		
		//初始化本页
		if(sp.getString("SWITCH", "false").equals("true")){
			switch_button.setChecked(true);
			logo.setImageResource(R.drawable.home_on);
			registerThis();
		}else{
			switch_button.setChecked(false);
			logo.setImageResource(R.drawable.home_off);
		}
		
		//监听本页控件
		switch_button.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if(isChecked){//选中时
						registerThis();
						editor.putString("SWITCH", "true");
						editor.commit();
						logo.setImageResource(R.drawable.home_on);
						Toast.makeText(Home.this, "已打开防护", Toast.LENGTH_SHORT).show();
						}
		            else{//非选中时
		            	unregisterThis();
						editor.putString("SWITCH", "false");
						editor.commit();
		    			logo.setImageResource(R.drawable.home_off);
						Toast.makeText(Home.this, "已关闭防护", Toast.LENGTH_SHORT).show();
						}
				}
		});
	}
	
	//按钮1-注册广播
	public void registerThis() {
		Log.i("MyPhoneBroadcastReceiver", "registerThis");
		mBroadcastReceiver = new MyPhoneBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		intentFilter.setPriority(Integer.MAX_VALUE);//优先级设置为最大
		registerReceiver(mBroadcastReceiver, intentFilter);
	}
		
	//按钮2-撤销广播
	public void unregisterThis() {
		Log.i("MyPhoneBroadcastReceiver", "unregisterThis");
		unregisterReceiver(mBroadcastReceiver);
	}
}
