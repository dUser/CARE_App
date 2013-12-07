package com.example.careapp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.Content;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * 
 * Calendar app on device only syncs last 30 days
 * 
 */

/**
 * 
 * Displays events pulled from the local CARE program google calendar in a listview
 * Events on the same day are grouped under the same date heading.
 *
 */
public class EventsActivity extends Activity {
	ArrayList<HashMap<String, String>> events = new ArrayList<HashMap<String,String>>();
	EventAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);


		//WebView webView = (WebView) findViewById(R.id.eventsWebView);
		//webView.getSettings().setJavaScriptEnabled(true);
		//webView.loadUrl("file:///android_asset/events.html"); 

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			String errorStr = "This feature requires a minimum Android version of 14. You are currently running " +
					Build.VERSION.SDK_INT + ". Many decives can have their version of Android upgraded.";
			Toast.makeText(this, errorStr, Toast.LENGTH_LONG).show();
		} else {
			//launch calendar
			//startCalendar();
			//TextView t = (TextView) findViewById(R.id.eventTextView);
			//t.setText(getCalendars());
			
			if (hasCareCalendar()) {
				
				startCalendar();
				
				ListView listView = (ListView) findViewById(R.id.eventListView);				
				adapter = new EventAdapter(this, events);	

				listView.setAdapter(adapter);
				this.registerForContextMenu(listView);
				listView.setOnItemClickListener(new OnItemClickListener() {					
	
					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
						String description = adapter.getDescription(pos);
				        AlertDialog.Builder builder = new AlertDialog.Builder(EventsActivity.this);
				        builder.setTitle("Description");
						if (description != null && description.length() != 0) {
					        builder.setMessage(description);
						} else {
							builder.setMessage("No description available.");
						}
			            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                       // exit
			                   }
			            });
						builder.create().show();
					}
				});				
			
			} else {
				//you need a care calendar
				String errorStr = "Please subscribe to the Stockton CARE Program Google calendar. Instructions on how " +
						"to do this can be found in the top menu under Help.";
				Toast.makeText(this,errorStr , Toast.LENGTH_LONG).show();
				//TODO:other way of getting info if user doesn't follow Stockton CARE calendar
				//startCalendar_2();
			}
		}

		// Show the Up button in the action bar.
		setupActionBar();
	}
	

	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void startCalendar() {

		// Projection array. Creating indices for this array instead of doing
		// dynamic lookups improves performance.
		final String[] CAL_PROJECTION = new String[] {
				Calendars._ID,                           // 0
		};

		// The indices for the projection array above.
		final int PROJECTION_ID_INDEX = 0;



		Cursor cur = null;
		ContentResolver cr = getContentResolver();
		Uri uri = Calendars.CONTENT_URI;  
		
		
		String selection = "((" + Calendars.CALENDAR_DISPLAY_NAME + " = ?) AND (" + Calendars.OWNER_ACCOUNT + " = ?))";
		//String selection = "(" + Calendars.NAME + " = ?)";
		
		String[] selectionArgs = new String[] {"CARE Stockton", "care@stockton.edu"}; 
		//String[] selectionArgs = new String[] {"Test Calendar"}; 
		
		
		//Submit the query and get a Cursor object back. 
		cur = cr.query(uri, CAL_PROJECTION, selection, selectionArgs, null);

		// Use the cursor to step through the returned records
		long calID = 0;
		while (cur.moveToNext()) {

			calID = cur.getLong(PROJECTION_ID_INDEX);
		}

		if (calID != 0) {

			final String[] EVENT_PROJECTION = new String[] {
			    Events.TITLE,          // 0
			    Events.DESCRIPTION,    // 1
			    Events.DTSTART,        // 2
			    Events.DTEND,           //3
			    Events.ALL_DAY,         //4
			    Events.EVENT_LOCATION   //5
			};
			  
			// The indices for the projection array above.
			final int PROJECTION_TITLE_INDEX = 0;
			final int PROJECTION_DESCRIPTION_INDEX = 1;
			final int PROJECTION_DTSTART_INDEX = 2;
			final int PROJECTION_DTEND_INDEX = 3;
			final int PROJECTION_ALLDAY_INDEX = 4;
			final int PROJECTION_EVENT_LOCATION_INDEX = 5;
			
			// Run query
			Cursor eventCur = null;
			ContentResolver eventCr = getContentResolver();
			Uri eventUri = Events.CONTENT_URI; 

			String eventSelection = "((" + Events.CALENDAR_ID + " = ?))";
			String[] eventSelectionArgs = new String[] {String.valueOf(calID)}; 
			//Submit the query and get a Cursor object back. 
			eventCur = eventCr.query(eventUri, EVENT_PROJECTION, eventSelection, eventSelectionArgs, null);

			// Use the cursor to step through the returned records
			while (eventCur.moveToNext()) {
			    String title = null;
			    String description = null;
			    String dtstart = null;
			    String dtend = null;
			    String allday = null;
			    String event_location = null;
			    
			      
			    // Get the field values
			    title = eventCur.getString(PROJECTION_TITLE_INDEX);
			    description = eventCur.getString(PROJECTION_DESCRIPTION_INDEX);
			    dtstart = eventCur.getString(PROJECTION_DTSTART_INDEX);
			    dtend = eventCur.getString(PROJECTION_DTEND_INDEX);
			    allday = eventCur.getString(PROJECTION_ALLDAY_INDEX);
			    event_location = eventCur.getString(PROJECTION_EVENT_LOCATION_INDEX); 
			    
			    // Do something with the values...
			    HashMap<String, String> event = new HashMap<String, String>();
			    event.put("title", title);
			    event.put("description", description);
			    event.put("dtstart", dtstart);
			    event.put("dtend", dtend);
			    event.put("allday", allday);
			    event.put("event_location", event_location);
			    events.add(event);
			}

		} else {
			//you need a care calendar
			String errorStr = "Couldn't load access the CARE program Google calendar.";
			Toast.makeText(this, errorStr, Toast.LENGTH_LONG).show();
		}




	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private boolean hasCareCalendar() {

		// Projection array. Creating indices for this array instead of doing
		// dynamic lookups improves performance.
		final String[] EVENT_PROJECTION = new String[] {
				Calendars.CALENDAR_DISPLAY_NAME,         // 0
				Calendars.OWNER_ACCOUNT                  // 1
		};


		// Run query
		Cursor cur = null;
		ContentResolver cr = getContentResolver();
		Uri uri = Calendars.CONTENT_URI;   
		String selection = "((" + Calendars.CALENDAR_DISPLAY_NAME + " = ?) AND (" 
				+ Calendars.OWNER_ACCOUNT + " = ?))";
		String[] selectionArgs = new String[] {"CARE Stockton", "care@stockton.edu"}; 
		//Submit the query and get a Cursor object back. 
		cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

		// Use the cursor to step through the returned records
		if (cur.getCount() == 0) {
			return false;
		} else {
			return true;
		}

	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void startCalendar_2() {
		try {

			//TODO:move networking off main thread
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);

			// Create a CalenderService and authenticate
			CalendarService myService = new CalendarService("exampleCo-exampleApp-1");
			//myService.setUserCredentials("jo@gmail.com", "mypassword");

			// Send the request and print the response
			URL feedUrl = new URL("https://www.google.com/calendar/feeds/care%40stockton.edu/public/basic");

			CalendarEventFeed resultFeed = myService.getFeed(feedUrl, CalendarEventFeed.class);
			String s = "";
			s += "Your calendars:\n\n";

			for (int i = 0; i<resultFeed.getEntries().size(); i++) {
				CalendarEventEntry entry = resultFeed.getEntries().get(i);
				s += "\t" + entry.getTitle().getPlainText() + "\n";

				Content content = entry.getContent();
				if (content instanceof TextContent) {
					TextContent tc = (TextContent) content;

					s += "content = { " + tc.getContent().getPlainText() + " }\n";
				}


			}

			TextView tv = (TextView) findViewById(R.id.eventTextView);
			tv.setText(s);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
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
		getMenuInflater().inflate(R.menu.events, menu);
		return true;
	}
	
	/**
	 * Is a switch to pull which option item is selected and then do the appropriate action
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		
		case R.id.action_help:
			displayHelp();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displayHelp() {
		String subscribe = "To add the CARE program Google Calendar to your device " + 
				"go to calendar.google.com and under Other calendars " +
				"in 'Add a friend's calendar' type care@stockton.edu and press enter." +
				"\n\nMake sure the calendar titled Care Stockton is added on both the webpage" +
				" and under your Android calendar.";
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Help");
		builder.setMessage(subscribe);
		builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub	
			}
		});
		builder.create().show();
			
	}
		
	/**
	 * Returns user calendar
	 */
	@TargetApi(14)
	private String getCalendars() {
		// Projection array. Creating indices for this array instead of doing
		// dynamic lookups improves performance.
		final String[] EVENT_PROJECTION = new String[] {
		    Calendars._ID,                           // 0
		    Calendars.ACCOUNT_NAME,                  // 1
		    Calendars.CALENDAR_DISPLAY_NAME,         // 2
		    Calendars.OWNER_ACCOUNT                  // 3
		};
		  
		// The indices for the projection array above.
		final int PROJECTION_ID_INDEX = 0;
		final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
		final int PROJECTION_DISPLAY_NAME_INDEX = 2;
		final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
		
		// Run query
		Cursor cur = null;
		ContentResolver cr = getContentResolver();
		Uri uri = Calendars.CONTENT_URI;   
		
		
		
		String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
		                        + Calendars.ACCOUNT_TYPE + " = ?) AND ("
		                        + Calendars.OWNER_ACCOUNT + " = ?))";
		String[] selectionArgs = new String[] {"sampleuser@gmail.com", "com.google",
		        "sampleuser@gmail.com"}; 
		
		
		
		// Submit the query and get a Cursor object back. 
		cur = cr.query(uri, EVENT_PROJECTION, null, null, null);
		// Use the cursor to step through the returned records
		String s = "";
		while (cur.moveToNext()) {
		    long calID = 0;
		    String displayName = null;
		    String accountName = null;
		    String ownerName = null;
		      
		    // Get the field values
		    calID = cur.getLong(PROJECTION_ID_INDEX);
		    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
		    accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
		    ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
		              
		    // Do something with the values...
		    s += calID + "\t" + displayName + "\t" + accountName + "\t" + ownerName + "\n";
		  
		}
		return s;
	}

}
