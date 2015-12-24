package edu.MD.modeling;

import edu.MD.number.MDVector;

public abstract class Particle {

	public abstract MDVector getVelocity();
	public abstract MDVector getPosition();
	public abstract void setInitialPosition(MDVector initialPosition);
	public abstract void setInitialVelocity(MDVector initialVelocity);

	public abstract double getMass();
	public abstract int getDOF();

}
