package com.example.careapp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.careapp.TodoDialog.TodoDialogListener;

public class TodoActivity extends FragmentActivity implements TodoDialogListener {

	ArrayList<HashMap<String, String>> todoMap; //to store hashmap ArrayList from parser
	TodoAdapter adapter;
	TodoDialog todoDialog;
	View topSettingsView;
	
	boolean[] savedSettings;
	boolean[] workingSettings;
	String savedCalName;
	String workingCalName;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);
		boolean creationSuccessful = true;

		// create blank file if it doesn't exist
		boolean todoExists = true;
		try {
			FileInputStream todoIn = this.openFileInput("todo.txt");
		} catch (FileNotFoundException e) {
			todoExists = false;
		}
		if (!todoExists) {
			try {
				writeTo("todo.txt", MODE_PRIVATE, "");	
			} catch (IOException e) {
				creationSuccessful = false;
				Toast.makeText(this, "Couldn't create new todo file: " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		boolean readSuccessful = true;
		if (creationSuccessful) {
			//read in todo file
			String todoListStr = "";
			try {
				todoListStr = readIn("todo.txt");			
			} catch (IOException e) {
				readSuccessful = false;
				Toast.makeText(this, "Couldn't open todo file:  " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
			if (readSuccessful) {

				TodoParser dp = new TodoParser(todoListStr);
				todoMap = dp.getTodoList();
				ListView listView = (ListView) findViewById(R.id.todo_listView);				
				adapter = new TodoAdapter(this, todoMap);	

				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
						// on click listeners block on text views in listitems keep this function
						// from receiving clicks
					}

				});
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					initSettings();
				}
			} // end if successful read in of data file
		} // end if creation of file is successful
		
		
		
		
		
		// Show the Up button in the action bar.
		setupActionBar();

	} //end onCreate

	private void initSettings() {
		final int NUM_RADIO_BUTTONS = 4;
		boolean creationSuccessful = true;

		// create file if it doesn't exist
		boolean settingsExists = true;
		try {
			FileInputStream todoIn = this.openFileInput("todo_settings.txt");
		} catch (FileNotFoundException e) {
			settingsExists = false;
		}
		if (!settingsExists) {
			
			try {
				
				String event_settings = "";
				for (int i = 0; i < NUM_RADIO_BUTTONS; i++) {
					event_settings += i + "=false" + ",\n"; 
				}
				event_settings += "calendar=__blank__";				
				
				writeTo("todo_settings.txt", MODE_PRIVATE, event_settings);	
			} catch (IOException e) {
				creationSuccessful = false;
				Toast.makeText(this, "Couldn't create new settings file: " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		boolean readSuccessful = true;
		if (creationSuccessful) {
			//read in todo file
			String todoSettingsStr = "";
			try {
				todoSettingsStr = readIn("todo_settings.txt");			
			} catch (IOException e) {
				readSuccessful = false;
				Toast.makeText(this, "Couldn't open settings file:  " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
			if (readSuccessful) {

				TodoSettingsParser dp = new TodoSettingsParser(todoSettingsStr);
				ArrayList<HashMap<String, String>> todoSettings = dp.getSettings();
				savedSettings = new boolean[todoSettings.size()];
				for (int i = 0; i < NUM_RADIO_BUTTONS; i++) {
					savedSettings[i] = Boolean.parseBoolean(todoSettings.get(i).get(String.valueOf(i)));
				}
				
				savedCalName = todoSettings.get(todoSettings.size() - 1).get("calendar");
				
				workingCalName = "__blank__";
				workingSettings = new boolean[savedSettings.length];
			}
		}
		
	}

	public void onSubmitChanges(View view) {
		if (adapter != null) {
			boolean writeSuccessful = true;
			int[] deletedPos = adapter.getChecked();
			ArrayList<HashMap<String, String>> tempList = new ArrayList<HashMap<String, String>>();
			for (int i = 0; i < todoMap.size(); i++) {
				tempList.add(todoMap.get(i));
			}
			//insert nulls into removed positions, so 
			//deletedPos refers to the same entry.
			for (int i = 0; i < deletedPos.length; i++) {
				tempList.set(deletedPos[i], null);
			}
			//remove nulls
			ArrayList<HashMap<String, String>> finalList = new ArrayList<HashMap<String, String>>();
			for (int i = 0; i < tempList.size(); i++) {
				if (tempList.get(i) != null) {
					finalList.add(tempList.get(i));
				}
			}

			//overwrite todo.txt with new todo items and reload
			try {
				//assume todo.txt exists because to get to this point onCreate()
				//must have been called				
				writeTo("todo.txt", MODE_PRIVATE, generateTodoString(finalList));

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && !savedSettings[0]) {
					for (int i = 0; i < deletedPos.length; i++) {
						deleteRow(Long.parseLong(todoMap.get(deletedPos[i]).get("id")),
								  todoMap.get(deletedPos[i]).get("name"),
								  todoMap.get(deletedPos[i]).get("date"));
					}
				}

				
			} catch (IOException e) {
				writeSuccessful = false;
				Toast.makeText(this, "Error saving changes.", Toast.LENGTH_LONG).show();
			}	

			//restart activity
			if (writeSuccessful) {
				finish();
				startActivity(getIntent());
			}
		} // end if adapter != null
	}	

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void deleteRow(long eventID, String title, String date) {
		if (eventID != -1) {
			
			if (verifiyEventModificationRow(eventID, title, date)) {
				Uri deleteUri = null;
				deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
				int rows = getContentResolver().delete(deleteUri, null, null);
			}

		}

	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public boolean verifiyEventModificationRow(long eventID, String title, String date) {
		boolean eventIdMatches = false;
		final String[] EVENT_PROJECTION = new String[] {
				Events.TITLE,          // 0
				Events.DTSTART,        // 1

		};

		// The indices for the projection array above.
		final int PROJECTION_TITLE_INDEX = 0;
		final int PROJECTION_DTSTART_INDEX = 1;

		// Run query
		Cursor eventCur = null;
		ContentResolver eventCr = getContentResolver();
		Uri eventUri = Events.CONTENT_URI; 

		String eventSelection = "((" + Events._ID + " = ?))";
		String[] eventSelectionArgs = new String[] {String.valueOf(eventID)}; 
		//Submit the query and get a Cursor object back. 
		eventCur = eventCr.query(eventUri, EVENT_PROJECTION, eventSelection, eventSelectionArgs, null);

		// Use the cursor to step through the returned records
		while (eventCur.moveToNext()) {
			String queryTitle = null;
			String dtstart = null;

			queryTitle = eventCur.getString(PROJECTION_TITLE_INDEX);
			dtstart = eventCur.getString(PROJECTION_DTSTART_INDEX);

			Calendar cal = Calendar.getInstance();
			if (!date.contains(":")) {
				 cal.setTimeZone(TimeZone.getTimeZone(Time.TIMEZONE_UTC));
			}
			cal.setTime(new Date(Long.parseLong(dtstart)));
			String dateStr = (cal.get(Calendar.MONTH) + 1) + "/" +
					cal.get(Calendar.DATE)        + "/" +
					cal.get(Calendar.YEAR);

			if (date.contains(":")) {
				String[] datePortion = date.split(" ");
				date = datePortion[0];
			}
			if (title.equals(queryTitle) && date.equals(dateStr)) {
				eventIdMatches = true;
			}	
		}

		return eventIdMatches;
	}
 	public void onAdd(View view) {	

		todoDialog = new TodoDialog();
		todoDialog.show(getSupportFragmentManager(), "todoDialog");		
	}	
	
	private String readIn(String internalStorageFileName) throws IOException {
		String fileString = "";
		StringBuffer sb = new StringBuffer();
		FileInputStream fis = this.openFileInput(internalStorageFileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null; 
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");			
		}
		fileString = sb.toString(); 
		br.close();	
		return fileString;		 
	}
	private void writeTo(String internalStorageFileName, int writeMode, String content) throws IOException {
		FileOutputStream fos = this.getApplicationContext().openFileOutput(internalStorageFileName, writeMode);
		DataOutputStream dos = new DataOutputStream(fos);
		dos.write(content.getBytes()); 		
		dos.close(); 	
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

	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo, menu);
		return true;
	}

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
		case R.id.action_settings:
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				displaySettingsUnavailable();
			} else {
				displaySettings();
			}
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	@TargetApi(14)
	private void displaySettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Settings");
        LayoutInflater inflater = getLayoutInflater();
        topSettingsView = inflater.inflate(R.layout.todo_settings, null);
        if (savedCalName != null) {
        	builder.setView(topSettingsView);
        	final RadioButton radio_dont_synch_to_cal = (RadioButton) topSettingsView.findViewById(R.id.radio_dont_synch_to_cal);
        	final RadioButton radio_synch_to_cal = (RadioButton) topSettingsView.findViewById(R.id.radio_synch_to_cal);
        	final RadioButton radio_choose_cal_every = (RadioButton) topSettingsView.findViewById(R.id.radio_choose_cal_every);
        	final RadioButton radio_set_synch_cal = (RadioButton) topSettingsView.findViewById(R.id.radio_set_synch_cal);
        	final EditText calendarName = (EditText) topSettingsView.findViewById(R.id.synched_todo_calendar);
        	radio_dont_synch_to_cal.setChecked(savedSettings[0]);
        	radio_synch_to_cal.setChecked(savedSettings[1]);
        	radio_choose_cal_every.setChecked(savedSettings[2]);
        	radio_set_synch_cal.setChecked(savedSettings[3]);
        	
        	onSynchRadioButtonClicked(radio_dont_synch_to_cal);      	
        	onSynchRadioButtonClicked(radio_synch_to_cal);
        	onCalRadioButtonClicked(radio_choose_cal_every);
        	onCalRadioButtonClicked(radio_set_synch_cal);

        	if (!savedCalName.equals("__blank__")) {
        		calendarName.setHint("");
        		calendarName.setText(savedCalName);
        	}
        	
        	builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			if (!(calendarName.getText().toString().length() > 0) && radio_set_synch_cal.isChecked() ) {
        				Toast.makeText(TodoActivity.this, "You need to specify a calendar, not saving.", Toast.LENGTH_LONG).show();        				
        			} else {
           				workingSettings[0] = radio_dont_synch_to_cal.isChecked();
        				workingSettings[1] = radio_synch_to_cal.isChecked();
        				workingSettings[2] = radio_choose_cal_every.isChecked();
        				workingSettings[3] = radio_set_synch_cal.isChecked();
        				workingCalName = calendarName.getText().toString();
        				if ("".equals(workingCalName)) {
        					workingCalName = "__blank__";
        				}
        				saveSettingChanges();
        			}
        		}
        	});
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // exit
                }
            });
        	
        	
        } else {
        	//error
        	TextView tv = new TextView(this);
        	tv.setText("     No settings found");
        	builder.setView(tv);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // exit
                }
            });
        }
        
        

		builder.create().show();
		
	}
	private void saveSettingChanges() {

		boolean writeSuccessful = true;
		final int NUM_RADIO_BUTTONS = 4;
		try {
			String event_settings = "";
			for (int i = 0; i < NUM_RADIO_BUTTONS; i++) {
				event_settings += i + "=" + workingSettings[i] + ",\n"; 
			}
			event_settings += "calendar=" + workingCalName;				
			writeTo("todo_settings.txt", MODE_PRIVATE, event_settings);
		} catch (IOException e) {
			writeSuccessful = false;
			Toast.makeText(this, "Error saving changes.", Toast.LENGTH_LONG).show();
		}	

		//restart activity
		if (writeSuccessful) {
			finish();
			startActivity(getIntent());
		}
	}
	public void onSynchRadioButtonClicked(View view) {
		
    	LinearLayout synch_calendar_group = (LinearLayout) topSettingsView.findViewById(R.id.synch_calendar_group);
    	EditText synched_todo_calendar = (EditText) topSettingsView.findViewById(R.id.synched_todo_calendar);

	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_dont_synch_to_cal:
	            if (checked) {
	                synch_calendar_group.setVisibility(View.GONE);
	                
	                RadioButton radio_choose_cal_every = (RadioButton) topSettingsView.findViewById(R.id.radio_choose_cal_every);
	                radio_choose_cal_every.setChecked(true);

	                RadioButton radio_set_synch_cal = (RadioButton) topSettingsView.findViewById(R.id.radio_set_synch_cal);
	                radio_set_synch_cal.setChecked(false);
	            	synched_todo_calendar.setVisibility(View.GONE);
	            }
	            break;
	        case R.id.radio_synch_to_cal:
	            if (checked) {
	                synch_calendar_group.setVisibility(View.VISIBLE);
	            }
	            break;
	    }
	}
	public void onCalRadioButtonClicked(View view) {
		
    	EditText synched_todo_calendar = (EditText) topSettingsView.findViewById(R.id.synched_todo_calendar);

	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_choose_cal_every:
	            if (checked) {
	            	synched_todo_calendar.setVisibility(View.GONE);
	            }
	            break;
	        case R.id.radio_set_synch_cal:
	            if (checked) {
	            	synched_todo_calendar.setVisibility(View.VISIBLE);
	            }
	            break;
	    }
	}
	private void displaySettingsUnavailable() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Settings Unavailable");
        String message = "To access the settings to sync the todo list with the calendar, you must have" +
                  " a minimum Android version of 14. You are currently running version " + Build.VERSION.SDK_INT;        
        
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   // exit
               }
        });
		builder.create().show();
		
	}

	/**
	 * when user clicks ok on add dialog
	 */
	
	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		//set up vars and ensure no null pointers 
		if (dialog != null) {
			AlertDialog topDialog = (AlertDialog) dialog.getDialog();
			View topLayout = topDialog.findViewById(R.id.todoDialog);
			View datePickerView = null;
			if (topLayout != null)
				datePickerView = topLayout.findViewById(R.id.datePicker);
			if (datePickerView != null) {
				DatePicker datePicker = (DatePicker) datePickerView;				
				EditText eventNameEditText = (EditText) this.findViewById(R.id.todo_event_name);
				String eventName = eventNameEditText.getText().toString();
				if (eventName == null || eventName.length() == 0) {
					Toast.makeText(this, "Not adding, blank input.", Toast.LENGTH_LONG).show();
					
					
				//add new entry	
				} else {
					int day = datePicker.getDayOfMonth();
					int month = datePicker.getMonth() + 1;
					int year = datePicker.getYear();
					try {
						TodoParser todoParser = new TodoParser(readIn("todo.txt"));
						ArrayList<HashMap<String, String>> todoListMap = todoParser.getTodoList();
						HashMap<String, String> newEntry = new HashMap<String, String>();
						newEntry.put("name", eventName);
						//create string
						TimePicker timePicker = null;
						if (todoDialog != null && todoDialog.timeSelected() && topLayout.findViewById(R.id.timePicker) != null) {
							timePicker = (TimePicker) topLayout.findViewById(R.id.timePicker);
							newEntry.put("date", month + "/" + day + "/" + year +  " " + 
									twentyFour2TwelveHourTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
						} else {
							newEntry.put("date", month + "/" + day + "/" + year);
						}
						
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
							
							writeToCalendar(datePicker, timePicker, newEntry);
						} else {
							newEntry.put("id", "-1");
						}
						
						todoListMap.add(newEntry);
						boolean writeSuccessful = true;
						try {
 

							//assume todo.txt exists because to get to this point onCreate()
							//must have been called
							writeTo("todo.txt", MODE_PRIVATE, generateTodoString(sort(todoListMap)));

						} catch (IOException e) {
							writeSuccessful = false;
							Toast.makeText(TodoActivity.this, "Error saving changes." + e.getMessage(), Toast.LENGTH_LONG).show();
						}			
						//restart activity
						if (writeSuccessful) {
							finish();							
							startActivity(getIntent());
						}
					} catch (IOException e) {
						Toast.makeText(TodoActivity.this, "Error reading file." + e.getMessage(), Toast.LENGTH_LONG).show();
					}				
				} //end else
				
				
				
				
			}
		}	
	}
	@TargetApi(14)
	private void writeToCalendar(DatePicker datePicker, TimePicker timePicker, HashMap<String, String> newEntry) {
		
		int day = datePicker.getDayOfMonth();
		int month = datePicker.getMonth() + 1;
		int year = datePicker.getYear();
		
		//choose every time
		if (savedSettings[1] && savedSettings[2]) {
			Calendar beginTime = Calendar.getInstance();
			if (timePicker != null) {
				beginTime.set(year, month - 1, day, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
				Intent intent = new Intent(Intent.ACTION_INSERT)
		        .setData(Events.CONTENT_URI)
		        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
		        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, beginTime.getTimeInMillis())
		        .putExtra(Events.TITLE, newEntry.get("name"));
				startActivity(intent);
				
				
			} else {
				//all day
				beginTime.set(year, month - 1, day, 0, 0);
				Intent intent = new Intent(Intent.ACTION_INSERT)
		        .setData(Events.CONTENT_URI)
		        .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
		        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
		        .putExtra(Events.TITLE, newEntry.get("name"));
				startActivity(intent);
			}
			newEntry.put("id", String.valueOf(getEventId(newEntry.get("name"), beginTime.getTimeInMillis())));

			//use stored calendar name	
		} else if (savedSettings[1] &&  savedSettings[3]) {
			long calID = getCalendarId(savedCalName);
			if (calID == -1) {
				//couldn't get the id
				Toast.makeText(this, "Couldn't locate the calendar: " + savedCalName, Toast.LENGTH_LONG).show();
				
			} else {
				Calendar beginTime = Calendar.getInstance();
				if (timePicker != null) {
					beginTime.set(year, month - 1, day, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
					ContentResolver cr = getContentResolver();
					ContentValues values = new ContentValues();
					values.put(Events.DTSTART, beginTime.getTimeInMillis());
					values.put(Events.DTEND, beginTime.getTimeInMillis());
					values.put(Events.TITLE, newEntry.get("name"));
					values.put(Events.CALENDAR_ID, calID);
					values.put(Events.EVENT_TIMEZONE, "America/New_York");
					Uri uri = cr.insert(Events.CONTENT_URI, values);
					
					// get the event ID that is the last element in the Uri
					long eventID = Long.parseLong(uri.getLastPathSegment());
					newEntry.put("id", String.valueOf(eventID));

				} else {
					//all day

					
					beginTime.clear();
					beginTime.set(year, month - 1, day, 0, 0);
					long startMillis = beginTime.getTimeInMillis();
					long endMillis = beginTime.getTimeInMillis();
		            if (endMillis < startMillis + DateUtils.DAY_IN_MILLIS) {
		                endMillis = startMillis + DateUtils.DAY_IN_MILLIS;
		            }
					
					ContentResolver cr = getContentResolver();
					ContentValues values = new ContentValues();
					values.put(Events.DTSTART, startMillis);
		            values.put(Events.DURATION, (String) null);     
		            values.put(Events.DTEND, endMillis);
					values.put(Events.TITLE, newEntry.get("name"));
					values.put(Events.CALENDAR_ID, calID);
					values.put(Events.EVENT_TIMEZONE, Time.TIMEZONE_UTC);
					values.put(Events.ALL_DAY, 1);

					Uri uri = cr.insert(Events.CONTENT_URI, values);
					
					// get the event ID that is the last element in the Uri
					long eventID = Long.parseLong(uri.getLastPathSegment());
					newEntry.put("id", String.valueOf(eventID));
				}
			}
			
		//dont synch with calendar	
		} else if (savedSettings[0]) {
			newEntry.put("id", "-1");
		}
	}
	@TargetApi(14)
	private long getEventId(String title, long dtstart) {
		final String[] EVENT_PROJECTION = new String[] {
			    Events._ID, 
			    Events.DTSTART,   
			    Events.TITLE       
			};
			  
			// The indices for the projection array above.
			final int PROJECTION_ID_INDEX = 0;
			final int PROJECTION_DTSTART_INDEX = 1;
			final int PROJECTION_TITLE_INDEX = 2;
			
			// Run query
			Cursor eventCur = null;
			ContentResolver eventCr = getContentResolver();
			Uri eventUri = Events.CONTENT_URI; 

			//Submit the query and get a Cursor object back. 
			eventCur = eventCr.query(eventUri, EVENT_PROJECTION, null, null, null);

			// Use the cursor to step through the returned records
			long id = -1;
			while (eventCur.moveToNext()) {

			    if (eventCur.isLast()) {
			    	if(eventCur.getString(PROJECTION_TITLE_INDEX).equals(title) &&
			    			eventCur.getLong(PROJECTION_DTSTART_INDEX) == dtstart) {
			    		id = eventCur.getLong(PROJECTION_ID_INDEX);
			    	}
			    }
			      
			}
			    
			//TODO:if, for whatever reason the last row ins't the just added entry, do a query
			    //for the row with the matching name and date (highly unlikely situation)
			
			
			return id;

	}

	@TargetApi(14)
	private long getCalendarId(String savedCalName) {
		
		// Projection array. Creating indices for this array instead of doing
		// dynamic lookups improves performance.
		final String[] EVENT_PROJECTION = new String[] {
		    Calendars._ID,                           // 0

		};
		  
		// The indices for the projection array above.
		final int PROJECTION_ID_INDEX = 0;
		
		// Run query
		Cursor cur = null;
		ContentResolver cr = getContentResolver();
		Uri uri = Calendars.CONTENT_URI;   
		String selection = "(" + Calendars.CALENDAR_DISPLAY_NAME + " = ?)";
		String[] selectionArgs = new String[] {savedCalName}; 
		// Submit the query and get a Cursor object back. 
		cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
		
		// Use the cursor to step through the returned records
		long calID = -1;
		while (cur.moveToNext()) {	    

		    // Get the field values
		    calID = cur.getLong(PROJECTION_ID_INDEX);
		}
		return calID;
	}

	private ArrayList<HashMap<String, String>> sort(ArrayList<HashMap<String, String>> todoListMap) throws IOException{
		ArrayList<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>();

		dateFormats.add(new SimpleDateFormat("M/d/yyyy h:mm a"));
		dateFormats.add(new SimpleDateFormat("M/d/yyyy"));
		
		ArrayList<HashMap<Integer, Date>> sortMap = new ArrayList<HashMap<Integer, Date>>();
		try {

			for (int i = 0; i < todoListMap.size(); i++) {
				int invalidCounter = 0;
				for (int j = 0; j < dateFormats.size(); j++) {
					invalidCounter++;
					try {
						HashMap<Integer, Date> newItem = new HashMap<Integer, Date>();
						newItem.put(i, dateFormats.get(j).parse(todoListMap.get(i).get("date")));
						newItem.put(-1, dateFormats.get(j).parse(todoListMap.get(i).get("date")));
						sortMap.add(newItem);
						break;
					} catch (ParseException e) {
						if (invalidCounter == dateFormats.size()) {
							throw new ParseException("Couldn't match date format: " + e.getMessage(), i);
						}
					}
				}
			}
			boolean flipped = true;
			while (flipped) {
				flipped = false;
				for (int i = 0; i < sortMap.size() - 1; i++) {

					if (sortMap.get(i).get(-1).after(sortMap.get(i + 1).get(-1))) {
						HashMap<Integer, Date> temp = sortMap.get(i);
						sortMap.set(i, sortMap.get(i + 1));
						sortMap.set(i + 1, temp);
						flipped = true;
						break;
					}
				}
			}
		} catch (ParseException e) {
			throw new IOException("Invalid date format - " + e.getMessage());
		}
		ArrayList<HashMap<String, String>> returnMap = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < sortMap.size(); i++) {
			for (int pos : sortMap.get(i).keySet()) {
				if (pos != -1) {
					returnMap.add(todoListMap.get(pos));
				}
			}
		}
		return returnMap;
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		//user exits without adding
		
	}
	
	public void editTodoItemName(View view, int position) {
		final int pos = position;

		LayoutInflater inflater = getLayoutInflater();	
		View conent = inflater.inflate(R.layout.todo_name_edit_dialog, null);
		final EditText editText = (EditText) conent.findViewById(R.id.todoNameEditText);
		final String previousName = ((TextView)view).getText().toString();		
		editText.setText(previousName);
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
		.setTitle("Edit")
		.setView(conent)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String newName = editText.getText().toString();
				if (newName != null && newName.length() != 0) {
					// delete selected entry from todo.txt
					String todoListStr = "";
					try {
						//read in todo.txt
						todoListStr = readIn("todo.txt");	

						//parse todo list to data structure
						TodoParser todoParser = new TodoParser(todoListStr);
						ArrayList<HashMap<String, String>> todoListMap = todoParser.getTodoList();
						
						//Save for calendar entry
						String oldName = todoListMap.get(pos).get("name");
						
						//edit entry in data structure
						todoListMap.get(pos).put("name", newName);

						//generate string of new todo items
						String newTodoFile = generateTodoString(todoListMap);	

						boolean writeSuccessful = true;
						try {
							//write new list to todo.txt			
							writeTo("todo.txt", MODE_PRIVATE, newTodoFile);
							
							//propagate to calendar
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && !savedSettings[0]) {
								editCalendarEventName(Long.parseLong(todoListMap.get(pos).get("id")), oldName,
													  todoListMap.get(pos).get("name"),
													  todoListMap.get(pos).get("date"));
							}


						} catch (IOException e) {
							writeSuccessful = false;
							Toast.makeText(TodoActivity.this, "Error saving changes." + e.getMessage(), Toast.LENGTH_LONG).show();
						}			
						//restart activity if successful
						if (writeSuccessful) {
							finish();
							startActivity(getIntent());
						}
					} catch (IOException e) {
						Toast.makeText(TodoActivity.this, "Couldn't open todo file:  " + e.getMessage(), Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(TodoActivity.this, "Not saving, blank input.", Toast.LENGTH_LONG).show();
				} //end if-else input string isn't empty
			}     //end onClick
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
			}
		});	
		builder.create().show();

	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void editCalendarEventName(long eventID, String oldTitle, String newTitle, String date) {
		if (eventID != -1) {

			if (verifiyEventModificationRow(eventID, oldTitle, date)) {

				ContentValues values = new ContentValues();
				Uri updateUri = null;
				// The new title for the event
				values.put(Events.TITLE, newTitle); 
				updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
				int rows = getContentResolver().update(updateUri, values, null, null);
			}
		}

	}
	public void editTodoItemDate(View view, int position) {
		final int pos = position;
		
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		//Get the layout inflater
		LayoutInflater inflater = getLayoutInflater();
		
		//get initial message
		View dateContentView = inflater.inflate(R.layout.todo_dialog, null);
		
		TextView tv = (TextView) view;
		String previousDate = tv.getText().toString();
		DatePicker datePicker = (DatePicker) dateContentView.findViewById(R.id.datePicker);
		String[] dateSplit = previousDate.split("\\s+");
		String[] dateCompSplit = dateSplit[0].split("/");
		int day = Integer.parseInt(dateCompSplit[1]);
		int month = Integer.parseInt(dateCompSplit[0]) - 1;
		int year = Integer.parseInt(dateCompSplit[2]);
		datePicker.updateDate(year, month, day);
		
		
		
		String initialMsg = "";
		
		//If time was previously set, get it and set it to the time picker
		if (previousDate.contains(":")) {
			initialMsg = "Date and Time";
			LinearLayout layout = (LinearLayout) dateContentView;
			LinearLayout topLevelTimeView = (LinearLayout) inflater.inflate(R.layout.todo_time_select, null);
			TimePicker timePicker = (TimePicker) topLevelTimeView.findViewById(R.id.timePicker);
			Pattern p = Pattern.compile("[0-9]+:[0-9]+");
			Matcher m = p.matcher(previousDate);
			String time = "";
			if (m.find()) {
				time = m.group();
			}
			String[] timeSplit = time.split(":");
			int curHour = Integer.parseInt(timeSplit[0]);
			int curMin = Integer.parseInt(timeSplit[1]);
			if (previousDate.contains("pm") && curHour != 12) {
				curHour = curHour + 12;
			}
			if (previousDate.contains("am") && curHour == 12) {
				curHour = 0;
			}
			timePicker.setCurrentHour(curHour);
			timePicker.setCurrentMinute(curMin);
			layout.addView(topLevelTimeView);
		} else {
			initialMsg = "Date only";
		}

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because  its going in the dialog layout
		builder.setView(dateContentView)

		.setMessage(initialMsg)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Dialog d = (Dialog) dialog;
				AlertDialog a = (AlertDialog) d;
				View topView = a.findViewById(R.id.todoDialog);
				
				//determine if date or date and time
				LinearLayout topViewLinearLayout = null;
				if (topView != null && topView instanceof LinearLayout) {
					String itemEntry = "";
					int month = -1, day = -1, year = -1, hour = -1, minute = -1;
					topViewLinearLayout = (LinearLayout) topView;
					if (topViewLinearLayout.findViewById(R.id.datePicker) != null) {
						
						DatePicker datePicker = (DatePicker) topViewLinearLayout.findViewById(R.id.datePicker);
						day = datePicker.getDayOfMonth();
						month = datePicker.getMonth() + 1;
						year = datePicker.getYear();
						
						
					}	
					if (topViewLinearLayout.findViewById(R.id.timePicker) != null) {
						TimePicker timePicker = (TimePicker) topViewLinearLayout.findViewById(R.id.timePicker);
						itemEntry = month + "/" + day + "/" + year + " " +
								twentyFour2TwelveHourTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
						
					} else {
						itemEntry = month + "/" + day + "/" + year;
					}
					
					try {
						//parse todo list to data structure
						TodoParser todoParser = new TodoParser(readIn("todo.txt"));
						ArrayList<HashMap<String, String>> todoListMap = todoParser.getTodoList();
						String oldDate = todoListMap.get(pos).get("date");
						todoListMap.get(pos).put("date", itemEntry);
						boolean writeSuccessful = true;				
						
						try {
							//write new list to todo.txt			
							writeTo("todo.txt", MODE_PRIVATE, generateTodoString(sort(todoListMap)));
							
							
							//propagate to calendar
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && !savedSettings[0]) {
								editCalendarEventDate(Long.parseLong(todoListMap.get(pos).get("id")),
													  todoListMap.get(pos).get("name"), oldDate,
													  todoListMap.get(pos).get("date"));
							}

						} catch (IOException e) {
							writeSuccessful = false;
							Toast.makeText(TodoActivity.this, "Error saving changes." + e.getMessage(), Toast.LENGTH_LONG).show();
						}			
						//restart activity if successful
						if (writeSuccessful) {
							finish();
							startActivity(getIntent());
						}
					} catch (IOException e) {
						Toast.makeText(TodoActivity.this, "Couldn't open todo file:  " + e.getMessage(), Toast.LENGTH_LONG).show();
					}
				}

			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
			}
		})
		.setNeutralButton("Time", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//overridden later
			}
		});

		final AlertDialog alertDialog = builder.create();
		alertDialog.show();
		
		Button neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
		if (neutralButton != null) {
			neutralButton.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					if (alertDialog.findViewById(R.id.todoTimeSelect) == null) {
						LinearLayout layout = (LinearLayout) alertDialog.findViewById(R.id.todoDialog);
						layout.addView(TodoActivity.this.getLayoutInflater().inflate(R.layout.todo_time_select, null));
						alertDialog.setMessage("Date and Time");
					} else {
						LinearLayout layout = (LinearLayout) alertDialog.findViewById(R.id.todoDialog);
						layout.removeView(alertDialog.findViewById(R.id.todoTimeSelect));	
						alertDialog.setMessage("Date only");
					}					
				}
			});
		}
		
		
	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void editCalendarEventDate(long eventID, String title, String oldDate, String newDate) {
		if (eventID != -1) {

			if (verifiyEventModificationRow(eventID, title, oldDate)) {
				ContentValues values = new ContentValues();
				Uri updateUri = null;

				//get date correct
				if (!newDate.contains(":")) {
					String dateValues[] = newDate.split("/");
					Calendar beginTime = Calendar.getInstance();
					beginTime.clear();
					beginTime.set(Integer.parseInt(dateValues[2]),
							      Integer.parseInt(dateValues[0]) - 1, 
							      Integer.parseInt(dateValues[1]), 0, 0);
					long startMillis = beginTime.getTimeInMillis();
					long endMillis = beginTime.getTimeInMillis();
		            if (endMillis < startMillis + DateUtils.DAY_IN_MILLIS) {
		                endMillis = startMillis + DateUtils.DAY_IN_MILLIS;
		            }
					values.put(Events.ALL_DAY, 1); 
					values.put(Events.DTSTART, startMillis);
		            values.put(Events.DURATION, (String) null);     
		            values.put(Events.DTEND, endMillis);
					values.put(Events.EVENT_TIMEZONE, Time.TIMEZONE_UTC);
					
				}
				else if (newDate.contains(":")) {
					// The new date for the event
					String[] dateSplit = newDate.split("\\s+");
					String[] dateCompSplit = dateSplit[0].split("/");
					int day = Integer.parseInt(dateCompSplit[1]);
					int month = Integer.parseInt(dateCompSplit[0]) - 1;
					int year = Integer.parseInt(dateCompSplit[2]);

					Pattern p = Pattern.compile("[0-9]+:[0-9]+");
					Matcher m = p.matcher(newDate);
					String time = "";
					if (m.find()) {
						time = m.group();
					}
					String[] timeSplit = time.split(":");
					int curHour = Integer.parseInt(timeSplit[0]);
					int curMin = Integer.parseInt(timeSplit[1]);
					if (newDate.contains("pm") && curHour != 12) {
						curHour = curHour + 12;
					}
					if (newDate.contains("am") && curHour == 12) {
						curHour = 0;
					}
					
					Calendar cal = Calendar.getInstance();
					cal.clear();
					cal.set(year, month, day, curHour, curMin);
					
					long startMillis = cal.getTimeInMillis();
					long endMillis = startMillis;
					
					values.put(Events.DTSTART, startMillis);
					values.put(Events.DTEND, endMillis);
					values.put(Events.EVENT_TIMEZONE, Time.getCurrentTimezone());
					values.put(Events.ALL_DAY, 0);
				}
				
				updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
				int rows = getContentResolver().update(updateUri, values, null, null);

			}
		}

	}

	private String generateTodoString(ArrayList<HashMap<String, String>> todoDataStructure) {
		
		StringBuffer newTodoFile = new StringBuffer();
		for (int i = 0; i < todoDataStructure.size(); i++) {
			String itemEntry = "<item>\n" +
									"<name>" + todoDataStructure.get(i).get("name") + "<name>\n" +
									"<date>" + todoDataStructure.get(i).get("date") + "<date>\n" +
									"<id>"   + todoDataStructure.get(i).get("id")   + "<id>\n" +
								"<item>\n";
			newTodoFile.append(itemEntry);											
		}
		return newTodoFile.toString();
	}
	private String twentyFour2TwelveHourTime(int hour, int minute) {
		String minuteStr = "";
		if (minute < 10) {
			minuteStr = "0" + minute;
		} else {
			minuteStr = Integer.toString(minute);
		}
		String amPm = "";
		if (hour >= 12 && hour <= 23) {
			amPm = "pm";
		} else {
			amPm = "am";
		}
		if (hour > 12) {
			hour = hour - 12;
		}
		if (hour == 0) {
			hour = 12;
		}
		return hour + ":" + minuteStr + " " + amPm;
	}



}
