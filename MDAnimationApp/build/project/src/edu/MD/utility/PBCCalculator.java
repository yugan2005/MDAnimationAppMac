package edu.MD.utility;

import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;

public class PBCCalculator {
	private static PBCCalculator instance;
	private static MDVector systemBoundary;

	private PBCCalculator() {
	}

	/**
	 * Factory method to get the singleton PBCCalculator
	 * 
	 * @return PBCCalculator.instance
	 */
	public static PBCCalculator getInstance() {
		if (instance == null)
			throw new UnsupportedOperationException(
					"Need set the PBCCalculator first by calling setPBCCalculator() method");
		return instance;
	}

	/**
	 * Set the singleton PBCCalculator
	 * 
	 * @param systemBoundary
	 */
	public static void setPBCCalculator(MDVector systemBoundary) {
		if (instance != null) {
			if (!PBCCalculator.systemBoundary.approximateEqual(systemBoundary))
				throw new UnsupportedOperationException(
						"The PBCCalculator has been set and the setting is incompatible with request setting, call destroyInstance() method first");
		}

		PBCCalculator.systemBoundary = systemBoundary;
		instance = new PBCCalculator();
	}

	public static void destroyInstance() {
		if (instance == null)
			throw new UnsupportedOperationException("instance has not been initialized yet, nothing to destroy");
		systemBoundary = null;
		instance = null;

	}

	/**
	 * Applies the minimum image convention to adjust the distance between two
	 * MDVectors
	 * 
	 * @param distanceVector
	 *            The unadjusted distanceVector
	 * @return the adjusted distance Vector - a new vector
	 */
	public MDVector applyMinimumImageConvention(MDVector distanceVector) {
		MDNumber[] oldDistance = distanceVector.getCartesianComponent();
		MDNumber[] adjustedDistances = new MDNumber[oldDistance.length];
		MDNumber[] bound = systemBoundary.getCartesianComponent();
		for (int i = 0; i < distanceVector.getDimension(); i++) {
			// The following guard should not be here to save time
			// if (dist[i].abs().compareTo(bound[i])>0) throw new
			// IllegalArgumentException("The input MDVector is larger than the
			// system's boundary");

			// corner case, when the distance is right at the middle of the
			// System Boundary
			if (!oldDistance[i].approximateEqual(bound[i].times(0.5))) {
				adjustedDistances[i] = oldDistance[i]
						.minus(bound[i].times(oldDistance[i].divide(bound[i]).plus(0.5).floor()));
			} else {
				adjustedDistances[i] = oldDistance[i];
			}
		}

		return distanceVector.getVectorFromCartesianComps(adjustedDistances);

		// Even this logical is wrong, because here the Cartesian vector is
		// returned. This reflection works. So still keep here as a record. Ref:
		// http://stackoverflow.com/questions/5760569/problem-with-constructing-class-using-reflection-and-array-arguments
		// Class<?>[] clazz = new Class[dist.length];
		// for (int i = 0; i < dist.length; i++) {
		// clazz[i] = MDNumber.class;
		// }
		// Object[] castForConstructor = (Object[]) dist;
		// Constructor<? extends MDVector> constructor =
		// distanceVector.getClass().getConstructor(clazz);
		// return constructor.newInstance(castForConstructor);
	}

	/**
	 * Applies the Periodic Boundary Condition to the position vector
	 * 
	 * @param position
	 *            The unadjusted position vector
	 * @return the adjusted position vector - a new Vector
	 */
	public MDVector applyPBC(MDVector position) {
		MDNumber[] oldComp = position.getCartesianComponent();
		MDNumber[] newComp = new MDNumber[oldComp.length];
		MDNumber[] bound = systemBoundary.getCartesianComponent();
		for (int i = 0; i < oldComp.length; i++) {
			newComp[i] = oldComp[i].mod(bound[i]);
		}
		return position.getVectorFromCartesianComps(newComp);
	}

}
