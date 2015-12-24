package edu.MD.number;

import edu.MD.utility.MDConstants;

public interface MDNumber extends Comparable<MDNumber>{

	public MDNumber abs();

	public MDNumber sqrt();

	public MDNumber plus(MDNumber in);

	public MDNumber plus(double in);

	public MDNumber minus(MDNumber in);

	public MDNumber minus(double in);

	public MDNumber times(MDNumber in);

	public MDNumber times(double in);

	public MDNumber divide(MDNumber in);

	public MDNumber divide(double in);

	public MDNumber pow(MDNumber in);

	public MDNumber pow(int in);
	
	public MDNumber pow(double in);

	public MDNumber zero();

	public MDNumber one();

	public int getPrecision();

	@Override
	public String toString();

	@Override
	public boolean equals(Object that);

	@Override
	public int hashCode();

	public double toDouble();

	/**
	 * Use Constants.RELATIVE_DOUBLE_ERROR to decide whether they are equal
	 */
	public default boolean approximateEqual(double in) {
		if (in == 0)
			return Math.abs(this.toDouble()) < MDConstants.MACHINE_DOUBLE_ERROR;
		return Math.abs(this.divide(in).minus(1).toDouble()) < MDConstants.RELATIVE_DOUBLE_ERROR;
	}

	/**
	 * Use Constants.RELATIVE_DOUBLE_ERROR to decide whether they are equal
	 */
	public default boolean approximateEqual(MDNumber in) {
		if (in.toDouble() == 0)
			return Math.abs(this.toDouble()) < MDConstants.RELATIVE_DOUBLE_ERROR;
		return Math.abs(this.divide(in).minus(1).toDouble()) < MDConstants.RELATIVE_DOUBLE_ERROR;

	}

	public MDNumber floor();
	
	public int floorToInt();
	
	public int round();
	
	public int compareTo(MDNumber in);
	
	/**
	 * This should always give the positive result in the range between 0 and in
	 * @param in
	 * @return this % in
	 */
	public MDNumber mod(MDNumber in);
}
