package com.example.paabooking;

import java.util.Map;

import android.util.Log;

import com.firebase.client.Config;
import com.firebase.client.DataSnapshot;
import com.firebase.client.EventTarget;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class FirebaseHelper {
	private final String TAG = "FirebaseHelper";
	private String username = "jdk17";
	private String userFullname;
	private Object userObj = new Object();
	
	public FirebaseHelper() {}
	
	public String getString()  {
		Log.d(TAG, "getString()");
		
		Config firebaseConf = Firebase.getDefaultConfig();
		EventTarget et = new EventTarget() {

			@Override
			public void postEvent(Runnable arg0) {
				Log.d(TAG, "postEvent()");
				
			}
			
			
		};
		firebaseConf.setEventTarget(et);
		
		Firebase firebaseRef = new Firebase("https://demoandroid.firebaseio.com/user/username/" + username);
		
		firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onCancelled(FirebaseError arg0) {Log.d(TAG, "cancelled.");}
			
			@Override
			public void onDataChange(DataSnapshot snap) {
				
				Log.d(TAG, "***********onDataChange()***********");
				// TODO Auto-generated method stub
				Object obj = snap.getValue();
				userFullname = (String)((Map)obj).get("fullname");
				Log.d(TAG, "********* The text = " + userFullname);
				synchronized(userObj) {
					userObj.notify();
				}	
			}
			
		});
		
		try {
			synchronized (userObj) {
				Log.d(TAG, "Calling wait()");
				userObj.wait();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	
		
		Log.d(TAG,"getString() returning text = " + userFullname);
		return userFullname;
		
	}
}
