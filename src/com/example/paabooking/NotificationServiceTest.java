package com.example.paabooking;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

public class NotificationServiceTest extends Service {
    
    private WakeLock mWakeLock;
    private Context context;
    public NotificationServiceTest() {
    	context = this;
    	Log.d("NotiServTest", "NotificationServiceTest Constructor/");
    	start();
    	Log.d("NotiServTest", "started foreground");
    }
    
    /**
     * Simply return null, since our Service will not be communicating with
     * any other components. It just does its work silently.
     */
    @Override
    public IBinder onBind(Intent intent) {
    	Log.d("NotiServTest", "onBind()");
        return null;
    }
    
    /**
     * This is where we initialize. We call this when onStart/onStartCommand is
     * called by the system. We won't do anything with the intent here, and you
     * probably won't, either.
     */
	/*private void handleIntent(Intent intent) {
		Log.d("NotiServTest", "handleIntent()");
        // obtain the wake lock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        // Partial wake lock so that the system won't kill the service in background
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tag_wake_lock");
        mWakeLock.acquire();
        // do the actual work, in a separate thread
        //new PollTask(intent).execute();
    }*/
    
  /*  private class PollTask extends AsyncTask<Void, Void, Void> {
    	private Intent intent;
    	private Context context;
    	public PollTask(Intent intent) {
    		Log.d("NotiServTest", "PollTask Constructor.....");
    		this.intent = intent;
    		context = getApplicationContext();
		}
        *//**
         * This is where YOU do YOUR work. There's nothing for me to write here
         * you have to fill this in. Make your HTTP request(s) or whatever it is
         * you have to do to get your updates in here, because this is run in a
         * separate thread
         *//*
        @Override
        protected Void doInBackground(Void... params) {
        	Log.d("NotiServTest", "doInBackground..");
        	try {
				Thread.sleep(30000);
			   	displayNotification("messageTitle", "message");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	 
        	
    		
            return null;
        } // End of doInBackground()
*/        
       /* private void displayNotification(String messageTitle, String message) {
    		//Pending intent to launch the activity for the user notification
    		Intent i = new Intent(context, LoginActivity.class);
    		int notificationID = 1;
    		i.putExtra("notificationID", notificationID);
    		
    		PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);
    		
    		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    		
    		//Notification notification = new Notification(R.drawable.gg, message, System.currentTimeMillis());
    		Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    		Notification n  = new Notification.Builder(context)
            .setContentTitle(messageTitle)
            .setContentText(message)
            .setSmallIcon(R.drawable.calendar_bar)
            .setContentIntent(pIntent)
            .setAutoCancel(true)
            .setTicker("Your Appointment is due.")
            .setSound(notificationSound)
            .addAction(R.drawable.calendar_bar, "And more", pIntent).build();
    		//100ms delay, vibration for 250ms, 100ms delay and 500ms vibration
    		n.vibrate = new long[] { 100, 250, 100, 500};
    		nm.notify(notificationID, n);
    		
    	}// End of displayNotification
*/        
        /**
         * In here you should interpret whatever you fetched in doInBackground
         * and push any notifications you need to the status bar, using the
         * NotificationManager. I will not cover this here, go check the docs on
         * NotificationManager.
         *
         * What you HAVE to do is call stopSelf() after you've pushed your
         * notification(s). This will:
         * 1) Kill the service so it doesn't waste precious resources
         * 2) Call onDestroy() which will release the wake lock, so the device
         *    can go to sleep again and save precious battery.
         */
       /* @Override
        protected void onPostExecute(Void result) {
            // handle your data
            stopSelf();
        }*/
    
 
    
    /**
     * This is called on 2.0+ (API level 5 or higher). Returning
     * START_NOT_STICKY tells the system to not restart the service if it is
     * killed because of poor resource (memory/cpu) conditions.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d("NotiServTest", "onStartCommand...");
    	start();
        //handleIntent(intent);
        return START_NOT_STICKY;
    }
    
    private void start() {
		// TODO Auto-generated method stub
    	Intent intent = new Intent(this, LoginActivity.class);
    	PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
    	Log.d("NotiServTest", "Building Notification");
    	Notification n  = new Notification.Builder(context)
        .setContentTitle("Title")
        .setContentText("msg")
        .setSmallIcon(R.drawable.calendar_bar)
        .setAutoCancel(true)
        .setTicker("Your Appointment is due.")
        .addAction(R.drawable.calendar_bar, "And more", pi).build();
    	n.vibrate = new long[] { 100, 250, 100, 500};
	}

	/**
     * In onDestroy() we release our wake lock. This ensures that whenever the
     * Service stops (killed for resources, stopSelf() called, etc.), the wake
     * lock will be released.
     */
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }
    
    
    
    private Notification getNotification() {
		Log.d("NotiServTest", "getNotification");
        // obtain the wake lock
        /*PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        // Partial wake lock so that the system won't kill the service in background
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tag_wake_lock");
        mWakeLock.acquire();*/
        // do the actual work, in a separate thread
       // new PollTask(intent).execute();
        
      //Pending intent to launch the activity for the user notification
		Intent intent = new Intent(context, LoginActivity.class);
		int notificationID = 1;
		intent.putExtra("notificationID", notificationID);
		
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//Notification notification = new Notification(R.drawable.gg, message, System.currentTimeMillis());
		Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Notification n  = new Notification.Builder(context)
        .setContentTitle("Title")
        .setContentText("msg")
        .setSmallIcon(R.drawable.calendar_bar)
        .setAutoCancel(true)
        .setTicker("Your Appointment is due.")
        .setSound(notificationSound)
        .addAction(R.drawable.calendar_bar, "And more", pIntent).build();
		//100ms delay, vibration for 250ms, 100ms delay and 500ms vibration
		n.vibrate = new long[] { 100, 250, 100, 500};
		//nm.notify(notificationID, n);
		return n;
        
    }
    
  
    
}