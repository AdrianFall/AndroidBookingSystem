package com.example.paabooking;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

/** 
 * Class for performing one single Service task which is
 * the notification. 
 * @author Adrian
 *
 */
public class NotificationService extends IntentService  {
	private Context context;
	public NotificationService() {
		super("NotificationService");
		// TODO Auto-generated constructor stub
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		context = getApplicationContext();
		// e.g. PAA2/13March2014/times/12:30
		/*String urlPath = intent.getStringExtra("path");
		// OR
		String day = intent.getStringExtra("day");
		int month = intent.getIntExtra("month", 0);
		String year = intent.getStringExtra("year");
		String time = intent.getStringExtra("time");
		Log.d("NotificationService", "Day " + day + " Month " + month + " Year " + year + " Time " + time);
		
		Calendar appointmentCalendar = new GregorianCalendar(Integer.valueOf(year), month, Integer.valueOf(day));
		String[] splitTime = time.split(":");
		int hours = Integer.valueOf(splitTime[0]);
		int minutes = Integer.valueOf(splitTime[1]);
		appointmentCalendar.set(Calendar.HOUR_OF_DAY, hours);
		appointmentCalendar.set(Calendar.MINUTE, minutes);
		Log.d("NotificationService", "Appointment Calendar = " + appointmentCalendar.getTime().toString());
		
		Calendar currentCalendar = Calendar.getInstance();
		Log.d("NotificationService", "Current Calendar = " + currentCalendar.getTime().toString());*/
		
		// Calculate the difference in milliseconds between the dates
		/*long millisecondsDifference = appointmentCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
		Log.d("NotificationService", "Difference in ms - " + millisecondsDifference);
		
		int minutesBeforeAppointment = 10;*/
		
		try {
			Thread.sleep(1);
			//Thread.sleep(millisecondsDifference - (1000 * minutesBeforeAppointment * 60));
			Log.d("NotificationService", "Obtaining GPS coordinates.");
			GPSTracker gps = new GPSTracker(context);
			// Obtain the distance to the university in metres
			float distanceInMetres = gps.getDistanceToUni();
			float distanceInKilometres = distanceInMetres / 1000;
			Log.d("NotificationService", "Distance in kilometres to uni - " + Float.toString(distanceInKilometres));
			if (distanceInKilometres > 4) {
				// Send notification 
				displayNotification("You have a PAA Appointment today.", "Please do not be late for the PAA Appointment. It starts in - " + 20 + " minutes.");
			} else {
				// Send notification
				displayNotification("You have a PAA Appointment today.", "This is a reminder about your PAA Appointment which starts in - " + 20 + " minutes.");
			}
			
		} catch (InterruptedException e) {
			Log.d("NotificationService", "InterruptedException - " + e.getMessage());
		}
		
		
		/*
		int count = 8;
		for (int i = 0; i < count; i++) {
			try {
				
				Log.d("NotificationService", "Loop - " + (i+1));
				GPSTracker gps = new GPSTracker(context);
				float distanceInMetres = gps.getDistanceToUni();
				float distanceInKilometres = distanceInMetres / 1000;
				Log.d("NotificationService", Float.toString(gps.getDistanceToUni()));
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				Log.d("NotificationService", "InterruptedException - " + e.getMessage());
			}
		}
		*/
	} // End of onHandleIntent method
	
	private void displayNotification(String messageTitle, String message) {
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
	
	

}
