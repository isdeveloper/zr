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
	
     //在状态栏显示通知
    @SuppressWarnings("deprecation")
	public void ShowNotification(){
    	// 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager)
            context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        
        // 定义Notification的各种属性
        Notification notification =new Notification(R.drawable.logo,
                "", System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中   
        notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用   
        notification.flags |= Notification.FLAG_INSISTENT;
        //DEFAULT_ALL     使用所有默认值，比如声音，震动，闪屏等等
        notification.defaults = Notification.DEFAULT_LIGHTS;
        //叠加效果常量
        //notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS =5000; //闪光时间
         
        // 设置通知的事件消息
        CharSequence contentTitle ="音盾"; // 通知栏标题
        CharSequence contentText ="垃圾语音拦截"; // 通知栏内容
        Intent notificationIntent =new Intent(context, Main.class);// 点击该通知后要跳转的Activity
        PendingIntent contentItent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentItent);
        
        // 把Notification传递给NotificationManager
        notificationManager.notify(0, notification);
    }
    
    //删除通知
    public void ClearNotification(){
    	// 启动后删除之前我们定义的通知
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
}
