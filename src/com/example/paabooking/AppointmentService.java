package com.example.paabooking;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import com.example.paabooking.BookingActivity.AppointmentFragment;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.app.IntentService;
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

public class AppointmentService extends Service {

	public AppointmentService() {
		
		// TODO Auto-generated constructor stub
	}

	private static String TAG = "AppointmentService";
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "AppointmentService onCreate()");
		// Stop service in case other service was running beforehand
		stopSelf();
		
		
	} // End on Create
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        Log.d(TAG, "AppointmentService - onStartCommand() ..  Received start id " + startId + ": and Intent " + intent);
        onHandleIntent(intent);
        return START_NOT_STICKY;
    }
	
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "AppointmentService - onHandleIntent()");
		final String appointmentPath = intent.getStringExtra("appointmentPath");
		Log.d(TAG, "AppointmentService - onHandleIntent() - path = " + appointmentPath);
		final String username = intent.getStringExtra("username");
		
		Log.d(TAG, "appointmentPath = " + appointmentPath);
		String[] splitAppointmentPath = appointmentPath.split("/");
		String paaId = splitAppointmentPath[0];
		final String appointmentDate = splitAppointmentPath[1];
		final String appointmentTime = splitAppointmentPath[3];
		
		Firebase appointmentRef = new Firebase("https://demoandroid.firebaseio.com/appointments/" + appointmentPath);
		//TODO Service for the value event listener
		appointmentRef.addValueEventListener(new ValueEventListener() {
		
			@Override
			public void onCancelled(FirebaseError arg0) {
				
			}
		
			@Override
			public void onDataChange(DataSnapshot snap) {
				Log.d(TAG, "onDataChange().");
				Object appointment = snap.getValue();
				Log.d(TAG, "appointment object: " + appointment.toString());
				String cancelled = (String)((Map)appointment).get("cancelled");
				String cancelledBy = (String)((Map)appointment).get("cancelledBy");
				
				// If the appointment has been cancelled
				if (cancelled != null && cancelledBy != null) {
					
					
					//Set the current number of appointments of the student to 0
					Firebase userRef = new Firebase("https://demoandroid.firebaseio.com/user/username/" + username + "/");
					userRef.child("currentappointments/").setValue("0");
					userRef.child("currentAppointment/").setValue("none");
					
					
					Log.d(TAG, "cancelled by = " + cancelledBy);
					//TODO if cancelledBy PAA then make a notification
					if (cancelledBy.equals("PAA")) {
						
						Log.d(TAG, "AppointmentService- appointment cancelled by PAA");
						/*AppointmentFragment.appointmentIdTV.setText("The following appointment:");
						AppointmentFragment.dateTV.setText(appointmentDate + " " + appointmentTime);
						AppointmentFragment.timeTV.setText("Has been deleted by the PAA");
						AppointmentFragment.cancelAppointmentButton.setVisibility(4);*/
						//TODO Make a notification
						displayNotification("Appointment Cancelled.", "Your Appointment was cancelled by PAA.");
						
					} else if (cancelledBy.equals("expiration")) {
						
						/*AppointmentFragment.appointmentIdTV.setText("No current appointments...");
						AppointmentFragment.dateTV.setText("Your last appointment has expired.	");
						AppointmentFragment.timeTV.setText("");
						AppointmentFragment.paaNameTV.setText("");
						AppointmentFragment.cancelAppointmentButton.setVisibility(4);*/
						
					}// End If cancelled by expiration
					
				} else {
					// Check if the appointment date is expired
					Calendar currentDayCalendar = Calendar.getInstance(Locale.getDefault());
					String theYear = (String)((Map)appointment).get("year");
					String theMonth = (String)((Map)appointment).get("month");
					String theDay = (String)((Map)appointment).get("day");
					int theMonthAsInt = getMonthAsInt(theMonth);
					GregorianCalendar appointmentDayCalendar = new GregorianCalendar(Integer.valueOf(theYear), theMonthAsInt, Integer.valueOf(theDay));
					String[] appointmentTimeSplit = appointmentTime.split(":");
					String hour = appointmentTimeSplit[0];
					String minutes = appointmentTimeSplit[1];
					appointmentDayCalendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
					appointmentDayCalendar.set(Calendar.MINUTE, Integer.valueOf(minutes));
					
					if (appointmentDayCalendar.before(currentDayCalendar)) {
						Log.d(TAG, "Appointment Expired.");
						
						
						// Set the appointment as cancelled 
						Firebase appointmentRef = new Firebase("https://demoandroid.firebaseio.com/appointments/"
								  + appointmentPath);

						appointmentRef.child("cancelled").setValue("true");
						appointmentRef.child("cancelledBy").setValue("expiration");
						
						
						
					}
				}
			} // End of onDataChange
				
			private int getMonthAsInt(String theMonth) {
				int monthAsInt = -1;
				if (theMonth.equals("January")) {
					monthAsInt = 0;
				} else if (theMonth.equals("February")) {
					monthAsInt = 1;
				} else if (theMonth.equals("March")) {
					monthAsInt = 2;
				} else if (theMonth.equals("April")) {
					monthAsInt = 3;
				} else if (theMonth.equals("May")) {
					monthAsInt = 4;
				} else if (theMonth.equals("June")) {
					monthAsInt = 5;
				} else if (theMonth.equals("July")) {
					monthAsInt = 6;
				} else if (theMonth.equals("August")) {
					monthAsInt = 7;
				} else if (theMonth.equals("September")) {
					monthAsInt = 8;
				} else if (theMonth.equals("October")) {
					monthAsInt = 9;
				} else if (theMonth.equals("November")) {
					monthAsInt = 10;
				} else if (theMonth.equals("December")) {
					monthAsInt = 11;
				}
				return monthAsInt;
			} // End of getMonthAsInt method
			
		}); // End of appointmentRef ValueEventListener
	}
	
	private void displayNotification(String messageTitle, String message) {
		//Pending intent to launch the activity for the user notification
		Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		int notificationID = 1;
		i.putExtra("notificationID", notificationID);
		
		PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);
		
		NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		
		//Notification notification = new Notification(R.drawable.gg, message, System.currentTimeMillis());
		Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Notification n  = new Notification.Builder(getApplicationContext())
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
