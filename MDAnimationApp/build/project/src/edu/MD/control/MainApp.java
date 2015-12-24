package edu.MD.control;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import edu.MD.DAO.MDDataObject;
import edu.MD.view.MDSettingDialogView;
import edu.MD.view.RootPaneView;
import javafx.application.Application;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

	private MDDataObject model;
	private MDConfiguration conf;
	private RootPaneView view;
	private Stage primaryStage;

	public static void main(String[] args) {
		launch(args);
	}

	public MainApp() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		conf = new MDConfiguration();
		model = new MDDataObject(conf);

	}

	@Override
	public void start(Stage stage) throws Exception {
		this.primaryStage = stage;
		FXMLLoader loader = new FXMLLoader();
		URL fxmlUrl = MainApp.class.getResource("/edu/MD/view/RootPane.fxml");
		loader.setLocation(fxmlUrl);

		Parent root = loader.load();
		view = loader.<RootPaneView> getController();

		model.setController(this);

		view.setView(this);

		Scene scene = new Scene(root, 1000, 750);
		primaryStage.setScene(scene);
		hookupEvents();

		primaryStage.show();

	}

	private void hookupEvents() {

		view.getStartButton().setOnAction(actionEvent -> {
			((ScheduledService<double[][]>) model.getWorker()).restart();
			view.getStartButton().setText("Continue");
			view.getStartButton().setDisable(true);
			view.getPauseButton().setDisable(false);
			view.getSettingButton().setDisable(true);
			view.getVelocityRescaleButton().setDisable(true);

		});

		view.getPauseButton().setOnAction(actionEvent -> {
			((ScheduledService<double[][]>) model.getWorker()).cancel();
			view.getPauseButton().setDisable(true);
			view.getStartButton().setDisable(false);
			view.getSettingButton().setDisable(false);
			view.getVelocityRescaleButton().setDisable(false);

		});
		
		view.getVelocityRescaleButton().setOnAction(actionEvent -> {
			model.velocityControl();
			view.getPauseButton().setDisable(true);
			view.getStartButton().setDisable(false);
			view.getSettingButton().setDisable(false);
			view.getVelocityRescaleButton().setDisable(true);
		});
		
		view.getSettingButton().setOnAction(actionEvent -> {
			if (showMDSettingDialogView()) {
				try {
					model = new MDDataObject(conf);
					FXMLLoader loader = new FXMLLoader();
					URL fxmlUrl = MainApp.class.getResource("/edu/MD/view/RootPane.fxml");
					loader.setLocation(fxmlUrl);

					Parent root = loader.load();
					view = loader.<RootPaneView> getController();

					model.setController(this);

					view.setView(this);

					Scene scene = new Scene(root, 1000, 750);
					primaryStage.setScene(scene);
					hookupEvents();

					primaryStage.show();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				view.getStartButton().setText("Start");
			}
			view.getStartButton().setDisable(false);
			view.getVelocityRescaleButton().setDisable(true);
		});

		model.getWorker().setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				double[][] newPosition = (double[][]) event.getSource().getValue();

				for (int i = 0; i < newPosition.length; i++) {
					for (int j = 0; j < newPosition[i].length; j++) {
						switch (i) {
						case 0:
							view.getParticles()[j].setTranslateX(newPosition[i][j]);
							break;
						case 1:
							view.getParticles()[j].setTranslateY(newPosition[i][j]);
							break;
						case 2:
							view.getParticles()[j].setTranslateZ(newPosition[i][j]);
							break;
						}

					}

				}
				view.setCurrentStep(model.getCurrentStep());
				view.setCalculatedTemperature(model.getCalculatedTemperature());
				view.updateDensityChart();
				view.updateEnergyChart();

			}

		});

	}

	public double[] getSystemBoundary() {
		return model.getSystemBoundary();
	}

	public double[][] getPositions() {
		return model.getPositions();
	}

	public int getParticleNumber() {
		return model.getParticleNumber();
	}

	public RootPaneView getView() {
		return view;
	}

	public double[][] getDensityProfile() {
		return model.getDensityProfile();
	}

	public double[] getUnscaledSystemBoundary() {
		return model.getUnscaledSystemBoundary();
	}

	public double getVaporDensity() {
		return model.getVaporDensity();
	}

	public double getLiquidDensity() {
		return model.getLiquidDensity();
	}

	public double getCalculatedTemperature() {
		return model.getCalculatedTemperature();
	}

	public int getCurrentStep() {
		return model.getCurrentStep();
	}

	public double getSystemTemperature() {
		return model.getSystemTemperature();
	}

	public double getTimeStepSize() {
		return model.getTimeStepSize();
	}
	
	public boolean showMDSettingDialogView() {
	    try {
	        // Load the fxml file and create a new stage for the popup dialog.
	    	
	    	FXMLLoader loader = new FXMLLoader();
			URL fxmlUrl = MainApp.class.getResource("/edu/MD/view/MDSettingDialog.fxml");
			loader.setLocation(fxmlUrl);
			
			
	        AnchorPane page = (AnchorPane) loader.load();

	        // Create the dialog Stage.
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Edit MD Setting");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        // Set the person into the controller.
	        MDSettingDialogView settingDialogView = loader.getController();
	        settingDialogView.setDialogStage(dialogStage);
	        settingDialogView.setConfiguration(conf);
	        settingDialogView.setController(this);

	        // Show the dialog and wait until the user closes it
	        dialogStage.showAndWait();

	        return settingDialogView.isOkClicked();
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public double getMaxTemperature() {
		return model.getMaxTemperature();
	}
	
	public double getMinTemperature() {
		return model.getMinTemperature();
	}

	public MDConfiguration getConfiguration() {
		return conf;
	}

}
