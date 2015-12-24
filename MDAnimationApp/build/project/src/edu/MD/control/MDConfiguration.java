package edu.MD.control;

public class MDConfiguration {


	private int filmSize, filmThickness, vaporOneSideThickness, nDensityBin;
	private double temperature, timeStep, cutoff;
	private String fluid;
	
	public MDConfiguration(){
		filmSize = 4;
		filmThickness = 3;
		vaporOneSideThickness = 3;
		nDensityBin = 150;
		temperature = 100;
		timeStep = 5e-14;
		cutoff = 3.0;
		fluid = "ARGON";
	}

	public int getFilmSize() {
		return filmSize;
	}

	public void setFilmSize(int filmSize) {
		this.filmSize = filmSize;
	}

	public int getFilmThickness() {
		return filmThickness;
	}

	public void setFilmThickness(int filmThickness) {
		this.filmThickness = filmThickness;
	}

	public int getVaporOneSideThickness() {
		return vaporOneSideThickness;
	}

	public void setVaporOneSideThickness(int vaporOneSideThickness) {
		this.vaporOneSideThickness = vaporOneSideThickness;
	}

	public int getnDensityBin() {
		return nDensityBin;
	}

	public void setnDensityBin(int nDensityBin) {
		this.nDensityBin = nDensityBin;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getTimeStep() {
		return timeStep;
	}

	public void setTimeStep(double timeStep) {
		this.timeStep = timeStep;
	}

	public double getCutoff() {
		return cutoff;
	}

	public void setCutoff(double cutoff) {
		this.cutoff = cutoff;
	}

	public String getFluid() {
		return fluid;
	}

}
