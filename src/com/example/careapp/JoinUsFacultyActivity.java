package com.example.careapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
/**
 * 
 * Loads local html of the webpage for faculty joining, has button for launching form
 *
 */
public class JoinUsFacultyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_us_faculty);
		
 		
		int screenLayout = getResources().getConfiguration().screenLayout;
		
		WebView webView = (WebView) findViewById(R.id.joinUsFacultyWebView);
		if ( (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL  ||
			 (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			webView.loadUrl("file:///android_asset/join_us_faculty_small.html"); 
		} else {
			 webView.loadUrl("file:///android_asset/join_us_faculty.html"); 
		}
		// Show the Up button in the action bar.
		setupActionBar();
	}
	/**
	 * Launches webview with google form into webview
	 * @param view
	 */
	public void onFacultyJoin(View view) {
    	Intent intent = new Intent(this, JoinUsFacultyFormActivity.class);
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

//	/**
//	 * Creates and populates the option menu
//	 */
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.join_us_faculty, menu);
//		return true;
//	}

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
		}
		return super.onOptionsItemSelected(item);
	}

}
