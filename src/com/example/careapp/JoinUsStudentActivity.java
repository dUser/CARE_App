package com.example.careapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
/**
 * 
 *  Launches browser to join us student webpage link. Students joining the
 *  program is done through a web form so this the only way to do this for now.
 *
 */
public class JoinUsStudentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_us_student);
		
		String url = "http://intraweb.stockton.edu/eyos/page.cfm?siteID=21&pageID=20&action=JForm";
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);




		// Show the Up button in the action bar.
		setupActionBar();
	}
	@Override
	/**
	 * So when the user clicks back after viewing the webpage, they aren't taken to a blank screen.
	 */
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			onBackPressed();
		}
	}

/*	public void onSubmitStudentJoinUsForm(View view) {

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
			if (tv != null) {
				String content = tv.getText().toString();
				if (content != null) {
					content = content.replaceAll("\\s+","");
					if (content.length() == 0) {
						Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_LONG).show();
						return;
					}
				} else {
					Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_LONG).show();
					return;
				}
			}
		}

		ArrayList<Integer> requiredSpinnerControls = new ArrayList<Integer>();
		requiredSpinnerControls.add(R.id.stateControl);

		for (int i = 0; i < requiredSpinnerControls.size(); i++) {
			Spinner spinner = (Spinner) findViewById(requiredSpinnerControls.get(i));
			if (spinner != null) {
				String content = spinner.getSelectedItem().toString();
				if (content != null) {
					content = content.replaceAll("\\s+","");
					if (content.length() == 0) {
						Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_LONG).show();
						return;
					}
				} else {
					Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_LONG).show();
					return;
				}
			}
		}

		String firstName    = ((TextView) findViewById(R.id.firstNameControl)).getText().toString();
		String lastName     = ((TextView) findViewById(R.id.lastNameControl)).getText().toString();
		String zNumber      = ((TextView) findViewById(R.id.zNumberControl)).getText().toString();
		String major        = ((TextView) findViewById(R.id.majorControl)).getText().toString();
		String address      = ((TextView) findViewById(R.id.addressControl)).getText().toString();
		String addressLine2 = ((TextView) findViewById(R.id.addressLine2Control)).getText().toString();
		String city         = ((TextView) findViewById(R.id.cityControl)).getText().toString();
		String state        = ((Spinner)  findViewById(R.id.stateControl)).getSelectedItem().toString();
		String zip          = ((TextView) findViewById(R.id.zipCodeControl)).getText().toString();		
		String email        = ((TextView) findViewById(R.id.emailControl)).getText().toString();
		String cellPhone    = ((TextView) findViewById(R.id.cellControl)).getText().toString();
		String homePhone    = ((TextView) findViewById(R.id.homeControl)).getText().toString();
		String whyJoin      = ((TextView) findViewById(R.id.whyJoinControl)).getText().toString();	
		
		String emailConent = 
				"First Name:     " + firstName    + "\n" +
				"Last Name:      " + lastName     + "\n" +		
				"Z Number:       " + zNumber      + "\n" +
				"Major:          " + major        + "\n" +		
				"Address:        " + address      + "\n" +
				"Address Line 2: " + addressLine2 + "\n" +		
				"City:           " + city         + "\n" +
				"State:          " + state        + "\n" +	
				"Zip:            " + zip          + "\n" +
				"Email:          " + email        + "\n" +		
				"Cell Phone:     " + cellPhone    + "\n" +
				"Home Phone:     " + homePhone    + "\n" +	
				"Why Join:       " + whyJoin      + "\n";
		
		
		Intent signUp = new Intent(android.content.Intent.ACTION_SEND);		
		signUp.setType("plain/text");
		
		signUp.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "careuser001@gmail.com" });
		signUp.putExtra(android.content.Intent.EXTRA_SUBJECT, "CARE Program application");
		signUp.putExtra(android.content.Intent.EXTRA_TEXT, emailConent);

		startActivity(Intent.createChooser(signUp, "Signing up for CARE program"));

	}*/

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
