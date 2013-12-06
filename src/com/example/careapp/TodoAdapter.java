package com.example.careapp;
 
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
 /**
  * 
  * The Adapter for diplaying the todo event entries in the listview.
  * Determines how the events are displays.
  *
  */
public class TodoAdapter extends BaseAdapter {   

	private Activity activity;
    private ArrayList<HashMap<String, String>> todoList;
    private static LayoutInflater inflater = null;
    private boolean[] itemChecked;
    
    /**
     * 
     * @param activity Activity which todo list is created in 
     * @param todoList The event data to be displayed
     */
    public TodoAdapter(Activity activity, ArrayList<HashMap<String, String>> todoList) {
        this.activity = activity;
        this.todoList = todoList;
        itemChecked = new boolean[todoList.size()];
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    /**
     * @return the number of todo event entries
     */
    public int getCount() {
        return todoList.size();
    }
    /**
     * Needs to be overrridden, method is abstract in superclass, 
     * simply returns the parameter.
     */
    public Object getItem(int position) {
        return position;
    }
    /**
     * Needs to be overrridden, method is abstract in superclass, 
     * simply returns the parameter.
     */
    public long getItemId(int position) {
        return position;
    }
    /**
     * Gets the positions of items in the list which are checked
     * @return the array of the positions of the checked (marked for removal) events
     */
    public int[] getChecked() {
    	ArrayList<Integer> checkedArrList = new ArrayList<Integer>();
    	for (int i = 0; i < itemChecked.length; i++) {
    		if (itemChecked[i]) {
    			checkedArrList.add(i);
    		}
    	}
    	int[] checkedArr = new int[checkedArrList.size()];
    	for (int i = 0; i < checkedArr.length; i++) {
    		checkedArr[i] = checkedArrList.get(i);
    	}
    	return checkedArr;
    }
    /**
     * Responsible for constructing each view in the listview.
     * @return view to be added as a listview entry
     * 
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null) {
        	view = inflater.inflate(R.layout.todo_row, null);
        }
        final int pos = position;
		View checkbox = view.findViewById(R.id.checkbox_todoRow);
		checkbox.setTag(position);
		((CheckBox) checkbox).setChecked(itemChecked[position]);
		checkbox.setOnClickListener(new OnClickListener() {					
			@Override
			/**
			 * Toggles the checked state saved in the itemChecked int[] member
			 */
			public void onClick(View v) {
				if (v.getId() == R.id.checkbox_todoRow) {
					itemChecked[pos] = !itemChecked[pos];
				}

			}
		}); //end setup onClickListener
 
        final TextView name = (TextView) view.findViewById(R.id.text1_todoRow);
        final TextView date = (TextView) view.findViewById(R.id.text2_todoRow);
        
        //Set listener for editing the name
        name.setOnClickListener(new OnClickListener() {
			
			@Override
			/**
			 * @param v The name field in the todo event list entry. Clicking allows the user 
			 * to edit it. This method get the location of the entry in the list by finding the 
			 * id which was set to the checkbox (in getView) in the same row.
			 */
			public void onClick(View v) {
				if (activity != null && activity instanceof TodoActivity) {					
					
					CheckBox checkBox = null;
					ViewGroup listViewRow = (ViewGroup) v.getParent().getParent();
					int pos = listViewRow.getChildCount();
					for (int i = 0; i < pos; i++) {
					    View view = listViewRow.getChildAt(i);
					    if (view instanceof CheckBox) {
					    	checkBox = (CheckBox) view;
					        break;
					    }
					}	
					
					if (checkBox != null) {
						int position = (Integer) checkBox.getTag();
						((TodoActivity)activity).editTodoItemName(v, position);
					}
				}
				
			}
		});
        
        //set listener for editing date
		/**
		 * @param v The date field in the todo event list entry. Clicking allows the user 
		 * to edit it. This method get the location of the entry in the list by finding the 
		 * id which was set to the checkbox (in getView) in the same row.
		 */
        date.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (activity != null && activity instanceof TodoActivity) {					
					
					CheckBox checkBox = null;
					ViewGroup listViewRow = (ViewGroup) v.getParent().getParent();
					int pos = listViewRow.getChildCount();
					for (int i = 0; i < pos; i++) {
					    View view = listViewRow.getChildAt(i);
					    if (view instanceof CheckBox) {
					    	checkBox = (CheckBox) view;
					        break;
					    }
					}	
					
					if (checkBox != null) {
						int position = (Integer) checkBox.getTag();
						((TodoActivity)activity).editTodoItemDate(v, position);
					}
				}
				
			}
		}); 
       
	    HashMap<String, String> todoEvent = new HashMap<String, String>();
	    todoEvent = todoList.get(position);
 
        name.setText(todoEvent.get("name"));
        date.setText(todoEvent.get("date"));

        return view;
    }
}