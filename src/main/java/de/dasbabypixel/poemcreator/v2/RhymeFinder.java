package de.dasbabypixel.poemcreator.v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.dasbabypixel.poemcreator.PoemCreator;
import de.dasbabypixel.poemcreator.schema.RhymeSchema;
import de.dasbabypixel.poemcreator.sentencestructure.Rhyme;
import de.dasbabypixel.poemcreator.sentencestructure.RhymeCollection;
import edu.emory.mathcs.backport.java.util.Collections;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;

public class RhymeFinder {
	PoemCreator c = PoemCreator.instance;

	private void adjective(NPPhraseSpec spec, List<String> adjectives) {
		if (adjectives.size() != 0) {
			String adj = adjectives.get(c.random.nextInt(adjectives.size()));
			spec.addPreModifier(adj);
		}
	}

	public RhymeCollection findRhymes(RhymeSchema schema) {
		RhymeCollection col = new RhymeCollection();
		List<String> nomens = c.nomens.stream().map(n -> n.word.word).collect(Collectors.toList());
		List<String> verbs = c.verbs.stream().map(n -> n.word.word).collect(Collectors.toList());
		List<String> adjectives = c.adjektives.stream().map(n -> n.word.word).collect(Collectors.toList());
		Map<String, Collection<String>> sentencesWithWord = new HashMap<>();
		Map<String, Collection<String>> wordsForSentence = new HashMap<>();
		col.sentencesWithWord = sentencesWithWord;
		col.wordsForSentence = wordsForSentence;

		List<String> sentences1 = new ArrayList<>();

		// Create all possible sentences
		for (String nomen1 : nomens) {
			for (String nomen2 : nomens) {
				if (nomen1.equals(nomen2)) {
					continue;
				}
				for (String verb : verbs) {
					NPPhraseSpec spec1 = c.nlgFactory.createNounPhrase("a ", nomen1);
					NPPhraseSpec spec2 = c.nlgFactory.createNounPhrase("a ", nomen2);

					if (c.random.nextGaussian() < c.adjectiveChance) {
						adjective(spec1, adjectives);
					}
					if (c.random.nextGaussian() < c.adjectiveChance) {
						adjective(spec2, adjectives);
					}

					SPhraseSpec spec = c.nlgFactory.createClause(spec1, verb, spec2);
					String s1 = c.realiser.realiseSentence(spec);
					sentences1.add(s1);
					add(nomen1, s1, sentencesWithWord, wordsForSentence);
					add(nomen2, s1, sentencesWithWord, wordsForSentence);
					add(verb, s1, sentencesWithWord, wordsForSentence);
				}
			}
		}

		Map<ScoreKey, Float> scores = new HashMap<>();
		Map<ScoreKey, List<ScoreKey>> sentencesByScore = new HashMap<>();

		long last = System.currentTimeMillis();

		int sdone = 0;
		int ssize = sentences1.size();
		ssize = ssize * ssize;
		// find rhymes and score them
		for (String s1 : sentences1) {
			sdone++;

			String l1 = last(s1);
			for (String s2 : sentences1) {
				sdone++;
				if (System.currentTimeMillis() - 100 > last) {
					last += 100;
					System.out.printf("%2.0f%%%n", ((float) sdone / (float) ssize) * 100);
				}
				if (s1.equals(s2)) {
					continue;
				}
				String l2 = last(s2);
				ScoreKey key = new ScoreKey(l1, l2);

				if (scores.containsKey(key)) {
					if (sentencesByScore.containsKey(key)) {
						sentencesByScore.get(key).add(new ScoreKey(s1, s2));
					}
					continue;
				}

				float score = reim(l1, l2);
				scores.put(key, score);
				if (score > 0) {
					if (!sentencesByScore.containsKey(key)) {
						sentencesByScore.put(key, new ArrayList<>());
						sentencesByScore.get(key).add(new ScoreKey(s1, s2));
					}
				}
			}
		}
		for (ScoreKey key : new HashSet<>(scores.keySet())) {
			if (scores.get(key) == 0) {
				scores.remove(key);
				sentencesByScore.remove(key);
			}
		}

		List<String> used = new ArrayList<>();
		List<String> usedWords = new ArrayList<>();

		List<ScoreKey> skeys = new ArrayList<>(sentencesByScore.keySet());
		Collections.shuffle(skeys, c.random);

		// Pick sentences
		for (ScoreKey key : skeys) {
			List<ScoreKey> sentences2 = sentencesByScore.get(key);
			Collections.shuffle(sentences2, c.random);
			int ssize2 = sentences2.size();
			f2: for (int i = 0; i < ssize2; i++) {
				ScoreKey sk = sentences2.get(i);
				if (used.contains(sk.l1) || used.contains(sk.l2)) {
					continue;
				}
				List<String> words = new ArrayList<>();
				words.addAll(wordsForSentence.get(sk.l1));
				words.addAll(wordsForSentence.get(sk.l2));

				for (String w : words) {
					if (usedWords.contains(w)) {
						continue f2;
					}
				}

				usedWords.addAll(words);

				used.add(sk.l1);
				used.add(sk.l2);

				Rhyme rhyme = new Rhyme();
				rhyme.partner = new Rhyme();
				rhyme.partner.partner = rhyme;
				rhyme.sentence = sk.l1;
				rhyme.partner.sentence = sk.l2;
				col.rhymes.add(rhyme);
			}
		}

		sentences1.removeAll(used);
		List<String> sentences2 = new ArrayList<>(sentences1);

		for (String uw : used) {
			for (String w1 : wordsForSentence.get(uw)) {
				for (String s2 : sentencesWithWord.get(w1)) {
					sentences2.remove(s2);
				}
			}
		}

		col.unused.addAll(sentences2);

		return col;
	}

	public static float reim(String s1, String s2) {
		if (s1.equals(s2)) {
			return 0;
		}
		PoemCreator c = PoemCreator.instance;
		String ps1 = c.metaphone.Encode(s1);
		String ps2 = c.metaphone.Encode(s2);

//		System.out.printf("%s %s %s %s%n", s1, s2, ps1, ps2);
		if (ps1.charAt(ps1.length() - 1) == ps2.charAt(ps2.length() - 1)) {
			return 2F / (ps1.length() + ps2.length());
		}
		return 0;
	}

	private void add(String word, String sentence, Map<String, Collection<String>> ww,
			Map<String, Collection<String>> ws) {
		Collection<String> c = ww.get(word);
		if (c == null) {
			c = new ArrayList<>();
			ww.put(word, c);
		}
		c.add(sentence);
		c = ws.get(sentence);
		if (c == null) {
			c = new ArrayList<>();
			ws.put(sentence, c);
		}
		c.add(word);
	}

	private String last(String sentence) {
		String[] s = sentence.split("[ \\.]");
		return s[s.length - 1];
	}
}
