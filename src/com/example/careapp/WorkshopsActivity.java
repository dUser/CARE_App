package com.example.careapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 
 * Display the workshop pdf links in a list view. Clicking will download and display the file if 
 * it isn't already downloaded.
 *
 */
public class WorkshopsActivity extends Activity {
	
	 ListView workshopListView;
	 
	 String[] workshopTitles = {"Goal Setting", "Time Management", "Understanding Consumer and Student Debt",
			 					"Coping With Life On Campus", "Active Reading and Listening", "How to Manage Test Anxiety",
			 					"Developing Winning Habits"};
	 PdfDownloader pdfDownloader;

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
		
		pdfDownloader = new PdfDownloader(this);
		
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	//I think View parameter is useless here
	private void onlistItem(int position, View view) {
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
	//Called when items in list view are clicked
	/**
	 * Download and display CAREGoal.pdf
	 */
	public void onGoalButton(View view) {
		String fileName = "goal";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/CAREGoal.pdf";
		String title = "Goal Setting";
		pdfDownloader.viewPDF(fileName, url, title);
	}
	/**
	 * Download and display time management pdf
	 */	
	public void onTimeButton(View view) {
		String fileName = "time";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/CARE_Time_120312.pdf";
		String title = "Time Management";
		pdfDownloader.viewPDF(fileName, url, title);
	}
	/**
	 * Download and display understanding debt pdf
	 */	
	public void onDebtButton(View view) {
		String fileName = "debt";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/Figart%20Presentation%20on%20Understanding%20Consumer%20%20Student%20Debt%20for%20CARE.pdf";
		String title = "Understanding Debt";
		pdfDownloader.viewPDF(fileName, url, title);
	}
	/**
	 * Download and display coping pdf
	 */	
	public void onCopingButton(View view) {
		String fileName = "coping";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/COPING%20WITH%20LIFE%20ON%20CAMPUS.pdf";
		String title = "Coping on Campus";
		pdfDownloader.viewPDF(fileName, url, title);
	}
	/**
	 * Download and display active reading pdf
	 */	
	public void onActiveButton(View view) {
		String fileName = "active";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/CARE%20Active%20Reading%20and%20Listening.pdf";
		String title = "Active Reading";
		pdfDownloader.viewPDF(fileName, url, title);
	}
	/**
	 * Download and display test anxiety pdf
	 */	
	public void onTestButton(View view) {
		String fileName = "test";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/test%20anxiety.pdf";
		String title = "Test Anxiety";
		pdfDownloader.viewPDF(fileName, url, title);
	}
	/**
	 * Download and display winning habits pdf
	 */	
	public void onHabitsButton(View view) {
		String fileName = "habits";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE/DEVELOPING%20WINNING%20%20HABITS_GCalicdanApostle_2013.pdf";
		String title = "Winning Habits";
		pdfDownloader.viewPDF(fileName, url, title);
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

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.resources, menu);
//		return true;
//	}

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

}
