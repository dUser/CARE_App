package com.example.careapp;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactUsAdapter extends BaseAdapter {
	
	private Activity activity;
    private ArrayList<HashMap<String, String>> contactUsList;
    private static LayoutInflater inflater = null;
 
    public ContactUsAdapter(Activity activity, ArrayList<HashMap<String, String>> contactUsList) {
        this.activity = activity;
        this.contactUsList = contactUsList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return contactUsList.size();
    }
 
    public Object getItem(int pos) {
        return pos;
    }

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null) {
        	view = inflater.inflate(R.layout.contact_us_row, null);
        }
        TextView name = (TextView) view.findViewById(R.id.contactUsNameRow);
        TextView phone = (TextView) view.findViewById(R.id.contactUsPhoneRow);
        TextView email = (TextView) view.findViewById(R.id.contactUsEmailRow);
        TextView room = (TextView) view.findViewById(R.id.contactUsRoomRow);		
        
        HashMap<String, String> contactUsEntry = contactUsList.get(position);
        
        name.setText(contactUsEntry.get("name"));
        phone.setText(contactUsEntry.get("phone"));
        email.setText(contactUsEntry.get("email"));
        room.setText(contactUsEntry.get("room"));

        return view;
	}

}
