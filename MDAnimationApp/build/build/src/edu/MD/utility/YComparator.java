package edu.MD.utility;

import java.util.Comparator;

import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;

public class YComparator implements Comparator<MDVector> {

	@Override
	public int compare(MDVector vector1, MDVector vector2) {
		MDNumber y1 = vector1.getCartesianComponent()[1];
		MDNumber y2 = vector2.getCartesianComponent()[1];

		return y1.compareTo(y2);
	}

}