package edu.MD.number;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Vector3DCartesian implements MDVector {
	private static final List<String> SUPPORTED_VECTOR_TYPE = new ArrayList<String>(Arrays.asList("Vector3DCartesian"));
	private final MDNumber compX, compY, compZ;
	private final MDNumber[] cartesianCoordinates;

	public Vector3DCartesian(MDNumber x, MDNumber y, MDNumber z) {
		compX = x;
		compY = y;
		compZ = z;
		cartesianCoordinates = new MDNumber[] { compX, compY, compZ };
	}

	public Vector3DCartesian(double x, double y, double z) {
		this(NumberFactory.getInstance().valueOf(x), NumberFactory.getInstance().valueOf(y), NumberFactory.getInstance().valueOf(z));
	}

	public Vector3DCartesian(MDNumber x, double y, double z) {
		this(x, NumberFactory.getInstance().valueOf(y), NumberFactory.getInstance().valueOf(z));
	}

	public Vector3DCartesian(double x, MDNumber y, double z) {
		this(NumberFactory.getInstance().valueOf(x), y, NumberFactory.getInstance().valueOf(z));
	}

	public Vector3DCartesian(double x, double y, MDNumber z) {
		this(NumberFactory.getInstance().valueOf(x), NumberFactory.getInstance().valueOf(y), z);
	}

	public Vector3DCartesian(MDNumber x, MDNumber y, double z) {
		this(x, y, NumberFactory.getInstance().valueOf(z));
	}

	public Vector3DCartesian(MDNumber x, double y, MDNumber z) {
		this(x, NumberFactory.getInstance().valueOf(y), z);
	}

	public Vector3DCartesian(double x, MDNumber y, MDNumber z) {
		this(NumberFactory.getInstance().valueOf(x), y, z);
	}

	public Vector3DCartesian(MDNumber[] corrdinates) {
		if (corrdinates.length != 3)
			throw new IllegalArgumentException("The corrdinates is of wrong dimensions");
		compX = corrdinates[0];
		compY = corrdinates[1];
		compZ = corrdinates[2];
		cartesianCoordinates = new MDNumber[] { compX, compY, compZ };
	}

	@Override
	public MDNumber[] getCartesianComponent() {
		return cartesianCoordinates;
	}

	@Override
	public MDVector plus(MDVector vector) {
		checkDimension(vector);
		checkClass(vector);
		Vector3DCartesian that = castToVector3DCartesian(vector);
		return addVector3DCartesian(that);
	}

	private Vector3DCartesian castToVector3DCartesian(MDVector vector) {
		switch (vector.getClass().getSimpleName()) {
		case "Vector3DCartesian":
			return (Vector3DCartesian) vector;
		}
		return null;
	}

	@Override
	public MDNumber getCartesianDistance(MDVector vector) {
		checkDimension(vector);
		checkClass(vector);
		Vector3DCartesian that = castToVector3DCartesian(vector);
		return this.minus(that).norm();
	}

	@Override
	public MDVector minus(MDVector vector) {
		checkDimension(vector);
		checkClass(vector);
		Vector3DCartesian that = castToVector3DCartesian(vector);
		return subtractVector3DCartesian(that);
	}

	@Override
	public int getDimension() {
		return 3;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof MDVector))
			return false;

		MDVector vector = (MDVector) obj;

		if (getDimension() != vector.getDimension())
			return false;
		if (!SUPPORTED_VECTOR_TYPE.contains(vector.getClass().getSimpleName()))
			return false;

		Vector3DCartesian that = castToVector3DCartesian(vector);
		return equals(that);
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(cartesianCoordinates);
	}

	private Vector3DCartesian addVector3DCartesian(Vector3DCartesian vector) {
		MDNumber x = cartesianCoordinates[0].plus(vector.getCartesianComponent()[0]);
		MDNumber y = cartesianCoordinates[1].plus(vector.getCartesianComponent()[1]);
		MDNumber z = cartesianCoordinates[2].plus(vector.getCartesianComponent()[2]);
		return new Vector3DCartesian(x, y, z);
	}

	private MDVector subtractVector3DCartesian(Vector3DCartesian vector) {
		MDNumber x = cartesianCoordinates[0].minus(vector.getCartesianComponent()[0]);
		MDNumber y = cartesianCoordinates[1].minus(vector.getCartesianComponent()[1]);
		MDNumber z = cartesianCoordinates[2].minus(vector.getCartesianComponent()[2]);
		return new Vector3DCartesian(x, y, z);
	}

	private boolean equals(Vector3DCartesian vector) {
		MDNumber[] coord1 = getCartesianComponent();
		MDNumber[] coord2 = vector.getCartesianComponent();
		for (int i = 0; i < coord1.length; i++) {
			if (!coord1[i].approximateEqual(coord2[i]))
				return false;
		}
		return true;

	}

	private void checkDimension(MDVector vector) {
		if (vector.getDimension() != this.getDimension())
			throw new IllegalArgumentException(
					"The two vectors have different dimensions, cannot have vector operation");
	}

	private void checkClass(MDVector vector) {
		if (!SUPPORTED_VECTOR_TYPE.contains(vector.getClass().getSimpleName()))
			throw new IllegalArgumentException(
					vector.getClass().getSimpleName() + " is not supported for vector operation.");
	}

	@Override
	public MDNumber normSquare() {
		MDNumber normSquare = NumberFactory.getInstance().valueOf(0);
		for (int i = 0; i < 3; i++)
			normSquare = normSquare.plus(cartesianCoordinates[i].times(cartesianCoordinates[i]));
		return normSquare;
	}

	@Override
	public MDNumber norm() {
		return normSquare().sqrt();
	}

	@Override
	public MDVector times(MDNumber c) {
		MDNumber x = cartesianCoordinates[0].times(c);
		MDNumber y = cartesianCoordinates[1].times(c);
		MDNumber z = cartesianCoordinates[2].times(c);
		return new Vector3DCartesian(x, y, z);
	}

	@Override
	public MDVector plus(MDNumber c) {
		MDNumber x = cartesianCoordinates[0].plus(c);
		MDNumber y = cartesianCoordinates[1].plus(c);
		MDNumber z = cartesianCoordinates[2].plus(c);
		return new Vector3DCartesian(x, y, z);
	}

	@Override
	public MDVector plus(double c) {
		MDNumber x = cartesianCoordinates[0].plus(c);
		MDNumber y = cartesianCoordinates[1].plus(c);
		MDNumber z = cartesianCoordinates[2].plus(c);
		return new Vector3DCartesian(x, y, z);
	}

	@Override
	public MDVector elementwiseTimes(MDVector vector) {
		checkDimension(vector);
		checkClass(vector);
		Vector3DCartesian that = castToVector3DCartesian(vector);
		MDNumber x = cartesianCoordinates[0].times(that.getCartesianComponent()[0]);
		MDNumber y = cartesianCoordinates[1].times(that.getCartesianComponent()[1]);
		MDNumber z = cartesianCoordinates[2].times(that.getCartesianComponent()[2]);
		return new Vector3DCartesian(x, y, z);
	}

	@Override
	public MDVector elementwiseDivide(MDVector vector) {
		checkDimension(vector);
		checkClass(vector);
		Vector3DCartesian that = castToVector3DCartesian(vector);
		MDNumber x = cartesianCoordinates[0].divide(that.getCartesianComponent()[0]);
		MDNumber y = cartesianCoordinates[1].divide(that.getCartesianComponent()[1]);
		MDNumber z = cartesianCoordinates[2].divide(that.getCartesianComponent()[2]);
		return new Vector3DCartesian(x, y, z);
	}

	@Override
	public MDVector floor() {
		MDNumber x = NumberFactory.getInstance().valueOf(Math.floor(cartesianCoordinates[0].toDouble()));
		MDNumber y = NumberFactory.getInstance().valueOf(Math.floor(cartesianCoordinates[1].toDouble()));
		MDNumber z = NumberFactory.getInstance().valueOf(Math.floor(cartesianCoordinates[2].toDouble()));
		return new Vector3DCartesian(x, y, z);
	}

	@Override
	public MDVector minus(double c) {
		MDNumber x = cartesianCoordinates[0].minus(c);
		MDNumber y = cartesianCoordinates[1].minus(c);
		MDNumber z = cartesianCoordinates[2].minus(c);
		return new Vector3DCartesian(x, y, z);
	}

	@Override
	public MDVector times(double c) {
		MDNumber x = cartesianCoordinates[0].times(c);
		MDNumber y = cartesianCoordinates[1].times(c);
		MDNumber z = cartesianCoordinates[2].times(c);
		return new Vector3DCartesian(x, y, z);
	}

	@Override
	public MDVector divide(MDNumber c) {
		MDNumber x = cartesianCoordinates[0].divide(c);
		MDNumber y = cartesianCoordinates[1].divide(c);
		MDNumber z = cartesianCoordinates[2].divide(c);
		return new Vector3DCartesian(x, y, z);
	}

	@Override
	public MDVector divide(double c) {
		MDNumber x = cartesianCoordinates[0].divide(c);
		MDNumber y = cartesianCoordinates[1].divide(c);
		MDNumber z = cartesianCoordinates[2].divide(c);
		return new Vector3DCartesian(x, y, z);
	}

	@Override
	public boolean approximateEqual(MDVector vector) {
		checkDimension(vector);
		checkClass(vector);
		Vector3DCartesian that = castToVector3DCartesian(vector);
		for (int i = 0; i < that.getDimension(); i++) {
			if (!this.cartesianCoordinates[i].approximateEqual(that.cartesianCoordinates[i]))
				return false;
		}
		return true;
	}

	@Override
	public MDNumber cuboidVolume(MDVector vector) {
		checkDimension(vector);
		checkClass(vector);
		Vector3DCartesian that = castToVector3DCartesian(vector);
		MDVector diagonalVect = that.minus(this);
		MDNumber result = diagonalVect.getCartesianComponent()[0];
		for (int i = 1; i < diagonalVect.getDimension(); i++)
			result = result.times(diagonalVect.getCartesianComponent()[i]);
		result = result.abs();
		return result;
	}

	@Override
	public MDVector getVectorFromCartesianComps(MDNumber[] comps) {
		if (comps.length!=3) throw new UnsupportedOperationException("This is not a 3D Cartesian Vector");
		
		return new Vector3DCartesian(comps);
	}

}
