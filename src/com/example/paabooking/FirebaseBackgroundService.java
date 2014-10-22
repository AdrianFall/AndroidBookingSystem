package com.example.paabooking;
 
import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
 
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
 
public class FirebaseBackgroundService extends Service {
	
	private Firebase f = new Firebase("https://demoandroid.firebaseio.com/");
	private ValueEventListener handler;
	
	public FirebaseBackgroundService() {
		Log.d("BackgroundThread", "Service Constructor");
	}
 
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		stopSelf();
		Log.d("BackgroundThread", "Service onCreate()");
		handler = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot arg0) {
				postNotif(arg0.getValue().toString());
			}
			

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}
		};
		
		f.addValueEventListener(handler);
	}
 
	private void postNotif(String notifString) {
	/*	NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.arrow_down_float;
		Notification notification = new Notification(icon, "Firebase" + Math.random(), System.currentTimeMillis());
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Context context = getApplicationContext();
		CharSequence contentTitle = "Background" + Math.random();
		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, notifString, contentIntent);
		mNotificationManager.notify(1, notification);*/
		Log.d("BackgroundThread", "Service postNotif()");
		Context context = getApplicationContext();
		
		Intent i = new Intent(context, LoginActivity.class);
		int notificationID = 1;
		i.putExtra("notificationID", notificationID);
		
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);
		
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//Notification notification = new Notification(R.drawable.gg, message, System.currentTimeMillis());
		Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Notification n  = new Notification.Builder(context)
        .setContentTitle("Title")
        .setContentText(notifString)
        .setSmallIcon(R.drawable.btn_default_small)
        .setContentIntent(pIntent)
        .setAutoCancel(true)
        .setTicker("Your Appointment is due.")
        .setSound(notificationSound)
        .addAction(R.drawable.btn_radio, "And more", pIntent).build();
		//100ms delay, vibration for 250ms, 100ms delay and 500ms vibration
		n.vibrate = new long[] { 100, 250, 100, 500};
		nm.notify(notificationID, n);
	}
 
}