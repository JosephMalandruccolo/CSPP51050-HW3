package phfmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;



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
	
	public static final String kCONSTANT_PRESSURE_RECIPE_KEY = "ConstantPressure";
	public static final String kCONSTANT_CURRENT_RECIPE_KEY = "ConstantCurrent";
	public static final String kRAMP_RECIPE_KEY = "Ramp";
	public static final String kREFERENCE_FILE_PATH = "src/referenceFiles/";
	public static final String kREFERENCE_FILE_SUFFIX = ".reference.csv";
	public static final int kCONSTANT_PRESSURE_RUNTIME = 10;
	public static final int kCONSTANT_CURRENT_RUNTIME = 20;
	public static final int kRAMP_RUNTIME = 30;
	
	
	
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
	
	
	@SuppressWarnings("resource")
	public String runMachineFromRecipe(String recipeFilePath) {
		
		File recipeFile = new File(recipeFilePath);
		
		BufferedReader br = null;
		
		try {
			
			//	store the result
			String logfile;
			
			//	parse the single line
			String singleLine;
			br = new BufferedReader(new FileReader(recipeFile));
			singleLine = br.readLine();
			String[] inputs = singleLine.split(",");
			String referenceFileName = inputs[0];
			String recipe = inputs[1];
			int partSize = Integer.parseInt(inputs[2]);
			
			
			//	call the appropriate method
			if (recipe.equals(kCONSTANT_PRESSURE_RECIPE_KEY)) logfile = constantPressueMode(kCONSTANT_PRESSURE_RUNTIME, partSize);
			else if (recipe.equals(kCONSTANT_CURRENT_RECIPE_KEY)) logfile = constantCurrentMode(kCONSTANT_CURRENT_RUNTIME, partSize);
			else if (recipe.equals(kRAMP_RECIPE_KEY)) logfile = rampMode(kRAMP_RUNTIME, partSize);
			else throw new IllegalArgumentException(recipe + " - is an invalid recipe");
			
			//	validate log file with reference file
			boolean success = validateLogFileWithReferenceFile(logfile, referenceFileName);
			
			//	print a message depending on the results of validation
			if (success) return new String("good part See log file named '" + logfile + "' for details\n");
			else return new String ("bad part");
			
		}
		catch (IOException e) {
			return new String("Failed to read recipe from file, please check file format");
		}
	}
	
	
	//=====================================================================
	//	=>	PRIVATE METHODS
	//=====================================================================
	/**
	 * Run the Hardware in constant pressure mode
	 * @param T - seconds to run
	 * @param partSize - size of the part
	 * @return - true if run is successful, false otherwise
	 */
	 public String constantPressueMode(int T, int partSize) {
		 
		 this.underlyingHardware.startHardware();
		 
		 for (int i = 0; i <= T; i++) {
			 
			 HashMap<String, Integer> inputs = new HashMap<String, Integer>();
			 inputs.put(Hardware.INPUT_KEY_FOR_ELECTRICAL_CURRENT, i * 2);
			 inputs.put(Hardware.INPUT_KEY_FOR_AIR_PRESSURE, partSize + kCONSTANT_PRESSURE_FLOOR);
			 inputs.put(Hardware.INPUT_KEY_FOR_SECONDS, 1);
			 
			 if (!this.underlyingHardware.performOneSecondOfWork(inputs, i)) {
				 this.underlyingHardware.stopHardware();
				 throw new IllegalStateException("hardware failed to perform one second of work");
			 }
			 
		 }
		 
		 String logfile = this.underlyingHardware.stopHardware();
		 return logfile;
		 
	 }
	 
	 
	 /**
	  * Run the Hardware in constant current mode
	  * @param T - seconds to run the Hardware
	  * @param partSize - size of the part
	  * @return - true if run successful, false otherwise
	  */
	 public String constantCurrentMode(int T, int partSize) {
		 
		 this.underlyingHardware.startHardware();
		 
		 for (int i = 0; i <= T; i++) {
			 
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
				 throw new IllegalStateException("hardware failed to perform one second of work");
			 }
		 }
		 
		 String logfile = this.underlyingHardware.stopHardware();
		 return logfile;
		 
	 }
	 
	 
	 /**
	  * Run the Hardware in Ramp mode
	  * @param T - number of seconds to run the hardware
	  * @param partSize - the size of the part
	  * @return - true if successful, false otherwise
	  */
	 public String rampMode(int T, int partSize) {
		 
		 if (partSize < 50) throw new IllegalArgumentException(new String("Minimum part size is 51"));
		 
		 this.underlyingHardware.startHardware();
		 
		 for (int i = 0; i <= T; i++) {
			 
			 HashMap<String, Integer> inputs = new HashMap<String, Integer>();
			 
			 int calculatedPSI = i * 10;
			 if (calculatedPSI >= kRAMP_PRESSURE_CEILING ) inputs.put(Hardware.INPUT_KEY_FOR_AIR_PRESSURE, kRAMP_PRESSURE_CEILING);
			 else inputs.put(Hardware.INPUT_KEY_FOR_AIR_PRESSURE, calculatedPSI);
			 
			 int calculatedAmps = partSize + i * 20;
			 inputs.put(Hardware.INPUT_KEY_FOR_ELECTRICAL_CURRENT, calculatedAmps);
			 
			 if (!this.underlyingHardware.performOneSecondOfWork(inputs, i)) {
				 this.underlyingHardware.stopHardware();
				 throw new IllegalStateException("hardware failed to perform one second of work");
			 }
				 
		 }
		 
		 String logfile = this.underlyingHardware.stopHardware();
		 return logfile;
		 
		 
	 }
	 
	 private boolean validateLogFileWithReferenceFile(String logfile, String recipeName) {
		 
		 //	get records from reference file
		 String fullPath = kREFERENCE_FILE_PATH + recipeName + kREFERENCE_FILE_SUFFIX;
		 
		 File reference = new File(fullPath);
		 
		 LinkedList<Record> referenceRecords = new LinkedList<Record>();
		 LinkedList<Record> logRecords = new LinkedList<Record>();
		 
		 try {
			 Scanner s = new Scanner(reference);
			 String row;
			 String[] parsedRow;
			 
			 while (s.hasNextLine()) {
				 row = s.nextLine();
				 //System.out.println(row);
				 parsedRow = row.split(",");
				 referenceRecords.addLast(new Record(parsedRow[0], parsedRow[1], parsedRow[2]));
			 }
		 }
		 catch (Exception e) { throw new IllegalArgumentException("failed to open reference file at path: " + fullPath); }
		 
		 
		 //	get records from log file
		 File l = new File(logfile);
		 
		 try {
			 Scanner scn = new Scanner(l);
			 String row;
			 
			 while (scn.hasNextLine()) {
				 row = scn.nextLine();
				 //System.out.println(row);
				 String[] parsedRow = row.split(",");
				 logRecords.addLast(new Record(parsedRow[0], parsedRow[1], parsedRow[2]));
			 }
		 }
		 catch (Exception e) { throw new IllegalArgumentException("failed to open reference file"); }
		 
		 
		 if (logRecords.size() != referenceRecords.size()) return false;
		 else {
			 
			 //	 a more efficient way to iterate would be to use an iterator
			 for (int i = 0; i < logRecords.size(); i ++) {
				 if (logRecords.get(i).current != referenceRecords.get(i).getCurrent() 
						 && logRecords.get(i).getPressure() != referenceRecords.get(i).getPressure() 
						 && logRecords.get(i).getSecond() != referenceRecords.get(i).getSecond()) {
					 return false;
				 }
			 }
			 
			 return true;
			 
		 }
	 }
	 
	 class Record {
		 
		 int second;
		 int pressure;
		 int current;
		 
		 Record (String second, String pressure, String current) {
			 this.second = Integer.parseInt(second);
			 this.pressure = Integer.parseInt(pressure);
			 this.current = Integer.parseInt(current);
		 }
		 
		 public int getSecond() { return this.second; }
		 public int getPressure() { return this.pressure; }
		 public int getCurrent() { return this.current; }
		 
	 }
	 
}