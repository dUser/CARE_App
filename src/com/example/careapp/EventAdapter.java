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
/**
 * 
 * Adapter for displaying events from the CARE calendar
 * The events are displayed in a list view, events occurring on the same day
 * will be under a common header (date - mm/dd/yyyy) in the listview.
 *
 */
public class EventAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> eventList;
	private static LayoutInflater inflater = null;
	private ArrayList<Integer> headerPositions;
	long gmtComp = 18000000;
	/**
	 * 
	 * @param activity The activity that the adapter is constructed within
	 * @param eventList The data structure containing the events and the info about them.
	 */
	public EventAdapter(Activity activity, ArrayList<HashMap<String, String>> eventList) {
		this.activity = activity;
		this.eventList = eventList;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		headerPositions = getHeaderPositions(eventList);
	}
	/**
	 * Find where the date headers will be in the list view
	 * @param eventList The events and their data in a data structure
	 * @return The positions of where the headers will be in the events list
	 */
	private ArrayList<Integer> getHeaderPositions(ArrayList<HashMap<String, String>> eventList) {
		//need to also sort eventList.
		//save the positions into an array which will
		//remember where each object is located
		int[] positions = new int[eventList.size()];
		for (int i = 0; i < eventList.size(); i++) {
			positions[i] = i;
		}

		//Add dates of events to array to be sorted
		ArrayList<Calendar> dates = new ArrayList<Calendar>();
		for (int i = 0; i < eventList.size(); i++) {
			Calendar cal = Calendar.getInstance();

			if (eventList.get(i).get("allday").equals("1")) {
				//All day events are in UTC timezone (Google spec) so you need to add
				//a compensation to get the Calendar object to the correct time
				cal.setTime(new Date( Long.parseLong(eventList.get(i).get("dtstart")) + gmtComp));
			} else {
				cal.setTime(new Date( Long.parseLong(eventList.get(i).get("dtstart"))  ));
			}

			Calendar calFinal = Calendar.getInstance();
			calFinal.clear();
			calFinal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0);
			dates.add(calFinal);
		}
		//bubble sort dates
		boolean flipped = true;
		while (flipped) {
			flipped = false;
			for (int i = 0; i < dates.size() - 1; i++) {
				if (dates.get(i).after(dates.get(i + 1))) {
					Calendar temp = dates.get(i);
					dates.set(i, dates.get(i + 1));
					dates.set(i + 1, temp);
					
					//Sorting positions also
					int tempPos = positions[i];
					positions[i] = positions[i + 1];
					positions[i + 1] = tempPos;

					flipped = true;
				}				
			}
		}
		//determine header locations
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

		//sort eventlist by getting positions from sorted array
		ArrayList<HashMap<String, String>> sortedEventList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < eventList.size(); i++) {
			sortedEventList.add(eventList.get(positions[i]));
		}
		this.eventList = sortedEventList;

		return headerPositions;
	}

	/**
	 * Returns event list size
	 */
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
	/**
	 * Every entry in the event list has a built in header, but if that event
	 * is not in a header location then the header is set invisible.
	 */
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
			
			//Include date if the event spans multiple days.
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

	/**
	 * 
	 * @param pos of event in the list
	 * @return The description of the selected event
	 */
	public String getDescription(int pos) {
		return eventList.get(pos).get("description");
	}



}
