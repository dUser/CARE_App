// Adding this line just to test Git.
package com.example.careapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
/**
 * 
 * Top level interface for user interaction. Contains a grid of buttons for each 
 * seperate  fucntionality of the app
 *
 */
public class MainActivity extends Activity {

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    
//    /**
//	 * Creates and populates the option menu
//	 */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
    
    
    /**
     * button listeners
     * @param view
     */
    public void onEventsButton(View view) {
    	Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
    }
    
    /**
     * Starts the Todo activity
     * @param view
     */
    public void onTodoButton(View view) {
    	Intent intent = new Intent(this, TodoActivity.class);
        startActivity(intent);    	
    }
    
    /**
     * Starts the Directory activity
     * @param view
     */
    public void onDirectoryButton(View view) {
    	Intent intent = new Intent(this, DirectoryActivity.class);
        startActivity(intent);    	
    }
    
    /**
     * Starts the Contact Us activity
     * @param view
     */
    public void onContactUsButton(View view) {
    	Intent intent = new Intent(this, ContactUsActivity.class);
        startActivity(intent);    	
    }
    
    /**
     * Starts the Resources activity
     * @param view
     */
    public void onResourcesButton(View view) {
    	Intent intent = new Intent(this, ResourcesActivity.class);
        startActivity(intent);     	
    }
    
    /**
     * Starts the Calendar Activity
     * @param view
     */
    public void onCalendarButton(View view) {
    	Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);    	
    }
    
}
