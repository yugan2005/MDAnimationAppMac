package edu.MD.application;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import edu.MD.control.MDConfiguration;
import edu.MD.globalSetting.NumberFactorySetting;
import edu.MD.globalSetting.PBCBoundarySetting;
import edu.MD.initialization.MonatomicPositionInitializer;
import edu.MD.initialization.MonatomicVelocityInitializer;
import edu.MD.modeling.LJForceCalculator;
import edu.MD.modeling.PBCDistanceCalculator;
import edu.MD.modeling.VelocityCalculator;
import edu.MD.modeling.VelocityStandardizor;
import edu.MD.modeling.PositionCalculator;
import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;
import edu.MD.number.Vector3DCartesian;
import edu.MD.statThermodynamic.IDensityCalculator;
import edu.MD.statThermodynamic.MonatomicSysTemperatureCalculator;
import edu.MD.statThermodynamic.MonatomicYAxialTraditionalDensityCalculator;
import edu.MD.utility.MDConstants;

public class ArgonMDSimualtion implements MDSimulation {
	private int filmSize, filmThickness, vaporOneSideThickness, totalNumParticles, nDensityBin;
	private double temperature, timeStep, cutoff;
	private String name = "ARGON";
	private List<MDVector> positions, velocities, forces;
	private MDVector systemBoundary;
	private MonatomicPositionInitializer positionInitializer;
	private MonatomicVelocityInitializer velocityInitializer;
	private LJForceCalculator forceCalculator;
	private PositionCalculator positionCalculator;
	private VelocityCalculator velocityCalculator;
	private IDensityCalculator densityCalculator;
	private int step;

	public ArgonMDSimualtion() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		filmSize = 5;
		filmThickness = 3;
		vaporOneSideThickness = 3;
		temperature = 100;
		timeStep = 5e-14;
		cutoff = 3.0;
		nDensityBin = 150;
		initialization();
	}

	public ArgonMDSimualtion(MDConfiguration conf) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		filmSize = conf.getFilmSize();
		filmThickness = conf.getFilmThickness();
		vaporOneSideThickness = conf.getVaporOneSideThickness();
		temperature = conf.getTemperature();
		timeStep = conf.getTimeStep();
		cutoff = conf.getCutoff();
		nDensityBin = conf.getnDensityBin();
		initialization();
	}

	@Override
	public void initialization() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		NumberFactorySetting.set();

		String forceCalculatorType = name + "_" + name + "_" + String.valueOf(cutoff);
		String positionCalculatorType = name + "_" + String.valueOf(timeStep);
		String velocityCalculatorType = name + "_" + String.valueOf(timeStep / 2.0);
		positionInitializer = new MonatomicPositionInitializer(name, filmThickness, filmSize, vaporOneSideThickness,
				temperature);
		totalNumParticles = positionInitializer.getTotalNumberOfParticles();
		positions = positionInitializer.getPositions();
		systemBoundary = positionInitializer.getSystemBoundary();
		PBCBoundarySetting.set(systemBoundary);
		velocityInitializer = new MonatomicVelocityInitializer(name, totalNumParticles, temperature);
		velocities = velocityInitializer.getVelocities();
		forceCalculator = LJForceCalculator.getInstance(forceCalculatorType);
		positionCalculator = PositionCalculator.getInstance(positionCalculatorType);
		velocityCalculator = VelocityCalculator.getInstance(velocityCalculatorType);
		densityCalculator = MonatomicYAxialTraditionalDensityCalculator.getInstance(nDensityBin, systemBoundary);
		
		forces = new ArrayList<>(totalNumParticles);
		for (int i = 0; i < positions.size(); i++) {
			MDVector force = new Vector3DCartesian(0, 0, 0);
			for (int j = 0; j < positions.size(); j++) {
				if (i == j)
					continue;
				MDVector distance = PBCDistanceCalculator.calculate(positions.get(i), positions.get(j));
				force = force.plus(forceCalculator.calculate(distance));
			}
			forces.add(force);
		}
		step = 0;
	}

	@Override
	public void stepMove() {
		step++;
		// This is the Verlet algorithm
		List<MDVector> newPositions = new ArrayList<>(positions.size());
		for (int i = 0; i < positions.size(); i++) {
			newPositions.add(positionCalculator.calculate(positions.get(i), velocities.get(i), forces.get(i)));
		}
		positions = newPositions;
		
		List<MDVector> newVelocities = new ArrayList<>(velocities.size());
		for (int i = 0; i < velocities.size(); i++) {
			newVelocities.add(velocityCalculator.calculate(velocities.get(i), forces.get(i)));
		}
		
		List<MDVector> newForces = new ArrayList<>(forces.size());
		for (int i = 0; i < positions.size(); i++) {
			MDVector force = new Vector3DCartesian(0, 0, 0);
			for (int j = 0; j < positions.size(); j++) {
				if (i == j)
					continue;
				MDVector distance = PBCDistanceCalculator.calculate(positions.get(i), positions.get(j));
				force = force.plus(forceCalculator.calculate(distance));
			}
			newForces.add(force);
		}
		forces = newForces;
		
		velocities =  new ArrayList<>(newVelocities.size());
		for (int i = 0; i < newVelocities.size(); i++) {
			velocities.add(velocityCalculator.calculate(newVelocities.get(i), forces.get(i)));
		}
	}

	@Override
	public List<MDVector> getPostions() {
		return positions;
	}

	@Override
	public MDVector getSystemBoundary() {
		return systemBoundary;
	}

	@Override
	public int getParticleNumber() {
		return totalNumParticles;
	}

	@Override
	public List<List<MDNumber>> getDensityProfile() {
		return densityCalculator.calculate(positions);
	}

	@Override
	public double getVaporDensity() {
		return MDConstants.getMolarDensity(name, temperature, "vapor");
	}

	@Override
	public double getLiquidDensity() {
		return MDConstants.getMolarDensity(name, temperature, "liquid");
	}

	@Override
	public MDNumber getCalculatedTemperature() {
		return MonatomicSysTemperatureCalculator.calculate(velocities, name);
	}

	@Override
	public int getCurrentStep() {
		return step;
	}

	@Override
	public double getSystemTemperature() {
		return temperature;
	}

	@Override
	public double getTimeStepSize() {
		return timeStep;
	}

	@Override
	public double getMaxTemperature() {
		return MDConstants.getMaxTemperature(name);
	}

	@Override
	public double getMinTemperature() {
		return MDConstants.getMinTemperature(name);
	}

	@Override
	public void velocityControl() {
		velocities = VelocityStandardizor.calculate(velocities, name, temperature);
	}

}
