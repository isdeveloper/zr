package com.audio.obtain;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.audio.mfcc.Distance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class AudioObtain {
	private AudioRecord audioRecord = null;
	private SharedPreferences sp = null;
	private double distnce = 0;//����������ʼ����
	private double threshold_min = 8.5;//���������������ֵ
	private double threshold_max = 10;//���������������ֵ
	
	@SuppressWarnings("static-access")
	public AudioObtain(Context c){
		sp = c.getSharedPreferences("DEFENDER", c.MODE_PRIVATE);
	}
	
	@SuppressWarnings("deprecation")
	public boolean Judge() {
	    int channel_config = AudioFormat.CHANNEL_CONFIGURATION_DEFAULT;//And also Mono
		int format = AudioFormat.ENCODING_PCM_16BIT;
		int rate = 8000;//rate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM);
		//your sample rate is wrong, try 8000Hz. It is an emulator limitation.
		int group = 100;//¼��������640byte/��
        
	    try {
	        int bufferSize = AudioRecord.getMinBufferSize(rate, channel_config, format);// Create AudioRecord object to record
	        
	        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
	        		rate, channel_config, format, bufferSize);//initial
	        byte[] tempbuffer = new byte[bufferSize];// a group of audio data
	        byte[] buffer = new byte[group*bufferSize];// all data
	        
	        //¼������
	        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        audioRecord.startRecording();//start recording
	        Date curDate = new Date(System.currentTimeMillis());//start time
	        
	        for(int i = 0; i <= group-1; i++)
	        	if(audioRecord.read(tempbuffer, 0, bufferSize) > 0)
	        		System.arraycopy(tempbuffer,0,buffer,bufferSize*i,bufferSize);
	        
	        Date endDate = new Date(System.currentTimeMillis());//stop record && start analysis
	        long diff = endDate.getTime() - curDate.getTime();
        	Log.i("time between record",diff+"");
        	
        	Distance distance = new Distance();
        	distnce = distance.calculate(buffer);//������������
        	
	        Date endDate2 = new Date(System.currentTimeMillis());//��������ʱ��
	        long diff2 = endDate2.getTime() - endDate.getTime();
        	Log.i("time between analysis",diff2+"");
	    } catch (Exception t) {
	    	t.printStackTrace();
		}
	    
    	if(audioRecord != null){
    		audioRecord.stop();
        	audioRecord.release();
    	}
    	
    	if(sp.getString("FLAG", "").equals("ans")){////�򿪹رշ���״̬
			System.out.println("Get Ans");
    		return true;
    	}else if(sp.getString("FLAG", "").equals("hang")){
			System.out.println("Get Hang");
    		return false;
    	}
    	
    	if(distnce >= threshold_min && distnce <= threshold_max)//����С����ֵ
    		return false;
    	return true;
	}
}
