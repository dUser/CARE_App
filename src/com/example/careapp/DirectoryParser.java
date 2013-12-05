package com.example.careapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 * For taking a string representing the contents of the local directory file and parsing it into a 
 * data structure. The form of the data in the directory file is as follows:
 * <listing>
 * 	<name>contactname<name>
 * 	<link>contactlink<link>
 * <listing>, etc
 *
 */
public class DirectoryParser {
	
	ArrayList<HashMap<String, String>> directory = new ArrayList<HashMap<String, String>>();
	
	public DirectoryParser(String dir) {
		String[] listings_with_whitespace = dir.split("(<listing>)+");
		ArrayList<String> listings = new ArrayList<String>();
		for (int i = 0; i < listings_with_whitespace.length; i++) {
			if (!listings_with_whitespace[i].matches("\\s") && listings_with_whitespace[i].length() != 0) {
				listings.add(listings_with_whitespace[i]);
			}
		}
		for (int i = 0; i < listings.size(); i++) {
			Pattern p = Pattern.compile("<name>.+<name>");
			Matcher m = p.matcher(listings.get(i));
			String name = null;
			if (m.find()) {
			    name = m.group();
			    name = name.replaceAll("<name>", "");
			} else {
				continue;
			}
			p = Pattern.compile("<link>.+<link>");
			m = p.matcher(listings.get(i));
			String link = null;
			if (m.find()) {
			    link = m.group();
			    link = link.replaceAll("<link>", "");
			} else {
				continue;
			}
			p = Pattern.compile("<phone>.+<phone>");
			m = p.matcher(listings.get(i));
			String phone = null;
			if (m.find()) {
				phone = m.group();
				phone = phone.replaceAll("<phone>", "");
			} else {
				continue;
			}
			HashMap<String, String> listingMap = new HashMap<String, String>();
			listingMap.put("name", name);
			listingMap.put("link", link);
			listingMap.put("phone", phone);
			directory.add(listingMap);
		}
	}
	/**
	 * 
	 * @return Parsed data structure
	 */
	public ArrayList<HashMap<String, String>> getDirectory() {
		return directory;
	}

}
