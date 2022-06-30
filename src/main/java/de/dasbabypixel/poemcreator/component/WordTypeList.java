package de.dasbabypixel.poemcreator.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

public class WordTypeList extends WordType {

	public Class<?>[] classes;

	@Override
	public JsonElement serialize() {
		return JsonNull.INSTANCE;
	}

	@Override
	public void deserialize(JsonElement element) {
	}
	
}
