package edu.MD.application;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;

public interface MDSimulation {
	
	public void initialization() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;
	
	public void stepMove();
	
	public void velocityControl();
	
	public List<MDVector> getPostions();

	public MDVector getSystemBoundary();

	public int getParticleNumber();

	public List<List<MDNumber>> getDensityProfile();

	public double getVaporDensity();

	public double getLiquidDensity();

	public MDNumber getCalculatedTemperature();

	public int getCurrentStep();

	public double getSystemTemperature();

	public double getTimeStepSize();

	public double getMaxTemperature();

	public double getMinTemperature();
	
}
