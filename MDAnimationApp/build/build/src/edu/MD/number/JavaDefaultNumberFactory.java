package edu.MD.number;

public final class JavaDefaultNumberFactory extends NumberFactory {
	private static final JavaDefaultNumberFactory INSTANCE = new JavaDefaultNumberFactory();
	
	
	private JavaDefaultNumberFactory() {
	}

	@Override
	public MDNumber valueOf(double in) {
		return new JavaDefaultNumber(in);
	}
	
	public static JavaDefaultNumberFactory getInstance(){
		return INSTANCE;
	}
	
	public static void destroyInstance(){};
	
}
