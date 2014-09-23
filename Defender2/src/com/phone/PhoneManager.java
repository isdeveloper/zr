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
	 * δ�Ӻ���Ĵ���
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
		//ȡ�����ݿ��¼
    	this.context = context;
		prodb = new DBProcess(this.context);//���ݿ����
		sp = context.getSharedPreferences("DEFENDER", context.MODE_PRIVATE);
		time=new Time();//ʱ��
		telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);//telephony����
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
		
		//ģʽ����
		if(0 == mode){
			prodb.insertLog(phone, datetime, "�˹��ж�ģʽ-����ʾ");
		    prodb.close();
			return false;
		}
		else if (1 == mode){
			try {
				iTelephony.endCall();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			prodb.insertLog(phone, datetime, "ҹ��ģʽ-�ѹҶ�");
		    prodb.close();
		    return false;
		}
		
		//ģʽΪ�����ж�ģʽ�����Զ������ж�
		if(flag.equals("endcall"))//---------����������-------
			try {
				iTelephony.endCall();
				prodb.insertLog(phone, datetime, "������-�ѹҶ�");
			} catch (Exception e) {
				e.printStackTrace();
			}
		else if(flag.equals("remindcall")){//-----------����������--------
			prodb.insertLog(phone, datetime, "������-����ʾ");
		}else//------------δ֪�����Զ��ж�----------
			try{
				//�Զ�����
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
		//¼������
		if(record.Judge()){
			prodb.insertLog(phone, datetime, "����������-����ʾ");
			addFloatRemindWindow(phone);
			try{
				ShowMark(phone);
			}catch(Exception e){
				Toast.makeText(context, "�޷���ȡ��Ǵ�����������������", Toast.LENGTH_SHORT).show();
			}
		}else
			try {
				iTelephony.endCall();
				prodb.insertLog(phone, datetime, "��������-�ѹҶ�");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		prodb.close();
	}
	
	//��Ƿ����
	public void mark(final String phone){
		final String TAG = "MarkFloatWindowTest";
		final WindowManager mWindowManager;
		WindowManager.LayoutParams wmParams;
		final RelativeLayout mFloatLayout;
		Button unmark;
		Button mark;
		TextView phoneshow;
		
		//��ȡLayoutParams����
        wmParams = new WindowManager.LayoutParams();
        //��ȡ����LocalWindowManager����
        mWindowManager = (WindowManager)context.getApplicationContext().getSystemService("window");
        //��ȡ����CompatModeWrapper����
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
        
        //�󶨵������
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
								Toast.makeText(context, "��ǳɹ���", Toast.LENGTH_LONG).show();
							else
								Toast.makeText(context, "���ʧ�ܣ������������ӣ�", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(context, "���ʧ�ܣ������������ӣ�", Toast.LENGTH_SHORT).show();
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
		
		//��ȡLayoutParams����
        wmParams = new WindowManager.LayoutParams();
        //��ȡ����LocalWindowManager����
        mWindowManager = (WindowManager)context.getApplicationContext().getSystemService("window");
        //��ȡ����CompatModeWrapper����
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
        
        //�󶨵������
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
					Toast.makeText(context, "�޷���ȡ��Ǵ�����������������", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(context, "�˺��ѱ�"+msg.obj.toString()+"�˱��Ϊɧ�ŵ绰", Toast.LENGTH_LONG).show();;
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

	public String GetType(String phone){//�ж��Ƿ�ڰ���������
		Cursor blackCursor = prodb.select("blacklist_table");
		Cursor whiteCursor = prodb.select("whitelist_table");
		String flag = "strange";
		//ƥ���������ƥ�䵽��ر����ݿⲢ����
		while(blackCursor.moveToNext())
	    	if(phone.equals(blackCursor.getString(2))){
	    		flag = "endcall";
	    	    blackCursor.close();
	    	    whiteCursor.close();
	    		return flag;
	    	}
	    //������δƥ�䵽����ƥ���������ƥ�䵽��ر����ݿⲢ����
    	while(whiteCursor.moveToNext())
    		if(phone.equals(whiteCursor.getString(2))){
    			flag = "remindcall";
    		    blackCursor.close();
    		    whiteCursor.close();
    		    return flag;
    		}
	    //�ڰ�������δƥ�䵽���ر����ݿ⣬���ؽ��
	    blackCursor.close();
	    whiteCursor.close();
		return flag;
	}
}
