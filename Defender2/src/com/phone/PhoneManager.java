package com.phone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.audio.obtain.AudioObtain;
import com.example.defender2.R;
import com.http.HttpUtil;
import com.sql.DBProcess;

@SuppressLint("HandlerLeak")
public class PhoneManager {
	/**
	 * 未接号码的处理
	 */
    private Context context;
	private SharedPreferences sp = null;
	private DBProcess prodb = null;
	private Time time = null;
	private TelephonyManager telephony = null;
	private ITelephony iTelephony;
    private AudioObtain record = null;
    
	@SuppressWarnings("static-access")
	public PhoneManager(Context context){
		//取出数据库记录
    	this.context = context;
		prodb = new DBProcess(this.context);//数据库对象
		sp = context.getSharedPreferences("DEFENDER", context.MODE_PRIVATE);
		time=new Time();//时间
		telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);//telephony反射
		try {
			iTelephony = PhoneUtils.getITelephony(telephony);
		} catch (Exception e) {
			e.printStackTrace();
		}
		record = new AudioObtain(context);
    }
    
	@SuppressLint("HandlerLeak")
	public boolean RINGING_Process(final String phone){
		Log.i("PhoneProcess", phone);
		String flag = GetType(phone);
		int mode = sp.getInt("MODE", 0);
	    
	    time.setToNow();
		String datetime = time.year+"-"+time.month+"-"+time.monthDay+" "+
				time.hour+":"+time.minute+":"+time.second;
		
		//模式优先
		if(0 == mode){
			prodb.insertLog(phone, datetime, "人工判断模式-已提示");
		    prodb.close();
			return false;
		}
		else if (1 == mode){
			try {
				iTelephony.endCall();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			prodb.insertLog(phone, datetime, "夜间模式-已挂断");
		    prodb.close();
		    return false;
		}
		
		//模式为智能判断模式，则自动接听判断
		if(flag.equals("endcall"))//---------黑名单号码-------
			try {
				iTelephony.endCall();
				prodb.insertLog(phone, datetime, "黑名单-已挂断");
			} catch (Exception e) {
				e.printStackTrace();
			}
		else if(flag.equals("remindcall")){//-----------白名单号码--------
			prodb.insertLog(phone, datetime, "白名单-已提示");
		}else//------------未知号码自动判断----------
			try{
				//自动接听
				//iTelephony.answerRingingCall();
				Intent ansintent = new Intent("android.intent.action.MEDIA_BUTTON");
				KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
				ansintent.putExtra("android.intent.extra.KEY_EVENT",keyEvent);
				context.sendOrderedBroadcast(ansintent,"android.permission.CALL_PRIVILEGED");
				
			    prodb.close();
			    return true;
			} catch (Exception e2) {
				e2.printStackTrace();
			}
	    prodb.close();
	    
	    return false;
	}
	
	public void OFFHOOK_Process(final String phone){
		time.setToNow();
		String datetime = time.year+"-"+time.month+"-"+time.monthDay+" "+
				time.hour+":"+time.minute+":"+time.second;
		//录音分析
		if(record.Judge()){
			prodb.insertLog(phone, datetime, "非垃圾语音-已提示");
			addFloatRemindWindow(phone);
			try{
				ShowMark(phone);
			}catch(Exception e){
				Toast.makeText(context, "无法获取标记次数，请检查网络连接", Toast.LENGTH_SHORT).show();
			}
		}else
			try {
				iTelephony.endCall();
				prodb.insertLog(phone, datetime, "垃圾语音-已挂断");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		prodb.close();
	}
	
	//标记服务端
	public void mark(final String phone){
		final String TAG = "MarkFloatWindowTest";
		final WindowManager mWindowManager;
		WindowManager.LayoutParams wmParams;
		final RelativeLayout mFloatLayout;
		Button unmark;
		Button mark;
		TextView phoneshow;
		
		//获取LayoutParams对象
        wmParams = new WindowManager.LayoutParams();
        //获取的是LocalWindowManager对象
        mWindowManager = (WindowManager)context.getApplicationContext().getSystemService("window");
        //获取的是CompatModeWrapper对象
        Log.i(TAG, "mWindowManager3--->" + mWindowManager);
        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER | Gravity.CENTER;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.mark, null);
        mWindowManager.addView(mFloatLayout, wmParams);
        unmark = (Button)mFloatLayout.findViewById(R.id.mark_unm);
        mark = (Button)mFloatLayout.findViewById(R.id.mark_m);
        phoneshow = (TextView)mFloatLayout.findViewById(R.id.mark_phoneShow);
        phoneshow.setText(phone);
        
        //绑定点击监听
        unmark.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mWindowManager.removeView(mFloatLayout);
			}
		});
        mark.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try{
					final Handler handler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							if(msg.obj.toString().equals("1"))
								Toast.makeText(context, "标记成功！", Toast.LENGTH_LONG).show();
							else
								Toast.makeText(context, "标记失败，请检查网络连接！", Toast.LENGTH_SHORT).show();
			            }
				    };
					new Thread(new Runnable() {
						@Override
						public void run() {
							String url = HttpUtil.BASE_URL+"UpdateServlet?phone="+phone;
							String Times = HttpUtil.queryStringForPost(url);
							Message msg = new Message();
							msg.obj = Times;
							handler.sendMessage(msg);
						}
					}).start();
				}catch(Exception e){
					Toast.makeText(context, "标记失败，请检查网络连接！", Toast.LENGTH_SHORT).show();
				}
				mWindowManager.removeView(mFloatLayout);
			}
		});
	}
	
	private void addFloatRemindWindow(final String phone){
		final String TAG = "RemindFloatWindowTest";
		final WindowManager mWindowManager;
		WindowManager.LayoutParams wmParams;
		final RelativeLayout mFloatLayout;
		Button cancel;
		Button answer;
		TextView phoneshow;
		
		//获取LayoutParams对象
        wmParams = new WindowManager.LayoutParams();
        //获取的是LocalWindowManager对象
        mWindowManager = (WindowManager)context.getApplicationContext().getSystemService("window");
        //获取的是CompatModeWrapper对象
        Log.i(TAG, "mWindowManager3--->" + mWindowManager);
        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER | Gravity.CENTER;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.remind, null);
        mWindowManager.addView(mFloatLayout, wmParams);
        cancel = (Button)mFloatLayout.findViewById(R.id.remind_cancel);
        answer = (Button)mFloatLayout.findViewById(R.id.remind_answer);
        phoneshow = (TextView)mFloatLayout.findViewById(R.id.remind_phoneShow);
        phoneshow.setText(phone);
        
        final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.xiaomiring);
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
        
        //绑定点击监听
        cancel.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					iTelephony.endCall();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				try {
					ShowMark(phone);
				} catch (Exception e) {
					Toast.makeText(context, "无法获取标记次数，请检查网络连接", Toast.LENGTH_SHORT).show();
				}
				mediaPlayer.stop();
				mWindowManager.removeView(mFloatLayout);
			}
		});
        answer.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mediaPlayer.stop();
				mWindowManager.removeView(mFloatLayout);
			}
		});
	}
	
	@SuppressLint("HandlerLeak")
	private void ShowMark(final String Phone){
		
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Toast.makeText(context, "此号已被"+msg.obj.toString()+"人标记为骚扰电话", Toast.LENGTH_LONG).show();;
            }
	    };
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = HttpUtil.BASE_URL+"CheckServlet?phone="+Phone;
				String Times = HttpUtil.queryStringForPost(url);
				Message msg = new Message();
				msg.obj = Times;
				handler.sendMessage(msg);
			}
		}).start();
	}

	public String GetType(String phone){//判断是否黑白名单号码
		Cursor blackCursor = prodb.select("blacklist_table");
		Cursor whiteCursor = prodb.select("whitelist_table");
		String flag = "strange";
		//匹配黑名单，匹配到则关闭数据库并返回
		while(blackCursor.moveToNext())
	    	if(phone.equals(blackCursor.getString(2))){
	    		flag = "endcall";
	    	    blackCursor.close();
	    	    whiteCursor.close();
	    		return flag;
	    	}
	    //黑名单未匹配到，则匹配白名单，匹配到则关闭数据库并返回
    	while(whiteCursor.moveToNext())
    		if(phone.equals(whiteCursor.getString(2))){
    			flag = "remindcall";
    		    blackCursor.close();
    		    whiteCursor.close();
    		    return flag;
    		}
	    //黑白名单都未匹配到，关闭数据库，返回结果
	    blackCursor.close();
	    whiteCursor.close();
		return flag;
	}
}
