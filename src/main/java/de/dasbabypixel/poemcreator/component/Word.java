package de.dasbabypixel.poemcreator.component;

import java.util.Arrays;

public class Word {

	public Syllable[] content;
	public WordType type;
	public String word;

	@Override
	public String toString() {
		return "Word [content=" + Arrays.toString(content) + ", type=" + type + "]";
	}
}
