package com.example.paabooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
	
public class BookingActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private static final String TAG = "BookingActivity";

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
	static Button selectedDayMonthYearButton;
	static DateFormat dateFormatter;
	static String username;
	static String fullname;
	static String idnumber;
	static String paaId;
	static String paaName;
	static String paaMail;
	static Activity myActivity;
	String appointmentId;
	String appointmentDateDay;
	String appointmentDateMonth;
	String appointmentDateYear;
	String appointmentTime;
	static String appointmentPath;
	String email;
	static Thread thread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking);
		
		Intent intent = getIntent();
		myActivity = this;
		username = intent.getExtras().getString("username");
		Log.d("IntentDebug", "Obtained username from login activity: " + username);
		fullname = intent.getExtras().getString("fullname");
		idnumber = intent.getExtras().getString("idnumber");
		paaId = intent.getExtras().getString("paaId");
		paaName = intent.getExtras().getString("paaName");
		email = intent.getExtras().getString("email");
		paaMail = intent.getExtras().getString("paaMail");
		
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
		
		
		
	}// End onCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.booking, menu);
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

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem instantiates the appropriate class fragment based
			// upon the position, the position 0 is allocated for bookings,
			// position 1 for appointments of the user and position 2 for
			// the information about the user

			Fragment fragment;
			Bundle args = new Bundle();

			if (position == 0) {
				fragment = new BookingFragment();
			} else if (position == 1) {
				fragment = new AppointmentFragment();
			} else {
				fragment = new MyInfoFragment();
			}

			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total tabs.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.booking_section).toUpperCase(l);
			case 1:
				return getString(R.string.apoointments_section).toUpperCase(l);
			case 2:
				return getString(R.string.my_info_section).toUpperCase(l);
			}
			return null;
		}
	}

	public static class BookingFragment extends Fragment {
		
		DaysAdapter adapter;
		static Calendar calendar;
		Button currentMonth;
		String dateFormat;
		GridView calendarView;
		static int month;
		int year;
		String paaDaysOfWorkString;
		static String paaTimesOfAvailability;
		static String paaId;
		static String dayAsNumber;
		static String theMonth;
		static String theYear;
		static String dayAsString;
		static String monthAsNumber;
		
		public static Button bookingDaySlotsButton;
		public static Button cancelDaySlot;
		
		static ArrayList<Integer> paaWorkDaysList = new ArrayList<Integer>(0);

		public BookingFragment() {
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_booking,
					container, false);

			
			bookingDaySlotsButton = (Button) rootView.findViewById(R.id.booking_day_slots);
			cancelDaySlot = (Button) rootView.findViewById(R.id.cancel_day_slot);
			
			//Set the bookingDaySlotsButton & cancelDaySlot to invisible by default
			bookingDaySlotsButton.setVisibility(4);
			cancelDaySlot.setVisibility(4);
			
			// Initialise the days of week in the grid view
			GridView DaysOfWeekGrid = (GridView) rootView
					.findViewById(R.id.calendarDaysTitles);
			if (DaysOfWeekGrid == null) {
				Log.d("Main", "DaysOfWeekGrid is null");
			}
			Log.d("Main", "DaysOfWeekGrid initialised.");
			DaysOfWeekGrid.setAdapter(new DaysOfWeekAdapter(getActivity()));
			
			calendar = Calendar.getInstance(Locale.getDefault());
			month = calendar.get(Calendar.MONTH) + 1;
			year = calendar.get(Calendar.YEAR);
			Log.d("Main", "Calendar Instance:= " + "Month: " + month + " "
					+ "Year: " + year);
			
			selectedDayMonthYearButton = (Button) rootView
					.findViewById(R.id.selectedDate);
			selectedDayMonthYearButton.setText("Selected: ");

			//Obtain the paaWorkDaysList through calling a method to prepare and return the array list
			paaWorkDaysList = getPAADaysOfWorkAsIntArrayList();
			
			ImageView prevMonth = (ImageView) rootView
					.findViewById(R.id.prevMonth);
			prevMonth.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//TODO Ensure that clicking previous month button, will
					//grant a month that can be booked, otherwise don't even 
					// allow to get into the previous month
					if (month <= 1) {
						month = 12;
						year--;
					} else {
						month--;
					}
					Log.d("Main", "Setting Prev Month in GridCellAdapter: "
							+ "Month: " + month + " Year: " + year);
					setGridCellAdapterToDate(month, year);
				}
			});

			dateFormatter = new DateFormat();
			dateFormat = "MMMM yyyy";

			currentMonth = (Button) rootView
					.findViewById(R.id.currentMonthDisplay);
			currentMonth.setText(DateFormat.format(dateFormat,
					calendar.getTime()));

			ImageView nextMonth = (ImageView) rootView
					.findViewById(R.id.nextMonth);
			nextMonth.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//TODO ensure that the next month is not a new academic year
					if (month > 11) {
						month = 1;
						year++;
					} else {
						month++;
					}
					Log.d("Main", "Setting Next Month in GridCellAdapter: "
							+ "Month: " + month + " Year: " + year);
					setGridCellAdapterToDate(month, year);

				}

			});

			// Initialise the days of month in the grid
			calendarView = (GridView) rootView.findViewById(R.id.calendaryDays);

			// Initialised

			adapter = new DaysAdapter(getActivity(), month, year);
			adapter.notifyDataSetChanged();
			calendarView.setAdapter(adapter);
			return rootView;
		}
		
		public ArrayList<Integer> getPAADaysOfWorkAsIntArrayList() {
			//obtain the PAA day of work as String, add the days of work as int to an array list
			//and return the array list
			final ArrayList<Integer> paaDaysOfWork = new ArrayList<Integer>();
			Intent i = getActivity().getIntent();
			String course = i.getExtras().getString("course");
			Firebase courseRef = new Firebase("https://demoandroid.firebaseio.com/courses/" + course + "/");
			
			courseRef.addValueEventListener(new ValueEventListener() {

				@Override
				public void onCancelled(FirebaseError arg0) {
					
				}

				@Override
				public void onDataChange(DataSnapshot snap1) {
					Object course = snap1.getValue();
					if (course == null) {
						Log.d("Main", course + " doesn't exist.");
					} else {
						Log.d("Main", course + " does exist.");
						
						paaDaysOfWorkString = (String)((Map)course).get("DaysOfWork");
						paaTimesOfAvailability = (String)((Map)course).get("Availability");
						paaId = (String)((Map)course).get("PAA");
						paaDaysOfWork.clear();
						Log.d("Main", "The obtained days of work: " + paaDaysOfWorkString);
						Log.d("Main", "Substringing the Days of work obtained from the data store, and storing them as elements of the array list");
						// Obtain the days of work from the paaDaysOfWorkString, substring them
						// and add their representation of integers to an array list.
						// Representation meaning, 0 = Sunday, 1 Monday, etc.
						while(paaDaysOfWorkString.contains(",")) {
							int tempIndexOfComma = paaDaysOfWorkString.indexOf(",");
							String tempSubstringDay = paaDaysOfWorkString.substring(0, tempIndexOfComma);
							Log.d("Main", "Obtained day of paa: " + tempSubstringDay);
							if (tempSubstringDay.equals("Mon")) {
								paaWorkDaysList.add(1);
							} else if (tempSubstringDay.equals("Tue")) {
								paaWorkDaysList.add(2);
							} else if (tempSubstringDay.equals("Wed")) {
								paaWorkDaysList.add(3);
							} else if (tempSubstringDay.equals("Thu")) {
								paaWorkDaysList.add(4);
							} else if (tempSubstringDay.equals("Fri")) {
								paaWorkDaysList.add(5);
							}
							paaDaysOfWorkString = paaDaysOfWorkString.substring(tempIndexOfComma+1, paaDaysOfWorkString.length());
						}
						Log.d("Main", "The last day of paa work: " + paaDaysOfWorkString);
						if (paaDaysOfWorkString.equals("Mon")) {
							paaWorkDaysList.add(1);
						} else if (paaDaysOfWorkString.equals("Tue")) {
							paaWorkDaysList.add(2);
						} else if (paaDaysOfWorkString.equals("Wed")) {
							paaWorkDaysList.add(3);
						} else if (paaDaysOfWorkString.equals("Thu")) {
							paaWorkDaysList.add(4);
						} else if (paaDaysOfWorkString.equals("Fri")) {
							paaWorkDaysList.add(5);
						}
						Log.d("Main", "Data obtained, setting the gridcelladaptertodate!");
						setGridCellAdapterToDate(month, year);
					}
				}
				
			});// End courseRef event listener
			return paaDaysOfWork;
		}

		private void setGridCellAdapterToDate(int month, int year) {
			adapter = new DaysAdapter(getActivity(), month, year);
			calendar.set(year, month - 1, calendar.get(Calendar.DAY_OF_MONTH));
			currentMonth.setText(dateFormatter.format(dateFormat,
					calendar.getTime()));
			adapter.notifyDataSetChanged();
			calendarView.setAdapter(adapter);
		}

	}// End of BookingFragment class

	// Innerclass for DaysAdapter
	public static class DaysAdapter extends BaseAdapter implements
			OnClickListener {
		Activity myActivity;
		int month;
		int year;
		ArrayList<String> daysList = new ArrayList<String>();
		private final String[] weekdays = new String[] { "Sun", "Mon", "Tue",
				"Wed", "Thu", "Fri", "Sat" };
		private final String[] months = { "January", "February", "March",
				"April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		private final int[] endDaysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31,
				30, 31, 30, 31 };
		int currentDayOfMonth;
		int currentWeekDay;

		int paaWorkDaysListCount = 0;
		private HashMap eventsPerMonthMap;
		private TextView num_events_per_day;
		
		
		
		public DaysAdapter(Activity myActivity, final int month, final int year) {
			this.myActivity = myActivity;
			this.month = month;
			this.year = year;
			
			
			setCurrentDayOfMonth(BookingFragment.calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(BookingFragment.calendar.get(Calendar.DAY_OF_WEEK));
			
			printTheMonth(month, year);
			eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
		}
		
		

		
		private HashMap findNumberOfEventsPerMonth(int yy, int mm) {

			HashMap map = new HashMap<String, Integer>();
			// TODO
			return map;
		}

		private String getMonthAsString(int i) {
			return months[i];
		}

		private String getWeekDayAsString(int i) {
			return weekdays[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return endDaysOfMonth[i];
		}

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}
		
		public void setCurrentWeekDay(int currentWeekDay)
		{
			this.currentWeekDay = currentWeekDay;
		}
		public int getCurrentWeekDay()
		{
			return currentWeekDay;
		}
		
		public int getDayAsText() {
			
			return 0;
		}
		
		

		private void printTheMonth(int month, int year) {
			Log.d("Main", "Printing month: " + month + " Year: " + year);
			// The number of days to leave blank at
			// the start of this month.	
			int trailingSpaces = 0;
			int leadSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;
			 
			int currentMonth = month - 1;
			String currentMonthName = getMonthAsString(currentMonth);
			int daysInMonth = getNumberOfDaysOfMonth(currentMonth);

			Log.d("Main", "Current Month: " + " " + currentMonthName
					+ " having " + daysInMonth + " days.");

			GregorianCalendar cal = new GregorianCalendar(year, currentMonth, 1);
			Log.d("Main", "Gregorian Calendar:= " + cal.getTime().toString());
			if (cal.isLeapYear(cal.get(Calendar.YEAR)) && currentMonth == 1) {
				Log.d("LeapYear", "Incrementing current month by one day.");
				++daysInMonth;
				
			}
			// If the current month is december, assign the previous month and its days and
			// the next monh to be january of the next year.
			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = year;
				nextYear = year + 1;
				Log.d("Main", "PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else if (currentMonth == 0) { // If current month is january, assign the previous month
											// to be the december of the previous year and its days
				prevMonth = 11;
				prevYear = year - 1;
				nextYear = year;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
				Log.d("Main", "PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else { // Otherwise assign the previous and next month of this year
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = year;
				prevYear = year;
				// If the current month is March and it is a leap year, obtain the days in previous
				// month and increment it
				if (cal.isLeapYear(cal.get(Calendar.YEAR)) && currentMonth == 2) {
					daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
					++daysInPrevMonth;
				} else { // Otherwise if it isn't a leap year, assign the days in previous month normally
					daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
					Log.d("Main", "PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
				}
			}

			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			// Calculates how many days to leave before the current month (i.e. trailing days)
			trailingSpaces = currentWeekDay;

			Log.d("Main", "Starting position of the week:" + currentWeekDay + " is "
					+ getWeekDayAsString(currentWeekDay));
			Log.d("Main", "No. Trailing space to Add: " + trailingSpaces);
			Log.d("Main", "No. of Days in Previous Month: " + daysInPrevMonth);

			// Add the trailing days to the month
			for (int i = 0; i < trailingSpaces; i++) {
				
				int DAY_OFFSET = 1;
				Log.d("Main",
						"PREV MONTH = "
								+ prevMonth
								+ " > "
								+ getMonthAsString(prevMonth)
								+ " "
								+ String.valueOf((daysInPrevMonth
								- trailingSpaces + DAY_OFFSET)
								+ i));
				
				// If the previous month is December, set the calendar date to december of previous year
				if (prevMonth == 11) {
					cal.set(year-1, prevMonth, ((daysInPrevMonth
							- trailingSpaces + DAY_OFFSET)
							+ i));
					/*tempCal = new GregorianCalendar(year - 1, prevMonth, ((daysInPrevMonth
							- trailingSpaces + DAY_OFFSET)
							+ i));*/
				} else { // Otherwise set the calendar date to previous month of this year
					cal.set(year, prevMonth, ((daysInPrevMonth
							- trailingSpaces + DAY_OFFSET)
							+ i));
				}
				int tempWeekDay = cal.get(Calendar.DAY_OF_WEEK);
				--tempWeekDay;
				Log.d("PreviousMonth", "Temp week day = " + tempWeekDay + "PrevMonth =" + prevMonth + "year: " + (year -1) + "Day: " + ((daysInPrevMonth
						- trailingSpaces + DAY_OFFSET)
						+ i));
				// If the working day list of PAA doesn't contain his working day
				if (!BookingFragment.paaWorkDaysList.contains(tempWeekDay)) {
					daysList.add(String
							.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i)
							+ "/RED2"
							+ "/"
							+ getMonthAsString(prevMonth)
							+ "/"
							+ prevYear
							+ "/"
							+ getDayOfWeekAsString(tempWeekDay)
							+ "/unavailable"
							+ "/"
							+ prevMonth);
				} else { // The day the PAA works
					daysList.add(String
						.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i)
						+ "/GREY"
						+ "/"
						+ getMonthAsString(prevMonth)
						+ "/"
						+ prevYear
						+ "/"
						+ getDayOfWeekAsString(tempWeekDay)
						+ "/available"
						+ "/"
						+ prevMonth);
				}
			}

			// Style the days of current month and add them to the array list of daysList
			for (int i = 1; i <= daysInMonth; i++) {
				Log.d("Main", String.valueOf(i) 
						+ " "
						+ getMonthAsString(currentMonth) + " " + year);
				//setTime of calendar to the days of the current month
				cal.set(year, currentMonth, i);
				int tempWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
				//Log.d("Main", "Current week day as int = " + tempWeekDay);
				
				// Add the day as available for appointments with the PAA
				if (BookingFragment.paaWorkDaysList.contains(tempWeekDay)) {
					daysList.add(String.valueOf(i) 
							+ "/WHITE" 
							+ "/"
							+ getMonthAsString(currentMonth) 
							+ "/" 
							+ year
							+ "/"
							+ getDayOfWeekAsString(tempWeekDay)
							+ "/available"
							+ "/"
							+ currentMonth);
				} else {
					daysList.add(String.valueOf(i) 
							+ "/RED" 
							+ "/"
							+ getMonthAsString(currentMonth) 
							+ "/" + year
							+ "/"
							+ getDayOfWeekAsString(tempWeekDay)
							+ "/unavailable"
							+ "/"
							+ currentMonth);
				}
			}

			// Leading Month days
			for (int i = 0; i < daysList.size() % 7; i++) {
				Log.d("Main", "NEXT MONTH:= " + getMonthAsString(nextMonth));
				
				// setTime of calendar to the next months days, of potentially next year
				cal.set(nextYear, nextMonth, i);
				// If the previous month is December
				
				int tempWeekDay = cal.get(Calendar.DAY_OF_WEEK);
				// Add the leading day as available for appointments with PAA
				if (BookingFragment.paaWorkDaysList.contains(tempWeekDay)) {
					daysList.add(String.valueOf(i+1) 
							+ "/GREY" 
							+ "/"
							+ getMonthAsString(nextMonth) 
							+ "/" 
							+ nextYear
							+ "/"
							+ getDayOfWeekAsString(tempWeekDay)
							+ "/available"
							+ "/"
							+ nextMonth);
				} else { // Otherwise all the day as unavailable for appointments with the PAA
					daysList.add(String.valueOf(i+1) 
							+ "/RED2" 
							+ "/"
							+ getMonthAsString(nextMonth) 
							+ "/" 
							+ nextYear
							+ "/"
							+ getDayOfWeekAsString(tempWeekDay)
							+ "/unavailable"
							+ "/"
							+ nextMonth);
				}
				
			}
		}// End of printTheMonth method

		private String getDayOfWeekAsString(int dayAsInt) {
			Log.d("Debug2", "getDayOfWeekAsString method.");
			String dayOfWeekString = "";
			switch (dayAsInt) {
				case 0:
					dayOfWeekString = "Sun";
					break;
				case 1:
					dayOfWeekString = "Mon";
					break;
				case 2:
					dayOfWeekString = "Tue";
					break;
				case 3:
					dayOfWeekString = "Wed";
					break;
				case 4:
					dayOfWeekString = "Thu";
					break;
				case 5:
					dayOfWeekString = "Fri";
					break;
				case 6:
					dayOfWeekString = "Sat";
					break;
			}
			return dayOfWeekString;
		}
		
		@Override
		public int getCount() {
			return daysList.size();
		}

		@Override
		public Object getItem(int position) {
			return daysList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			String[] day = daysList.get(position).split("/");
			String dayAsNumber = day[0];
			String colour = day[1];
			String themonth = day[2];
			String theyear = day[3];
			String dayAsString = day[4];
			String availability = day[5];
			String monthAsNumber = day[6];
			if (row == null) {
				
				// Inflate the View
				LayoutInflater inflater = (LayoutInflater) myActivity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				if (colour.equals("RED") || colour.equals("RED2")) {
					row = inflater.inflate(R.layout.calendar_days_gridcell_red, parent,
							false);
				} else {
					row = inflater.inflate(R.layout.calendar_days_gridcell, parent,
					false);
				
				}
			}

			// Get a reference to the Day gridcell
			Button gridcell = (Button) row
					.findViewById(R.id.calendar_days_gridcell);
			gridcell.setOnClickListener(this);


			if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
				if (eventsPerMonthMap.containsKey(dayAsNumber)) {
					num_events_per_day = (TextView) row.findViewById(R.id.days);
					Integer numEvents = (Integer) eventsPerMonthMap.get(dayAsNumber);
					num_events_per_day.setText(numEvents.toString());
				}
			}

			// Set the Day GridCell
			gridcell.setText(dayAsNumber);
			gridcell.setTag(dayAsNumber + "/" + themonth + "/" + theyear + "/" + dayAsString + "/" + availability + "/" + monthAsNumber);

			if (colour.equals("GREY")) {
				gridcell.setTextColor(Color.LTGRAY);
			}
			if (colour.equals("WHITE")) {
				gridcell.setTextColor(Color.WHITE);
			}
			if (colour.equals("RED")) {
				//Log.d("Main", "Day color equals to RED");
				gridcell.setTextColor(Color.WHITE);
			}
			if (colour.equals("RED2")) {
				//Log.d("Main", "Day color equals to RED2");
				gridcell.setTextColor(Color.GRAY);
			}
			return row;
		}//End of GetView

		@Override
		public void onClick(View v) {
			String viewTag = (String) v.getTag();
			Log.d("Debug2", "View tag = " + viewTag);
			String[] splittedTag = viewTag.split("/");
			BookingFragment.dayAsNumber = splittedTag[0];
			BookingFragment.theMonth = splittedTag[1];
			BookingFragment.theYear = splittedTag[2];
			BookingFragment.dayAsString = splittedTag[3];
			String availability = splittedTag[4];
			BookingFragment.monthAsNumber = splittedTag[5];
			String day_month_year = BookingFragment.dayAsNumber 
									+ "/" 
									+ BookingFragment.theMonth 
									+ "/" 
									+ BookingFragment.theYear;
			selectedDayMonthYearButton.setText("Selected: " + day_month_year);
			Log.d("Debug2", "The availability of " + BookingFragment.dayAsString + " is: " + availability);
			if (availability.equals("available")) {
				// Set the bookingDaySlotsButton to visible
				BookingFragment.bookingDaySlotsButton.setVisibility(0);
				if (idnumber.contains("PAA")) {
					BookingFragment.cancelDaySlot.setVisibility(0);
				}
			} else {
				// Set the button to invisible
				BookingFragment.bookingDaySlotsButton.setVisibility(4);
				if (idnumber.contains("PAA")) {
					BookingFragment.cancelDaySlot.setVisibility(0);
				}
			}
		}

	} // End of DaysAdapter class

	// Inner class for GridCellAdapterDaysOfWeek
	public static class DaysOfWeekAdapter extends BaseAdapter {
		Activity myActivity;

		public DaysOfWeekAdapter(Activity myActivity) {
			Log.d("Main", "DaysOfWeekAdapter constructor.");
			this.myActivity = myActivity;
			Log.d("Main", "DaysOfWeekAdapter constructor - obtained activity.");
		}

		@Override
		public int getCount() {
			// Sets number of elements to be displayed on the grid
			return 7;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			View myView = view;

			if (view == null) {
				// Define the view to be displayed on the grid and inflate the
				// layout

				LayoutInflater li = myActivity.getLayoutInflater();
				myView = li.inflate(R.layout.calendar_days_of_week_gridcell,
						null);
				Log.d("Main", "Layout inflated.");
				// Obtain the TextView that is laid on top of the gridcell
				TextView tv = (TextView) myView.findViewById(R.id.days_of_week);
				// Set the text of the text view based on the position
				switch (position) {
				case 0:
					tv.setText("Sun");
					break;
				case 1:
					tv.setText("Mon");
					break;
				case 2:
					tv.setText("Tue");
					break;
				case 3:
					tv.setText("Wed");
					break;
				case 4:
					tv.setText("Thu");
					break;
				case 5:
					tv.setText("Fri");
					break;
				case 6:
					tv.setText("Sat");
					break;
				}
			}
			return myView;
		}// End of getView

	}// End of GridCellAdapterDaysOfWeek

	public static class AppointmentFragment extends Fragment {
		static TextView appointmentIdTV;
		String appointmentIdTVText;
		static TextView dateTV;
		static TextView timeTV;
		static TextView paaNameTV;
		static Button cancelAppointmentButton;
		ArrayList<String> appointmentDates = new ArrayList<String>();
		static String appointmentDate;
		static String appointmentTime;
		String appointmentWithFullname;
		String appointmentWithIdNumber;
		static Firebase appointmentsRef;
		static ValueEventListener valueListener;

		public AppointmentFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_appointment,
					container, false);
			// Obtain the Views 
			appointmentIdTV = (TextView) rootView.findViewById(R.id.myappointments_appointment_id);
			dateTV = (TextView) rootView.findViewById(R.id.myappointments_date);
			timeTV = (TextView) rootView.findViewById(R.id.myappointments_time);
			paaNameTV = (TextView) rootView.findViewById(R.id.myappointments_paa_name);
			cancelAppointmentButton = (Button) rootView.findViewById(R.id.appointments_cancel_appointment_button);
			cancelAppointmentButton.setVisibility(4);
			final TableLayout tl = (TableLayout) rootView.findViewById(R.id.appointments_table_layout);
			Log.d("Debug4", "idnumber = " + idnumber);
			if (idnumber.contains("PAA")) {
				appointmentIdTV.setVisibility(4);
				dateTV.setVisibility(4);
				timeTV.setVisibility(4);
				paaNameTV.setVisibility(4);
				
				appointmentsRef = new Firebase("https://demoandroid.firebaseio.com/appointments/" + idnumber + "/");
				//messageListQuery = appointmentsRef.limit(1000);
				appointmentsRef.addValueEventListener(valueListener = new ValueEventListener() {
					@Override
					public void onCancelled(FirebaseError arg0) {}

					@Override
					public void onDataChange(DataSnapshot snap) {
						
						boolean appointmentExists = false;
						
						tl.removeAllViews();
						/*GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
						Map<String, String> userData = snap.getValue(t);
						Log.d("Debug3", Integer.toString(userData.size()));*/
						Object appointments = snap.getValue();
						Log.d("Debug3", appointments.toString());
						Map allAppointments = (Map)((Map)appointments);
						//Log.d("Debug3", "all appointments map; " + allAppointments.toString());
						//Log.d("Debug3", "all appointments set: " + allAppointments.keySet().toString());
						Set allAppointmentsSet = allAppointments.keySet();
						
						Iterator allAppointmentsIterator = allAppointmentsSet.iterator();
						for (int i = 0; i < allAppointmentsSet.size(); i++) {
							Object tempAppointment = allAppointmentsIterator.next();
							Log.d("Debug3", "Appointment " + i + " " + tempAppointment.toString());
							appointmentDates.add(tempAppointment.toString());
							
							Object timesHierarchy = allAppointments.get(tempAppointment);
							//Log.d("Debug3", "Times label = " + timesLabel.toString());
							Map timesHierarchyMap = (Map)((Map)timesHierarchy);
							//Log.d("Debug3", "Times label map = " + timesLabelMap.toString());
							Set timesHierarchySet = timesHierarchyMap.keySet();
							Iterator timesHierarchyIterator = timesHierarchySet.iterator();
							Object times = timesHierarchyIterator.next();
							
							Object timesOfDay = timesHierarchyMap.get(times);
							
							//Log.d("Debug3", "timesOfAppointments = " + timesOfAppointments.toString());
							
							
							Map timesOfDayMap = (Map)((Map)timesOfDay);
							Set timesOfDaySet = timesOfDayMap.keySet();
							Iterator timesOfDayIterator = timesOfDaySet.iterator();
							for (int j = 0; j < timesOfDaySet.size(); j++) {
								Object tempTime = timesOfDayIterator.next();
								Log.d("Debug3", "Time of appointment: " + tempTime.toString());
								
								Object appointmentTimeDetails = timesOfDayMap.get(tempTime);
								Map appointmentTimeDetailsMap = (Map)((Map)appointmentTimeDetails);
							    String availability = (String) appointmentTimeDetailsMap.get("availability");
							    String cancelled = (String) appointmentTimeDetailsMap.get("cancelled");
							    String fullname = (String) appointmentTimeDetailsMap.get("fullname");
							    String studentIdNumber = (String) appointmentTimeDetailsMap.get("idnumber");
							    Log.d("Debug4", timesOfDaySet.toString());
							    if (availability.equals("unavailable") && cancelled == null) {
							    	appointmentExists = true;
							    	Activity tempActivity = getActivity();
							    	TableRow tr = new TableRow(tempActivity);
									tr.setId(100+i);
						            tr.setLayoutParams(new LayoutParams(
						                    LayoutParams.MATCH_PARENT,
						                    LayoutParams.WRAP_CONTENT));
						            // Create a TextView for the current appointment
						            TextView labelTV = new TextView(tempActivity);
						            labelTV.setId(200+i);
						            labelTV.setText(tempAppointment.toString() + " " + tempTime.toString() + " by " + fullname + " (" + studentIdNumber + ")");
						            labelTV.setTextColor(Color.BLACK);
						            labelTV.setBackgroundResource(R.drawable.book_day_selector);
						            labelTV.setPadding(25, 0, 0, 0);
						            
						            labelTV.setGravity(Gravity.CENTER);
						            labelTV.setTag(tempAppointment.toString() + "," + tempTime.toString() + "," + fullname + "," + studentIdNumber);
						            labelTV.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											Log.d("Appointments listener", "Appointment clicked - tag: " + v.getTag());
											String[] appointmentDetails = v.getTag().toString().split(",");
											appointmentDate = appointmentDetails[0];
											appointmentTime = appointmentDetails[1];
											appointmentWithFullname = appointmentDetails[2];
											appointmentWithIdNumber = appointmentDetails[3];
											TextView selectedAppointment = (TextView) rootView.findViewById(R.id.appointments_selected_appointment);
											selectedAppointment.setText("Do you want to cancel the appointment at: " 
																		+ appointmentDate 
																		+ " - " 
																		+ appointmentTime 
																		+ " with - " 
																		+ appointmentWithFullname 
																		+ "("
																		+ appointmentWithIdNumber
																		+ ") ?");
											cancelAppointmentButton.setVisibility(0);
											
										} // End of onClick for text views
						            	
						            });
						           
						            tr.addView(labelTV);
						            // Add the TableRow to the TableLayout
						            tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
							    }
								
							}
						}
						if (!appointmentExists) {
							TextView appointmentTextView = (TextView) rootView.findViewById(R.id.myappointments_appointment_id);
							appointmentTextView.setText("No ongoing appointments.");
							appointmentTextView.setVisibility(View.VISIBLE);
							
						}
						Log.d("Debug3", "appointmentDates = " + appointmentDates);
						//messageListQuery.removeEventListener(this);
					}

					
				}); // End of MessageListQuery ValueEventListener
			} else {
				
			}
			return rootView;
		} //End on createView
		
		@Override
		public void onResume() {
			super.onResume();
			
			
			
			// Create a background thread (only for students) which will determine whether an user with existing
			// appointment still is due on it or the date has passed by or even the PAA has
			// cancelled the appointment. Then update the online database.
			if (!idnumber.contains("PAA")) {
				thread = new Thread(new Runnable() {
					public void run() {
						
						/*Log.d("AppointmentThread", "Running appointment thread.");
						Log.d("AppointmentThread", "appointmentPath = " + appointmentPath);
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
								
								Object appointment = snap.getValue();
								Log.d("AppointmentThread", "appointment object: " + appointment.toString());
								String cancelled = (String)((Map)appointment).get("cancelled");
								String cancelledBy = (String)((Map)appointment).get("cancelledBy");
								
								// If the appointment has been cancelled
								if (cancelled != null && cancelledBy != null) {
									
									//Set the current number of appointments of the student to 0
									Firebase userRef = new Firebase("https://demoandroid.firebaseio.com/user/username/" + username + "/");
									userRef.child("currentappointments/").setValue("0");
									userRef.child("currentAppointment/").setValue("none");
									
									
									Log.d("AppointmentThread", "cancelled by = " + cancelledBy);
									//TODO if cancelledBy PAA then make a notification
									if (cancelledBy.equals("PAA")) {
										
										AppointmentFragment.appointmentIdTV.setText("The following appointment:");
										AppointmentFragment.dateTV.setText(appointmentDate + " " + appointmentTime);
										AppointmentFragment.timeTV.setText("Has been deleted by the PAA");
										AppointmentFragment.cancelAppointmentButton.setVisibility(4);
										//TODO Make a notification
										displayNotification("New client has connected.", myActivity);
										
									} else if (cancelledBy.equals("expiration")) {
										
										AppointmentFragment.appointmentIdTV.setText("No current appointments...");
										AppointmentFragment.dateTV.setText("Your last appointment has expired.	");
										AppointmentFragment.timeTV.setText("");
										AppointmentFragment.paaNameTV.setText("");
										AppointmentFragment.cancelAppointmentButton.setVisibility(4);
										
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
										Log.d("Appointment", "Appointment Expired.");
										
										
										// Set the appointment as cancelled 
										Firebase appointmentRef = new Firebase("https://demoandroid.firebaseio.com/appointments/"
												  + appointmentPath);
			
										appointmentRef.child("cancelled").setValue("true");
										appointmentRef.child("cancelledBy").setValue("expiration");
										
										//remove the appointment from the database
										Log.d("Debug5", "The username is " + username);
										
										
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
						
					*/
					}}); // End of run method
				
				Firebase userRef = new Firebase("https://demoandroid.firebaseio.com/user/username/" + username);
				
				userRef.addListenerForSingleValueEvent(new ValueEventListener() {

					@Override
					public void onCancelled(FirebaseError arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onDataChange(DataSnapshot snap) {
						appointmentPath = (String) snap.child("currentAppointment").getValue();
						if (appointmentPath != null && !appointmentPath.equals("none")) {
							//thread.start();
							
							/* Start the Service */
							Log.d("AppointmentService", "BookingActivity* Starting the service");
							
							/*myActivity.startService(new Intent(FirebaseBackgroundService.class.getName()));*/
							
							Intent intent = new Intent(myActivity, AppointmentService.class);
							intent.putExtra("appointmentPath", appointmentPath);
							intent.putExtra("username", username);
							myActivity.startService(intent);
							
						}
					}
					
				});
				
			}// End of if id number doesn't contain PAA
			
			
			
			// If the user is not PAA, display the information of the appointment (if one exists) inside the Appointments Fragment
			if (!idnumber.contains("PAA")) {
				
				Firebase userRef = new Firebase("https://demoandroid.firebaseio.com/user/username/" + username + "/");
				
				userRef.addListenerForSingleValueEvent(new ValueEventListener() {

					@Override
					public void onCancelled(FirebaseError arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onDataChange(DataSnapshot snap) {
						String currentAppointment = (String) snap.child("currentAppointment/").getValue();
						if (currentAppointment == null || currentAppointment.equals("none")) {
							AppointmentFragment.appointmentIdTV.setText("No current appointments");
							AppointmentFragment.dateTV.setText("");
							AppointmentFragment.timeTV.setText("");
							AppointmentFragment.paaNameTV.setText("");
							cancelAppointmentButton.setVisibility(4);
						}  else {
							
								String[] splitAppointmentPath = appointmentPath.split("/");
								String paaId = splitAppointmentPath[0];
								final String appointmentDate = splitAppointmentPath[1];
								final String appointmentTime = splitAppointmentPath[3];
							
								appointmentIdTV.setText("Your Appointment");
								dateTV.setText("Date: " + appointmentDate);
								timeTV.setText("Time: " + appointmentTime);
								paaNameTV.setText("PAA name: " + BookingActivity.paaName);
									
								cancelAppointmentButton.setVisibility(0);
							
						}
					}
					
				});
				
			} // End of if(id number doesn't contain "PAA"
			
				
			
		} // End onStart()
		 
		
		
	}// End of AppointmentFragment

	public static class MyInfoFragment extends Fragment {

		public MyInfoFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_my_info,
					container, false);
			// Obtain the username from the intent
			Intent i = getActivity().getIntent();
			username = i.getExtras().getString("username");
			// Obtain the details of the username from the back end datastore
			Firebase userRef = new Firebase(
					"https://demoandroid.firebaseio.com/user/username/"
							+ username + "/");

			// Add the event listener to the userRef, in order to authenticate
			// the user
			userRef.addListenerForSingleValueEvent(new ValueEventListener() {

				@Override
				public void onCancelled(FirebaseError arg0) {
					Log.d("Main", "userRef Listener was cancelled.");
				}

				@Override
				public void onDataChange(DataSnapshot snap) {
					Object user = snap.getValue();
					if (user == null) {
						Log.d("Main", username + " doesn't exist.");
					} else { // The user exists
						Log.d("Main", "User " + username + " exists.");

						String fullname = (String)((Map)user).get("fullname");
						String idnumber = (String)((Map)user).get("idnumber");
						String course = (String)((Map)user).get("course");
						//TODO obtain the text from TV before onDataChange, so when data is changed online, the
						// data won't be appended on top of each other
						TextView fullnameTV = (TextView) rootView.findViewById(R.id.my_info_fullname);
						fullnameTV.setText(fullnameTV.getText() + " " + fullname);
						
						TextView idnumberTV = (TextView) rootView.findViewById(R.id.my_info_idnumber);
						idnumberTV.setText(idnumberTV.getText() + " " + idnumber);
						
						TextView courseTV = (TextView) rootView.findViewById(R.id.my_info_course);
						courseTV.setText(courseTV.getText() + " " + course);
						
						Firebase courseRef = new Firebase("https://demoandroid.firebaseio.com/courses/" + course + "/");
						
						courseRef.addValueEventListener(new ValueEventListener() {

							@Override
							public void onCancelled(FirebaseError arg0) {
								
							}

							@Override
							public void onDataChange(DataSnapshot snap1) {
								Object course = snap1.getValue();
								if (course == null) {
									Log.d("Main", course + " doesn't exist.");
								} else {
									Log.d("Main", course + " does exist.");
									
									String paaName = (String)((Map)course).get("PAAname");
									String paaAvailabilityTimes = (String)((Map)course).get("Availability");
									
									TextView paaAvailabilityTV = (TextView) rootView.findViewById(R.id.my_info_paa_availability_times);
									paaAvailabilityTV.setText(paaAvailabilityTV.getText() + " " + paaAvailabilityTimes);
									
									TextView paaTV = (TextView) rootView.findViewById(R.id.my_info_paa);
									paaTV.setText(paaTV.getText() + " " + paaName);
								}
							}
							
						});// End courseRef event listener
						
						
					}// End else (the user exists)

				} // End onDataChange

			});// End userRef event listener

			return rootView;
		}
	}// End of MyInfoFragment

	public void bookDay(View v) {
		BookingFragment.calendar.set(Integer.valueOf(BookingFragment.theYear), Integer.valueOf(BookingFragment.monthAsNumber), Integer.valueOf(BookingFragment.dayAsNumber));
		BookingFragment.calendar.add(Calendar.DAY_OF_YEAR, 1);
		Log.d("Debug3" , "Selected date = " + BookingFragment.calendar.toString());
		Calendar currentDayCalendar = Calendar.getInstance(Locale.getDefault());
		/*currentDayCalendar.set(Calendar.HOUR_OF_DAY, 0);
		currentDayCalendar.set(Calendar.MINUTE, 0);*/
		Log.d("Debug3" , "Current date = " + currentDayCalendar.toString());
		/*int currentDayOfYear = currentDayCalendar.get(Calendar.DAY_OF_YEAR);
		int currentYear = currentDayCalendar.get(Calendar.YEAR)*/;
/*		int appointmentDayOfYear = BookingFragment.calendar.get(Calendar.DAY_OF_YEAR);
		int appointmentYear = BookingFragment.calendar.get(Calendar.YEAR);*/
		if ((BookingFragment.calendar.before(currentDayCalendar))) {
			Toast.makeText(this, "You can't book past appointments.", Toast.LENGTH_LONG).show();
		} else {
			
			Intent i = new Intent(this, BookingDaySlotsActivity.class);
			i.putExtra("availabilityTimes", BookingFragment.paaTimesOfAvailability);
			i.putExtra("paaId", BookingFragment.paaId);
			i.putExtra("paaName", paaName);
			i.putExtra("dayAsNumber", BookingFragment.dayAsNumber);
			i.putExtra("theMonth", BookingFragment.theMonth);
			i.putExtra("theYear", BookingFragment.theYear);
			i.putExtra("dayAsString", BookingFragment.dayAsString);
			i.putExtra("username", username);
			i.putExtra("fullname", fullname);
			i.putExtra("idnumber", idnumber);
			i.putExtra("email", email);
			startActivity(i);
		}
	} //End of BookDay
	
	public void cancelDay(View v) {
		Log.d(TAG, "");
		Log.d(TAG, "********CANCEL DAY******");
		
		final String FIREBASE_PATH = "https://demoandroid.firebaseio.com/appointments/" + idnumber + "/" + BookingFragment.dayAsNumber + BookingFragment.theMonth + BookingFragment.theYear +  "/";
		final ProgressDialog progDialog = ProgressDialog.show(myActivity, "Canceling day...", "Please wait.");
		
		Log.d(TAG, "Obtained firebase path to the appointment = " + FIREBASE_PATH);
		
		final Firebase appointmentRef = new Firebase(FIREBASE_PATH);
		appointmentRef.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onCancelled(FirebaseError arg0) {}

			@Override
			public void onDataChange(DataSnapshot snap) {
				Object appointment = snap.getValue();
				if (appointment == null) {
					Log.d(TAG, "Obtained record from firebase is null, creating the times and setting them to unavailable."); 
					//TODO create all time slots and set them to unavailable
				} else {
					Log.d(TAG, "Obtained record from firebase = " + appointment.toString());
					
					//TODO Iterate through each time slot checking if it is booked
					Map<String, String> timeSlotsMap = (Map)((Map)appointment).get("times");
					Log.d(TAG, "");
					Log.d(TAG, "Map = " + timeSlotsMap.toString());
					Log.d(TAG, "Map size " + timeSlotsMap.size());
					
					Set timeSlotsSet = timeSlotsMap.keySet();
					Iterator timeSlotsIterator = timeSlotsSet.iterator();
					
					for (int i = 0; i < timeSlotsSet.size(); i++) {
						final Object tempTimeSlot = timeSlotsIterator.next();
						Log.d(TAG, "Investingating time slot - " + tempTimeSlot);
						
						final Object time = timeSlotsMap.get(tempTimeSlot);
						
						String availability = (String)((Map)time).get("availability");
						Log.d(TAG, "   Time slot: " + availability);
						if (availability.equals("unavailable")) {
							Log.d(TAG, "      Checking if the cancelled field exists in Firebase");
							String cancelled = (String)((Map)time).get("cancelled");
							if (cancelled == null) {
								Log.d(TAG, "         cancelled doesn't exist. Checking if appointments is expired (to avoid sending email to expired appointment)");
								Calendar currentCal = Calendar.getInstance();
								
								
								GregorianCalendar appointmentCal = new GregorianCalendar(Integer.valueOf(BookingFragment.theYear), AppointmentHelper.getMonthAsInt(BookingFragment.theMonth), Integer.valueOf(BookingFragment.dayAsNumber));
								// Set the time of the appointment
								String[] splitTime = tempTimeSlot.toString().split(":");
								appointmentCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTime[0]));
								appointmentCal.set(Calendar.MINUTE, Integer.parseInt(splitTime[1]));
						
								final String FB_APPOINTMENT_PATH = FIREBASE_PATH + "times/" + tempTimeSlot.toString();
								Log.d(TAG, "            FB_APPOINTMENT_PATH = " + FB_APPOINTMENT_PATH);
								
								if (currentCal.after(appointmentCal)) {
									Log.d(TAG, "            Appointment expired.");
									
									
									
									
									// Create a Firebase object
									Firebase appointmentRef = new Firebase(FB_APPOINTMENT_PATH);
									
									// Set the cancelled to true, cancelledBy to expiration, user 
									appointmentRef.child("cancelled").setValue("true");
									appointmentRef.child("cancelledBy").setValue("expiration");
									
									// Obtain the username and update his record to have currentAppointment = none, currentappointments = 0;
									String username = (String)((Map)time).get("username");
									final String FB_USER_PATH = "https://demoandroid.firebaseio.com/user/username/" + username + "/";
									Firebase userRef = new Firebase(FB_USER_PATH);
									userRef.child("currentAppointment").setValue("none");
									userRef.child("currentappointments").setValue("0");
									
								} else {
									Log.d(TAG, "            Appointment ongoing.");
									//TODO Cancel the appointment
									// Create a Firebase object
									Firebase appointmentRef = new Firebase(FB_APPOINTMENT_PATH);
									
									// Set the cancelled to true, cancelledBy to expiration, user 
									appointmentRef.child("cancelled").setValue("true");
									appointmentRef.child("cancelledBy").setValue("PAA");
									
									// Obtain the username and update his record to have currentAppointment = none, currentappointments = 0;
									final String username = (String)((Map)time).get("username");
									final String FB_USER_PATH = "https://demoandroid.firebaseio.com/user/username/" + username + "/";
									Firebase userRef = new Firebase(FB_USER_PATH);
									userRef.child("currentAppointment").setValue("none");
									userRef.child("currentappointments").setValue("0");
									
									//TODO Send email to the Student and PAA about the cancellation of appointment
									
									// Obtain the administrator email login and password from the external data storage
									Firebase emailRef = new Firebase("https://demoandroid.firebaseio.com/email");
									emailRef.addListenerForSingleValueEvent(new ValueEventListener() {

										@Override
										public void onCancelled(FirebaseError arg0) {
											// TODO Auto-generated method stub
											
										}

										@Override
										public void onDataChange(DataSnapshot snap) {
											Object emailObj = snap.getValue();
											final String login = (String) ((Map)emailObj).get("login");
											final String pass = (String) ((Map)emailObj).get("pass");
											
											final String FB_USER_PATH = "https://demoandroid.firebaseio.com/user/username/" + username + "/";
											Firebase userRef = new Firebase(FB_USER_PATH);
											
											userRef.addListenerForSingleValueEvent(new ValueEventListener() {

												@Override
												public void onCancelled(
														FirebaseError arg0) {}

												@Override
												public void onDataChange(
														DataSnapshot snap) {
													Object student = snap.getValue();
													Log.d(TAG, "Obtained user Object - " + student);
													String studentEmail = (String)((Map)student).get("email");
													String studentName = (String)((Map)student).get("fullname");
													String studentId = (String)((Map)student).get("idnumber");
													
													// Send an email to the student and the PAA to confirm the cancellation.
									                GmailSender gmailSender = new GmailSender(login, pass);
									                try {
									                	// Obtain the date
									                	String day = (String)((Map)time).get("day");
									                	String month = (String)((Map)time).get("month");
									                	String year = (String)((Map)time).get("year");
									                	
									                	// Send an email to the Student about the cancellation.
														gmailSender.sendMail("PAA Appointment - " + day + " " + month + " " + year + " " + tempTimeSlot.toString(),   
																					"Your appointment has been CANCELLED with " + paaName + ", at the following date " + day + " " + month + " " + year + " " + " " + tempTimeSlot.toString() + " \n London Metropolitan University UG Office \n 166-220 Holloway Rd, London N7 8DB \n For further information please call 020 7423 000",   
																					login,   
																					studentEmail);
														// Send an email to the PAA to confirm the cancellation.
														gmailSender.sendMail("Cancelled appointment - " + day + " " + month + " " + year + " " + " " + tempTimeSlot.toString(),
						                						   "The appointment with " + studentName + " (" + studentId + ") has been successfully cancelled. \n History of appointment - Date: " + day + " " + month + " " + year + " " + ". Time - " + tempTimeSlot.toString(),
						                						   login,
						                						   email);
													} catch (Exception e) {
														Log.d(TAG, "gmailSender.sendMail raised Exception - " + e.getMessage());
														e.printStackTrace();
													}  
									                
													
												}
												
											});
											
											
											
											
											
											
							                
										}
									});
									
									
								}
							} 
						} else if (availability.equals("Available")) {
							// Cancel the available appointments
							
							final String FB_APPOINTMENT_PATH = FIREBASE_PATH + "times/" + tempTimeSlot.toString();
							
							// Create a Firebase object
							Firebase appointmentRef = new Firebase(FB_APPOINTMENT_PATH);
							
							// Set the cancelled to true, cancelledBy to expiration, user 
							appointmentRef.child("cancelled").setValue("true");
							appointmentRef.child("cancelledBy").setValue("PAA");
							appointmentRef.child("availability").setValue("unavailable");
							
						}
					}
					
					
					
					
					/*Set allAppointmentsSet = allAppointments.keySet();
					Iterator allAppointmentsIterator = allAppointmentsSet.iterator();
					for (int i = 0; i < allAppointmentsSet.size(); i++) {
						Object tempAppointment = allAppointmentsIterator.next();
						Log.d("Debug3", "Appointment " + i + " " + tempAppointment.toString());
						appointmentDates.add(tempAppointment.toString());
						
						Object timesHierarchy = allAppointments.get(tempAppointment);
						//Log.d("Debug3", "Times label = " + timesLabel.toString());
						Map timesHierarchyMap = (Map)((Map)timesHierarchy);
						//Log.d("Debug3", "Times label map = " + timesLabelMap.toString());
						Set timesHierarchySet = timesHierarchyMap.keySet();
						Iterator timesHierarchyIterator = timesHierarchySet.iterator();
						Object times = timesHierarchyIterator.next();
						
						Object timesOfDay = timesHierarchyMap.get(times);
					*/
					
					
					
					
					
					
					
					/*for (int i = 0; i < map.size(); i++) {
						map.
					}*/
				}
				
				progDialog.dismiss();
				Log.d(TAG, "********END OF CANCEL DAY******");
				Log.d(TAG, "");
			}
			
		});

		
	} // End of cancelDay

	public void cancelAppointment(View v) {
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Add the buttons
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				if (idnumber.contains("PAA")) {
					final Firebase appointmentRef = new Firebase("https://demoandroid.firebaseio.com/appointments/"
														  + BookingFragment.paaId
														  + "/"
														  + AppointmentFragment.appointmentDate
														  + "/times/"
														  + AppointmentFragment.appointmentTime);
					
					
					
					appointmentRef.child("/cancelled").setValue("true");
					if (BookingFragment.paaId.equals(idnumber)) {
						appointmentRef.child("/cancelledBy").setValue("PAA");
					} else {
						appointmentRef.child("/cancelledBy").setValue("student");
					}
					
					// Obtain the administrator email login and password from the external data storage
					Firebase emailRef = new Firebase("https://demoandroid.firebaseio.com/email");
					emailRef.addListenerForSingleValueEvent(new ValueEventListener() {

						@Override
						public void onCancelled(FirebaseError arg0) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onDataChange(DataSnapshot snap) {
							Object emailObj = snap.getValue();
							final String login = (String) ((Map)emailObj).get("login");
							final String pass = (String) ((Map)emailObj).get("pass");
							
							
								appointmentRef.addListenerForSingleValueEvent(new ValueEventListener() {

									@Override
									public void onCancelled(FirebaseError arg0) {}

									@Override
									public void onDataChange(DataSnapshot snap) {
										Object appointmentObj = snap.getValue();
										String studentUsername = (String) ((Map)appointmentObj).get("username");

										  Firebase userRef = new Firebase("https://demoandroid.firebaseio.com/user/username/" + studentUsername);
							                userRef.addListenerForSingleValueEvent(new ValueEventListener() {

												@Override
												public void onCancelled(FirebaseError arg0) {}

												@Override
												public void onDataChange(DataSnapshot snap) {
													Object studentRef = snap.getValue();
													String studentEmail = (String)((Map)studentRef).get("email");
													String studentName = (String)((Map)studentRef).get("fullname");
													String studentId = (String)((Map)studentRef).get("idnumber");
													Log.d("paabooking", "student email - " + studentEmail);
													try {   
														// Send an email to the student and the PAA to confirm the cancellation.
										                GmailSender gmailSender = new GmailSender(login, pass);
										                gmailSender.sendMail("PAA Appointment - " + AppointmentFragment.appointmentDate + " " + AppointmentFragment.appointmentTime,   
										                							"Your appointment has been CANCELLED with " + paaName + ", at the following date " + AppointmentFragment.appointmentDate + " " + AppointmentFragment.appointmentTime + " \n London Metropolitan University UG Office \n 166-220 Holloway Rd, London N7 8DB \n For further information please call 020 7423 000",   
										                							login,   
										                							studentEmail);  
										                // Send an email to the PAA to confirm the cancellation.
										                gmailSender.sendMail("Cancelled appointment - " +AppointmentFragment.appointmentDate + " " + AppointmentFragment.appointmentTime,
										                						   "The appointment with " + studentName + " (" + studentId + ") has been successfully cancelled. \n History of appointment - Date: " + AppointmentFragment.appointmentDate + ". Time - " + AppointmentFragment.appointmentTime,
										                						   login,
										                						   email);
										            } catch (Exception e) {   
										            	Log.d("Error", e.getStackTrace().toString());
										                Log.d("GmailSender", "LoginActivity - Exception: " +e.getMessage());   
										            } 
													Button cancelAppointmentButton = (Button) findViewById(R.id.appointments_cancel_appointment_button);
													TextView selectedAppointment = (TextView) findViewById(R.id.appointments_selected_appointment);
													cancelAppointmentButton.setVisibility(View.INVISIBLE);
													selectedAppointment.setVisibility(View.INVISIBLE);
													Toast.makeText(getBaseContext(), "Appointment cancelled.", Toast.LENGTH_LONG).show();
												}
							                	
							                });
										
									}
									
								});
							
				              
				                
				                
							
						} 
						
					});
					
					
				} else { // If the user is the student
					
					
						final Firebase appRef = new Firebase("https://demoandroid.firebaseio.com/appointments/" + appointmentPath + "/");
						
						/*appRef.child("cancelled/").setValue("true");
						appRef.child("cancelledBy/").setValue("student");*/
						appRef.child("availability").setValue("Available");
						
						// Hide the cancel appointment button
						AppointmentFragment.cancelAppointmentButton.setVisibility(4);
						
						AppointmentFragment.appointmentIdTV.setText("No current appointments");
						AppointmentFragment.dateTV.setText("");
						AppointmentFragment.timeTV.setText("");
						AppointmentFragment.paaNameTV.setText("");
						
						// Send an email to the student and PAA
						
							
							
								Firebase emailRef = new Firebase("https://demoandroid.firebaseio.com/email");
								emailRef.addListenerForSingleValueEvent(new ValueEventListener() {

									@Override
									public void onCancelled(FirebaseError arg0) {}

									@Override
									public void onDataChange(DataSnapshot snap) {
										// TODO Auto-generated method stub
										Object emailObj = snap.getValue();
										final String login = (String) ((Map)emailObj).get("login");
										final String pass = (String) ((Map)emailObj).get("pass");
										
										// Send the email to the PAA
										 // Send an email to the PAA to confirm the cancellation.
										GmailSender gmailSender = new GmailSender(login, pass);
										
										String[] splitAppointmentPath = appointmentPath.split("/");
										
										
										try {
											gmailSender.sendMail("Cancelled appointment - " + splitAppointmentPath[1] + " " + splitAppointmentPath[3],
																	   "The appointment has been cancelled by " + fullname + " (" + idnumber + "). \n History of appointment - Date: " + splitAppointmentPath[1] + ". Time - " + splitAppointmentPath[3],
																	   login,
																	   paaMail);
											gmailSender.sendMail("Cancelled appointment - " + splitAppointmentPath[1] + " " + splitAppointmentPath[3],
													   "You have cancelled the appointment with " + paaName + " (" + paaId + "). \n History of appointment - Date: " + splitAppointmentPath[1] + ". Time - " + splitAppointmentPath[3],
													   login,
													   email);
										} catch (Exception e) {
											Log.d("GmailSender", "Canceling of appointment by student Exception - " + e.getStackTrace());
										}
										


									}
									
								});
						
						//Set the current number of appointments of the student to 0
						Firebase userRef = new Firebase("https://demoandroid.firebaseio.com/user/username/" + username + "/currentappointments/");
						userRef.setValue("0");
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
		
		
		
	} //End of cancelAppointment
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if ((keyCode == KeyEvent.KEYCODE_BACK)) {
		Log.d("Debug4", "Back button pressed!");
		//AppointmentFragment.appointmentsRef.removeEventListener((ValueEventListener) AppointmentFragment.appointmentsRef);
		try {
			AppointmentFragment.appointmentsRef.removeEventListener(AppointmentFragment.valueListener);
		} catch (NullPointerException n) {
			Log.d("Debug4", "NullPointerException while removing event listener");
		}
	}
	return super.onKeyDown(keyCode, event);
	} // End of onKeyDown method
	/*
	private static void displayNotification(String string, Context c) {
		//Pending intent to launch the activity for the user notification
		Intent i = new Intent(myActivity, LoginActivity.class);
		int notificationID = 1;
		i.putExtra("notificationID", notificationID);
		
		PendingIntent pIntent = PendingIntent.getActivity(myActivity, 0, i, 0);
		
		NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//Notification notification = new Notification(R.drawable.gg, message, System.currentTimeMillis());
		Notification n  = new Notification.Builder(myActivity)
        .setContentTitle("PAA cancelled the appointment")
        .setContentText("Cancelation notification.")
        .setSmallIcon(R.drawable.calendar_bar)
        .setContentIntent(pIntent)
        .setAutoCancel(true)
        .setTicker("Appointment Cancelled.")
        .addAction(R.drawable.calendar_bar, "And more", pIntent).build();
		//100ms delay, vibration for 250ms, 100ms delay and 500ms vibration
		n.vibrate = new long[] { 100, 250, 100, 500};
		nm.notify(notificationID, n);
		
	}// End of displayNotification
	*/
}