package edu.MD.view;

import edu.MD.control.MDConfiguration;
import edu.MD.control.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MDSettingDialogView {
	@FXML
	private TextField liquidFilmThicknessField;
	@FXML
	private TextField liquidFilmSizeField;
	@FXML
	private TextField vaporThickessField;
	@FXML
	private TextField nDensityBinField;
	@FXML
	private TextField initialTemperatureField;
	@FXML
	private TextField timeStepSizeField;
	@FXML
	private TextField cutoffField;
	@FXML
	private TextField fluidField;

	private Stage dialogStage;
	private MDConfiguration configuration;
	private boolean okClicked = false;

	private int filmSizeInput, filmThicknessInput, vaporThicknessInput, nDensityBinInput;
	private double initialTemperatureInput, timeStepSizeInput, cutOffInput;
	private double maxTemperature, minTemperature;
	private MainApp mainApp;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		filmSizeInput = Integer.parseInt(liquidFilmSizeField.getText());
		filmThicknessInput = Integer.parseInt(liquidFilmThicknessField.getText());
		vaporThicknessInput = Integer.parseInt(vaporThickessField.getText());
		nDensityBinInput = Integer.parseInt(nDensityBinField.getText());
		initialTemperatureInput = Double.parseDouble(initialTemperatureField.getText());
		timeStepSizeInput = Integer.parseInt(timeStepSizeField.getText()) * 1e-15;
		cutOffInput = Double.parseDouble(cutoffField.getText());
		if (isInputValid()) {
			configuration.setFilmSize(filmSizeInput);
			configuration.setFilmThickness(filmThicknessInput);
			configuration.setVaporOneSideThickness(vaporThicknessInput);
			configuration.setnDensityBin(nDensityBinInput);
			configuration.setTemperature(initialTemperatureInput);
			configuration.setTimeStep(timeStepSizeInput);
			configuration.setCutoff(cutOffInput);
			okClicked = true;
			dialogStage.close();
		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Validates the user input in the text fields.
	 * 
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		maxTemperature = mainApp.getMaxTemperature();
		minTemperature = mainApp.getMinTemperature();
		StringBuilder errorMessage = new StringBuilder();

		if (filmSizeInput <= 0)
			errorMessage.append("filmSize must be positive. \n");
		if (filmSizeInput > 6)
			errorMessage.append("filmSize too large for the live animation application. \n");
		if (filmThicknessInput <= 0)
			errorMessage.append("filmThickness must be positive. \n");
		if (filmThicknessInput > 8)
			errorMessage.append("filmThickness too large for the live animation application. \n");
		if (initialTemperatureInput > maxTemperature)
			errorMessage.append("Temperature is greater than critical point. \n");
		if (initialTemperatureInput < minTemperature)
			errorMessage.append("Temperature is lower than triple point. \n");
		if (timeStepSizeInput < 10e-15)
			errorMessage.append("Time step is too small to see the animation. \n");

		if (errorMessage.length() == 0)
			return true;
		// Show the error message.
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(dialogStage);
		alert.setTitle("Invalid Fields");
		alert.setHeaderText("Please correct invalid fields");
		alert.setContentText(errorMessage.toString());

		alert.showAndWait();

		return false;
	}

	public void setConfiguration(MDConfiguration conf) {
		this.configuration = conf;
		liquidFilmSizeField.setText(String.valueOf(configuration.getFilmSize()));
		liquidFilmThicknessField.setText(String.valueOf(configuration.getFilmThickness()));
		vaporThickessField.setText(String.valueOf(configuration.getVaporOneSideThickness()));
		nDensityBinField.setText(String.valueOf(configuration.getnDensityBin()));
		initialTemperatureField.setText(String.format("%.2f", configuration.getTemperature()));
		timeStepSizeField.setText(String.valueOf(Math.round(configuration.getTimeStep() * 1e15)));
		cutoffField.setText(String.format("%.2f", configuration.getCutoff()));
		fluidField.setText(configuration.getFluid());
		fluidField.setDisable(true);
	}

	public void setController(MainApp mainApp) {
		this.mainApp = mainApp;
	}

}
