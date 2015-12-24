package edu.MD.view;

import java.util.Arrays;
import java.util.List;
import edu.MD.control.MainApp;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.StringConverter;

public class RootPaneView {

	private static final double WIDTH = 1000;
	private static final double ROOTPANE_HEIGHT = 800;
	private static final double SIMULATION_PANE_HEIGHT = 500;
	private static final double BUTTON_BAR_HEIGHT = 40;

	private static final double SPHERE_SIZE = 4;
	private static final int SPHERE_DIV = 64;

	private static final double LINE_SIZE = 2;

	private static final int E_DISPLAY_STEP = 1000;

	// private static final double AXIS_LENGTH = 20;

	private static final Rotate CAMERA_DEFAULT_ROT = new Rotate(5, new Point3D(1, 1, 0));
	private static final double CHART_AXIS_MARGIN = 0.2;

	@FXML
	private SplitPane rootPane;

	@FXML
	private AnchorPane simulationPane;

	@FXML
	private AnchorPane chartPane;

	@FXML
	private ButtonBar buttonBar;

	@FXML
	private Button startButton;

	@FXML
	private Button pauseButton;

	@FXML
	private Button defaulViewButton;

	@FXML
	private Button densityButton;

	@FXML
	private Button settingButton;

	@FXML
	private Button kineticEnergyButton;
	
	@FXML
	private Button velocityRescaleButton;

	@FXML
	private HBox statusBox;

	@FXML
	private Label totalNumberLabel;

	@FXML
	private Label currentStepLabel;

	@FXML
	private Label initialTempLabel;

	@FXML
	private Label currentTempLabel;

	@FXML
	private Label TimeStepSizeLabel;

	private LineChart<Number, Number> densityChart;

	private LineChart<Number, Number> energyChart;

	private SubScene simulationScene;

	private Sphere[] particles;

	private double[] systemBounday;

	private double[][] positions;

	private int numOfParticles;

	private Group simulationGroup;

	private ParallelCamera simulationCamera;

	private MainApp controller;

	double simulationSceneWidth, simulationSceneHeight;

	private Rotate cameraRotX;
	private Rotate cameraRotY;
	private DoubleProperty cameraRotateAngleX = new SimpleDoubleProperty(0);
	private DoubleProperty cameraRotateAngleY = new SimpleDoubleProperty(0);
	private double anchorX, anchorY, anchorAngleX, anchorAngleY;

	private double[] densityProfile;
	private double[] densityProfileLocation;
	private XYChart.Series<Number, Number> densitySeries;

	private IntegerProperty energyChartXUpperBoundProperty;
	private IntegerProperty energyChartXLowerBoundProperty;
	private ObservableList<XYChart.Data<Number, Number>> energySeriesData;
	private DoubleProperty systemTemperature;

	private IntegerProperty currentStep;
	private DoubleProperty calculatedTemperature;

	public RootPaneView() {
	}

	@FXML
	private void initialize() {
		rootPane.setPrefHeight(ROOTPANE_HEIGHT);
		rootPane.setPrefWidth(WIDTH);
		simulationPane.setPrefHeight(SIMULATION_PANE_HEIGHT);
		simulationPane.setPrefWidth(WIDTH);
		buttonBar.setPrefHeight(BUTTON_BAR_HEIGHT);
		// obtain data from the FXML view for constructing the subscene and
		// center the group;
		simulationSceneWidth = simulationPane.getPrefWidth();
		simulationSceneHeight = simulationPane.getPrefHeight() - buttonBar.getPrefHeight()
				- AnchorPane.getBottomAnchor(buttonBar) - 5;
	}

