package com.example.paabooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class BookingDaySlotsActivity extends Activity {
	String availabilityTimes;
	String paaId;
	String day;
	String theMonth;
	String theYear;
	String theDate;
	String userFullname;
	String userId;
	String username;
	String numberOfAppointments;
	String paaName;
	String paaUsername;
	String emailAddress;
	String emailSender;
	String emailPass;
	String appointmentState;
	String userWithAppointment;
	String userFullNameWithAppointment;
	Context thisContext;
	static final String TAG = "BookingDaySlotsActivity";
	boolean addingDay = false;
	static boolean appointmentGiven = false;
	ArrayList<String> availabilityTimesArrayList = new ArrayList<String>();
	TextView selectedTimeTV, dateTV; 
	Button bookAppointmentButton;
	Button cancelAppointmentButton;
	
	static String availability;
	String time;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking_day_slots);
		Intent i = getIntent();
		availabilityTimes = i.getExtras().getString("availabilityTimes");
		paaId = i.getExtras().getString("paaId");
		paaName = i.getExtras().getString("paaName");
		paaUsername = i.getExtras().getString("paaUsername");
		day = i.getExtras().getString("dayAsNumber");
		theMonth = i.getExtras().getString("theMonth");
		theYear = i.getExtras().getString("theYear");
		userFullname = i.getExtras().getString("fullname");
		userId = i.getExtras().getString("idnumber");
		username = i.getExtras().getString("username");
		emailAddress = i.getExtras().getString("email");
		
		bookAppointmentButton = (Button) findViewById(R.id.book_day_time_button);
		cancelAppointmentButton = (Button) findViewById(R.id.cancel_day_time_button);
		
		bookAppointmentButton.setVisibility(View.INVISIBLE);
		cancelAppointmentButton.setVisibility(View.INVISIBLE);
		
		dateTV = (TextView) findViewById(R.id.date_label);
		dateTV.setPadding(25, 0, 0, 0);
        
		dateTV.setGravity(Gravity.CENTER);
		dateTV.setText(day + " " + theMonth + " " + theYear);
		
		Log.d("IntentDebug", "Obtained username from BookingActivity: " + username);
		thisContext = this;
		
		theDate = day + theMonth + theYear;
		Log.d("BookingDays", "Obtained from intent - paaID: " 
			  + paaId 
			  + " date: " 
			  + theDate);	
		selectedTimeTV = (TextView) findViewById(R.id.booking_day_selected_time);
		assembleAvailabilityTimesOfDay();
		obtainAvailableTimes();
		
		
	}

	private void obtainAvailableTimes() {
		final Firebase timesRef = new Firebase("https://demoandroid.firebaseio.com/appointments/" 
										+ paaId 
										+ "/" 
										+ theDate
										+ "/times/");
		timesRef.addValueEventListener(new ValueEventListener() {

			@Override
			public void onCancelled(FirebaseError arg0) {
				
			}

			@Override
			public void onDataChange(DataSnapshot snap) {
				Object times = snap.getValue();
				
				// If the day has not been added in data store, add it with all times available
				if (times == null) {
					timesRef.removeEventListener(this);
					
					Log.d("BookingDays", "No times for the day " 
										+ theDate
										+ ", making times and setting all as available");
					
					TableLayout tl = (TableLayout) findViewById(R.id.timeslots_table_layout);
					tl.removeAllViews();
					//Make the day in the data store and set all times as available
					for (int i = 0; i < availabilityTimesArrayList.size(); i++) {
						Firebase newTime = timesRef.child(availabilityTimesArrayList.get(i) + "/");
						newTime.child("availability").setValue("Available");
						//int tempTimesId = getResources().getIdentifier("booking_day_" + (i+1) + "_slot", "id", getPackageName());
						//int tempTimesAvailabilityId = getResources().getIdentifier("booking_day_" + (i+1) + "_slot_availability", "id", getPackageName());
						
						
						
						
						/*TextView tempTimesTextView = (TextView) findViewById(tempTimesId);
						TextView tempTimesAvailabilityTextView = (TextView) findViewById(tempTimesAvailabilityId);
						tempTimesTextView.setTag(availabilityTimesArrayList.get(i) + "/available");
						tempTimesTextView.setText(availabilityTimesArrayList.get(i));
						tempTimesAvailabilityTextView.setTag(availabilityTimesArrayList.get(i) + "/available");
						tempTimesAvailabilityTextView.setText("Available");
						*/
						
						
						
						TableRow tr = new TableRow(getApplicationContext());
						tr.setId(100+i);
			            tr.setLayoutParams(new LayoutParams(
			                    LayoutParams.MATCH_PARENT,
			                    LayoutParams.WRAP_CONTENT));
			            // Create a TextView for the current appointment
			            TextView labelTV = new TextView(getApplicationContext());
			            labelTV.setId(200+i);
			            labelTV.setText(availabilityTimesArrayList.get(i) + "             Available");
			            labelTV.setTextColor(Color.BLACK);
			            labelTV.setBackgroundResource(R.drawable.book_day_selector);
			            labelTV.setPadding(25, 0, 0, 0);
			            
			            labelTV.setGravity(Gravity.CENTER);
			            labelTV.setTag(availabilityTimesArrayList.get(i) + "/available");
			            labelTV.setOnClickListener(new OnClickListener() {

							

							@Override
							public void onClick(View v) {
								Log.d(TAG, "Appointment clicked - tag: " + v.getTag());
								String[] timeDetails = v.getTag().toString().split("/");
								
								time = timeDetails[0];
								availability = timeDetails[1];
								
								
								// If the user is the PAA
								if (userId.contains("PAA")) {
									if (appointmentState.equals("ongoing") || appointmentState.equals("available")) {
										selectedTimeTV.setText("Cancel appointment on " + time + " ?");
										bookAppointmentButton.setVisibility(View.INVISIBLE);
										cancelAppointmentButton.setVisibility(View.VISIBLE);
									} else { // Already cancelled or expired
										selectedTimeTV.setText("Unavailable time (" + time + ").");
										bookAppointmentButton.setVisibility(View.INVISIBLE);
										cancelAppointmentButton.setVisibility(View.INVISIBLE);
									}
								} else { // The user is a student
									if (appointmentState.equals("available")) {
										bookAppointmentButton.setVisibility(View.VISIBLE);
										cancelAppointmentButton.setVisibility(View.INVISIBLE);
										selectedTimeTV.setText("Book appointment on " + time + " ?");
									} else {
										selectedTimeTV.setText("Unavailable time (" + time + ").");
										bookAppointmentButton.setVisibility(View.INVISIBLE);
										cancelAppointmentButton.setVisibility(View.INVISIBLE);
									}
								} // End of if (user is PAA)
								
								
							/*	if (availability.equals("available")) {
									TextView view = (TextView) v;
									if (userId.contains("PAA")) {
										cancelAppointmentButton.setVisibility(View.VISIBLE);
										bookAppointmentButton.setVisibility(View.INVISIBLE);
										selectedTimeTV.setText("Cancel appointment on " + time + " ?");
									} else {
										bookAppointmentButton.setVisibility(View.VISIBLE);
										cancelAppointmentButton.setVisibility(View.INVISIBLE);
										selectedTimeTV.setText("Book appointment on " + time + " ?");
									}
								} else {
									if (userId.contains("PAA")) {
										if (appointmentState.equals("ongoing")) {
											selectedTimeTV.setText("Cancel appointment on " + time + " ?");
											cancelAppointmentButton.setVisibility(View.VISIBLE);
											bookAppointmentButton.setVisibility(View.INVISIBLE);
										}
									} else {
										selectedTimeTV.setText("Unavailable time (" + time + ").");
										bookAppointmentButton.setVisibility(View.INVISIBLE);
										cancelAppointmentButton.setVisibility(View.INVISIBLE);
									}
									
									
								}*/
								/*TextView selectedAppointment = (TextView) rootView.findViewById(R.id.appointments_selected_appointment);
								selectedAppointment.setText("Do you want to cancel the appointment at: " 
															+ appointmentDate 
															+ " - " 
															+ appointmentTime 
															+ " with - " 
															+ appointmentWithFullname 
															+ "("
															+ appointmentWithIdNumber
															+ ") ?");
								cancelAppointmentButton.setVisibility(0);*/
							} // End of onClick for text views
			            	
			            });
			           
			            tr.addView(labelTV);
			            // Add the TableRow to the TableLayout
			            tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
						
						
						
						
						/*TextView tempTimesTextView = (TextView) findViewById(tempTimesId);
						TextView tempTimesAvailabilityTextView = (TextView) findViewById(tempTimesAvailabilityId);
						tempTimesTextView.setTag(availabilityTimesArrayList.get(i) + "/available");
						tempTimesTextView.setText(availabilityTimesArrayList.get(i));
						tempTimesAvailabilityTextView.setTag(availabilityTimesArrayList.get(i) + "/available");
						tempTimesAvailabilityTextView.setText("Available");*/
					}
					timesRef.addValueEventListener(this);
				} else  { // The day already exists in the data store, obtain the
						// available and unavaliable times and display it to user
					Log.d("BookingDays", "There are times for the day: " 
						  + theDate
						  + "obtaining the available and unavailable times.");
					TableLayout tl = (TableLayout) findViewById(R.id.timeslots_table_layout);
					tl.removeAllViews();
					for (int i = 0; i < availabilityTimesArrayList.size(); i++) {
						//Determine whether the time is available
						/*Firebase currentTime = timesRef.child(availabilityTimesArrayList.get(i) + "/");*/
						DataSnapshot availabilitySnap = (DataSnapshot) snap.child(availabilityTimesArrayList.get(i) + "/" + "availability/");
						String availability = (String) availabilitySnap.getValue();
						
						
						
						TableRow tr = new TableRow(getApplicationContext());
						tr.setId(100+i);
			            tr.setLayoutParams(new LayoutParams(
			                    LayoutParams.MATCH_PARENT,
			                    LayoutParams.WRAP_CONTENT));
			            // Create a TextView for the current appointment
			            TextView labelTV = new TextView(getApplicationContext());
			            labelTV.setId(200+i);
			            if (availability.equals("Available")) {
			            	appointmentState = "available"; 
			            	labelTV.setText(availabilityTimesArrayList.get(i) + "             Available");
			            	labelTV.setTag(availabilityTimesArrayList.get(i) + "/available");
			            } else {
			            	// If the person logged in is the PAA then display him a range of appointment states (i.e. Cancelled, Ongoing & Expired)
			            	if (userId.contains("PAA")) {
			            		
			            		String cancelled = (String) snap.child(availabilityTimesArrayList.get(i) + "/" + "cancelledBy/").getValue();
			            		if (cancelled != null) {
			            			if (cancelled.equals("expiration")) {
			            				labelTV.setText(availabilityTimesArrayList.get(i) + "             Expired");
						            	labelTV.setTag(availabilityTimesArrayList.get(i) + "/expired/");
			            			} else if (cancelled.equals("PAA") || cancelled.equals("student")) {
			            				
			            				labelTV.setText(availabilityTimesArrayList.get(i) + "             Cancelled");
						            	labelTV.setTag(availabilityTimesArrayList.get(i) + "/cancelled");
			            			}
			            		} else {
			            			labelTV.setText(availabilityTimesArrayList.get(i) + "             Ongoing");
					            	labelTV.setTag(availabilityTimesArrayList.get(i) + "/ongoing/" + (String) snap.child(availabilityTimesArrayList.get(i) + "/" + "username/").getValue() + "/" + (String) snap.child(availabilityTimesArrayList.get(i) + "/fullname").getValue());
			            		}
			            	} else { // The user is a student
			            		labelTV.setText(availabilityTimesArrayList.get(i) + "             Unavailable");
				            	labelTV.setTag(availabilityTimesArrayList.get(i) + "/unavailable");
			            	}
			            	
			            }
			            
			            labelTV.setTextColor(Color.BLACK);
			            labelTV.setBackgroundResource(R.drawable.book_day_selector);
			            labelTV.setPadding(25, 0, 0, 0);
			            
			            labelTV.setGravity(Gravity.CENTER);
			            
			            labelTV.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Log.d(TAG, "------------onClick() - tag: " + v.getTag() + " appointmentState: " + appointmentState + " ------------");
								String[] timeDetails = v.getTag().toString().split("/");
								time = timeDetails[0];
								BookingDaySlotsActivity.availability = timeDetails[1];
								appointmentState = timeDetails[1];
								try {
									userWithAppointment = timeDetails[2];
									userFullNameWithAppointment = timeDetails[3];
								} catch (Exception npe) {
									Log.d(TAG, "The array with time details doesn't contain 3rd index --> " + timeDetails);
								}
								Button bookAppointmentButton = (Button) findViewById(R.id.book_day_time_button);
								Button cancelAppointmentButton = (Button) findViewById(R.id.cancel_day_time_button);
								
								
								
								// If the user is the PAA
								if (userId.contains("PAA")) {
									
									if (BookingDaySlotsActivity.availability.equals("ongoing") || BookingDaySlotsActivity.availability.equals("available")) {
										selectedTimeTV.setText("Cancel appointment on " + time + " ?");
										bookAppointmentButton.setVisibility(View.INVISIBLE);
										cancelAppointmentButton.setVisibility(View.VISIBLE);
									} else { // Already cancelled or expired
										selectedTimeTV.setText("Unavailable time (" + time + ").");
										bookAppointmentButton.setVisibility(View.INVISIBLE);
										cancelAppointmentButton.setVisibility(View.INVISIBLE);
									}
								} else { // The user is a student
									if (BookingDaySlotsActivity.availability.equals("available")) {
										bookAppointmentButton.setVisibility(View.VISIBLE);
										cancelAppointmentButton.setVisibility(View.INVISIBLE);
										selectedTimeTV.setText("Book appointment on " + time + " ?");
									} else {
										selectedTimeTV.setText("Unavailable time (" + time + ").");
										bookAppointmentButton.setVisibility(View.INVISIBLE);
										cancelAppointmentButton.setVisibility(View.INVISIBLE);
									}
								} // End of if (user is PAA)
								
								
								
								
								
							/*	if (BookingDaySlotsActivity.availability.equals("available")) {
									if (userId.contains("PAA")) {
										cancelAppointmentButton.setVisibility(View.VISIBLE);
										bookAppointmentButton.setVisibility(View.INVISIBLE);
										selectedTimeTV.setText("Cancel appointment on " + time + " ?");
									} else {
										
									}
								} else {
									
									
									
									if (userId.contains("PAA")) {
										if (appointmentState.equals("ongoing") || appointmentState.equals("available")) {
											selectedTimeTV.setText("Cancel appointment on " + time + " ?");
											bookAppointmentButton.setVisibility(View.INVISIBLE);
											cancelAppointmentButton.setVisibility(View.VISIBLE);
										} else {
											selectedTimeTV.setText("Unavailable time (" + time + ").");
											bookAppointmentButton.setVisibility(View.INVISIBLE);
											cancelAppointmentButton.setVisibility(View.INVISIBLE);
										}
									} else {
										selectedTimeTV.setText("Unavailable time (" + time + ").");
										bookAppointmentButton.setVisibility(View.INVISIBLE);
										cancelAppointmentButton.setVisibility(View.INVISIBLE);
									}
									
								}*/
								/*TextView selectedAppointment = (TextView) rootView.findViewById(R.id.appointments_selected_appointment);
								selectedAppointment.setText("Do you want to cancel the appointment at: " 
															+ appointmentDate 
															+ " - " 
															+ appointmentTime 
															+ " with - " 
															+ appointmentWithFullname 
															+ "("
															+ appointmentWithIdNumber
															+ ") ?");
								cancelAppointmentButton.setVisibility(0);*/
								
							Log.d(TAG, "------------ END OF onClick() ------------");
							} // End of onClick for text views
			            });
						
			            tr.addView(labelTV);
			            // Add the TableRow to the TableLayout
			            tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
						
						
						
						
						//int tempTimesId = getResources().getIdentifier("booking_day_" + (i+1) + "_slot", "id", getPackageName());
						//int tempTimesAvailabilityId = getResources().getIdentifier("booking_day_" + (i+1) + "_slot_availability", "id", getPackageName());
						
						//TextView tempTimesTextView = (TextView) findViewById(tempTimesId);
						//TextView tempTimesAvailabilityTextView = (TextView) findViewById(tempTimesAvailabilityId);
						
						//tempTimesTextView.setText(availabilityTimesArrayList.get(i));
						
						/*if (availability.equals("Available")) {
							tempTimesAvailabilityTextView.setText("Available");
							tempTimesTextView.setTag(availabilityTimesArrayList.get(i) + "/available");
							tempTimesAvailabilityTextView.setTag(availabilityTimesArrayList.get(i) + "/available");
						} else {
							tempTimesAvailabilityTextView.setText("Unavailable");
							tempTimesTextView.setTag(availabilityTimesArrayList.get(i) + "/unavailable");
							tempTimesAvailabilityTextView.setTag(availabilityTimesArrayList.get(i) + "/unavailable");
						}*/
					}
				}
			}
			
		});
	}

	private void assembleAvailabilityTimesOfDay() {
		
		if (availabilityTimes.contains(",")) {
			String[] availabilityTimesArray =availabilityTimes.split(",");
			String availableTimesBeforeLunch = availabilityTimesArray[0];
			Log.d("BookingDays", "availableTimesBeforeLunch = " + availableTimesBeforeLunch);
			String availableTimesAfterLunch = availabilityTimesArray[1];
			Log.d("BookingDays", "availableTimesAfterLunch = " + availableTimesAfterLunch);
			
			getAvailabilityTimesAsArrayList(availableTimesBeforeLunch);
			getAvailabilityTimesAsArrayList(availableTimesAfterLunch);
			
		} else {
			Log.d("BookingDays", "availabilityTimes = " + availabilityTimes);
			getAvailabilityTimesAsArrayList(availabilityTimes);
		}
		
		
	}
	//Obtain the availability times on 30 minutes intervals and add each to array list
	private void getAvailabilityTimesAsArrayList(
			String availableTimesBeforeLunch) {
		//Split the availability times into the start and end time
		String[] splittedAvailableTimes = availableTimesBeforeLunch.split("-");
		String startTime = splittedAvailableTimes[0];
		String endTime = splittedAvailableTimes[1];
		String[] splittedStartTime = startTime.split(":");
		//Obtain the hour and minutes from the splitted start time
		String startOfAvailableTimesHour = splittedStartTime[0];
		String startOfAvailableTimesMinutes = splittedStartTime[1];
		float startOfAvailableTimesMinutesAsFloat;
		float startOfAvailableTimesHoursAsFloat;
		//Convert the minutes of starting time to float (i.e. 30 past will be converted to 0.5)
		if (startOfAvailableTimesMinutes.equals("30")) {
			startOfAvailableTimesMinutesAsFloat = (float) 0.5;
		} else {
			startOfAvailableTimesMinutesAsFloat = (float) 0.0;
		}
		startOfAvailableTimesHoursAsFloat = Float.parseFloat(startOfAvailableTimesHour);
		float startTimeAsFloat = startOfAvailableTimesHoursAsFloat + startOfAvailableTimesMinutesAsFloat;
		Log.d("BookingDays", "startTimeAsFloat = " + startTimeAsFloat);
		
		String[] splittedEndTime = endTime.split(":");
		//Obtain the hour and minutes from the splitted end time
		String endOfAvailableTimesHour = splittedEndTime[0];
		String endOfAvailableTimesMinutes = splittedEndTime[1];
		float endOfAvailableTimesMinutesAsFloat;
		float endOfAvailableTimesHoursAsFloat;
		//Convert the minutes of ending time to float
		if (endOfAvailableTimesMinutes.equals("30")) {
			endOfAvailableTimesMinutesAsFloat = (float) 0.5;
		} else {
			endOfAvailableTimesMinutesAsFloat = (float) 0.0;
		}
		endOfAvailableTimesHoursAsFloat = Float.parseFloat(endOfAvailableTimesHour);
		float endTimeAsFloat = endOfAvailableTimesHoursAsFloat + endOfAvailableTimesMinutesAsFloat;
		int numberOfIterationsNeeded = (int) ((endTimeAsFloat - startTimeAsFloat) * 2);
		String tempAppointmentTime = startTime;
		Log.d("BookingDays", "tempAppointmentTime = " + tempAppointmentTime);
		availabilityTimesArrayList.add(tempAppointmentTime);
		Log.d("BookingDays", "availabilityTimesArrayList = " + availabilityTimesArrayList);
		// Loop to obtain the times of appointments on 30 minutes intervals, and add
		// each to an array list
		for (int i = 0; i < numberOfIterationsNeeded-1; i++) {
			String[] splitTempAppointmentTime = tempAppointmentTime.split(":");
			String hourTempAppointmentTime = splitTempAppointmentTime[0];
			Log.d("BookingDays","hourTempAppointmentTime = " + hourTempAppointmentTime);
			String minutesTempAppointmentTime = splitTempAppointmentTime[1];
			Log.d("BookingDays","minutesTempAppointmentTime = " + minutesTempAppointmentTime);
			if (minutesTempAppointmentTime.equals("30")) {
				float tempNewAppointmentTimeHour = Float.valueOf(hourTempAppointmentTime);
				++tempNewAppointmentTimeHour;
				
				tempAppointmentTime = Integer.toString((int)tempNewAppointmentTimeHour) + ":00";
				availabilityTimesArrayList.add(tempAppointmentTime);
			} else {
				tempAppointmentTime = hourTempAppointmentTime + ":30";
				availabilityTimesArrayList.add(tempAppointmentTime);
			}
		}
		availabilityTimesArrayList.add(endTime);
		Log.d("BookingDays", "availabilityTimesArrayList = " + availabilityTimesArrayList);
	} // End of getAvailabilityTimesAsArrayList method

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.booking_day_slots, menu);
		return true;
	}
	
