package edu.MD.modeling;

import java.util.ArrayList;
import java.util.List;

import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;
import edu.MD.statThermodynamic.MonatomicSysTemperatureCalculator;
import edu.MD.utility.IterableCounter;

public class VelocityStandardizor {

	public static List<MDVector> calculate(List<MDVector> velocities, String name, double temperature) {
		
		int totalNumParticles = IterableCounter.count(velocities);
		
		// re-center
		MDVector meanVelocity = velocities.get(0);
		for (int i=1; i<totalNumParticles; i++){
			meanVelocity = meanVelocity.plus(velocities.get(i));
		}
		meanVelocity = meanVelocity.divide(totalNumParticles);
		
		List<MDVector> newVelocities = new ArrayList<>(totalNumParticles);
		for (int i=0; i<totalNumParticles; i++){
			newVelocities.add(velocities.get(i).minus(meanVelocity));
		}
		
		// normalize

		MDNumber calculatedTemperature = MonatomicSysTemperatureCalculator.calculate(newVelocities, name);
		MDNumber rescaleFactor = calculatedTemperature.pow(-1).times(temperature).sqrt();
		
		for (int i=0; i<totalNumParticles; i++){
			newVelocities.set(i, newVelocities.get(i).times(rescaleFactor));
		}

		return newVelocities;
	}

}
