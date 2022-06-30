package de.dasbabypixel.poemcreator.v2;

import de.dasbabypixel.poemcreator.PoemCreator;
import de.dasbabypixel.poemcreator.schema.RhymeSchema;
import de.dasbabypixel.poemcreator.wordlist.WordList;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

public class App {

	public static void open() {

		PoemCreator c = new PoemCreator();
		c.lexicon = Lexicon.getDefaultLexicon();
		c.nlgFactory = new NLGFactory(c.lexicon);
		c.realiser = new Realiser(c.lexicon);

		RhymeSchema schema = new RhymeSchema();
		WordList wl = new WordList();
		
		wl.add("live", "try", "chase", "money", "Mary", "school", "spend", "skyscraper", "tree", "house", "mouse",
				"rock", "chalk", "tall", "fall from", "watch", "look", "honey", "shy", "angry", "cute", "scared");

		schema.add("ABBACC");

		String res = c.create(wl, schema);
		System.out.println(res);
		c.componentLoader.saveSaved();
	}
}