/*	public void selectTime(View v) {
		String timeAndAvailability = (String) v.getTag();
		// If there is a time allocated in the clicked slot
		if (timeAndAvailability != null) { 
			Log.d("BookingDays", "Obtained timeAndAvailability: " + timeAndAvailability);
			String[] splitTimeAndAvailability = timeAndAvailability.split("/");
			time = splitTimeAndAvailability[0];
			availability = splitTimeAndAvailability[1];
			
			Log.d("BookingDays", "Splitted time: " + time + " availability: " + availability);
			if (availability.equals("available")) {
				selectedTime.setText("Would you like to book your appointment at " + time + " ?");
			} else {
				selectedTime.setText("The " + time+ " slot is unavailable.");
			}
		} else { // There is no time in the clicked slot
			time = "0";
			availability = "unavailable";
			selectedTime.setText("The slot you selected has no time.");
		}
	}*/
	
	public void checkAppointment(final View v) {
		final Firebase userRef = new Firebase("https://demoandroid.firebaseio.com/user/username/" + username + "/");
		
		userRef.addValueEventListener(new ValueEventListener() {

			@Override
			public void onCancelled(FirebaseError arg0) {
				
			}

			@Override
			public void onDataChange(DataSnapshot snap) {
				//TODO check that the user has got 0 appointments (i.e. 1 appointment per user)
				Object user = snap.getValue();
				if (user == null) {
					Log.d("Main", username + " doesn't exist.");
				} else { // The user exists
					numberOfAppointments = (String) ((Map)user).get("currentappointments");
					/*emailAddress = (String) ((Map)user).get("email");*/
					Log.d("BookingDays", "number of appointments = " + numberOfAppointments);
				} // End of else (user exists)
				
				// Remove the value event listener
				userRef.removeEventListener(this);
				// TODO Consider using SQLite database instead of the online data store
				// If the user has no appointments then call the method to book the appointment
				if (numberOfAppointments.equals("0")) {
					bookTheAppointment(v);
				} else {
					selectedTimeTV.setText("You already have an appointment, hence you can only have 1 appointment at a time.");
				}
			}// End of onDataChange
			
		});// End of ValueEventListener for userRef
	}
	
	public void bookTheAppointment(final View v) {
	    //TODO Ensure that the availability has not changed
		// check the position of time in the array list
		if (availabilityTimesArrayList.contains(time)) {
			int positionOfTime = availabilityTimesArrayList.indexOf(time);
			// instantiate the text view corresponding to that time
			//int tempTimesId = getResources().getIdentifier("booking_day_" + (positionOfTime+1) + "_slot", "id", getPackageName());
			//TextView timeTextView = (TextView) findViewById(tempTimesId);
			// obtain its tag and obtain the current availability
		
			//String timeAndAvailabilityTag = (String) timeTextView.getTag();
			//String[] splittedTimeAndAvailability = timeAndAvailabilityTag.split("/");
			//String timeFromTag = splittedTimeAndAvailability[0];
			//String availabilityFromTag = splittedTimeAndAvailability[1];
			//availability = availabilityFromTag;
		}
		if (availability.equals("available")) {
			
			final Firebase appointmentRef = new Firebase("https://demoandroid.firebaseio.com/appointments/" 
												   + paaId 
												   + "/" 
												   + theDate
												   + "/times/"
												   + time
												   + "/");
			appointmentRef.child("availability").setValue("unavailable");
			
			Log.d("Debug4", "The username = " + username);
			appointmentRef.child("username").setValue(username);
			appointmentRef.child("fullname").setValue(userFullname);
			appointmentRef.child("idnumber").setValue(userId);
			
			appointmentRef.child("day").setValue(day);
			appointmentRef.child("month").setValue(theMonth);
			appointmentRef.child("year").setValue(theYear);
			appointmentRef.child("paaId").setValue(paaUsername);
			final Firebase userRef = new Firebase("https://demoandroid.firebaseio.com/user/username/" + username + "/");
			
			//Ensure that the appointment was booked to this user
			final Firebase userAppRef = new Firebase("https://demoandroid.firebaseio.com/appointments/" + paaId + "/" + theDate + "/times/" + time + "/");
			int randomDelay = (int)(Math.random() * 1000 + 400);
			
			try {
				TimeUnit.MILLISECONDS.sleep(randomDelay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			userAppRef.addValueEventListener(new ValueEventListener() {

				@Override
				public void onCancelled(FirebaseError arg0) {
					
				}

				@Override
				public void onDataChange(DataSnapshot snap) {
					//TODO check that the user has got 0 appointments (i.e. 1 appointment per user)
					Object userApp = snap.getValue();
					if (userApp == null) {
						Log.d("Main",  "Appointment doesn't exist.");
					} else { // The user exists
						String userFullnameWithAppointment = (String) ((Map)userApp).get("fullname");
						Log.d("Debug4", "user full name with app. = " + userFullnameWithAppointment);
						if (userFullname.equals(userFullnameWithAppointment)) {
							Toast.makeText(thisContext, "You have got the appointment.", Toast.LENGTH_LONG).show();
							selectedTimeTV.setText("You have got the appointment on " + day + " " + theMonth + " " + theYear + "Time - " + time);
							userRef.child("currentappointments").setValue("1");
							BookingDaySlotsActivity.appointmentGiven = true;
							
						} else {
							Toast.makeText(thisContext, "Somebody else has got the appointment, SORRY ABOUT THAT!", Toast.LENGTH_LONG).show();
						}
					} // End of else (user exists)
					
					// Remove the value event listener
					userAppRef.removeEventListener(this);
					
					if (BookingDaySlotsActivity.appointmentGiven) {
						
						
						// Create a FireBase Object in order to mark the user's record with his current appointment
						Firebase userRef = new Firebase("https://demoandroid.firebaseio.com/user/username/" + username + "/");
						userRef.child("currentAppointment/").setValue(paaId + "/" + theDate + "/times/" + time + "/");
						
						
						AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
					
						// Call the Notification Service passing the day, month, year and time of the appointment in order to notify the user in appropriate time.
						Intent notificationIntent = new Intent(thisContext, NotificationService.class);
						PendingIntent pi = PendingIntent.getService(thisContext, 0, notificationIntent, 0);
						//am.cancel(pi);
						notificationIntent.putExtra("day", day);
						notificationIntent.putExtra("month", AppointmentHelper.getMonthAsInt(theMonth));
						notificationIntent.putExtra("year", theYear);
						
						notificationIntent.putExtra("time",time);
						
						// Create a new Gregorian Calendar and set it with the appointment date
						final Calendar appointmentCalendar = new GregorianCalendar(Integer.valueOf(theYear), AppointmentHelper.getMonthAsInt(theMonth), Integer.valueOf(day));
						String[] splitTime = time.split(":");
						final int hours = Integer.valueOf(splitTime[0]);
						final int minutes = Integer.valueOf(splitTime[1]);
						appointmentCalendar.set(Calendar.HOUR_OF_DAY, hours);
						appointmentCalendar.set(Calendar.MINUTE, minutes);
				
						
						
						// am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + millisecondsDifference, pi);
						am.set(AlarmManager.RTC_WAKEUP, appointmentCalendar.getTimeInMillis() - 1200000, pi);
						
						// Obtain the email login and password from the external data storage
						Firebase emailRef = new Firebase("https://demoandroid.firebaseio.com/email");
						emailRef.addListenerForSingleValueEvent(new ValueEventListener() {

							@Override
							public void onCancelled(FirebaseError arg0) { }

							@Override
							public void onDataChange(DataSnapshot snap) {
								Object emailObj = snap.getValue();
								String login = (String) ((Map)emailObj).get("login");
								String pass = (String) ((Map)emailObj).get("pass");
								
								try {   
				                    GmailSender gmailSender = new GmailSender(login, pass);
				                    
				                    
				                    gmailSender.sendMail("PAA Appointment - " + day + "/" + theMonth + "/" + theYear + " " + time,   
				                    		"Your appointment has been booked with " + paaName + ", at the following date " + day + "/" + theMonth + "/" + theYear + ".\n Please be there by " + time + " \n London Metropolitan University UG Office \n 166-220 Holloway Rd, London N7 8DB \n 020 7423 000",   
				                    		login,   
				                            emailAddress);   
				                } catch (Exception e) {   
				                	Log.d("Error", e.getStackTrace().toString());
				                    Log.d("GmailSender", "LoginActivity - Exception: " +e.getMessage());   
				                } 
								
								//thisContext.startService(notificationIntent);
								
								Log.d("TestV2", "Month " + theMonth);
								Log.d("TestV2", "Day " + day);
								// Insert the appointment into internal Calendar of Android
								
								Intent intent = new Intent(Intent.ACTION_INSERT);
								intent.setType("vnd.android.cursor.item/event");
								
								// Add the details of the appointment
								intent.putExtra(Events.TITLE, "PAA Booking with " + paaName);
								intent.putExtra(Events.EVENT_LOCATION, "London Metropolitan University UG Office");
								intent.putExtra(Events.DESCRIPTION, "Try not be late.");

								// Set the hours and minutes to the calendar
								appointmentCalendar.set(Calendar.HOUR_OF_DAY, hours);
								appointmentCalendar.set(Calendar.MINUTE, minutes);
								
								// Add the begin time of the appointment
								intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
										appointmentCalendar.getTimeInMillis());
								// Add the end time of the appointment (30 minutes intervals)
								intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
										appointmentCalendar.getTimeInMillis()+1800000);

								// Make the appointment private and set as busy
								intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
								intent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY); 
								startActivity(intent); 
							}
							
						});
						
						
						
					}
					
					
				}// End of onDataChange
				
			});// End of ValueEventListener for userRef
			
		} else { // The availability is unavailable
			Toast.makeText(this, "The time slot is unavailable.", Toast.LENGTH_LONG).show();
		}
	} // End of BookTheAppointment method
	
	public void cancelAppointment(View v) {
		Log.d(TAG, "//////////////cancelAppointment()\\\\\\\\\\\\\\\\");
		
		/*new AlertDialog.Builder(this)
	    .setTitle("Delete entry")
	    .setMessage("Are you sure you want to delete this entry?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	     .show();*/
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Add the buttons
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   final Firebase appointmentRef = new Firebase("https://demoandroid.firebaseio.com/appointments/" 
		    				   + paaId 
		    				   + "/" 
		    				   + theDate
		    				   + "/times/"
		    				   + time
		    				   + "/");
		    		
		    		if (appointmentState.equals("ongoing")) {
		    			appointmentRef.child("cancelled").setValue("true");
		    			appointmentRef.child("cancelledBy").setValue("PAA");
		    			
		    			
		    			
		    			
		    			
		    			// Obtain the email login and password from the external data storage
		    			Firebase emailRef = new Firebase("https://demoandroid.firebaseio.com/email");
		    			emailRef.addListenerForSingleValueEvent(new ValueEventListener() {

		    				@Override
		    				public void onCancelled(FirebaseError arg0) { }

		    				@Override
		    				public void onDataChange(DataSnapshot snap) {
		    					Object emailObj = snap.getValue();
		    					final String login = (String) ((Map)emailObj).get("login");
		    					final String pass = (String) ((Map)emailObj).get("pass");
		    					
		    					
		    					
		    					final Firebase userRef = new Firebase("https://demoandroid.firebaseio.com/user/username/" + userWithAppointment + "/");
		    					
		    					userRef.child("currentappointments").setValue("0");
		    					userRef.child("currentAppointment").setValue("none");
		    					
		    					userRef.addListenerForSingleValueEvent(new ValueEventListener() {

		    						@Override
		    						public void onCancelled(FirebaseError arg0) {
		    							
		    						}

		    						@Override
		    						public void onDataChange(DataSnapshot snap) {
		    							//TODO check that the user has got 0 appointments (i.e. 1 appointment per user)
		    							Object user = snap.getValue();
		    							if (user == null) {
		    								Log.d("Main", userWithAppointment + " doesn't exist.");
		    							} else {
		    								String studentEmail = (String)((Map)user).get("email");
		    								
		    								
		    								
		    								try {   
		    				                    GmailSender gmailSender = new GmailSender(login, pass);
		    				                    
		    				                    
		    				                    gmailSender.sendMail("PAA Appointment - " + day + "/" + theMonth + "/" + theYear + " " + time,   
		    				                    		"Your appointment has been cancelled with " + paaName + ", at the following date " + day + "/" + theMonth + "/" + theYear + " and time " + time + ". \n London Metropolitan University UG Office \n 166-220 Holloway Rd, London N7 8DB \n 020 7423 000",   
		    				                    		login,   
		    				                            studentEmail); 
		    				                    
		    				                    Log.d(TAG, "Sent mail to the student");
		    				                    Log.d(TAG, "The PAA email address = " + emailAddress);
		    				                    
		    				                    gmailSender.sendMail("Appointment cancelled - " + day + "/" + theMonth + "/" + theYear + " " + time,   
		    				                    		"You have cancelled the appointment with " + userFullNameWithAppointment + ", at the following date " + day + "/" + theMonth + "/" + theYear + ". and time " + time + " \n London Metropolitan University UG Office \n 166-220 Holloway Rd, London N7 8DB \n 020 7423 000",   
		    				                    		login,   
		    				                    		emailAddress); 
		    				                    
		    				                } catch (Exception e) {   
		    				                	Log.d("Error", e.getStackTrace().toString());
		    				                    Log.d("GmailSender", "LoginActivity - Exception: " +e.getMessage());   
		    				                } 
		    								
		    							}
		    						}// End of onDataChange
		    						
		    					});// End of ValueEventListener for userRef
		    					
		    				}
		    			});
		    			
		    		} else if (appointmentState.equals("available")) {
		    			appointmentRef.child("cancelled").setValue("true");
		    			appointmentRef.child("cancelledBy").setValue("PAA");
		    			appointmentRef.child("availability").setValue("unavailable");
		    		}
		           }
		       });
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		           }
		       });
		// Set other dialog properties
		builder.setMessage("Are you sure you want to cancel this appointment?")
	       .setTitle("Appointment cancellation");

		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
		
		
		
		Log.d(TAG, "////////////// END OF cancelAppointment()\\\\\\\\\\\\\\\\");
	}
	
}
