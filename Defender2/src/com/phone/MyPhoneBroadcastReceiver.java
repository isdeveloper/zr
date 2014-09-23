package com.phone;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneBroadcastReceiver extends BroadcastReceiver {
	/**
	 * 电话处理广播
	 */
	private SharedPreferences sp = null;
	private Editor editor = null;
	private PhoneManager manager = null;
	private boolean need_analysis = false;
	
	@SuppressLint("CommitPrefEdits")
	@SuppressWarnings("static-access")
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i("MyPhoneBroadcastReceiver", "[Broadcast]"+action);
		
		sp = context.getSharedPreferences("DEFENDER", context.MODE_PRIVATE);
		editor = sp.edit();
		manager = new PhoneManager(context);
		
		//电话状态改变
		if(action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)){
			String phoneNumber = null;
			TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			int state = telephony.getCallState();
			if(state == TelephonyManager.CALL_STATE_RINGING){
				phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
				editor.putString("PHONENUMBER", phoneNumber);
				editor.commit();
			}else
				phoneNumber = sp.getString("PHONENUMBER","");
			
			switch(state){
			case TelephonyManager.CALL_STATE_RINGING:
				Log.i("MyPhoneBroadcastReceiver", "待接电话:"+phoneNumber);
				need_analysis = manager.RINGING_Process(phoneNumber);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				Log.i("MyPhoneBroadcastReceiver", "挂断电话:"+phoneNumber);
				if(need_analysis && sp.getString("FLAG", "").equals("ans"))
					manager.mark(phoneNumber);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.i("MyPhoneBroadcastReceiver", "正在通话:"+phoneNumber);
				if(need_analysis)
					manager.OFFHOOK_Process(phoneNumber);
				break;
			}
		}
	}
}
