package edu.MD.DAO;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import edu.MD.application.ArgonMDSimualtion;
import edu.MD.application.MDSimulation;
import edu.MD.control.MDConfiguration;
import edu.MD.control.MainApp;
import edu.MD.globalSetting.NumberFactorySetting;
import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;

public class MDDataObject {

	private MDSimulation simulation;
	private ScheduledService<double[][]> worker;
	private int particleNumber;
	private double scalingFactor;
	private double effectiveViewRatio = 1.3;
	private MainApp controller;
	private final int CONVERT_COORDINATE = 1;

	public MDDataObject(MDSimulation model) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		NumberFactorySetting.set();
		this.simulation = model;
		this.particleNumber = simulation.getParticleNumber();
		worker = new ScheduledService<double[][]>() {
			@Override
			protected Task<double[][]> createTask() {
				return new Task<double[][]>() {

					@Override
					protected double[][] call() throws Exception {
						simulation.stepMove();
						return getPositions();
					}

				};
			}

		};
		worker.setPeriod(Duration.millis(0));
	}

	public MDDataObject() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this(new ArgonMDSimualtion());
	}

	public MDDataObject(MDConfiguration conf) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this(new ArgonMDSimualtion(conf));

	}

	public double[] getUnscaledSystemBoundary() {
		MDNumber[] boundVector = simulation.getSystemBoundary().getCartesianComponent();
		double[] bound = new double[boundVector.length];
		for (int i = 0; i < bound.length; i++) {
			bound[i] = boundVector[(i + CONVERT_COORDINATE) % 3].toDouble();
		}
		return bound;
	}

	private double[][] getUnscaledPositions() {
		List<MDVector> positionVectors = simulation.getPostions();

		double[][] particlePositions = new double[positionVectors.get(0).getDimension()][particleNumber];
		for (int i = 0; i < particleNumber; i++) {
			MDNumber[] currentPosition = positionVectors.get(i).getCartesianComponent();
			for (int j = 0; j < currentPosition.length; j++) {
				particlePositions[j][i] = currentPosition[(j + CONVERT_COORDINATE) % 3].toDouble();
			}
		}

		return particlePositions;
	}

	public int getParticleNumber() {
		return particleNumber;
	}

	public Service<double[][]> getWorker() {
		return worker;
	}

	public double[] getSystemBoundary() {
		double simulationViewHeight = controller.getView().getSimulationSceneHeight();
		double simulationViewWidth = controller.getView().getSimulationSceneWidth();
		double[] unScaledBoundary = getUnscaledSystemBoundary();
		scalingFactor = Math.min(simulationViewWidth / unScaledBoundary[0], simulationViewHeight / unScaledBoundary[1])
				/ effectiveViewRatio;
		double[] scaledBoundary = new double[unScaledBoundary.length];
		for (int i = 0; i < scaledBoundary.length; i++) {
			scaledBoundary[i] = unScaledBoundary[i] * scalingFactor;
		}
		return scaledBoundary;
	}

	public double[][] getPositions() {
		double[][] unScaledPosition = getUnscaledPositions();
		double[][] scaledPosition = new double[unScaledPosition.length][unScaledPosition[0].length];
		for (int i = 0; i < unScaledPosition.length; i++) {
			for (int j = 0; j < unScaledPosition[0].length; j++) {
				scaledPosition[i][j] = unScaledPosition[i][j] * scalingFactor;
			}
		}
		return scaledPosition;
	}

	public void setController(MainApp mainApp) {
		this.controller = mainApp;
	}

	public double[][] getDensityProfile() {
		List<List<MDNumber>> densityProfileFromSimulation = simulation.getDensityProfile();
		double[][] densityProfile = new double[2][];
		double[] yPositions = new double[densityProfileFromSimulation.get(0).size()];
		double[] densities = new double[densityProfileFromSimulation.get(1).size()];
		for (int i = 0; i < yPositions.length; i++) {
			yPositions[i] = densityProfileFromSimulation.get(0).get(i).toDouble();
			densities[i] = densityProfileFromSimulation.get(1).get(i).toDouble();
		}
		densityProfile[0] = yPositions;
		densityProfile[1] = densities;
		return densityProfile;
	}

	public double getVaporDensity() {
		return simulation.getVaporDensity();
	}

	public double getLiquidDensity() {
		return simulation.getLiquidDensity();
	}

	public double getCalculatedTemperature() {
		return simulation.getCalculatedTemperature().toDouble();
	}

	public int getCurrentStep() {
		return simulation.getCurrentStep();
	}

	public double getSystemTemperature() {
		return simulation.getSystemTemperature();
	}

	public double getTimeStepSize() {
		return simulation.getTimeStepSize();
	}

	public double getMaxTemperature() {
		return simulation.getMaxTemperature();
	}

	public double getMinTemperature() {
		return simulation.getMinTemperature();
	}

	public void velocityControl() {
		simulation.velocityControl();
	}


}
