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
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    private boolean[] itemChecked;
   // public ImageLoader imageLoader; 
 
    public TodoAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        itemChecked = new boolean[d.size()];
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      //  imageLoader=new ImageLoader(activity.getApplicationContext());
    }
 
    public int getCount() {
        return data.size();
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
        View vi=convertView;
        if(convertView==null) {
            vi = inflater.inflate(R.layout.todo_row, null);
        }
        final int pos = position;
		View checkbox = vi.findViewById(R.id.checkbox_todoRow);
		checkbox.setTag(position);
		checkbox.setOnClickListener(new OnClickListener() {					
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == R.id.checkbox_todoRow) {
					Toast.makeText(activity, "Checkbox "+v.getTag(), Toast.LENGTH_SHORT).show();
					itemChecked[pos] = !itemChecked[pos];
				}

			}
		}); //end setup onClickListener
 
        TextView title = (TextView)vi.findViewById(R.id.text1_todoRow); // title
        TextView artist = (TextView)vi.findViewById(R.id.text2_todoRow); // artist name
        //TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
        //ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
 
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
 
        // Setting all values in listview
        title.setText(song.get("name"));
        String s1 = song.get("name");
        String s3 = (String) title.getText();
        artist.setText(song.get("date"));
        String s2 = song.get("date");
        String s4 = (String) artist.getText();
        //duration.setText(song.get(CustomizedListView.KEY_DURATION));
        //imageLoader.DisplayImage(song.get(CustomizedListView.KEY_THUMB_URL), thumb_image);
        return vi;
    }
}