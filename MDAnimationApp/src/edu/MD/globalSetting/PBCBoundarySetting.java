package edu.MD.globalSetting;

import edu.MD.number.MDVector;
import edu.MD.utility.PBCCalculator;

public class PBCBoundarySetting {
	
	public static void set(MDVector systemBoundary){
		try {
			PBCCalculator.setPBCCalculator(systemBoundary);
		} catch (Exception ex) {
			PBCCalculator.destroyInstance();
			PBCCalculator.setPBCCalculator(systemBoundary);
		}
	}

}
