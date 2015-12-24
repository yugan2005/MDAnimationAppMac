package edu.MD.statThermodynamic;

import edu.MD.modeling.Particle;
import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;
import edu.MD.number.NumberFactory;

public class MonatomicSysKineticECalculator {

	public static MDNumber calculate(Iterable<MDVector> velocities, double mass) {
		MDNumber kinecticE = NumberFactory.getInstance().valueOf(0);

		for (MDVector velocity : velocities) {
			kinecticE = kinecticE.plus(velocity.normSquare());
		}

		return kinecticE.times(mass).times(0.5);
	}

	public static MDNumber calculate(Iterable<Particle> particles) {
		Particle sampleParticle = particles.iterator().next();
		double mass = sampleParticle.getMass();

		MDNumber kinecticE = NumberFactory.getInstance().valueOf(0);

		for (Particle particle : particles) {
			kinecticE = kinecticE.plus(particle.getVelocity().normSquare());
		}

		return kinecticE.times(mass).times(0.5);
	}

}
