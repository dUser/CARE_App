package com.example.careapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
/**
 * 
 * Display the two versions of the faqs for mentors and students. Uses two buttons to
 * switch between webviews containing local html copies of the faq documents. 
 *
 */
public class FaqActivity extends Activity {
	boolean mentorsView;
	final int SELECTED   = 0xFF0099CC;
	final int UNSELECTED = 0xFF8AD5F0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faq);
		
		mentorsView = true;
		
		WebView webView = (WebView) findViewById(R.id.faqWebView);	
		
		//get text of faq html		
		webView.loadUrl("file:///android_asset/faq_mentors.html");
		


		// Show the Up button in the action bar.
		setupActionBar();
	}
	/**
	 * Switch to mentors faq, changes button colors to indicate selection.
	 * @param view
	 */
	public void onMentorsFaqButton(View view) {
		if (!mentorsView) {
			
			mentorsView = true;	
			
			((Button) view).setBackgroundColor(SELECTED);
			Button studentButton = (Button) findViewById(R.id.studentsFaqButton);
			studentButton.setBackgroundColor(UNSELECTED);
			 
			WebView webView = (WebView) findViewById(R.id.faqWebView);	

			//get text of faq html		
			webView.loadUrl("file:///android_asset/faq_mentors.html");
		}
	}
	/**
	 * Switch to students faq button, changes button colors.
	 * @param view
	 */
	public void onStudentsFaqButton(View view) {
		if (mentorsView) {
			
			mentorsView = false;
			
			((Button) view).setBackgroundColor(SELECTED);
			Button mentorButton = (Button) findViewById(R.id.mentorsFaqButton);
			mentorButton.setBackgroundColor(UNSELECTED);
			
			WebView webView = (WebView) findViewById(R.id.faqWebView);	

			//get text of faq html		
			webView.loadUrl("file:///android_asset/faq_students.html");
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
		getMenuInflater().inflate(R.menu.faq, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}

}