	public void setView(MainApp mainApp) {
		this.controller = mainApp;

		// ***
		// This part builds up the simulation animation pane (top pane)

		// obtain data from model
		systemBounday = controller.getSystemBoundary();
		positions = controller.getPositions();
		numOfParticles = controller.getParticleNumber();
		currentStep = new SimpleIntegerProperty(controller.getCurrentStep());
		calculatedTemperature =new SimpleDoubleProperty(controller.getCalculatedTemperature());

		// building the Group as the root of subscene
		simulationGroup = new Group();

		// Visualize the simulation boundary
		Cylinder[] simulationBoundayBox = getSimulationBoundaryBox(systemBounday[0], systemBounday[1],
				systemBounday[2]);
		simulationGroup.getChildren().addAll(simulationBoundayBox);

		// // Add the axes to identify x,y,z directions at the bottom of
		// simulation
		// // boundary
		// Cylinder[] axes = getAxes(new Point3D(systemBounday[0] * 1.1,
		// systemBounday[1] * 1.1, 0));
		// simulationGroup.getChildren().addAll(axes);

		// Add the particles
		particles = new Sphere[numOfParticles];
		for (int i = 0; i < numOfParticles; i++) {
			particles[i] = new Sphere(SPHERE_SIZE, SPHERE_DIV);
			particles[i].setTranslateX(positions[0][i]);
			particles[i].setTranslateY(positions[1][i]);
			particles[i].setTranslateZ(positions[2][i]);
		}
		simulationGroup.getChildren().addAll(particles);

		simulationScene = new SubScene(simulationGroup, simulationSceneWidth, simulationSceneHeight, true,
				SceneAntialiasing.BALANCED);

		// move Group to the center of the scene
		Bounds simulationGroupBounds = simulationGroup.getLayoutBounds();

		double centerX = (simulationSceneWidth - simulationGroupBounds.getWidth()) / 2
				- simulationGroupBounds.getMinX();
		double centerY = (simulationSceneHeight - simulationGroupBounds.getHeight()) / 2
				- simulationGroupBounds.getMinY();
		double moveZ = 1 - simulationGroupBounds.getMinX(); // for putting the
															// camera
		simulationGroup.setTranslateX(centerX);
		simulationGroup.setTranslateY(centerY);
		simulationGroup.setTranslateZ(moveZ);

		// setting the camera
		simulationCamera = new ParallelCamera();
		simulationCamera.getTransforms().add(CAMERA_DEFAULT_ROT);

		cameraRotX = new Rotate();
		cameraRotX.setAxis(Rotate.X_AXIS);
		cameraRotX.angleProperty().bind(cameraRotateAngleX);

		cameraRotY = new Rotate();
		cameraRotY.setAxis(Rotate.Y_AXIS);
		cameraRotY.angleProperty().bind(cameraRotateAngleY);

		simulationCamera.getTransforms().addAll(cameraRotX, cameraRotY);

		simulationScene.setCamera(simulationCamera);
		simulationScene.setFill(Color.LIGHTGREY); // strange need this fill to
													// catch mouse event

		// fix the scene on the simulationPane
		simulationPane.getChildren().add(simulationScene);
		AnchorPane.setTopAnchor(simulationScene, 0.0);
		AnchorPane.setLeftAnchor(simulationScene, 0.0);
		AnchorPane.setRightAnchor(simulationScene, 0.0);
		pauseButton.setDisable(true);

		// ***
		// This part builds up the statisticl chart pane (bottom pane)
		setupDensityChart();
		setupEnergyChart();
		chartPane.getChildren().add(densityChart);

		// ***
		// This part builds up the status labels (bottom pane)
		setupStatusLabel();
		AnchorPane.setLeftAnchor(statusBox, 0.0);
		AnchorPane.setRightAnchor(statusBox, 0.0);
		AnchorPane.setBottomAnchor(statusBox, 0.0);

		hookupViewEvents();

	}

	private void setupStatusLabel() {
		totalNumberLabel.setText("Tot#Particle: " + numOfParticles + ";");
		initialTempLabel.setText(String.format("Inital Temperature: %.2f K", controller.getConfiguration().getTemperature()));
		TimeStepSizeLabel.setText(
				String.format("TimeStep: %d e-15Sec;", Math.round(controller.getTimeStepSize() * 1e15)));
		StringBinding currentStepBinding = new StringBinding() {

			{
				super.bind(currentStep);
			}

			@Override
			protected String computeValue() {
				return "CurrentStep#: " + currentStep.get() + ";";
			}
		};
		currentStepLabel.textProperty().bind(currentStepBinding);

		StringBinding calculatedTemperatureBinding = new StringBinding() {

			{
				super.bind(calculatedTemperature);
			}

			@Override
			protected String computeValue() {
				return String.format("InstantTemp: %.2f K;", calculatedTemperature.get());
			}
		};
		currentTempLabel.textProperty().bind(calculatedTemperatureBinding);

	}

