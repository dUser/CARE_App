package com.example.careapp;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.DownloadManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class WorkshopsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workshops);
		// Show the Up button in the action bar.
		setupActionBar();
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
	
	public void viewPDF(String fileName, String url, String title){
		File file = new File("mnt/sdcard/Download/" + fileName + ".pdf");

		
        if (file.exists()) {
            Uri path = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            } 
            catch (ActivityNotFoundException e) {
                Toast.makeText(WorkshopsActivity.this, 
                    "No Application Available to View PDF", 
                    Toast.LENGTH_SHORT).show();
            }
        } else {
			DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
			request.setDescription("Download Stockton PDF");
			request.setTitle(title);
			// in order for this if to run, you must use the android 3.2 to compile your app
			/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			    request.allowScanningByMediaScanner();
			    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			}*/
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + ".pdf");
	
			// get download service and enqueue file
			DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
			manager.enqueue(request);
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

}
