package com.example.paabooking;

import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	Intent i;
	String course;
	Context context;
	
	private String loginUsername = null;
	private String loginPassword = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//XXX
		/*
		Log.d("FirebaseHelper", "LoginActivity - calling getStudentRecord");
		FirebaseHelper fbHelper = new FirebaseHelper();
		Object obj = fbHelper.getString();
		Log.d("FirebaseHelper", "LoginActivity - obtained object = " + obj.toString());
		*/
		//XXX
		
		setContentView(R.layout.activity_login);
		context = this;
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	} // End onCreate
	
	@Override
	protected void onResume() {
		super.onResume();
		/*Log.d("NotiServTest", "onResume()");
		Intent i = new Intent(getApplicationContext(), NotificationServiceTest.class);
		ComponentName cn = startService(i);
		if (cn == null) {
			Log.d("NotiServTest", "startService returned null");
		} else {
			Log.d("NotiServTest", "startService worked.");
		}*/
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem instantiates the appropriate class fragment based
			// upon the position, the position 0 is allocated for log-in
			// and the position 1 for registration of new user.
			Fragment fragment;
			Bundle args = new Bundle();

			if (position == 0) {
				fragment = new LoginFragment();
			} else {
				fragment = new RegisterFragment();
			}

			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 2 pages, first one dedicated to log-in and second to
			// registering
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0: // login
				return getString(R.string.login_section).toUpperCase(l);
			case 1: // register
				return getString(R.string.register_section).toUpperCase(l);
			}
			return null;
		}
	}

	public void login(View view) {
		Log.d("Main", "Connect button pressed");

		if (hasInternetConnection()) {
			
			// Declare and initialize a ProgressDialog
			
			
			// Obtain the username
			EditText usernameEditText = (EditText) findViewById(R.id.login_username);
			EditText passwordEditText = (EditText) findViewById(R.id.login_password);
			final String password;
			final String username;
			if (loginUsername != null && loginPassword != null) {
				username = loginUsername;
				password = loginPassword;
			} else {
				username = usernameEditText.getText().toString();
				password = passwordEditText.getText().toString();
			}
			 
			
	
			Log.d("Main", "Obtained username: " + username + " and password: "
					+ password);
			final TextView errorMessage = (TextView) findViewById(R.id.login_err);
			// Make the error message visible
			errorMessage.setVisibility(0);
			// Test whether the username is empty
			
			//TODO detect the internet connection
			if (username.equals("")) {
				Log.d("Main", "The username is empty.");
	
				errorMessage.setText("The username can't be empty.");
			} 
			
			else if (password.equals("")) {
				errorMessage.setText("The password can't be empty.");
			} else { // Authenticate the user
				final ProgressDialog progDialog = ProgressDialog.show(context, "Authenticating...", "Please wait.");
				// initialise a Firebase object to connect to the back end storage
				final Firebase userRef = new Firebase(
						"https://demoandroid.firebaseio.com/user/username/"
								+ username + "/");
	
				// Add the event listener to the userRef, in order to authenticate
				// the user
				userRef.addValueEventListener(new ValueEventListener() {
	
					@Override
					public void onCancelled(FirebaseError arg0) {
						Log.d("Main", "userRef Listener was cancelled.");
					}
	
					@Override
					public void onDataChange(DataSnapshot snap) {
						Object user = snap.getValue();
						if (user == null) {
							Log.d("Main", username + " doesn't exist.");
							progDialog.cancel();
							errorMessage.setText("The username doesn't exist.");
						} else { // The user exists
							Log.d("Main", "User " + username + " exists.");
							// Authenticate the password
							String pw = (String) ((Map) user).get("password");
							if (pw.equals(password)) {
								Log.d("Main", "Authentication successful");
								
								/*try {   
				                    CopyOfGmailSender sender = new CopyOfGmailSender("qiftei@gmail.com", "adrianq92");
				                    
				                    
				                    CopyOfGmailSender.sendMail("This is a test message.",   
				                            "This works!",   
				                            "qiftei@gmail.com",   
				                            "adrianq92@hotmail.com,jmlozano80@gmail.com");   
				                } catch (Exception e) {   
				                	Log.d("Error", e.getStackTrace().toString());
				                    Log.d("GmailSender", "LoginActivity - Exception: " +e.getMessage());   
				                } */
								
								// TODO Save the username and password in SharedPreferences
								SharedPreferences sharedPreferences = getSharedPreferences("bookingAppPreferences", Context.MODE_PRIVATE);
								Editor editor = sharedPreferences.edit();
								editor.putString("Login", username);
								editor.putString("Password", password);
								editor.commit();
								
								// Hide the error message
								errorMessage.setVisibility(4);
								Toast.makeText(getApplicationContext(),
										"Authentication Successful",
										Toast.LENGTH_SHORT).show();
								// Open the BookingActivity
								i = new Intent(getApplicationContext(),
										BookingActivity.class);
								String fullname = (String) ((Map) user)
										.get("fullname");
								String idnumber = (String) ((Map) user)
										.get("idnumber");
								String email = (String) ((Map)user).get("email");
								course = (String) ((Map) user).get("course");
								i.putExtra("username", username);
								i.putExtra("course", course);
								i.putExtra("fullname", fullname);
								i.putExtra("idnumber", idnumber);
								i.putExtra("email", email);
								userRef.removeEventListener(this);
								progDialog.dismiss();
								obtainPaaIdAndDelegateActivity();
	
							} else {
								progDialog.dismiss();
								Log.d("Main", "Authentication Unsuccessful");
								errorMessage
										.setText("The password you entered is incorrect.");
							}
						}// End else (the user exists)
	
					} // End onDataChange
	
					private void obtainPaaIdAndDelegateActivity() {
						final Firebase courseRef = new Firebase(
								"https://demoandroid.firebaseio.com/courses/"
										+ course);
	
						courseRef.addValueEventListener(new ValueEventListener() {
	
							@Override
							public void onCancelled(FirebaseError arg0) {
	
							}
	
							@Override
							public void onDataChange(DataSnapshot snap) {
								Object course = snap.getValue();
								String paaId = (String) ((Map) course).get("PAA");
								String paaName = (String) ((Map) course)
										.get("PAAname");
								String paaMail = (String) ((Map)course).get("PAAmail");
								courseRef.removeEventListener(this);
								i.putExtra("paaId", paaId);
								i.putExtra("paaName", paaName);
								i.putExtra("paaMail", paaMail);
								
								
								
								startActivity(i);
							}
	
						});
					}
	
				});// End userRef event listener
			}// End of else (authenticate user)
		} // End of if (hasInternetConnection())
		else {
			// If no Internet connection is available
			Toast.makeText(this, "You need to have Internet connection to login.", Toast.LENGTH_LONG).show();
		}
	}// End of login method

	private boolean hasInternetConnection() {
		boolean isConnected = false;
		
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
		        Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    if (wifiNetwork != null && wifiNetwork.isConnected()) {
	      return true;
	    }

	    NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	    if (mobileNetwork != null && mobileNetwork.isConnected()) {
	      return true;
	    }

	    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	    if (activeNetwork != null && activeNetwork.isConnected()) {
	      return true;
	    }	
		
		return isConnected;
	}

	public void register(View view) {
		Log.d("Main", "Register button pressed.");
		
		// Obtain the username
		EditText usernameEditText = (EditText) findViewById(R.id.register_user);
		final String username = usernameEditText.getText().toString();
		// Obtain the password
		EditText passwordEditText = (EditText) findViewById(R.id.register_password);
		final String password = passwordEditText.getText().toString();
		// Obtain the full name
		EditText fullnameEditText = (EditText) findViewById(R.id.register_fullname);
		final String fullname = fullnameEditText.getText().toString();
		// Obtain the ID number
		EditText idNumberEditText = (EditText) findViewById(R.id.register_idnumber);
		final String idNumber = idNumberEditText.getText().toString();

		// Obtain the course
		Spinner courseSpinner = (Spinner) findViewById(R.id.courses_spinner);
		final String course = courseSpinner.getSelectedItem().toString();
		Log.d("Main", "Course = " + course);
		
		// Obtain the email
		EditText emailEditText = (EditText) findViewById(R.id.register_email);
		final String email = emailEditText.getText().toString();

		// Obtain the error text view
		final TextView errorTextView = (TextView) findViewById(R.id.register_error);
		// Set to visible
		errorTextView.setVisibility(0);

		// Check whether the given username is not blank
		if (username.trim().equals("")) {
			Log.d("Main", "Username is empty.");
			errorTextView.setText("The username can't be empty.");
		} else if (password.equals("")) {
			Log.d("Main", "Password is empty.");
			errorTextView.setText("The password can't be empty");
		} else if (fullname.trim().equals("")) {
			Log.d("Main", "Full name is empty.");
			errorTextView.setText("The full name can't be empty.");
		} else if (idNumber.equals("")) {
			Log.d("Main", "ID number is empty.");
			errorTextView.setText("The ID number can't be empty.");
		} else if (username.contains(".") || username.contains("#") || username.contains("$") || username.contains("[") || username.contains("]") || username.contains(",")) {
			// Display an error, since firebase won't accept the following symbol in the URL.
			errorTextView.setText("The username can't contain '.', '#', '$', '[', ']' or , ");
		} else if (!email.contains("@") || !email.contains(".")) {
			errorTextView.setText("Wrong email format.");
		} else { // Check whether the username is available, and create the user
			final ProgressDialog progDialog = ProgressDialog.show(context, "Registering...", "Please wait.");
			// initialise a Firebase object to connect to the back end storage
			final Firebase userRef = new Firebase(
					"https://demoandroid.firebaseio.com/user/username/"
							+ username + "/");
			// Add the event listener to the userRef, in order to authenticate
			// the user
			userRef.addValueEventListener(new ValueEventListener() {

				@Override
				public void onCancelled(FirebaseError arg0) {
					Log.d("Main", "userRef Listener was cancelled.");
				}

				@Override
				public void onDataChange(DataSnapshot snap) {
					Object user = snap.getValue();
					// Check whether the user already exists
					if (user != null) {
						progDialog.dismiss();
						Log.d("Main", username + " already exists.");
						errorTextView
								.setText("The username already exists, please pick another one.");
					} else { // Create the user

						Log.d("Main", "Username " + username + " is available.");
						Firebase userReg = new Firebase(
								"https://demoandroid.firebaseio.com/user/");
						Firebase newUser = userReg.child("username/" + username
								+ "/");
						newUser.child("password/").setValue(password);
						newUser.child("fullname/").setValue(fullname);
						newUser.child("idnumber/").setValue(idNumber);
						newUser.child("course/").setValue(course);
						newUser.child("currentappointments/").setValue("0");
						newUser.child("email").setValue(email);
						Log.d("Main", "Account created.");
						// Set to invisible
						errorTextView.setVisibility(4);
						Toast.makeText(getApplicationContext(),
								"Account has been created.", Toast.LENGTH_SHORT)
								.show();
						final Intent i = new Intent(getApplicationContext(),
								BookingActivity.class);

						// TODO pass PAA name through the intent

						final Firebase courseRef = new Firebase(
								"https://demoandroid.firebaseio.com/courses/"
										+ course);

						courseRef.addValueEventListener(new ValueEventListener() {

									@Override
									public void onCancelled(FirebaseError arg0) {

									}

									@Override
									public void onDataChange(DataSnapshot snap) {
										Object courseObj = snap.getValue();
										String paaId = (String) ((Map) courseObj)
												.get("PAA");
										String paaName = (String) ((Map) courseObj)
												.get("PAAname");
										String paaMail = (String) ((Map) courseObj).get("PAAmail");
										courseRef.removeEventListener(this);
										i.putExtra("paaId", paaId);
										i.putExtra("paaName", paaName);
										i.putExtra("paaMail", paaMail);
										i.putExtra("username", username);
										i.putExtra("course", course);
										i.putExtra("fullname", fullname);
										i.putExtra("idnumber", idNumber);
										i.putExtra("email", email);
										
										// Insert the username and password into the Shared Preferences
										SharedPreferences sharedPreferences = getSharedPreferences("bookingAppPreferences", Context.MODE_PRIVATE);
										Editor editor = sharedPreferences.edit();
										/*Delete first in case they exist*/
										editor.remove("Login");
										editor.remove("Password");
										editor.commit();
										editor.putString("Login", username);
										editor.putString("Password", password);
										editor.commit();
										progDialog.dismiss();
										startActivity(i);
										courseRef.removeEventListener(this);
									} // End onDataChange

								}); // End courseRef ValueEventListener
					} // End if (user not null)

				} // End onDataChange

			});// End userRef event listener
		}// End of else (edit texts are not empty)

	} // End of register method
	
	
	/**
	 * A login fragment
	 */
	public class LoginFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		// public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public void onResume() {
			super.onResume();
			SharedPreferences sharedPreferences = getSharedPreferences("bookingAppPreferences", Context.MODE_PRIVATE);	
			if (sharedPreferences.contains("Login") && sharedPreferences.contains("Password")) {
				loginUsername = sharedPreferences.getString("Login", "-1");
				loginPassword = sharedPreferences.getString("Password", "-1");
				Log.d("SharedPreferences", "sharedPrefs contains Login - " + loginUsername + " Password - " + loginPassword);
				
				AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setMessage("Would you like to login as: " + loginUsername + " ?")
				.setIcon(R.drawable.book2)
				.setTitle("Welcome back.")
				
				.setPositiveButton("Login", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						login(null);
					}
					
				})
				.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SharedPreferences sharedPreferences = getSharedPreferences("bookingAppPreferences", Context.MODE_PRIVATE);
						Editor editor = sharedPreferences.edit();
						editor.remove("Login");
						editor.remove("Password");
						editor.commit();
						loginUsername = null;
						loginPassword = null;
					}
				})
				
				.create();
				alertDialog.show();
				
				
				
				/*LoginDialogFragment dialog = new LoginDialogFragment();
				FragmentManager fragManager = getFragmentManager();
				dialog.show(fragManager, "LoginDialogFragment");*/
				/*
				LoginDialogFragment dialog = new LoginDialogFragment();
				*/
				//login(null);
				/*try {
				EditText usernameEditText = (EditText) rootView.findViewById(R.id.login_username);
				EditText passwordEditText = (EditText) rootView.findViewById(R.id.login_password);
				usernameEditText.setText(loginUsername);
				passwordEditText.setText(loginPassword);
				} catch (NullPointerException e) { 
					Log.d("SharedPreferences", "NullPointerException - " + e.getMessage());
				}*/
				
				
			}
			else Log.d("SharedPreferences", "sharedPrefs doesn't contain Login");
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater,
				final ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_login,
					container, false);
			EditText password = (EditText) rootView
					.findViewById(R.id.login_password);
			
			password.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView arg0, int arg1,
						KeyEvent arg2) {
					// TODO needs to be checked whether 6 is done button on
					// other devices + emulator
					if (arg1 == 6) {
						Log.d("Main", "Done button has been pressed");
						// Automatically press the login button for the user
						login(container);
					}
					return false;
				}

			});
			// dummyTextView.setText(getArguments().getString(ARG_SECTION_NUMBER));
			return rootView;
		}// End on CreateView

	}

	public class RegisterFragment extends Fragment {


		@Override
		public View onCreateView(LayoutInflater inflater,
				final ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_register,
					container, false);

			// Obtain the spinner
			Spinner courseSpinner = (Spinner) rootView 
					.findViewById(R.id.courses_spinner);

			// Create an ArrayAdapter for the use of string array and the
			// default courseSpinner layout

			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(getActivity(), R.array.course_list,
							android.R.layout.simple_spinner_item);

			// Specifies the layout to be used once the list of choices appears
			// to the user
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Applies the adapter to the spinner
			courseSpinner.setAdapter(adapter);

			EditText idNumberEditText = (EditText) rootView
					.findViewById(R.id.register_idnumber);
			idNumberEditText
					.setOnEditorActionListener(new OnEditorActionListener() {

						@Override
						public boolean onEditorAction(TextView arg0, int arg1,
								KeyEvent arg2) {
							// TODO needs to be checked whether 6 is done button
							// on other devices + emulator
							if (arg1 == 6) {
								Log.d("Main", "Done button has been pressed");
								// Automatically press the register button for
								// the user
								register(container);
							}
							return false;
						}

					});

			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);

		}
	}// End of RegisterFragment class
}
