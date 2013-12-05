package com.example.careapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ResourcesActivity extends Activity {
	
	 ListView resourceListView;
	 
	 String[] resourcesTitles = {"Office Locations & Phone Numbers", "Mentee Improvement Plan", "Mentee Weekly Update",
			 					 "Personal Mentor (Faculty & Staff) Guidebook", "FAQ's"};
	 PdfDownloader pdfDownloader;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resources);
		
		resourceListView = (ListView)findViewById(R.id.resourcesListView);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resourcesTitles);		 
		resourceListView.setAdapter(arrayAdapter);
		
		this.registerForContextMenu(resourceListView);
		resourceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		     public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
		    	 ResourcesActivity.this.onlistItem(position, view);
		     }
		});	
		
		pdfDownloader = new PdfDownloader(this);
		
		
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	//I think View parameter is useless here
	private void onlistItem(int position, View view) {
		switch (position) {
		case 0: onOfficesButton(view);
			break;
		case 1: onImprovementButton(view);
			break;			
		case 2: onUpdateButton(view);
			break;
		case 3: onGuidebookButton(view);
			break;
		case 4: onFAQButton(view);
			break;						
		}
	}

	//Button Listeners
	public void onJoinButton(View view) {
		String[] options = { "Student", "Faculty" };
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Are you?")
	           .setItems(options, new DialogInterface.OnClickListener() {
	        	   
	        	   @Override
	               public void onClick(DialogInterface dialog, int which) {
	        		   switch (which) {
	        		   case 0:  Intent student_intent = new Intent(ResourcesActivity.this, JoinUsStudentActivity.class);
	        		   			ResourcesActivity.this.startActivity(student_intent); 	
	        			   break;
	        		   case 1: Intent faculty_intent = new Intent(ResourcesActivity.this, JoinUsFacultyActivity.class);
   		   					   ResourcesActivity.this.startActivity(faculty_intent); 	
	        			   break;
	        		   }
	               }
              
	           });
	    builder.setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// cancel
				
			}
		});
	    builder.create().show();
	}
	
	public void onOfficesButton(View view) {
		String fileName = "offices";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/Office%20Locations%20EYOS.pdf";
		String title = "Offices";
		pdfDownloader.viewPDF(fileName, url, title);
	}
	
	public void onImprovementButton(View view) {
		String fileName = "improvement";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/MIP%20in%20Writeable%20%283%29.pdf";
		String title = "Improvement Plan";
		pdfDownloader.viewPDF(fileName, url, title);
	}
	
	public void onUpdateButton(View view) {
		Intent weekly_update_intent = new Intent(ResourcesActivity.this, WeeklyUpdateActivity.class);
		ResourcesActivity.this.startActivity(weekly_update_intent); 
	}
	
	public void onGuidebookButton(View view) {
		String fileName = "guidebook";
		String url = "http://intraweb.stockton.edu/eyos/dean_students/content/docs/CARE%20Mentor%20Guide%20Book.pdf";
		String title = "Guidebook";
		pdfDownloader.viewPDF(fileName, url, title);
	}
	
	public void onFAQButton(View view) {
	    Intent intent = new Intent(this, FaqActivity.class);
	    startActivity(intent);
	}
	
	public void onWorkshopsButton(View view) {
		Intent intent = new Intent(this, WorkshopsActivity.class);
        startActivity(intent);  
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

}
