package edu.MD.modeling;

import edu.MD.number.MDVector;
import edu.MD.utility.PBCCalculator;

public class PBCDistanceCalculator{
	
	/**
	 * This version applies the PBC and uses the minimum image convention
	 * 
	 */
	public static MDVector calculate(MDVector p1Position, MDVector p2Position) {
		MDVector distanceVector = p1Position.minus(p2Position);
		PBCCalculator pbc = PBCCalculator.getInstance();
		MDVector adjustedDistanceVector = pbc.applyMinimumImageConvention(distanceVector);
		return adjustedDistanceVector;
	}

}
