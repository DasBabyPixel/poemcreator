package de.dasbabypixel.poemcreator.component;

public class Syllable {

	public String content;

	public Syllable(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return content;
	}
}
