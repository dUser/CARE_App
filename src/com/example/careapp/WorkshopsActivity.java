package com.example.careapp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class WorkshopsActivity extends Activity {
	
	 ListView workshopListView;
	 
	 String[] workshopTitles = {"Goal Setting", "Time Management", "Understanding Consumer and Student Debt",
			 					"Coping With Life On Campus", "Active Reading and Listening", "How to Manage Test Anxiety",
			 					"Developing Winning Habits"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workshops);
		
		workshopListView = (ListView)findViewById(R.id.workshopsListView);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, workshopTitles);		 
		workshopListView.setAdapter(arrayAdapter);
		
		this.registerForContextMenu(workshopListView);
		workshopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		     public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
		    	 WorkshopsActivity.this.onlistItem(position, view);
		     }
		});	

		
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	//I think View parameter is useless here
	public void onlistItem(int position, View view) {
		switch (position) {
		case 0: onGoalButton(view);
			break;
		case 1: onTimeButton(view);
			break;			
		case 2: onDebtButton(view);
			break;
		case 3: onCopingButton(view);
			break;
		case 4: onActiveButton(view);
			break;			
		case 5: onTestButton(view);
			break;			
		case 6: onHabitsButton(view);
			break;				
		}
	}
	//Button Listeners
	public void onGoalButton(View view) {
		String fileName = "goal";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/CAREGoal.pdf";
		String title = "Goal Setting";
		viewPDF(fileName, url, title);
	}
	
	public void onTimeButton(View view) {
		String fileName = "time";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/CARE_Time_120312.pdf";
		String title = "Time Management";
		viewPDF(fileName, url, title);
	}
	
	public void onDebtButton(View view) {
		String fileName = "debt";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/Figart%20Presentation%20on%20Understanding%20Consumer%20%20Student%20Debt%20for%20CARE.pdf";
		String title = "Understanding Debt";
		viewPDF(fileName, url, title);
	}
	
	public void onCopingButton(View view) {
		String fileName = "coping";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/COPING%20WITH%20LIFE%20ON%20CAMPUS.pdf";
		String title = "Coping on Campus";
		viewPDF(fileName, url, title);
	}
	
	public void onActiveButton(View view) {
		String fileName = "active";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/CARE%20Active%20Reading%20and%20Listening.pdf";
		String title = "Active Reading";
		viewPDF(fileName, url, title);
	}
	
	public void onTestButton(View view) {
		String fileName = "test";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/test%20anxiety.pdf";
		String title = "Test Anxiety";
		viewPDF(fileName, url, title);
	}
	
	public void onHabitsButton(View view) {
		String fileName = "habits";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/DEVELOPING%20WINNING%20%20HABITS_GCalicdanApostle_2013.pdf";
		String title = "Winning Habits";
		viewPDF(fileName, url, title);
	}
	
	public void viewPDF(String fileName, String url, String title) {
		
		//Check if external storage is available (code from google)
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		if (mExternalStorageAvailable || mExternalStorageWriteable) {
			//check if file exists
			File file = new File(getExternalFilesDir(null), fileName + ".pdf");
			
	        if (file.exists()) {
	        	Toast.makeText(WorkshopsActivity.this, file.getPath(), Toast.LENGTH_LONG).show();
	        	
	            Uri path = Uri.fromFile(file);
	            Intent intent = new Intent(Intent.ACTION_VIEW);
	            intent.setDataAndType(path, "application/pdf");
	            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

	            try {
	                startActivity(intent);
	            } 
	            catch (ActivityNotFoundException e) {
	                Toast.makeText(WorkshopsActivity.this, "No Application Available to View PDF", Toast.LENGTH_LONG).show();
	            }
	            
	        //file doesn't exist    
	        } else {
	        	//Download if external storage is available and writable
	        	if (mExternalStorageAvailable) {
	        		
	        		//check if there's enough space for the file
	        		
	        		//get available space on sd card
	        	    StatFs fileSysemStats = new StatFs(getExternalFilesDir(null).getPath());
	        	    long availableBytes = 0;
	        	    int pdfSize = 0;
	        	    
	        	    //call method depending on api level
	        	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
	        	    	availableBytes = calculateSize_JELLY_BEAN_MR2(fileSysemStats);
	        	    } else {
	        	    	availableBytes = calculateSize(fileSysemStats);
	        	    }
	        	    try {
	        	    	//get size of file to be downloaded
	        	    	
	        	    	//allow networking on main thread
	        	    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        	    	StrictMode.setThreadPolicy(policy);
	        	    	//todo: start thread requesting info, if it takes to long end it and just try to download
	        	    	
	        	    	URL downloadFileUrl = new URL(url);
	        	    	URLConnection connection = downloadFileUrl.openConnection();
	        	    	connection.connect();
	        	    	pdfSize = connection.getContentLength();
	        	    	if (pdfSize == -1) {
	        	    		throw new IOException("Content length of pdf not set.");
	        	    	}
	        	    //Could't determine size of the file to download
	        	    } catch (IOException e) {
	        	    	//Oh well, try to download anyway, there is more than likely enough space
	        	    }
	        	    
	        	    if (availableBytes >= pdfSize) {
		        		Toast.makeText(WorkshopsActivity.this, Long.toString(availableBytes), Toast.LENGTH_LONG).show();	        	    	

	        	    	//Download the pdf
	        			DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
	        			request.setDescription("Download Stockton PDF");
	        			request.setDestinationInExternalFilesDir(this, null, fileName + ".pdf");
	        			DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
	        			manager.enqueue(request);
	        			
	        			//Problem checking if file is downloaded for download manager downloads it
	        			try {
							Thread.sleep(3000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							Toast.makeText(WorkshopsActivity.this, "couldn't sleep", Toast.LENGTH_LONG).show();
						}
	        			//show downloaded file
	        			File downloadFile = new File(getExternalFilesDir(null), fileName + ".pdf");
	        			Toast.makeText(WorkshopsActivity.this, downloadFile.getPath(), Toast.LENGTH_LONG).show();
	        			
	        	        if (downloadFile.exists()) {
	        	            Uri path = Uri.fromFile(downloadFile);
	        	            Intent intent = new Intent(Intent.ACTION_VIEW);
	        	            intent.setDataAndType(path, "application/pdf");
	        	            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

	        	            try {
	        	                startActivity(intent);
	        	            } 
	        	            catch (ActivityNotFoundException e) {
	        	                Toast.makeText(WorkshopsActivity.this, "No Application Available to View PDF", Toast.LENGTH_LONG).show();
	        	            }
	        	            
	        	        //file doesn't exist    
	        	        } else {
	        	        	Toast.makeText(WorkshopsActivity.this, "Download failed.", Toast.LENGTH_LONG).show();
	        	        }
	        	    	
	        	    //there's not enough space for the download	
	        	    } else {
		        		Toast.makeText(WorkshopsActivity.this, "Not enough storage memory available. Can't download pdf.", Toast.LENGTH_LONG).show();	        	    	
	        	    }
	        		
	        	//external storage not writable, can't download
	        	} else {
	        		Toast.makeText(WorkshopsActivity.this, "External storage is not writable. Can't download pdf.", Toast.LENGTH_LONG).show();
	        	}
			
	        }
	    // can't read or write external storage	
		} else { 
			Toast.makeText(WorkshopsActivity.this, "External storage is not available. Can't access or download pdf.", Toast.LENGTH_LONG).show();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.resources, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	@TargetApi(18)
	private long calculateSize(StatFs fileSysemStats) {
		return fileSysemStats.getBlockSizeLong() * fileSysemStats.getAvailableBlocksLong();
	}
	@SuppressWarnings("deprecation")
	@TargetApi(10)
	private long calculateSize_JELLY_BEAN_MR2(StatFs fileSysemStats) {
		
		return (long) fileSysemStats.getBlockSize() * (long) fileSysemStats.getBlockCount();
	}

}
