package de.dasbabypixel.poemcreator.sentencestructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.dasbabypixel.poemcreator.PoemCreator;
import de.dasbabypixel.poemcreator.component.Nomen;
import de.dasbabypixel.poemcreator.component.Verb;
import de.dasbabypixel.poemcreator.component.WordType;
import de.dasbabypixel.poemcreator.schema.RhymeSchema;
import edu.emory.mathcs.backport.java.util.Collections;
import simplenlg.phrasespec.SPhraseSpec;

public class SentenceStructure {

	public static String work(PoemCreator c, RhymeSchema schema) {
		float schemaSize = schema.schema.size();
		int wordCount = c.words.size();
		float wordsPerSentence = wordCount / schemaSize;
		float verbsPerSentence = c.verbs.size() / schemaSize;
		float nounsPerSentence = c.nomens.size() / schemaSize;

		if (wordsPerSentence < 3) {
			System.out.println("Few words per sentence: " + wordsPerSentence);
		}
		if (verbsPerSentence < 1) {
			System.out.println("Few verbs per sentence: " + verbsPerSentence);
		}
		if (nounsPerSentence < 2) {
			System.out.println("Few nouns per sentence: " + nounsPerSentence);
		}

		List<Nomen> nomens = new ArrayList<>(c.nomens);
		Collections.shuffle(nomens);
		List<Verb> verbs = new ArrayList<>(c.verbs);
		Collections.shuffle(verbs);

		Collection<WordType> used = new ArrayList<>();

		Collection<String> sentences = new ArrayList<>();
		Collection<Collection<String>> rows = new ArrayList<>();
		Map<Integer, Collection<String>> possibilities = new HashMap<>();
		int isentence = 0;

		while (true) {
			Nomen subj = random(nomens, used);
			Nomen obj = random(nomens, used);
			Verb verb = random(verbs, used);

			Collection<String> poss = new ArrayList<>();
			SPhraseSpec spec1 = c.nlgFactory.createClause("a " + subj.word.word, verb.word.word, "a " + obj.word.word);
			SPhraseSpec spec2 = c.nlgFactory.createClause("a " + obj.word.word, verb.word.word, "a " + subj.word.word);
			String sentence2 = c.realiser.realiseSentence(spec2);
			String sentence1 = c.realiser.realiseSentence(spec1);

			poss.add(sentence1);
			poss.add(sentence2);

			possibilities.put(isentence, poss);

			sentences.add(sentence1);
			sentences.add(sentence2);
			List<String> pos = new ArrayList<>();
			pos.add(sentence1);
			pos.add(sentence2);
			rows.add(pos);

			isentence++;
//			if (isentence >= schemaSize) {
//				break;
//			}
			if(used.containsAll(verbs)) {
				break;
			}
		}

//		Map<Integer, List<Collection<String>>> reordered = new HashMap<>();
//		for (int i : possibilities.keySet()) {
//			int id = schema.schema.get(i);
//			List<Collection<String>> ls = reordered.get(id);
//			if (!reordered.containsKey(id)) {
//				ls = new ArrayList<>();
//				reordered.put(id, ls);
//			}
//			ls.add(possibilities.get(i));
//		}

		RhymeCollection col = rhymes(sentences, rows);

		float score = reim("house", "mouse");
		System.out.println(score);
		System.out.println(reim("rock", "chalk"));
		System.out.println(reim("tall", "fall"));
		System.out.println(reim("One", "Gun"));
		System.out.println(reim("Face", "place"));
		System.out.println(reim("porn", "money"));

		return String.join("\n", sentences);
	}

	private static RhymeCollection rhymes(Collection<String> sentences, Collection<Collection<String>> rows) {
		RhymeCollection col = new RhymeCollection();
//		Collection<Collection<String>> rows = new ArrayList<>();
//		List<String> sentences = new ArrayList<>();
//		for (int sid : reordered.keySet()) {
//			for (Collection<String> row : reordered.get(sid)) {
//				sentences.addAll(row);
//				rows.add(row);
//			}
//		}
		rows = new ArrayList<>(rows);
		sentences = new ArrayList<>(sentences);
		Map<Float, Rhyme> rhymesByScore = new HashMap<>();
		Collection<String> ignore = new ArrayList<>();
		Iterator<String> it1 = sentences.iterator();
		Iterator<String> it2 = sentences.iterator();
		while (it1.hasNext()) {
			String sentence1 = it1.next();
			if (ignore.contains(sentence1)) {
				continue;
			}
			it2: while (it2.hasNext()) {
				String sentence2 = it2.next();
				if (ignore.contains(sentence2)) {
					continue;
				}
				if (sentence1.equals(sentence2)) {
					continue;
				}

				for (Collection<String> row : rows) {
					if (row.contains(sentence1) && row.contains(sentence2)) {
						continue it2;
					}
				}

				String last1 = last(sentence1);
				String last2 = last(sentence2);

				float score = reim(last1, last2);
				if (score > 0) {
					Rhyme rhyme = new Rhyme();
					Rhyme partner = new Rhyme();
					rhyme.partner = partner;
					partner.partner = rhyme;
					rhyme.sentence = sentence1;
					partner.sentence = sentence2;

					ignore.add(rhyme.sentence);
					ignore.add(partner.sentence);

					rhymesByScore.put(score, rhyme);
				}
				System.out.println(sentence1 + ": " + last1);
				System.out.println(sentence2 + ": " + last2);
			}
		}
		System.out.println(rhymesByScore);
		return col;
	}

	private static String last(String s) {
		String[] split = s.split("[ \\.]");
		return split[split.length - 1];
	}

	private static <T extends WordType> T random(List<T> l, Collection<WordType> used) {
		if (used.containsAll(l)) {
			return l.get(PoemCreator.instance.random.nextInt(l.size()));
		}
		T t = l.stream().filter(p -> !used.contains(p)).findFirst().get();
		used.add(t);
		return t;
	}

	public static float reim(String s1, String s2) {
		if (s1.equals(s2)) {
			return 0;
		}
		PoemCreator c = PoemCreator.instance;
		String ps1 = c.metaphone.Encode(s1);
		String ps2 = c.metaphone.Encode(s2);

		System.out.printf("%s %s %s %s%n", s1, s2, ps1, ps2);
		if (ps1.charAt(ps1.length() - 1) == ps2.charAt(ps2.length() - 1)) {
			return 2F / (ps1.length() + ps2.length());
		}
		return 0;
	}
}
