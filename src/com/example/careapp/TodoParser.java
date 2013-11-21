package com.example.careapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TodoParser {
	ArrayList<HashMap<String, String>> todoList = new ArrayList<HashMap<String, String>>();
	
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

			HashMap<String, String> todoMap = new HashMap<String, String>();
			todoMap.put("name", name);
			todoMap.put("date", link);
			todoList.add(todoMap);
		}
	}
	public ArrayList<HashMap<String, String>> getTodoList() {
		return todoList;
	}
}
