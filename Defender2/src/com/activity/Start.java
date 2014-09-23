package com.activity;

import com.example.defender2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class Start extends Activity {
	/**
	 * ∆Ù∂ØΩÁ√Ê
	 */
	private final int Delay_Time=800;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.startpage);
		
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				Intent intent=new Intent(Start.this,Main.class);
				Start.this.startActivity(intent);
				Start.this.finish();
			}
		},Delay_Time);
	}

}
