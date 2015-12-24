package edu.MD.utility;

import java.util.HashMap;
import java.util.Map;


public class MDPotentialConstants {
	public static Map<String, Double> sigma = new HashMap<>();
	public static Map<String, Double> epsilon = new HashMap<>();
	
	static{
		String argon = "ARGON";
		double argonSigma = 3.4e-10; // LJ Length parameter in (m) - The original value
		double argonEpsilon = 1.67e-21; // LJ energy parameter in (J) - The original value
		sigma.put(argon, argonSigma); 
		epsilon.put(argon, argonEpsilon);
	}

	public static double getSigma(String name) {
		if (sigma.get(name)!=null) return sigma.get(name);
		throw new IllegalArgumentException("There is no sigma value for the particle: "+name);
	}

	public static Double getEpsilon(String name) {
		if (epsilon.get(name)!=null) return epsilon.get(name);
		throw new IllegalArgumentException("There is no epsilon value for the particle: "+name);
	}

}
