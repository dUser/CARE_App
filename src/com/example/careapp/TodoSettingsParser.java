package com.example.careapp;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * Takes the todo user customizable settings (for syncing the todo list 
 * with the google calendar)  as a formatted string and converts it to a data structure.
 * The string is in the form:
 * 1=[true|false],
 * 2=[true|false],
 *   ...
 * calendar=[calendars name]      
 *
 */
public class TodoSettingsParser {
	ArrayList<HashMap<String, String>> settingsStruct = new ArrayList<HashMap<String,String>>();
	
	/**
	 * 
	 * @param settingStr The string in the required format to be converted to a data structure
	 * Takes in a string in the required format and turns it to a data structure. Each setting is a
	 * hashmap with attribute - value. The hashmaps are stored in an ArrayList.
	 */
	public TodoSettingsParser(String settingStr) {
		String[] settings = settingStr.split(",");
		ArrayList<String> settingsNoWhiteSpace = new ArrayList<String>();
		for (int i = 0; i < settings.length; i++) {
			if (settings[i].trim().length() > 0) {
				settingsNoWhiteSpace.add(settings[i]);
			}
		}
		for (int i = 0; i < settingsNoWhiteSpace.size(); i++) {
			String[] attr_val = settingsNoWhiteSpace.get(i).split("=");
			if (attr_val.length == 2 && 
				attr_val[0].trim().length() > 0 &&
				attr_val[1].trim().length() > 0) {
				
				attr_val[0] = attr_val[0].replace("\n", "");
				attr_val[1] = attr_val[1].replace("\n", "");
				
				HashMap<String, String> entry = new HashMap<String, String>();
				entry.put(attr_val[0], attr_val[1]);
				settingsStruct.add(entry);
			}
		}
	}
	/**
	 * Returns the parsed settings as a data structure.
	 * @return The parsed settings string as a data structure.
	 */
	public ArrayList<HashMap<String, String>> getSettings() {
		return settingsStruct;
	}
	
 }
