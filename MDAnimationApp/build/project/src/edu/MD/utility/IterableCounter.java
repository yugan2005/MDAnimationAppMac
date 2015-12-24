package edu.MD.utility;

import java.util.Collection;

public class IterableCounter {

	public static int count(Iterable<?> iterable) {
		// This is the trick used by Iterables.size(Iterable) of Guava
		int counter = 0;
		if (iterable instanceof Collection<?>) {
			counter = ((Collection<?>) iterable).size();
		} else {
			for (@SuppressWarnings("unused")
			Object object : iterable)
				counter++;
		}
		return counter;
	}

}
