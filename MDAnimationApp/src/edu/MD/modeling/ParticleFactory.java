package edu.MD.modeling;

public abstract class ParticleFactory {

	public static Particle getParticle(String name) {

		switch (name) {
		case "ARGON":
			return new Argon();
		default:
			throw new UnsupportedOperationException("The specified particle: " + name + " has not been designed yet");
		}
	}
}
