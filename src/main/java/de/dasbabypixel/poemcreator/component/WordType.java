package de.dasbabypixel.poemcreator.component;

import com.google.gson.JsonElement;

public abstract class WordType {

	public Word word;
	
	public abstract JsonElement serialize();
	
	public abstract void deserialize(JsonElement element);
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
