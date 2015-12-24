package edu.MD.modeling;

import java.util.HashMap;
import java.util.Map;

import edu.MD.number.MDVector;
import edu.MD.utility.MDConstants;

public class VelocityCalculator {
	private static Map<String, VelocityCalculator> instances = new HashMap<>();

	private double mass, dt;

	private VelocityCalculator(double mass, double dt) {
		this.mass = mass;
		this.dt = dt;
	}

	/**
	 * Factory method, get a configured VelocityUpdater
	 * 
	 * @param type
	 *            In form of 'ARGON_1e-15'. The last 1e-15 means the delta time
	 *            is 1e-15 second.
	 * @return VelocityUpdater instance (from the HashMap)
	 */
	public static VelocityCalculator getInstance(String type) {
		if (instances.get(type) == null) {
			String name = type.split("_")[0];
			double dt = Double.parseDouble(type.split("_")[1]);
			double mass;
			try {
				mass = MDConstants.getMass(name);
			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException("The particle name is not correct, should be like 'ARGON_1e-15");
			}
			instances.put(type, new VelocityCalculator(mass, dt));
		}
		return instances.get(type);
	}
	
	/**
	 * This method uses the full timestep dt instead of dt/2 like in velocity Verlet algorithm
	 * @param oldVelocity
	 * @param forceVector
	 * @return newVelocity
	 */
	public MDVector calculate(MDVector oldVelocity, MDVector forceVector){
		return oldVelocity.plus(forceVector.divide(mass).times(dt));
	}

}
