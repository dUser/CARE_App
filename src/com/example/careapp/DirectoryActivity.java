package com.example.careapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DirectoryActivity extends Activity {
	SimpleAdapter simpleAdapter;
	ArrayList<HashMap<String, String>> directoryMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directory);

		//read directory file and fill arraylist with map of info
		ArrayList<HashMap<String,String>> directoryList = new ArrayList<HashMap<String,String>>();
		Resources resources = this.getResources();
		String directory = null;
		InputStream is = null;
		BufferedReader bufferedReader = null;
		boolean readSuccessful = true;
		try {
			is = resources.openRawResource(R.raw.directory);
			bufferedReader = new BufferedReader(new InputStreamReader(is));
			StringBuilder directoryBuilder = new StringBuilder();

			while ((directory = bufferedReader.readLine()) != null) {
				directoryBuilder.append(directory);
			}
			directory = directoryBuilder.toString();

			DirectoryParser dp = new DirectoryParser(directory);
			directoryMap = dp.getDirectory();
			if (directoryMap.size() == 0) {
				throw new Exception("No directory data was found.");
			}
			for (int i = 0; i < directoryMap.size(); i++) {
				HashMap<String, String> listingMap = new HashMap<String, String>();
				listingMap.put("line_1", directoryMap.get(i).get("name"));
				listingMap.put("line_2", directoryMap.get(i).get("phone"));
				directoryList.add(listingMap);
			}

		} catch (IOException e) {
			directory = "Error: Couldn't read the directory file.";
			readSuccessful = false;
		} catch (Exception e) {
			directory = e.getMessage();
			e.printStackTrace();
			readSuccessful = false;
		} finally {
			try {
				if (is != null && bufferedReader != null) {
					is.close();
					bufferedReader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!readSuccessful) {
			TextView tv = (TextView) findViewById(R.id.DirectoryError);
			tv.setText(directory);
		} else {


			simpleAdapter = new SimpleAdapter(this, directoryList,
					android.R.layout.two_line_list_item ,
					new String[] { "line_1","line_2" },
					new int[] {android.R.id.text1, android.R.id.text2});

			ListView listView = (ListView) findViewById(R.id.listView);
			
			listView.setAdapter(simpleAdapter);
			this.registerForContextMenu(listView);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			     public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
			    	 DirectoryActivity.this.openContextMenu(view);
			     }
			});	
		} // end else

		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo) {
		super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
		AdapterContextMenuInfo adapterContextMenuInfo = (AdapterContextMenuInfo) contextMenuInfo;

		if (simpleAdapter != null) {
			contextMenu.setHeaderTitle("Options");
			contextMenu.add(adapterContextMenuInfo.position, 1, 1, "Visit webpage");
			contextMenu.add(adapterContextMenuInfo.position, 2, 2, "Call");
		}

	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
	    int listId = menuItem.getGroupId();
	    if (menuItem.getItemId() == 1) { //needs some work
	    	try {
	    		Uri url = Uri.parse(directoryMap.get(listId).get("link"));
	    		if (url == null || (url == Uri.EMPTY)) {
	    			throw new Exception("Error: Invalid url.");
	    		}
	    		startActivity(new Intent(Intent.ACTION_VIEW, url));
	    	} catch (Exception e) {
	    		Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
	    	}
	    } else if (menuItem.getItemId() == 2) {
	    	try {
	    		Uri tele = Uri.parse("tel:" + directoryMap.get(listId).get("phone"));
	    		if (tele == null || (tele == Uri.EMPTY)) {
	    			throw new Exception("Error: Invalid phone number.");
	    		}
	    		startActivity(new Intent(Intent.ACTION_CALL, tele)); 	             
	    	} catch (Exception e) {
	    		Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
	    	}
	    }
	    return true;
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
		getMenuInflater().inflate(R.menu.directory, menu);
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
