package com.example.careapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

public class FaqActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faq);
		
		
		WebView webView = (WebView) findViewById(R.id.faqWebView);
		
		//get text of faq html
		ArrayList<HashMap<String,String>> directoryList = new ArrayList<HashMap<String,String>>();
		Resources resources = this.getResources();
		String faq = null, line = null;
		InputStream is = null;
		BufferedReader bufferedReader = null;
		boolean readSuccessful = true;
		try {
			is = resources.openRawResource(R.raw.faq);
			bufferedReader = new BufferedReader(new InputStreamReader(is));
			StringBuilder faqBuilder = new StringBuilder();

			while ((line = bufferedReader.readLine()) != null) {
				faqBuilder.append(line + "\n");
			}
			faq = faqBuilder.toString();
			
			bufferedReader.close();

		} catch (IOException e) {
			readSuccessful = false;
			Toast.makeText(this, "Unable to read FAQ file.", Toast.LENGTH_LONG).show();
		}
		
		//Add html to webview if read was successful
		if (readSuccessful && faq != null && faq.length() != 0) {
			webView.loadData(faq, "text/html", "UTF-8");
		}
		
		
		
		// Show the Up button in the action bar.
		setupActionBar();
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
		getMenuInflater().inflate(R.menu.faq, menu);
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
