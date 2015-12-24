package edu.MD.initialization;

import java.util.ArrayList;
import java.util.List;

import edu.MD.modeling.VelocityStandardizor;
import edu.MD.number.MDNumber;
import edu.MD.number.MDVector;
import edu.MD.number.NumberFactory;
import edu.MD.number.Vector3DCartesian;
import edu.MD.utility.MDConstants;

public class MonatomicVelocityInitializer {
	private List<MDVector> velocities;
	private MDNumber mass;
	private MDNumber characteristicVelocity;
	
	
	public MonatomicVelocityInitializer(String name, int totalNumParticles, double temperature){
		this.mass = NumberFactory.getInstance().valueOf(MDConstants.getMass(name));
		characteristicVelocity = mass.pow(-1).times(MDConstants.KB*temperature).pow(0.5);
		velocities = new ArrayList<>(totalNumParticles);
		
		for (int i=0; i<totalNumParticles; i++){
			double c1 = Math.sqrt(-2*Math.log(Math.random())); // Thesis (3.13)
			double c2 = 2*Math.PI*Math.random();
			MDNumber vX = characteristicVelocity.times(c1*Math.cos(c2));
			MDNumber vY = characteristicVelocity.times(c1*Math.sin(c2));
			c1 = Math.sqrt(-2*Math.log(Math.random()));
			c2 = 2*Math.PI*Math.random();
			MDNumber vZ = characteristicVelocity.times(c1*Math.cos(c2));
			velocities.add(new Vector3DCartesian(vX, vY, vZ));
		}
		
		// due to the statistical distribution, this need be rescaled to really match the temperature specified.
		// Also need zero the bulk movement
		velocities = VelocityStandardizor.standarize(velocities, name, temperature);
	}
	
	public List<MDVector> getVelocities(){
		return velocities;
	}

}
