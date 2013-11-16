package com.example.careapp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TodoActivity extends Activity {

	ArrayList<HashMap<String, String>> todoMap; //to store hashmap ArrayList from parser
	TodoAdapter adapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);
		boolean creationSuccessful = true;

		// create blank file if it doesn't exist
		boolean todoExists = true;
		try {
			FileInputStream todoIn = this.openFileInput("todo.txt");
		} catch (FileNotFoundException e) {
			todoExists = false;
		}
		if (!todoExists) {
			try {
				writeTo("todo.txt", MODE_PRIVATE, "");	
			} catch (IOException e) {
				creationSuccessful = false;
				Toast.makeText(this, "Couldn't create new todo file: " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		boolean readSuccessful = true;
		if (creationSuccessful) {
			//read in todo file
			String todoListStr = "";
			try {
				todoListStr = readIn("todo.txt");			
			} catch (IOException e) {
				readSuccessful = false;
				Toast.makeText(this, "Couldn't open todo file:  " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
			if (readSuccessful) {

				TodoParser dp = new TodoParser(todoListStr);
				todoMap = dp.getTodoList();
				ListView listView = (ListView) findViewById(R.id.todo_listView);				
				adapter = new TodoAdapter(this, todoMap);	

				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
						// TODO Auto-generated method stub
						TodoActivity.this.onEditTodoEntry(view);
					}

				});
			} // end if successful read in of data file
		} // end if creation of file is successful
		
		// Show the Up button in the action bar.
		setupActionBar();
		
	} //end onCreate
	
	
	public void onEditTodoEntry(View view) {
		//make sure view is todo_row.xml
		if (view.getId() == R.id.todoRow) {
			final String previousName, previousDate;
			LinearLayout todoRowInfo = (LinearLayout) ((LinearLayout)view).getChildAt(1);		
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Edit");
			
			//Create layout
			final LinearLayout layoutTopLevel = new LinearLayout(this);
			layoutTopLevel.setOrientation(LinearLayout.VERTICAL);
			final LinearLayout row1           = new LinearLayout(this);
			final LinearLayout row2           = new LinearLayout(this);
			
			row1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			row1.setOrientation(LinearLayout.HORIZONTAL);
			
			row2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			row2.setOrientation(LinearLayout.HORIZONTAL);
			
			final TextView namePrompt = new TextView(this);
			namePrompt.setPadding(10, 0, 0, 0);
			namePrompt.setText("Name");
			final EditText nameInput  = new EditText(this);
			LinearLayout.LayoutParams nameInputParams = 
					new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			nameInputParams.weight = 1;
			nameInput.setLayoutParams(nameInputParams);
			TextView nameTextView = (TextView) todoRowInfo.getChildAt(0);
			previousName = (String) nameTextView.getText();
			nameInput.setText(previousName);
			row1.addView(namePrompt);
			row1.addView(nameInput);
			
			final TextView datePrompt = new TextView(this);
			datePrompt.setPadding(10, 0, 0, 0);
			datePrompt.setText("Date ");
			final EditText dateInput  = new EditText(this);
			LinearLayout.LayoutParams dateInputParams = 
					new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			dateInputParams.weight = 1;
			dateInput.setLayoutParams(dateInputParams);
			TextView dateTextView = (TextView) todoRowInfo.getChildAt(1);
			previousDate = (String) dateTextView.getText();
			dateInput.setText(previousDate);
			row2.addView(datePrompt);
			row2.addView(dateInput);
			
			layoutTopLevel.addView(row1);
			layoutTopLevel.addView(row2);

			alert.setView(layoutTopLevel);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String name = nameInput.getText().toString();
					String date = dateInput.getText().toString();
					if (name != null && name.length() != 0 && date != null && date.length()	!= 0) {
						// delete selected entry from todo.txt
						String todoListStr = "";
						try {
							//read in todo.txt
							todoListStr = readIn("todo.txt");	
							int k = 0;
							//parse todo list to data structure
							TodoParser todoParser = new TodoParser(todoListStr);
							ArrayList<HashMap<String, String>> todoListMap = todoParser.getTodoList();
							
							//delete edited entry from data structure
							for (int i = 0; i < todoListMap.size(); i++) {
								if (todoListMap.get(i).get("name").equals(previousName) && 
										todoListMap.get(i).get("date").equals(previousDate)) {
									todoListMap.remove(i);
									break;
								}
							}
							
							//generate string of new todo items
							StringBuffer newTodoFile = new StringBuffer();
							for (int i = 0; i < todoListMap.size(); i++) {
								String itemEntry = "<item>\n" +
														"<name>" + todoListMap.get(i).get("name") + "<name>\n" +
														"<date>" + todoListMap.get(i).get("date") + "<date>\n" +
													"<item>\n";
								newTodoFile.append(itemEntry);											
							}		
							
							//create new edited entry
							String itemEntry = "<item>\n" +
													"<name>" + name + "<name>\n" +
													"<date>" + date + "<date>\n" +
												"<item>\n";
							newTodoFile.append(itemEntry);
							boolean writeSuccessful = true;
							try {
								//write new list to todo.txt			
								writeTo("todo.txt", MODE_PRIVATE, newTodoFile.toString());

							} catch (IOException e) {
								writeSuccessful = false;
								Toast.makeText(TodoActivity.this, "Error saving changes." + e.getMessage(), Toast.LENGTH_LONG).show();
							}			
							//restart activity if successful
							if (writeSuccessful) {
								finish();
								startActivity(getIntent());
							}
						} catch (IOException e) {
							Toast.makeText(TodoActivity.this, "Couldn't open todo file:  " + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(TodoActivity.this, "Not saving, blank input.", Toast.LENGTH_LONG).show();
					} //end if-else input strings aren't empty
				} // end onClick {
			}); //end alert.setPositiveButton

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			alert.show();
		} 	// end if view is todo_row	
	} // end onEditTodoEntry
	

	public void onSubmitChanges(View view) {
		if (adapter != null) {
			boolean writeSuccessful = true;
			int[] deletedPos = adapter.getChecked();
			ArrayList<HashMap<String, String>> tempList = new ArrayList<HashMap<String, String>>();
			for (int i = 0; i < todoMap.size(); i++) {
				tempList.add(todoMap.get(i));
			}
			//insert nulls into removed positions, so 
			//deletedPos refers to the same entry.
			for (int i = 0; i < deletedPos.length; i++) {
				tempList.set(deletedPos[i], null);
			}
			//remove nulls
			ArrayList<HashMap<String, String>> finalList = new ArrayList<HashMap<String, String>>();
			for (int i = 0; i < tempList.size(); i++) {
				if (tempList.get(i) != null) {
					finalList.add(tempList.get(i));
				}
			}
			//generate string of new todo items
			StringBuffer newTodoFile = new StringBuffer();
			for (int i = 0; i < finalList.size(); i++) {
				String itemEntry = "<item>\n" +
										"<name>" + finalList.get(i).get("name") + "<name>\n" +
										"<date>" + finalList.get(i).get("date") + "<date>\n" +
									"<item>\n";
				newTodoFile.append(itemEntry);											
			}		
			
			//overwrite todo.txt with new todo items and reload
			try {
				//assume todo.txt exists because to get to this point onCreate()
				//must have been called				
				writeTo("todo.txt", MODE_PRIVATE, newTodoFile.toString());
			} catch (IOException e) {
				writeSuccessful = false;
				Toast.makeText(this, "Error saving changes.", Toast.LENGTH_LONG).show();
			}	
			
			//restart activity
			if (writeSuccessful) {
				finish();
				startActivity(getIntent());
			}
		} // end if adapter != null
	}

	public void onAdd(View view) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Add");
		
		//Create layout
		final LinearLayout layoutTopLevel = new LinearLayout(this);
		layoutTopLevel.setOrientation(LinearLayout.VERTICAL);
		final LinearLayout row1           = new LinearLayout(this);
		final LinearLayout row2           = new LinearLayout(this);
		
		row1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		row1.setOrientation(LinearLayout.HORIZONTAL);
		
		row2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		row2.setOrientation(LinearLayout.HORIZONTAL);
		
		final TextView namePrompt = new TextView(this);
		namePrompt.setPadding(10, 0, 0, 0);
		namePrompt.setText("Name");
		final EditText nameInput  = new EditText(this);
		LinearLayout.LayoutParams nameInputParams = 
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		nameInputParams.weight = 1;
		nameInput.setLayoutParams(nameInputParams);
		row1.addView(namePrompt);
		row1.addView(nameInput);
		
		final TextView datePrompt = new TextView(this);
		datePrompt.setPadding(10, 0, 0, 0);
		datePrompt.setText("Date ");
		final EditText dateInput  = new EditText(this);
		LinearLayout.LayoutParams dateInputParams = 
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		dateInputParams.weight = 1;
		dateInput.setLayoutParams(dateInputParams);
		row2.addView(datePrompt);
		row2.addView(dateInput);
		
		layoutTopLevel.addView(row1);
		layoutTopLevel.addView(row2);

		alert.setView(layoutTopLevel);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String name = nameInput.getText().toString();
				String date = dateInput.getText().toString();
				if (name != null && name.length() != 0 && date != null && date.length()	!= 0) {
					//create string
					String itemEntry = "<item>\n" +
											"<name>" + name + "<name>\n" +
											"<date>" + date + "<date>\n" +
										"<item>\n";

					boolean writeSuccessful = true;
					try {
						//assume todo.txt exists because to get to this point onCreate()
						//must have been called
						writeTo("todo.txt", MODE_APPEND, itemEntry);
						
					} catch (IOException e) {
						writeSuccessful = false;
						Toast.makeText(TodoActivity.this, "Error saving changes." + e.getMessage(), Toast.LENGTH_LONG).show();
					}			
					//restart activity
					if (writeSuccessful) {
						finish();
						startActivity(getIntent());
					}
				} else {
					Toast.makeText(TodoActivity.this, "Not saving, blank input.", Toast.LENGTH_LONG).show();
				} //end if-else input strings aren't empty
			} // end onClick {
		}); //end alert.setPositiveButton

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
		
	} //end onAdd
	
	String readIn(String internalStorageFileName) throws IOException {
		String fileString = "";
		StringBuffer sb = new StringBuffer();
		FileInputStream fis = this.openFileInput(internalStorageFileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		fileString = sb.toString();
		br.close();	
		return fileString;
	}
	void writeTo(String internalStorageFileName, int writeMode, String content) throws IOException {
		FileOutputStream fos = this.getApplicationContext().openFileOutput(internalStorageFileName, writeMode);
		DataOutputStream dos = new DataOutputStream(fos);
		dos.write(content.getBytes());
		dos.close();	
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
		getMenuInflater().inflate(R.menu.todo, menu);
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
