package com.example.paabooking;

import android.os.AsyncTask;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class AppointmentHelper {

	
	public static int getMonthAsInt(String theMonth) {
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
	
}
