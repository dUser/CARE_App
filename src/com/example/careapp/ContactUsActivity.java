package com.example.careapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ContactUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_us);
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	public void onChangeNow(View view) {
		LayoutInflater inflater = getLayoutInflater();
		LinearLayout v = (LinearLayout) findViewById(R.id.linear_layout_parent);
		v.removeView(findViewById(R.id.hello));
		v.addView(inflater.inflate(R.layout.todo_row, null));
		

//		if (v != null) {
//			Toast.makeText(this, ((TextView)v).getText(), Toast.LENGTH_LONG).show();
//			if (((TextView)v).getText().equals("world"))  {
//				Toast.makeText(this, "one", Toast.LENGTH_LONG).show();
//				((TextView) v).setText("hello");
//			}
//			else if (((TextView)v).getText().equals("hello")) {
//				Toast.makeText(this, "two", Toast.LENGTH_LONG).show();
//				((TextView) v).setText("world");
//			}
//		}
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
		getMenuInflater().inflate(R.menu.contact_us, menu);
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
