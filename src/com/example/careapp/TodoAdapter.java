package com.example.careapp;
 
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
public class TodoAdapter extends BaseAdapter {   

	private Activity activity;
    private ArrayList<HashMap<String, String>> todoList;
    private static LayoutInflater inflater = null;
    private boolean[] itemChecked;
   // public ImageLoader imageLoader; 
 
    public TodoAdapter(Activity activity, ArrayList<HashMap<String, String>> todoList) {
        this.activity = activity;
        this.todoList = todoList;
        itemChecked = new boolean[todoList.size()];
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
        return todoList.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null) {
        	view = inflater.inflate(R.layout.todo_row, null);
        }
        final int pos = position;
		View checkbox = view.findViewById(R.id.checkbox_todoRow);
		checkbox.setTag(position);
		checkbox.setOnClickListener(new OnClickListener() {					
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == R.id.checkbox_todoRow) {
					itemChecked[pos] = !itemChecked[pos];
				}

			}
		}); //end setup onClickListener
 
        TextView name = (TextView) view.findViewById(R.id.text1_todoRow);
        TextView date = (TextView) view.findViewById(R.id.text2_todoRow); 
 
        HashMap<String, String> item = new HashMap<String, String>();
        item = todoList.get(position);
 
        name.setText(item.get("name"));
        date.setText(item.get("date"));

        return view;
    }
}