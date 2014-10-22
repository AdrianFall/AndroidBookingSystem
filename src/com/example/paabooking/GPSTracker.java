package com.example.paabooking;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class GPSTracker extends Service implements LocationListener {

	private final Context context;
	
	boolean isGPSEnabled = false;
	
	boolean isNetworkEnabled = false;
	
	final double UNIVERSITY_LATITUDE = 51.552185;
	final double UNIVERSITY_LONGITUDE = -0.111562;
	
	boolean canGetLocation = false;
	
	double longitude;
	double latitude;
	
	Location location;
    protected LocationManager locationManager;
	
	// Minimum distance for changing updates in meters
	private static final long MIN_DISTANCE_TO_CHANGE_UPDATES = 5; // Meters
	
	// Minimum time between updates
	private static final long MIN_TIME_BETWEEN_UPDATES = 20000; // 20 seconds
	
	public GPSTracker(Context context) {
		this.context = context;
		getCurrentLocation();
	}
	
	
	private void getCurrentLocation() {
		// TODO Auto-generated method stub
		locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
		
		// getting GPS status
		isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		// getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        if(!isGPSEnabled && !isNetworkEnabled) {
        	Log.d("GPSTracker", "GPS and Network aren't enabled.");
        } else {
        	this.canGetLocation = true;
        	
        	if (isGPSEnabled) {
        		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_DISTANCE_TO_CHANGE_UPDATES, MIN_TIME_BETWEEN_UPDATES, this);
        		Log.d("GPSTracker", "GPS on.");
        		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        		if (location != null) {
        			latitude = location.getLatitude();
    				Log.d("GPSTracker", "GPS obtained latitude = " + latitude);
    				longitude = location.getLongitude();
    				Log.d("GPSTracker", "GPS obtained longitude = " + longitude);
        		}
        	}
        	
        	if (isNetworkEnabled) {
        		Log.d("GPSTracker", "Network is enabled.");
        		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_DISTANCE_TO_CHANGE_UPDATES, MIN_TIME_BETWEEN_UPDATES, this);
        		if (locationManager != null) {
        			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        			if (location != null) {
        				latitude = location.getLatitude();
        				Log.d("GPSTracker", "Network obtained latitude = " + latitude);
        				longitude = location.getLongitude();
        				Log.d("GPSTracker", "Network obtained longitude = " + longitude);
        			}
        		}
        		locationManager.removeUpdates(this);
        	}
        }
		
	}
	
	public float getDistanceToUni() {
		float distance = -1;
		if (location != null) {
			Location uniLocation = new Location("uniLoc");
			uniLocation.setLatitude(UNIVERSITY_LATITUDE);
			uniLocation.setLongitude(UNIVERSITY_LONGITUDE);
			// Obtain the distance between locations in meters.
			distance = uniLocation.distanceTo(location);
			
		}
		return distance;
	}

	// Getter method for the latitude
	public double getLatitude() {
		double latitude = -1;
		
		if (location != null) {
			latitude = location.getLatitude();
		}
		
		return latitude;
	}
	
	// Getter method for the longitude
	public double getLongitude() {
		double longitude = -1;
		
		if (location != null) {
			longitude = location.getLongitude();
		}
		
		return longitude;
	}
	
	// Method for returning boolean whether location can be fetched
	public boolean canGetLoc() {
		return canGetLocation;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
