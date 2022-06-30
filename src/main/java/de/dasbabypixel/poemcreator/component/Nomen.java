package de.dasbabypixel.poemcreator.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.dasbabypixel.poemcreator.wordlist.WordData;

public class Nomen extends WordType {

	public Nomen parent;

	public Numerus numerus;

	@Override
	public JsonElement serialize() {
		JsonObject o = new JsonObject();
		o.add("numerus", numerus == null ? JsonNull.INSTANCE : new JsonPrimitive(numerus.ordinal()));
		return o;
	}

	public Nomen with(Numerus numerus) {
		Nomen o = new Nomen();
		o.numerus = numerus;
		o.word = new Word();
		o.parent = parent;
		o.word.type = o;
		String word = parent.word.word;
		WordData data = new WordData(word);
		o.word.content = ComponentLoader.instance.loadSyllables(data);
		o.calc();
		return o;
	}

	public void calc() {
		if (numerus == Numerus.SINGULAR) {
			parent = this;
		}
	}

	@Override
	public void deserialize(JsonElement element) {
		JsonObject o = element.getAsJsonObject();
		if (o.has("numerus")) {
			if (!o.get("numerus").isJsonNull()) {
				numerus = Numerus.values()[o.get("numerus").getAsInt()];
			}
		}
		if (numerus == null) {
			throw new NullPointerException();
		}
		calc();
	}
}
