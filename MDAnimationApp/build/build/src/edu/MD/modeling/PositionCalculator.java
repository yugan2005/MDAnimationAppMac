package edu.MD.modeling;

import java.util.HashMap;
import java.util.Map;

import edu.MD.number.MDVector;
import edu.MD.utility.MDConstants;
import edu.MD.utility.PBCCalculator;

public class PositionCalculator {
	private static Map<String, PositionCalculator> instances = new HashMap<>();

	private double mass, dt;

	private PositionCalculator(double mass, double dt) {
		this.mass = mass;
		this.dt = dt;
	}

	/**
	 * Factory method, get a configured VerletPositionUpdater
	 * 
	 * @param type
	 *            In form of 'ARGON_1e-15'. The last 1e-15 means the delta time
	 *            is 1e-15 second.
	 * @return VerletPositionUpdater instance (from the HashMap)
	 */
	public static PositionCalculator getInstance(String type) {
		if (instances.get(type) == null) {
			String name = type.split("_")[0];
			double dt = Double.parseDouble(type.split("_")[1]);
			double mass;
			try {
				mass = MDConstants.getMass(name);
			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException("The particle name is not correct, should be like 'ARGON_1e-15");
			}
			instances.put(type, new PositionCalculator(mass, dt));
		}
		return instances.get(type);
	}

	/**
	 * Refer to my PhD Thesis paper flow chart Fig3.11 P89 of 139. P.B.C is
	 * applied to the new Position vector. Also note that the full timestep dt
	 * is used
	 * 
	 * @param oldPosition
	 * @param oldVelocity
	 * @param forceVector
	 * @return newPosition
	 */
	public MDVector calculate(MDVector oldPosition, MDVector oldVelocity, MDVector forceVector) {
		PBCCalculator pbc = PBCCalculator.getInstance();
		MDVector newPosition = oldPosition.plus(oldVelocity.times(dt)).plus(forceVector.times(dt * dt).divide(2 * mass));
		MDVector adjustedPosition = pbc.applyPBC(newPosition);
		return adjustedPosition;
	}

}
