package de.dasbabypixel.poemcreator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.dasbabypixel.poemcreator.component.Adjektiv;
import de.dasbabypixel.poemcreator.component.Adverb;
import de.dasbabypixel.poemcreator.component.ComponentLoader;
import de.dasbabypixel.poemcreator.component.Nomen;
import de.dasbabypixel.poemcreator.component.Numerus;
import de.dasbabypixel.poemcreator.component.Verb;
import de.dasbabypixel.poemcreator.component.Word;
import de.dasbabypixel.poemcreator.component.WordType;
import de.dasbabypixel.poemcreator.component.WordTypeList;
import de.dasbabypixel.poemcreator.schema.Metaphone;
import de.dasbabypixel.poemcreator.schema.RhymeSchema;
import de.dasbabypixel.poemcreator.sentencestructure.RhymeCollection;
import de.dasbabypixel.poemcreator.v2.RhymeFinder;
import de.dasbabypixel.poemcreator.v2.RhymeRealiser;
import de.dasbabypixel.poemcreator.v2.App;
import de.dasbabypixel.poemcreator.wordlist.WordList;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

public class PoemCreator {

	public ComponentLoader componentLoader;
	public List<Nomen> nomens = new ArrayList<>();
	public List<Verb> verbs = new ArrayList<>();
	public List<Adjektiv> adjektives = new ArrayList<>();
	public List<Adverb> adverbs = new ArrayList<>();
	public List<WordTypeList> wtl = new ArrayList<>();
	public List<Word> words = new ArrayList<>();
	public int syllableCount = 0;
	public Lexicon lexicon;
	public NLGFactory nlgFactory;
	public Realiser realiser;
	public Metaphone metaphone = new Metaphone();
	public Random random = new Random(1019760);
	public double adjectiveChance = 0.5;

	public static PoemCreator instance;

	public PoemCreator() {
		try {
			this.componentLoader = new ComponentLoader();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		instance = this;
	}

	public String create(WordList list, RhymeSchema schema) {
		Word[] words = componentLoader.loadWords(list);

		for (Word word : words) {
			WordType type = word.type;
			type(type);
		}
		select(wtl);
		wtl.clear();

		RhymeFinder f = new RhymeFinder();

		RhymeCollection col = f.findRhymes(schema);

		RhymeRealiser r = new RhymeRealiser();
		String[] r2 = r.realise(schema, col);
//		String res = SentenceStructure.work(this, schema);

		return String.join("\n", r2);
	}

	private void type(WordType type) {
		words.add(type.word);
		syllableCount += type.word.content.length;
		JsonElement data = type.serialize();
		JsonObject o = new JsonObject();
		o.add("data", data);
		o.add("type", new JsonPrimitive(type.getClass().getName()));
		componentLoader.saved.add(type.word.word, o);
		if (type instanceof Nomen) {
			nomens.add((Nomen) type);
		} else if (type instanceof Verb) {
			verbs.add((Verb) type);
		} else if (type instanceof Adjektiv) {
			adjektives.add((Adjektiv) type);
		} else if (type instanceof Adverb) {
			adverbs.add((Adverb) type);
		} else if (type instanceof WordTypeList) {
			wtl.add((WordTypeList) type);
		}
	}

	public void select(Collection<WordTypeList> types) {
		Scanner s = new Scanner(System.in);
		for (WordTypeList list : types) {
			AtomicInteger i = new AtomicInteger(1);
			List<String> cnames = Arrays.asList(list.classes)
					.stream()
					.map(c -> c.getSimpleName())
					.map(st -> i.getAndIncrement() + ": " + st)
					.collect(Collectors.toList());
			System.out.println(list.word.word + ":");
			for (String cname : cnames) {
				System.out.println(cname);
			}
			Pattern pt = pattern(cnames.size());
			int id = 0;
			while (true) {
				String r = s.nextLine();
				if (!pt.matcher(r).matches()) {
					continue;
				}
				id = Integer.parseInt(r);
				break;
			}
			Class<?> cls = list.classes[id - 1];
			try {
				WordType type = cls.asSubclass(WordType.class).getConstructor().newInstance();
				type.word = list.word;
				if (type instanceof Nomen) {
					Nomen n = (Nomen) type;
					n.numerus = Numerus.SINGULAR;
					n.calc();
				}
				type(type);
			} catch (InstantiationException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			} catch (InvocationTargetException ex) {
				ex.printStackTrace();
			} catch (NoSuchMethodException ex) {
				ex.printStackTrace();
			} catch (SecurityException ex) {
				ex.printStackTrace();
			}
		}
		s.close();
	}

	private Pattern pattern(int size) {
		List<String> numbers = new ArrayList<>();
		for (int v = 1; v <= size; v++) {
			numbers.add(Integer.toString(v));
		}
		String nbs = String.format("(%s)", String.join("|", numbers));
		return Pattern.compile(nbs);
	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		App.open();
//		PoemCreator c = new PoemCreator();
//		c.lexicon = Lexicon.getDefaultLexicon();
//		c.nlgFactory = new NLGFactory(c.lexicon);
//		c.realiser = new Realiser(c.lexicon);
//		WordList wl = new WordList();
//		wl.add("live", "try", "chase", "money", "Mary", "school", "spend", "skyscraper", "tree", "house", "porn",
//				"slut", "mouse", "rock", "chalk", "tall", "fall from", "watch", "look", "honey");
//
//		RhymeSchema schema = new RhymeSchema();
//		schema.add("ABBACC");
//
//		String res = c.create(wl, schema);
//		System.out.println(res);
//
//		c.componentLoader.saveSaved();
	}
}
