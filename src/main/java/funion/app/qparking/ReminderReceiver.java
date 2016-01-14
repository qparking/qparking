package funion.app.qparking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver
{
	public PendingIntent getLauncherIntent(Context context, int flags)
	{
		Intent	intentLauncher	= new Intent(Intent.ACTION_MAIN);

		intentLauncher.putExtra("PickCar", true);
		intentLauncher.addCategory(Intent.CATEGORY_LAUNCHER);
		intentLauncher.setClass(context, MainActivity.class);
		
		PendingIntent pendingIntent= PendingIntent.getActivity(context, 0, intentLauncher, flags);
		
		return pendingIntent;
	} 
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		NotificationManager managerNotification = (NotificationManager) context.getSystemService("notification");
		NotificationCompat.Builder	builderNotification = new NotificationCompat.Builder(context);
		builderNotification.setContentTitle("趣停车")	//设置通知栏标题 
			.setContentText("该取车了")				//设置通知栏显示内容
			.setContentIntent(getLauncherIntent(context, Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
			.setTicker("取车提醒")	//通知首次出现在通知栏，带上升动画效果的 
			.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间 
			.setPriority(Notification.DEFAULT_ALL) //设置该通知优先级
			.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消 
			.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)  
			.setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合  
			.setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
		managerNotification.notify(1, builderNotification.build());
	}

}
