package de.dasbabypixel.poemcreator.sentencestructure;

public class Rhyme {

	public Rhyme partner;
	public String sentence;

	@Override
	public String toString() {
		return String.format("Rhyme(%s | %s)", sentence, partner.sentence);
	}
}
