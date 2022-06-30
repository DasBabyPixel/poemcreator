package de.dasbabypixel.poemcreator.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

public class Verb extends WordType {

	@Override
	public JsonElement serialize() {
		return JsonNull.INSTANCE;
	}

	@Override
	public void deserialize(JsonElement element) {
	}

}
