package edu.MD.modeling;

import java.util.ArrayList;
import java.util.List;

import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;
import edu.MD.statThermodynamic.MonatomicSysTemperatureCalculator;
import edu.MD.utility.IterableCounter;

public class VelocityStandardizor {

	/**
	 * This method first get rid of bulk velocity then re-scale (should use at initialization)
	 * @param velocities
	 * @param name
	 * @param temperature
	 * @return mean-zero temperature scaled velocities
	 */
	public static List<MDVector> standarize(List<MDVector> velocities, String name, double temperature) {

		int totalNumParticles = IterableCounter.count(velocities);

		// re-center
		MDVector meanVelocity = velocities.get(0);
		for (int i = 1; i < totalNumParticles; i++) {
			meanVelocity = meanVelocity.plus(velocities.get(i));
		}
		meanVelocity = meanVelocity.divide(totalNumParticles);

		List<MDVector> newVelocities = new ArrayList<>(totalNumParticles);
		for (int i = 0; i < totalNumParticles; i++) {
			newVelocities.add(velocities.get(i).minus(meanVelocity));
		}

		// re-scale
		newVelocities = rescale(newVelocities, name, temperature);

		return newVelocities;
	}

	/**
	 * This method only do the re-scaling (should used at normal temperature control)
	 * @param velocities
	 * @param name
	 * @param temperature
	 * @return rescaled velocities
	 */
	public static List<MDVector> rescale(List<MDVector> velocities, String name, double temperature) {

		int totalNumParticles = IterableCounter.count(velocities);
		List<MDVector> newVelocities = new ArrayList<>(totalNumParticles);
		
		MDNumber calculatedTemperature = MonatomicSysTemperatureCalculator.calculate(velocities, name);
		MDNumber rescaleFactor = calculatedTemperature.pow(-1).times(temperature).sqrt();

		for (int i = 0; i < totalNumParticles; i++) {
			newVelocities.add(velocities.get(i).times(rescaleFactor));
		}

		return newVelocities;
	}

}