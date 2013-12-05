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
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			onBackPressed();
		}
	}
	
	

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calendar, menu);
		
		return true;
	}

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
