package org.cytoscape.blueprints;

import java.util.ArrayList;
import java.util.Iterator;

public class IterateMe <T> implements Iterable<T>, Iterator<T> {

	ArrayList<Iterator<T>> its;
	int currentIt = 0;
	
	IterateMe(Iterable<T>... t) {
		its = new ArrayList<Iterator<T>>();
		combine(t);
	}
	
	public Iterable<T> combine(Iterable<T>... t){
		for (Iterable<T> l : t) {
			its.add(l.iterator());
		}
		return this;
	}
	
	@Override
	public boolean hasNext() {
		if (its.get(currentIt).hasNext()) {
			return true;
		} else {
			if (++currentIt >= its.size()) {
				return false;
			} else {
				return hasNext();
			}
			
		}
	}

	@Override
	public T next() {
		return its.get(currentIt).next();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}

}
