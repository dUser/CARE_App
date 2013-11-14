package com.example.careapp;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.os.Build;
import android.provider.CalendarContract;
import com.google.api.client.http.HttpTransport;  //Needed for query commented out block
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

public class CalendarActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		
		long startMillis = 0;
		
		Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
		builder.appendPath("time");
		ContentUris.appendId(builder, startMillis);
		Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
		startActivity(intent);
		
		//Query for the CARE's Calendar. Need the actual info and sign it or it crashes the app. Empty calendar now for show.
		/*private static final String DEBUG_TAG = "CalendarActivity";
		public static final String[] INSTANCE_PROJECTION = new String[] {
		    Instances.EVENT_ID,      // 0
		    Instances.BEGIN,         // 1
		    Instances.TITLE          // 2
		  };
		  
		private static final int PROJECTION_ID_INDEX = 0;
		private static final int PROJECTION_BEGIN_INDEX = 1;
		private static final int PROJECTION_TITLE_INDEX = 2;


		Calendar beginTime = Calendar.getInstance();
		beginTime.set(2011, 9, 23, 8, 0);
		long startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.set(2011, 10, 24, 8, 0);
		long endMillis = endTime.getTimeInMillis();
		  
		Cursor cur = null;
		ContentResolver cr = getContentResolver();

		String selection = Instances.EVENT_ID + " = ?";
		String[] selectionArgs = new String[] {"207"};

		Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(builder, startMillis);
		ContentUris.appendId(builder, endMillis);

		cur =  cr.query(builder.build(), 
		    INSTANCE_PROJECTION, 
		    selection, 
		    selectionArgs, 
		    null);
		   
		while (cur.moveToNext()) {
		    String title = null;
		    long eventID = 0;
		    long beginVal = 0;    
		    
		    eventID = cur.getLong(PROJECTION_ID_INDEX);
		    beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
		    title = cur.getString(PROJECTION_TITLE_INDEX);
		              
		    Log.i(DEBUG_TAG, "Event:  " + title); 
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(beginVal);  
		    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		    Log.i(DEBUG_TAG, "Date: " + formatter.format(calendar.getTime()));    
		    }
		 }*/
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
