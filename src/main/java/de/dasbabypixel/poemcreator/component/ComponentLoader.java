package de.dasbabypixel.poemcreator.component;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import de.dasbabypixel.poemcreator.wordlist.WordData;
import de.dasbabypixel.poemcreator.wordlist.WordList;
import de.mfietz.jhyphenator.HyphenationPattern;
import de.mfietz.jhyphenator.Hyphenator;

public class ComponentLoader {

	public static ComponentLoader instance;

	public Hyphenator hyphenator = Hyphenator.getInstance(HyphenationPattern.EN_GB);
	public Path savedWords = Paths.get("savedWords.json");
	public JsonObject saved;
	public Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

	public ComponentLoader() throws IOException {
		instance = this;
		if (Files.exists(savedWords)) {
			Reader r = Files.newBufferedReader(savedWords, StandardCharsets.UTF_8);
			saved = gson.fromJson(r, JsonObject.class);
			r.close();
		} else {
			saved = new JsonObject();
		}
	}

	public Word[] loadWords(WordList text) {
		List<Word> words = new ArrayList<>();
		for (WordData data : text.content) {
			Word w = new Word();
			w.content = loadSyllables(data);
			w.type = loadType(data);
			w.type.word = w;
			w.word = data.word;
			words.add(w);
		}
		return words.toArray(new Word[0]);
	}

	public void saveSaved() {
		try {
			Writer w = Files.newBufferedWriter(savedWords, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
			gson.toJson(saved, w);
			w.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public WordType loadType(WordData data) {
		String word = data.word;
		if (word.isEmpty()) {
			return null;
		}
		if (saved.has(word)) {
			JsonObject o = saved.get(word).getAsJsonObject();
			String cname = o.get("type").getAsString();
			try {
				Class<?> cls = Class.forName(cname);
				WordType type = cls.asSubclass(WordType.class).getConstructor().newInstance();
				type.deserialize(o.get("data"));
				return type;
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
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
		WordTypeList list = new WordTypeList();
		list.classes = new Class<?>[] {
				Adjektiv.class, Verb.class, Nomen.class
		};
		return list;
	}

	public Syllable[] loadSyllables(WordData data) {
		List<String> syllables = hyphenator.hyphenate(data.word);
		List<Syllable> ret = new ArrayList<>();
		for (String syllable : syllables) {
			Syllable syl = new Syllable(syllable);
			ret.add(syl);
		}
		return ret.toArray(new Syllable[ret.size()]);
	}
}
