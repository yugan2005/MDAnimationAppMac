package edu.MD.statThermodynamic;

import java.util.List;

import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;

public interface IDensityCalculator {
	
	public List<List<MDNumber>> calculate(Iterable<MDVector> positions);

}
