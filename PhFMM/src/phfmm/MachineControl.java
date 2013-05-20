package phfmm;

import java.util.HashMap;

/**
 * @author Joseph Malandruccolo
 * Machine control layer performs the following functions
 * 1. Get control values from the hardware system to the UI.
 * 2. Send control values to the hardware system.
 * 3. Support 3 different machine modes (i.e. types of recipe strategies), in which the hardware is controlled, and varies, in one of 3 particular ways over a given amount of time. 
 * 4. Support executing a recipe.
 * 5. Validate that a recipe executed successfully by comparing generated data with reference data. If the data matches, the MachineControl should return a 'good part' result, otherwise return a 'bad part' result.  When the UserInterface manually controls the hardware, no validation is necessary.
 * 
 * MachineControl works with a given instance of underlying hardware
 */
public class MachineControl {
	
	//=====================================================================
	//	=>	CONSTANTS
	//=====================================================================
	private static final int kCONSTANT_PRESSURE_FLOOR = 100;
	private static final int kCONSTANT_CURRENT_FLOOR = 50;
	private static final int kCONSTANT_CURRENT_PRESSURE_CEILING = 50;
	private static final int kCONSTANT_CURRENT_PRESSURE_FLOOR = 10;
	private static final int kRAMP_PRESSURE_CEILING = 100;
	
	
	
	//=====================================================================
	//	=>	PROPERTIES
	//=====================================================================
	private Hardware underlyingHardware;
	
	
	
	//=====================================================================
	//	=>	CONSTRUCTOR
	//=====================================================================
	public MachineControl() {
		this.underlyingHardware = new Hardware();
	}
	
	
	//=====================================================================
	//	=>	PUBLIC API
	//=====================================================================
	/**
	 * Get the control values for an underlying hardware instance
	 * @return - a human readable String representing the status of the underlying hardware
	 */
	public String getControlValues() {
		
		StringBuilder sb = new StringBuilder(100);
		
		//	hide the notion of ON/OFF from the user interface
		/*
		if (this.underlyingHardware.isOnline()) {
			sb.append("Status: ON\n");
		}
		else {
			sb.append("Status: OFF\n");
		}
		*/
		
		sb.append("Pressue: " + this.underlyingHardware.getAirPressure() + " PSI\n");
		sb.append("Current: " + this.underlyingHardware.getCurrent() + " amps\n");
		
		return sb.toString();
	}
	
	
	/**
	 * Set the control values of an underlying hardware object for an indeterminate period of time
	 * @param airPressure - in psi
	 * @param current - in amps
	 */
	public void setControlValues(int airPressure, int current) {
		
		this.underlyingHardware.setAirPressure(airPressure);
		this.underlyingHardware.setCurrent(current);
		
	}
	
	
	/**
	 * Run the underlying hardware at for T seconds at its current control settings
	 * @param T - the number of seconds to run the hardware
	 * @return - a human readable result message
	 */
	public String runMachineForTsecondsAtCurrentSettings(int T) {
		
		boolean started = this.underlyingHardware.startHardware();
		if (!started) return new String("Hardware failed to start");
		
		boolean success = this.underlyingHardware.work(T);
		if (success) {
			
			String result = new String("Good part! See log file named '" + this.underlyingHardware.getLogFileName() + "' for details\n");
			this.underlyingHardware.stopHardware();
			return result;
			
		}
		else {
			
			String result = new String("Bad part - attempted to write to log file named '" + this.underlyingHardware.getLogFileName() + "'\n");
			this.underlyingHardware.stopHardware();
			return result;
			
		}
	}
	
	
	
	//=====================================================================
	//	=>	PRIVATE METHODS
	//=====================================================================
	 public boolean constantPressueMode(int T, int partSize) {
		 
		 this.underlyingHardware.startHardware();
		 
		 for (int i = 0; i < T; i++) {
			 
			 HashMap<String, Integer> inputs = new HashMap<String, Integer>();
			 inputs.put(Hardware.INPUT_KEY_FOR_ELECTRICAL_CURRENT, i * 2);
			 inputs.put(Hardware.INPUT_KEY_FOR_AIR_PRESSURE, partSize + kCONSTANT_PRESSURE_FLOOR);
			 inputs.put(Hardware.INPUT_KEY_FOR_SECONDS, 1);
			 
			 if (!this.underlyingHardware.performOneSecondOfWork(inputs, i)) {
				 this.underlyingHardware.stopHardware();
				 return false;
			 }
			 
		 }
		 
		 this.underlyingHardware.stopHardware();
		 return true;
		 
	 }
	 
	 
	 public boolean constantCurrentMode(int T, int partSize) {
		 
		 this.underlyingHardware.startHardware();
		 
		 for (int i = 0; i < T; i++) {
			 
			 HashMap<String, Integer> inputs = new HashMap<String, Integer>();
			 inputs.put(Hardware.INPUT_KEY_FOR_ELECTRICAL_CURRENT, kCONSTANT_CURRENT_FLOOR + partSize);
			 
			 //		set pressure
			 if ((kCONSTANT_CURRENT_PRESSURE_CEILING - 2 * i) <= kCONSTANT_CURRENT_PRESSURE_FLOOR) {
				 inputs.put(Hardware.INPUT_KEY_FOR_AIR_PRESSURE, kCONSTANT_CURRENT_PRESSURE_FLOOR);
			 }
			 else inputs.put(Hardware.INPUT_KEY_FOR_AIR_PRESSURE, kCONSTANT_CURRENT_PRESSURE_CEILING - 2 * i);
			 
			 inputs.put(Hardware.INPUT_KEY_FOR_SECONDS, 1);
			 
			 if (!this.underlyingHardware.performOneSecondOfWork(inputs, i)) {
				 this.underlyingHardware.stopHardware();
				 return false;
			 }
		 }
		 
		 this.underlyingHardware.stopHardware();
		 return true;
		 
	 }
	 
	 
	 public boolean rampMode(int T, int partSize) {
		 
		 if (partSize <= 50) throw new IllegalArgumentException(new String("Minimum part size is 51"));
		 
		 this.underlyingHardware.startHardware();
		 
		 for (int i = 0; i < T; i++) {
			 
			 HashMap<String, Integer> inputs = new HashMap<String, Integer>();
			 
			 int calculatedPSI = i * 10;
			 if (calculatedPSI >= kRAMP_PRESSURE_CEILING ) inputs.put(Hardware.INPUT_KEY_FOR_AIR_PRESSURE, kRAMP_PRESSURE_CEILING);
			 else inputs.put(Hardware.INPUT_KEY_FOR_AIR_PRESSURE, calculatedPSI);
			 
			 int calculatedAmps = partSize + i * 20;
			 inputs.put(Hardware.INPUT_KEY_FOR_ELECTRICAL_CURRENT, calculatedAmps);
			 
			 if (!this.underlyingHardware.performOneSecondOfWork(inputs, i)) {
				 this.underlyingHardware.stopHardware();
				 return false;
			 }
				 
		 }
		 
		 this.underlyingHardware.stopHardware();
		 return true;
		 
		 
	 }
	
	
	
	

}