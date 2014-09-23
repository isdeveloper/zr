package com.notification;

import com.activity.Main;
import com.example.defender2.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
/**
 * 
 * @author
 * notification
 */
public class DefNotify {
	private Context context = null;
	
	public DefNotify(Context c){
		context = c;
	}
	
     //��״̬����ʾ֪ͨ
    @SuppressWarnings("deprecation")
	public void ShowNotification(){
    	// ����һ��NotificationManager������
        NotificationManager notificationManager = (NotificationManager)
            context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        
        // ����Notification�ĸ�������
        Notification notification =new Notification(R.drawable.logo,
                "", System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // ����֪ͨ�ŵ�֪ͨ����"Ongoing"��"��������"����   
        notification.flags |= Notification.FLAG_NO_CLEAR; // �����ڵ����֪ͨ���е�"���֪ͨ"�󣬴�֪ͨ�������������FLAG_ONGOING_EVENTһ��ʹ��   
        notification.flags |= Notification.FLAG_INSISTENT;
        //DEFAULT_ALL     ʹ������Ĭ��ֵ�������������𶯣������ȵ�
        notification.defaults = Notification.DEFAULT_LIGHTS;
        //����Ч������
        //notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS =5000; //����ʱ��
         
        // ����֪ͨ���¼���Ϣ
        CharSequence contentTitle ="����"; // ֪ͨ������
        CharSequence contentText ="������������"; // ֪ͨ������
        Intent notificationIntent =new Intent(context, Main.class);// �����֪ͨ��Ҫ��ת��Activity
        PendingIntent contentItent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentItent);
        
        // ��Notification���ݸ�NotificationManager
        notificationManager.notify(0, notification);
    }
    
    //ɾ��֪ͨ
    public void ClearNotification(){
    	// ������ɾ��֮ǰ���Ƕ����֪ͨ
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
}
