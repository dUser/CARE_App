package com.example.careapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EventAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> eventList;
	private static LayoutInflater inflater = null;
	private ArrayList<Integer> headerPositions;
	long gmtComp = 18000000;

	public EventAdapter(Activity activity, ArrayList<HashMap<String, String>> eventList) {
		this.activity = activity;
		this.eventList = eventList;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		headerPositions = getHeaderPositions(eventList);
	}

	private ArrayList<Integer> getHeaderPositions(ArrayList<HashMap<String, String>> eventList) {
		//need to also sort eventList
		int[] positions = new int[eventList.size()];
		for (int i = 0; i < eventList.size(); i++) {
			positions[i] = i;
		}

		//round each event datetime to just a day
		ArrayList<Calendar> dates = new ArrayList<Calendar>();
		for (int i = 0; i < eventList.size(); i++) {
			Calendar cal = Calendar.getInstance();

			if (eventList.get(i).get("allday").equals("1")) {
				cal.setTime(new Date( Long.parseLong(eventList.get(i).get("dtstart")) + gmtComp));
			} else {
				cal.setTime(new Date( Long.parseLong(eventList.get(i).get("dtstart"))  ));
			}

			Calendar calFinal = Calendar.getInstance();
			calFinal.clear();
			calFinal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0);
			dates.add(calFinal);
		}
		//sort
		boolean flipped = true;
		while (flipped) {
			flipped = false;
			for (int i = 0; i < dates.size() - 1; i++) {
				if (dates.get(i).after(dates.get(i + 1))) {
					Calendar temp = dates.get(i);
					dates.set(i, dates.get(i + 1));
					dates.set(i + 1, temp);

					int tempPos = positions[i];
					positions[i] = positions[i + 1];
					positions[i + 1] = tempPos;

					flipped = true;
				}				
			}
		}
		//determine headers
		ArrayList<Integer> headerPositions = new ArrayList<Integer>();
		Calendar previous = Calendar.getInstance();
		previous.setTime(new Date(0));
		for (int i = 0; i < dates.size(); i++) {
			if ( dates.get(i).get(Calendar.YEAR)  != previous.get(Calendar.YEAR)  ||
					dates.get(i).get(Calendar.MONTH) != previous.get(Calendar.MONTH) ||
					dates.get(i).get(Calendar.DATE)  != previous.get(Calendar.DATE)) {

				headerPositions.add(i);
			}
			previous = dates.get(i);
		}

		//sort eventlist
		ArrayList<HashMap<String, String>> sortedEventList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < eventList.size(); i++) {
			sortedEventList.add(eventList.get(positions[i]));
		}
		this.eventList = sortedEventList;

		return headerPositions;
	}

	@Override
	public int getCount() {
		return eventList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(convertView == null) {
			view = inflater.inflate(R.layout.event_row, null);
		}
		TextView dateSection = (TextView) view.findViewById(R.id.eventDateSection);
		dateSection.setVisibility(View.VISIBLE);
		TextView title = (TextView) view.findViewById(R.id.eventTitle);
		TextView time = (TextView) view.findViewById(R.id.eventTime);	
		TextView location = (TextView) view.findViewById(R.id.eventLocation);

		HashMap<String, String> eventEntry = eventList.get(position);

		if (headerPositions.contains(position)) {
			if (eventList.get(position).get("allday").equals("1")) {
				dateSection.setText( milli2DateString(Long.parseLong(eventEntry.get("dtstart"))  +  gmtComp));
			} else {
				dateSection.setText(milli2DateString(Long.parseLong(eventEntry.get("dtstart"))));
			}

		} else {
			dateSection.setVisibility(View.GONE);
		}

		title.setText(eventEntry.get("title"));
		location.setText("Where: " + eventEntry.get("event_location"));
		String timeStr = "";
		if (eventList.get(position).get("allday").equals("1")) {
			time.setText("All day");
		} else {

			Calendar start_cal = Calendar.getInstance();
			start_cal.setTime(new Date(Long.parseLong(eventEntry.get("dtstart"))));
			Calendar end_cal = Calendar.getInstance();
			end_cal.setTime(new Date(Long.parseLong(eventEntry.get("dtend"))));

			boolean includeDate = false;
			if (start_cal.get(Calendar.DATE)  != end_cal.get(Calendar.DATE)  ||
					start_cal.get(Calendar.MONTH) != end_cal.get(Calendar.MONTH) ||
					start_cal.get(Calendar.YEAR)  != end_cal.get(Calendar.YEAR)) {
				includeDate = true;
			}
			String startDateStr = "", endDateStr = "";
			if (includeDate) {

						
				startDateStr = (start_cal.get(Calendar.MONTH) + 1) + "/" + 
						start_cal.get(Calendar.DATE) + "/" + 
						start_cal.get(Calendar.YEAR);
				endDateStr = (end_cal.get(Calendar.MONTH) + 1) + "/" + 
						end_cal.get(Calendar.DATE) + "/" + 
						end_cal.get(Calendar.YEAR);
						
			} 
			String startTimeStr =  ((start_cal.get(Calendar.HOUR) == 0) ? 12 : start_cal.get(Calendar.HOUR)) + ":";
			if (start_cal.get(Calendar.MINUTE) < 10) {
				startTimeStr += "0" + start_cal.get(Calendar.MINUTE);
			} else {
				startTimeStr += start_cal.get(Calendar.MINUTE);
			}
			if (start_cal.get(Calendar.AM_PM) == Calendar.AM) {
				startTimeStr += " am ";
			} else {
				startTimeStr += " pm ";
			}
			
			String endTimeStr =  ((end_cal.get(Calendar.HOUR) == 0) ? 12 : end_cal.get(Calendar.HOUR)) + ":";
			if (end_cal.get(Calendar.MINUTE) < 10) {
				endTimeStr += "0" + end_cal.get(Calendar.MINUTE);
			} else {
				endTimeStr += end_cal.get(Calendar.MINUTE);
			}
			if (end_cal.get(Calendar.AM_PM) == Calendar.AM) {
				endTimeStr += " am ";
			} else {
				endTimeStr += " pm ";
			}
			timeStr = startTimeStr + startDateStr + " - " + endTimeStr + endDateStr;
			
			time.setText("When:  " + timeStr);
		}
		
		
		
		
		return view;

	}
	private String milli2DateString(Long milliTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(milliTime));

		String dateStr =  (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) + "/" + cal.get(Calendar.YEAR);
		return dateStr;
	}

	public String getDescription(int pos) {
		return eventList.get(pos).get("description");
	}



}
