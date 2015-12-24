package edu.MD.statThermodynamic;

import edu.MD.modeling.Particle;
import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;
import edu.MD.utility.IterableCounter;
import edu.MD.utility.MDConstants;

public class MonatomicSysTemperatureCalculator {

	public static MDNumber calculate(Iterable<Particle> particles) {
		MDNumber kinecticE = MonatomicSysKineticECalculator.calculate(particles);

		Particle sampleParticle = particles.iterator().next();
		int DOF = sampleParticle.getDOF();

		int counter = IterableCounter.count(particles);

		return kinecticE.times(2.0 / (counter * DOF * MDConstants.KB));
	}

	public static MDNumber calculate(Iterable<MDVector> velocities, String name) {
		MDNumber kinecticE = MonatomicSysKineticECalculator.calculate(velocities, MDConstants.getMass(name));
		int counter = IterableCounter.count(velocities);
		int DOF = MDConstants.getDOF(name);

		return kinecticE.times(2.0 / (counter * DOF * MDConstants.KB));
	}



}
