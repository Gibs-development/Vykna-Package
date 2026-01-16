package io.xeros.script;

import java.util.Iterator;
import java.util.Set;

/**
 * Iterates through the script repository.
 * 
 * @author Adil
 *
 * @param <E>
 * 		The iterator element.
 * @param <T>
 * 		The script subject.
 * 
 * @version 12/03/2017
 */
public class ScriptRepositoryIterator<E extends Script, T> implements Iterator<E> {
	
	/**
	 * The current element index.
	 */
	private int index;
	
	/**
	 * A scripts collection.
	 */
	private Object[] scripts, indices;
	
	/**
	 * Constructs a new {@link ScriptRepositoryIterator}.
	 * 
	 * @param scripts
	 * 			The scripts to iterate through.
	 * @param indicies
	 * 				The script element pointers.
	 */
	public ScriptRepositoryIterator(Object[] scripts, Set<Integer> indicies) {
		this.scripts = scripts;
		this.indices = indicies.toArray(new Integer[indicies.size()]);
	}
	
	@Override
	public boolean hasNext() {
		return indices.length != index;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E next() {
		Object script = scripts[(int) indices[index]];
		index++;
		return (E) script;
	}
}