	private void setupEnergyChart() {

		// x axis
		NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel("Simulation Step");
		xAxis.setAutoRanging(false);
		int xTickUnit = E_DISPLAY_STEP / 10;

		energyChartXUpperBoundProperty = new SimpleIntegerProperty(E_DISPLAY_STEP);
		energyChartXLowerBoundProperty = new SimpleIntegerProperty(0);

		xAxis.upperBoundProperty().bind(energyChartXUpperBoundProperty);
		xAxis.lowerBoundProperty().bind(energyChartXLowerBoundProperty);
		xAxis.setTickUnit(xTickUnit);
		xAxis.setSide(Side.LEFT);

		// y axis
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Temperature (Total Kinetic Energy) variation (K)");
		yAxis.setAutoRanging(false);
		double yUpperBound = controller.getSystemTemperature() * (1 + CHART_AXIS_MARGIN);
		double yLowerBound = 0;
		double yTickUnit = yUpperBound / 5;
		yAxis.setUpperBound(yUpperBound);
		yAxis.setLowerBound(yLowerBound);
		yAxis.setTickUnit(yTickUnit);
		yAxis.setSide(Side.BOTTOM);
		yAxis.setTickLabelFormatter(new StringConverter<Number>() {

			@Override
			public String toString(Number value) {
				double v = (double) value;
				return String.format("%.2f", v);
			}

			@Override
			public Number fromString(String string) {
				double v = Double.parseDouble(string);
				return v;
			}
		});

		// chart data

		energySeriesData = FXCollections.<XYChart.Data<Number, Number>> observableArrayList();
		XYChart.Series<Number, Number> energySeries = new XYChart.Series<>();
		energySeries.setData(energySeriesData);

		systemTemperature = new SimpleDoubleProperty();
		systemTemperature.set(controller.getSystemTemperature());

		XYChart.Data<Number, Number> leftMarker = new XYChart.Data<>();
		leftMarker.XValueProperty().bind(energyChartXLowerBoundProperty);
		leftMarker.YValueProperty().bind(systemTemperature);

		XYChart.Data<Number, Number> rightMarker = new XYChart.Data<>();
		rightMarker.XValueProperty().bind(energyChartXUpperBoundProperty);
		rightMarker.YValueProperty().bind(systemTemperature);

		ObservableList<XYChart.Data<Number, Number>> energyMarkerSeriesData = FXCollections.<XYChart
				.Data<Number, Number>> observableArrayList();
		energyMarkerSeriesData.add(leftMarker);
		energyMarkerSeriesData.add(rightMarker);

		XYChart.Series<Number, Number> energyMarkerSeries = new XYChart.Series<>();
		energyMarkerSeries.setData(energyMarkerSeriesData);

		ObservableList<XYChart.Series<Number, Number>> energyChartData = FXCollections.<XYChart
				.Series<Number, Number>> observableArrayList();
		energyChartData.add(energySeries);
		energyChartData.add(energyMarkerSeries);

		// create chart
		energyChart = new LineChart<>(xAxis, yAxis);
		energyChart.setTitle("T (Kinetic Energy) fluctuation with simulation steps");
		energyChart.setData(energyChartData);

		// chart display

		energyChart.setCreateSymbols(false);
		double chartHeight = chartPane.getPrefHeight() - 30;
		double chartWidth = chartPane.getPrefWidth();
		energyChart.setPrefHeight(chartHeight);
		energyChart.setPrefWidth(chartWidth);
		energyChart.setVerticalZeroLineVisible(false);
		energyChart.setHorizontalGridLinesVisible(false);
		energyChart.setLegendVisible(false);
		energyChart.setHorizontalGridLinesVisible(false);
		energyChart.setVerticalGridLinesVisible(false);
		energyChart.setAnimated(false);
	}

