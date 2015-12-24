package edu.MD.number;

public class JavaDefaultNumber implements MDNumber {
	private final double num;
	public static final JavaDefaultNumber ONE = new JavaDefaultNumber(1);
	public static final JavaDefaultNumber ZERO = new JavaDefaultNumber(0);

	public JavaDefaultNumber(double in) {
		num = in;
	}

	public JavaDefaultNumber(JavaDefaultNumber in) {
		num = in.num;
	}

	@Override
	public MDNumber abs() {
		return new JavaDefaultNumber(Math.abs(num));
	}

	@Override
	public MDNumber sqrt() {
		return new JavaDefaultNumber(Math.sqrt(num));
	}

	@Override
	public MDNumber plus(MDNumber in) {
		JavaDefaultNumber javaDefaultNumberIn = checkinput(in);
		return new JavaDefaultNumber(num + javaDefaultNumberIn.num);
	}

	@Override
	public MDNumber minus(MDNumber in) {
		JavaDefaultNumber javaDefaultNumberIn = checkinput(in);
		return new JavaDefaultNumber(num - javaDefaultNumberIn.num);
	}

	@Override
	public MDNumber times(MDNumber in) {
		JavaDefaultNumber javaDefaultNumberIn = checkinput(in);
		return new JavaDefaultNumber(num * javaDefaultNumberIn.num);
	}

	@Override
	public MDNumber divide(MDNumber in) {
		JavaDefaultNumber javaDefaultNumberIn = checkinput(in);
		return new JavaDefaultNumber(num / javaDefaultNumberIn.num);
	}

	@Override
	public MDNumber pow(MDNumber in) {
		JavaDefaultNumber javaDefaultNumberIn = checkinput(in);
		return new JavaDefaultNumber(Math.pow(num, javaDefaultNumberIn.num));
	}

	@Override
	public MDNumber pow(int in) {
		if (in==-1){
			return new JavaDefaultNumber(1.0/this.num);
		}
		return new JavaDefaultNumber(Math.pow(num, in));
	}

	@Override
	public MDNumber pow(double in) {
		if (in==0.5) return this.sqrt();

		return new JavaDefaultNumber(Math.pow(num, in));
	}

	@Override
	public int getPrecision() {
		throw new UnsupportedOperationException("The Java Default Numbers do not support the getPrecision method");
	}

	private JavaDefaultNumber checkinput(MDNumber in) {
		if (!(in instanceof JavaDefaultNumber))
			throw new IllegalArgumentException("The number type is not compatible!");
		return (JavaDefaultNumber) in;
	}

	@Override
	public String toString() {
		return "" + num;
	}

	@Override
	public int hashCode() {
		return ((Double) num).hashCode();
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (this == that)
			return true;
		if (!(that instanceof JavaDefaultNumber))
			return false;
		JavaDefaultNumber input = (JavaDefaultNumber) that;
		return ((Double) num).equals(input.num);
	}

	@Override
	public double toDouble() {
		return num;
	}

	@Override
	public MDNumber zero() {
		return ZERO;
	}

	@Override
	public MDNumber one() {
		return ONE;
	}

	@Override
	public MDNumber plus(double in) {
		return new JavaDefaultNumber(num + in);
	}

	@Override
	public MDNumber minus(double in) {
		return new JavaDefaultNumber(num - in);
	}

	@Override
	public MDNumber times(double in) {
		return new JavaDefaultNumber(num * in);
	}

	@Override
	public MDNumber divide(double in) {
		return new JavaDefaultNumber(num / in);
	}

	@Override
	public MDNumber floor() {
		return new JavaDefaultNumber(Math.floor(this.toDouble()));
	}

	@Override
	public int floorToInt() {
		return (int) (Math.floor(this.toDouble()));
	}

	@Override
	public int round() {
		return (int) (Math.round(this.toDouble()));
	}

	@Override
	public int compareTo(MDNumber in) {
		JavaDefaultNumber javaDefaultNumberIn = checkinput(in);
		return ((Double) this.num).compareTo(javaDefaultNumberIn.num);
	}

	@Override
	public MDNumber mod(MDNumber in) {
		JavaDefaultNumber javaDefaultNumberIn = checkinput(in);
		double thisDouble = this.num;
		double inDouble = javaDefaultNumberIn.num;
		return new JavaDefaultNumber((thisDouble % inDouble + inDouble) % inDouble);
	}

}
