package edu.MD.globalSetting;

import java.lang.reflect.InvocationTargetException;

import edu.MD.number.NumberFactory;

public class NumberFactorySetting {
	
	public static void set(String type, int precision) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		try{
			NumberFactory.setFactorySetting(type, precision);
		}
		catch (Exception ex){
			NumberFactory.destroyInstance();
			NumberFactory.setFactorySetting(type, precision);
		}
	}
	
	public static void set(String type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		try{
			NumberFactory.setFactorySetting(type);
		}
		catch (Exception ex){
			NumberFactory.destroyInstance();
			NumberFactory.setFactorySetting(type);
		}
	}
	
	public static void set() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
//		set("JavaBigDecimalFactory", 32);
		set("JavaDefaultNumberFactory");
	}

}
