package de.dasbabypixel.poemcreator.sentencestructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RhymeCollection {

	public List<Rhyme> rhymes = new ArrayList<>();
	public List<String> unused = new ArrayList<>();
	public Map<String, Collection<String>> sentencesWithWord;
	public Map<String, Collection<String>> wordsForSentence;

}
