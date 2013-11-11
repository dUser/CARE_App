package com.example.careapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DirectoryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directory);

		//read directory file and fill arraylist with map of info
		SimpleAdapter sa;
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
			ArrayList<HashMap<String, String>> directoryMap = dp.getDirectory();
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


			sa = new SimpleAdapter(this, directoryList,
					android.R.layout.two_line_list_item ,
					new String[] { "line_1","line_2" },
					new int[] {android.R.id.text1, android.R.id.text2});
			// setListAdapter( sa );


			// We get the ListView component from the layout
			ListView lv = (ListView) findViewById(R.id.listView);

			// This is a simple adapter that accepts as parameter
			// Context
			// Data list
			// The row layout that is used during the row creation
			// The keys used to retrieve the data
			// The View id used to show the data. The key number and the view id must match
			//	    SimpleAdapter simpleAdpt = new SimpleAdapter(this, 
			//	    											 planetsList, 
			//	    											 android.R.layout.simple_list_item_1, 
			//	    											 new String[] {"planet"}, 
			//	    											 new int[] {android.R.id.text1});

			lv.setAdapter(sa);
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
