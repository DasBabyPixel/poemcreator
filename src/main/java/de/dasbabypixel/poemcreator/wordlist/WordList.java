package de.dasbabypixel.poemcreator.wordlist;

import java.util.ArrayList;
import java.util.List;

public class WordList {

	public List<WordData> content = new ArrayList<>();

	public void verb(String word) {
		content.add(new WordData(word));
	}

	public void nomen(String word) {
		content.add(new WordData(word));
	}

	public void adjektiv(String word) {
		content.add(new WordData(word));
	}

	public void add(String... words) {
		for (int i = 0; i < words.length; i++) {
			String w0 = words[i];
			content.add(new WordData(w0));
		}
	}
}
