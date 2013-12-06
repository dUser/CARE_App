package com.example.careapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 * Parses a string in the specified format into a data structure.
 * Required input format:
 * <item>
 * 	<name> its name <name>
 *  <date> its date <date>
 *  <id> its id     <id>
 * <item>
 * etc...
 *
 */
public class TodoParser {
	ArrayList<HashMap<String, String>> todoList = new ArrayList<HashMap<String, String>>();
	
	/**
	 * 
	 * @param todoListStr A string in the required format to be parsed into a data structure.
	 * Takes a string in the required format and parses it into a data structure.
	 * 
	 */
	public TodoParser(String todoListStr) {		
		String[] todo_with_whitespace = todoListStr.split("(<item>)+");
		ArrayList<String> todo = new ArrayList<String>();
		for (int i = 0; i < todo_with_whitespace.length; i++) {
			if (!todo_with_whitespace[i].matches("\\s") && todo_with_whitespace[i].length() != 0) {
				todo.add(todo_with_whitespace[i]);
			}
		}
		for (int i = 0; i < todo.size(); i++) {
			
			Pattern p = Pattern.compile("<name>[\\s\\S]+<name>");
			Matcher m = p.matcher(todo.get(i));
			String name = null;
			if (m.find()) {
			    name = m.group();
			    name = name.replaceAll("<name>", "");
			} else {
				continue;
			}
			
			p = Pattern.compile("<date>.+<date>");
			m = p.matcher(todo.get(i));
			String link = null;
			if (m.find()) {
			    link = m.group();
			    link = link.replaceAll("<date>", "");
			} else {
				continue;
			}

			p = Pattern.compile("<id>.+<id>");
			m = p.matcher(todo.get(i));
			String id = null;
			if (m.find()) {
				id = m.group();
				id = id.replaceAll("<id>", "");
			} else {
				continue;
			}

			HashMap<String, String> todoMap = new HashMap<String, String>();
			todoMap.put("name", name);
			todoMap.put("date", link);
			todoMap.put("id", id);
			todoList.add(todoMap);
		}
	}
	/**
	 * Returns the data structure which was parsed from the passed in string.
	 * @return the data structure represented in the passed in string
	 */
	public ArrayList<HashMap<String, String>> getTodoList() {
		return todoList;
	}
}
