package edu.MD.number;

public interface MDVector {

	public int getDimension();

	public MDNumber[] getCartesianComponent();

	public MDNumber getCartesianDistance(MDVector vector);

	public MDVector plus(MDVector vector);

	public MDVector plus(MDNumber c);

	public MDVector plus(double c);

	public MDVector minus(MDVector vector);

	public MDVector minus(double c);

	@Override
	public boolean equals(Object obj);

	public boolean approximateEqual(MDVector vector);

	@Override
	public int hashCode();

	public MDNumber norm();

	public MDNumber normSquare();

	public MDVector times(MDNumber c);

	public MDVector times(double c);

	public MDVector divide(MDNumber c);

	public MDVector divide(double c);

	public MDVector elementwiseTimes(MDVector vector);

	public MDVector elementwiseDivide(MDVector vector);

	public MDVector floor();

	public MDNumber cuboidVolume(MDVector vector);

	/**
	 * This method returns true only when all components are greater than the
	 * other vector
	 * 
	 * @param vector
	 * @return boolean for whether this vector is strictly greater than the
	 *         other
	 */
	public default boolean allComponentGreaterOrEqual(MDVector vector) {
		if (this.getDimension() != vector.getDimension())
			throw new IllegalArgumentException("The input vector has different dimension. Cannot compare");
		boolean result = true;
		for (int i = 0; i < vector.getDimension(); i++) {
			if (this.getCartesianComponent()[i].compareTo(vector.getCartesianComponent()[i]) < 0) {
				result = false;
				break;
			}
		}
		return result;
	}

	/**
	 * This method returns true only when all components are less than the other
	 * vector
	 * 
	 * @param vector
	 * @return boolean for whether this vector is strictly less than the other
	 */
	public default boolean allComponentLessOrEqual(MDVector vector) {
		if (this.getDimension() != vector.getDimension())
			throw new IllegalArgumentException("The input vector has different dimension. Cannot compare");
		boolean result = true;
		for (int i = 0; i < vector.getDimension(); i++) {
			if (this.getCartesianComponent()[i].compareTo(vector.getCartesianComponent()[i]) > 0) {
				result = false;
				break;
			}
		}
		return result;
	}

	public MDVector getVectorFromCartesianComps(MDNumber[] comps);

}
