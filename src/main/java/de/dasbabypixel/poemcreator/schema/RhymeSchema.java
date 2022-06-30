package de.dasbabypixel.poemcreator.schema;

import java.util.ArrayList;
import java.util.List;

public class RhymeSchema {

	public List<Integer> schema = new ArrayList<>();

	public void add(char c) {
		schema.add((int) c);
	}

	public void add(String s) {
		for (char c : s.toCharArray()) {
			add(c);
		}
	}
}
