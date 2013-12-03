package com.example.careapp;

import java.util.ArrayList;
import java.util.HashMap;

public class TodoSettingsParser {
	ArrayList<HashMap<String, String>> settingsStruct = new ArrayList<HashMap<String,String>>();
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
	public ArrayList<HashMap<String, String>> getSettings() {
		return settingsStruct;
	}
	
 }
