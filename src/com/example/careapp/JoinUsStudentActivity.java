package com.example.careapp;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class JoinUsStudentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_us_student);
		

		
		
		
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	
	public void onSubmitStudentJoinUsForm(View view) {
		ArrayList<HashMap<ArrayList<Integer>, ArrayList<Integer>>> validationMap = 
				new ArrayList<HashMap<ArrayList<Integer>, ArrayList<Integer>>>();
		
		ArrayList<Integer> notRequiredTextControls = new ArrayList<Integer>();
		notRequiredTextControls.add(R.id.whyJoinControl);
		notRequiredTextControls.add(R.id.addressLine2Control);
		
		ArrayList<Integer> requiredTextControls = new ArrayList<Integer>();
		requiredTextControls.add(R.id.firstNameControl);
		requiredTextControls.add(R.id.lastNameControl);
		requiredTextControls.add(R.id.zNumberControl);
		requiredTextControls.add(R.id.majorControl);
		requiredTextControls.add(R.id.addressControl);
		requiredTextControls.add(R.id.cityControl);
		requiredTextControls.add(R.id.zipCodeControl);
		requiredTextControls.add(R.id.emailControl);
		requiredTextControls.add(R.id.cellControl);
		requiredTextControls.add(R.id.homeControl);
		
		for (int i = 0; i < requiredTextControls.size(); i++) {
			TextView tv = (TextView) findViewById(requiredTextControls.get(i));
			//if ()
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
		getMenuInflater().inflate(R.menu.join_us_student, menu);
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
