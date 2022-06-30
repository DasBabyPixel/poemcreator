package de.dasbabypixel.poemcreator.v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.dasbabypixel.poemcreator.PoemCreator;
import de.dasbabypixel.poemcreator.schema.RhymeSchema;
import de.dasbabypixel.poemcreator.sentencestructure.Rhyme;
import de.dasbabypixel.poemcreator.sentencestructure.RhymeCollection;
import edu.emory.mathcs.backport.java.util.Collections;

public class RhymeRealiser {

	PoemCreator c = PoemCreator.instance;

	public String[] realise(RhymeSchema schema, RhymeCollection col) {
		String[] sentences = new String[schema.schema.size()];
		int ssize = schema.schema.size();

		Collections.shuffle(col.rhymes, c.random);

		List<String> usedWords = new ArrayList<>();

		Iterator<Rhyme> it1 = col.rhymes.iterator();
		Map<Integer, Rhyme> rhymesById = new HashMap<>();
		for (int i : schema.schema) {
			if (rhymesById.containsKey(i)) {
				continue;
			}
			if (!it1.hasNext()) {
				break;
			}
			Rhyme rhyme = it1.next();

			usedWords.addAll(col.wordsForSentence.get(rhyme.sentence));
			usedWords.addAll(col.wordsForSentence.get(rhyme.partner.sentence));
			rhymesById.put(i, rhyme);
		}

		Collection<Integer> temp1 = new ArrayList<>();

		for (int i = 0; i < sentences.length; i++) {
			int sid = schema.schema.get(i);
			Rhyme rhyme = rhymesById.get(sid);
			if (rhyme != null) {
				if (temp1.contains(sid)) {
					sentences[i] = rhyme.partner.sentence;
				} else {
					temp1.add(sid);
					sentences[i] = rhyme.sentence;
				}
			}
		}
		Collections.shuffle(col.unused, c.random);

		for (int i = 0; i < sentences.length; i++) {
			if (sentences[i] == null) {
				sentences[i] = col.unused.get(i);
			}
		}

		return sentences;
	}
}
