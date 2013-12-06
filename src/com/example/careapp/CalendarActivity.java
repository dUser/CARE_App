package com.example.careapp;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


/**
 * Loads the Android devices Google Calendar in its own activity
 */
public class CalendarActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			startCalendar();
		} else {
			Toast.makeText(this, "To use this feature you need a minimum Android version of 14, " +
					"you are currently running version " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Starts the Android Calendar in the activity
	 */
	@TargetApi(14)
	public void startCalendar() {
		Calendar cal = new GregorianCalendar(); 
		cal.setTime(new Date()); 
		long time = cal.getTime().getTime(); 
		Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon(); 
		builder.appendPath("time"); 
		builder.appendPath(Long.toString(time)); 
		Intent intent = new Intent(Intent.ACTION_VIEW, builder.build()); 
		startActivity(intent);
	}
	
	/**
	 * When back is pressed window closes and goes back to the main activity
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			onBackPressed();
		}
	}
	
	
	/**
	 * Checks to see if the Android version is Honeycomb or above before setting up 
	 * action bar.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	/**
	 * Creates and populates the option menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calendar, menu);
		
		return true;
	}
	
	/**
	 * Is a switch to pull which option item is selected and then do the appropriate action
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
