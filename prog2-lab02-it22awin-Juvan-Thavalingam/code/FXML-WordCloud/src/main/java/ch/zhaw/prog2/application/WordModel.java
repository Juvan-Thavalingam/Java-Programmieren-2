package ch.zhaw.prog2.application;

import java.util.HashMap;
import java.util.Map;
/**
 * A WordModel object holds a map of words (String) with the actual count
 * @author bles
 *
 */
public class WordModel {
	private final Map<String, Integer> words = new HashMap<>();

	public void addWord(String word) {
		int count = words.getOrDefault(word, 0);
		words.put(word, ++count);
	}

	public void removeWord(String word) {
		int count = words.getOrDefault(word, 1);
		if (count == 1) {
			words.remove(word);
		} else {
			words.put(word, --count);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, Integer> entry : words.entrySet()) {
			sb.append(entry.getKey()).append(" - ").append(entry.getValue());
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

}
