package ch.zhaw.prog2.application;

import java.util.ArrayList;
import java.util.List;
/**
 * Adds observable functionality to one WordModel-object.
 * The decorator uses the original methods of the WordModel-object.
 * @author bles
 *
 */
public class WordModelDecorator implements IsObservable {
	private final WordModel wordModel;
	private final List<IsObserver> listener = new ArrayList<>();

	public WordModelDecorator(WordModel wordModel) {
		this.wordModel = wordModel;
	}

	@Override
	public void addListener(IsObserver observer) {
		listener.add(observer);
	}

	@Override
	public void removeListener(IsObserver observer) {
		listener.remove(observer);
	}

	public void addWord(String word) {
		wordModel.addWord(word);
		informListener();
	}

	public void removeWord(String word) {
		wordModel.removeWord(word);
		informListener();
	}

	private void informListener() {
		for(IsObserver observer : listener) {
			observer.update();
		}
	}

}