	private void setupDensityChart() {
		double[][] densityProfileAlongY = controller.getDensityProfile();
		densityProfile = densityProfileAlongY[1];
		densityProfileLocation = densityProfileAlongY[0];

		// build density profile chart
		densitySeries = new XYChart.Series<>();
		for (int i = 0; i < densityProfile.length; i++) {
			densitySeries.getData().add(new XYChart.Data<>(densityProfileLocation[i], densityProfile[i]));
		}
		NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel("Axial Location (nm)");
		xAxis.setAutoRanging(false);
		double xUpperBound = controller.getUnscaledSystemBoundary()[0] * (1 + 0.1 * CHART_AXIS_MARGIN);
		double xLowerBound = controller.getUnscaledSystemBoundary()[0] * (-0.1 * CHART_AXIS_MARGIN);
		double xTickUnit = xUpperBound / 10;
		xAxis.setUpperBound(xUpperBound);
		xAxis.setLowerBound(xLowerBound);
		xAxis.setTickUnit(xTickUnit);
		xAxis.setSide(Side.LEFT);
		xAxis.setTickLabelFormatter(new StringConverter<Number>() {

			@Override
			public String toString(Number value) {
				double v = (double) value;
				double vInUnitNM = v * 1e9;
				return String.format("%.2f", vInUnitNM);
			}

			@Override
			public Number fromString(String string) {
				double vInUnitNM = Double.parseDouble(string);
				double v = vInUnitNM * 1e-9;
				return v;
			}
		});
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Molar Density (1e4#/m3)");
		yAxis.setAutoRanging(false);
		double yUpperBound = controller.getLiquidDensity() * (1 + CHART_AXIS_MARGIN);
		double yLowerBound = 0;
		double yTickUnit = yUpperBound / 5;
		yAxis.setUpperBound(yUpperBound);
		yAxis.setLowerBound(yLowerBound);
		yAxis.setTickUnit(yTickUnit);
		yAxis.setSide(Side.BOTTOM);
		yAxis.setTickLabelFormatter(new StringConverter<Number>() {

			@Override
			public String toString(Number value) {
				double v = (double) value;
				double vInUnit = v * 1e-4;
				return String.format("%.2f", vInUnit);
			}

			@Override
			public Number fromString(String string) {
				double vInUnitNM = Double.parseDouble(string);
				double v = vInUnitNM * 1e4;
				return v;
			}
		});

		densityChart = new LineChart<>(xAxis, yAxis);
		densityChart.setTitle("Density fluctuation along axial direction");
		ObservableList<XYChart.Series<Number, Number>> densityData = FXCollections.<XYChart
				.Series<Number, Number>> observableArrayList();
		densityData.add(densitySeries);
		densityChart.setData(densityData);
		densityChart.setCreateSymbols(false);
		double chartHeight = chartPane.getPrefHeight() - 30;
		double chartWidth = chartPane.getPrefWidth();
		densityChart.setPrefHeight(chartHeight);
		densityChart.setPrefWidth(chartWidth);
		densityChart.setVerticalZeroLineVisible(false);
		densityChart.setHorizontalGridLinesVisible(false);
		densityChart.setLegendVisible(false);
		densityChart.setHorizontalGridLinesVisible(false);
		densityChart.setVerticalGridLinesVisible(false);
		densityChart.setAnimated(false);

		addMarkerSeries(densityChart, "horizontal", xLowerBound, xUpperBound, controller.getVaporDensity(),
				"dashline-g");
		addMarkerSeries(densityChart, "horizontal", xLowerBound, xUpperBound, controller.getLiquidDensity(),
				"dashline-b");
		// addMarkerSeries(densityChart, "vertical", yLowerBound, yUpperBound,
		// 0, "dashline-k");
		// addMarkerSeries(densityChart, "vertical", yLowerBound, yUpperBound,
		// controller.getUnscaledSystemBoundary()[0],
		// "dashLine-k");
		addMarkerSeries(densityChart, "vertical", yLowerBound, yUpperBound,
				controller.getUnscaledSystemBoundary()[0] / 2, "centerdashline-k");
	}

	private void hookupViewEvents() {

		simulationScene.setOnMousePressed((MouseEvent event) -> {
			anchorX = event.getSceneX();
			anchorY = event.getSceneY();
			anchorAngleX = cameraRotateAngleX.get();
			anchorAngleY = cameraRotateAngleY.get();

		});

		simulationScene.setOnMouseDragged((MouseEvent event) -> {
			cameraRotateAngleX.set(anchorAngleX - (anchorY - event.getSceneY()) / 10);
			cameraRotateAngleY.set(anchorAngleY + (anchorX - event.getSceneX()) / 10);
		});

		defaulViewButton.setOnAction(actionEvent -> {
			cameraRotateAngleX.set(0);
			cameraRotateAngleY.set(0);
		});

		densityButton.setOnAction(actionEvent -> {
			chartPane.getChildren().clear();
			chartPane.getChildren().add(densityChart);
			chartPane.getChildren().add(statusBox);
		});

		kineticEnergyButton.setOnAction(actionEvent -> {
			chartPane.getChildren().clear();
			chartPane.getChildren().add(energyChart);
			chartPane.getChildren().add(statusBox);
		});
	}

	private void addMarkerSeries(LineChart<Number, Number> chart, String direction, double lowerBound,
			double upperBound, double value, String lineType) {
		XYChart.Data<Number, Number> lowPoint;
		XYChart.Data<Number, Number> highPoint;
		if (direction.equals("horizontal")) {
			lowPoint = new XYChart.Data<>(lowerBound, value);
			highPoint = new XYChart.Data<>(upperBound, value);
		} else {
			lowPoint = new XYChart.Data<>(value, lowerBound);
			highPoint = new XYChart.Data<>(value, upperBound);
		}
		ObservableList<XYChart.Data<Number, Number>> marker = FXCollections.observableArrayList();
		marker.add(lowPoint);
		marker.add(highPoint);
		XYChart.Series<Number, Number> markerSeries = new XYChart.Series<>();
		markerSeries.setData(marker);
		chart.getData().add(markerSeries);

		List<String> specs = Arrays.<String> asList(lineType.trim().toLowerCase().split("-"));
		StringBuilder styleSB = new StringBuilder();
		if (specs.contains("centerdashline")) {
			styleSB.append("-fx-stroke-dash-array: 25 20 5 20;");
		}

		if (specs.contains("dashline")) {
			styleSB.append("-fx-stroke-dash-array: 25 10;");
		}

		if (specs.contains("b")) {
			styleSB.append("-fx-stroke: blue;");
		}

		if (specs.contains("k")) {
			styleSB.append("-fx-stroke: black;");
		}

		if (specs.contains("r")) {
			styleSB.append("-fx-stroke: red;");
		}

		if (specs.contains("g")) {
			styleSB.append("-fx-stroke: green;");
		}

		markerSeries.getNode().setStyle(styleSB.toString());

	}

	// private Cylinder[] getAxes(Point3D origin) {
	//
	// final PhongMaterial redMaterial = new PhongMaterial();
	// redMaterial.setDiffuseColor(Color.DARKRED);
	// redMaterial.setSpecularColor(Color.RED);
	//
	// final PhongMaterial greenMaterial = new PhongMaterial();
	// greenMaterial.setDiffuseColor(Color.DARKGREEN);
	// greenMaterial.setSpecularColor(Color.GREEN);
	//
	// final PhongMaterial blueMaterial = new PhongMaterial();
	// blueMaterial.setDiffuseColor(Color.DARKBLUE);
	// blueMaterial.setSpecularColor(Color.BLUE);
	//
	// Cylinder[] axes = new Cylinder[3];
	// axes[0] = build3DLine(origin, new Point3D(origin.getX() + AXIS_LENGTH,
	// origin.getY(), origin.getZ()));
	// axes[0].setMaterial(redMaterial);
	// axes[1] = build3DLine(origin, new Point3D(origin.getX(), origin.getY() +
	// AXIS_LENGTH, origin.getZ()));
	// axes[1].setMaterial(greenMaterial);
	// axes[2] = build3DLine(origin, new Point3D(origin.getX(), origin.getY(),
	// origin.getZ() + AXIS_LENGTH));
	// axes[2].setMaterial(blueMaterial);
	// return axes;
	// }

	private Cylinder build3DLine(Point3D origin, Point3D target) {
		// took from http://netzwerg.ch/blog/2015/03/22/javafx-3d-line/

		Point3D yAxis = new Point3D(0, 1, 0);
		Point3D diff = target.subtract(origin);
		double height = diff.magnitude();

		Point3D mid = target.midpoint(origin);
		Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

		Point3D axisOfRotation = diff.crossProduct(yAxis);
		double angle = Math.acos(diff.normalize().dotProduct(yAxis));
		Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

		Cylinder line = new Cylinder(LINE_SIZE, height);

		line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

		final PhongMaterial grayMaterial = new PhongMaterial();
		grayMaterial.setDiffuseColor(Color.LIGHTGRAY);
		grayMaterial.setSpecularColor(Color.LIGHTGRAY);

		line.setMaterial(grayMaterial);

		return line;
	}

	private Cylinder[] getSimulationBoundaryBox(double x, double y, double z) {
		Cylinder[] boundaryBox = new Cylinder[12];

		boundaryBox[0] = build3DLine(new Point3D(0, 0, 0), new Point3D(x, 0, 0));
		boundaryBox[1] = build3DLine(new Point3D(0, 0, 0), new Point3D(0, y, 0));
		boundaryBox[2] = build3DLine(new Point3D(x, 0, 0), new Point3D(x, y, 0));
		boundaryBox[3] = build3DLine(new Point3D(0, y, 0), new Point3D(x, y, 0));

		boundaryBox[4] = build3DLine(new Point3D(0, 0, z), new Point3D(x, 0, z));
		boundaryBox[5] = build3DLine(new Point3D(0, 0, z), new Point3D(0, y, z));
		boundaryBox[6] = build3DLine(new Point3D(x, 0, z), new Point3D(x, y, z));
		boundaryBox[7] = build3DLine(new Point3D(0, y, z), new Point3D(x, y, z));

		boundaryBox[8] = build3DLine(new Point3D(0, 0, 0), new Point3D(0, 0, z));
		boundaryBox[9] = build3DLine(new Point3D(x, 0, 0), new Point3D(x, 0, z));
		boundaryBox[10] = build3DLine(new Point3D(0, y, 0), new Point3D(0, y, z));
		boundaryBox[11] = build3DLine(new Point3D(x, y, 0), new Point3D(x, y, z));

		return boundaryBox;
	}

	public Button getStartButton() {
		return startButton;
	}

	public Button getPauseButton() {
		return pauseButton;
	}

	public double getSimulationSceneWidth() {
		return simulationSceneWidth;
	}

	public double getSimulationSceneHeight() {
		return simulationSceneHeight;
	}

	public Sphere[] getParticles() {
		return particles;
	}

	public void updateDensityChart() {
		double[][] densityProfileAlongY = controller.getDensityProfile();
		densityProfile = densityProfileAlongY[1];
		densityProfileLocation = densityProfileAlongY[0];

		// build density profile chart
		XYChart.Series<Number, Number> newDensitySeries = new XYChart.Series<>();
		for (int i = 0; i < densityProfile.length; i++) {
			newDensitySeries.getData().add(new XYChart.Data<>(densityProfileLocation[i], densityProfile[i]));
		}
		densityChart.getData().remove(densitySeries);
		densitySeries = newDensitySeries;
		densityChart.getData().add(densitySeries);

	}

	public void updateEnergyChart() {
		double calculatedTemp = calculatedTemperature.get();
		int currStep = currentStep.get();
		if (currStep >= E_DISPLAY_STEP * 4 / 5) {
			energyChartXLowerBoundProperty.set(energyChartXLowerBoundProperty.get() + 1);
			energyChartXUpperBoundProperty.set(energyChartXUpperBoundProperty.get() + 1);
			energySeriesData.remove(0);
		}

		XYChart.Data<Number, Number> currentTemperatureData = new XYChart.Data<>(currStep, calculatedTemp);
		energySeriesData.add(currentTemperatureData);
	}

	public void setCurrentStep(int step) {
		currentStep.set(step);
	}

	public void setCalculatedTemperature(double temp) {
		calculatedTemperature.set(temp);
	}

	public Button getSettingButton() {
		return settingButton;
	}

	public Button getVelocityRescaleButton() {
		return velocityRescaleButton;
	}

}
