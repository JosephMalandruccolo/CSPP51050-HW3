package phfmm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

/**
 * 
 * @author Joseph Malandruccolo
 * Hardware layer performs the following functions
 * 1. When requested, send the current values of the hardware subsystem to the client.
 * 2. When requested, apply a given control value to a hardware subsystem. The hardware must limit the control values to within its minimum or maximum. If a control value is above the maximum, use the maximum. If below the minimum, use the minimum.
 * 3. Turn the hardware on (Start).
 * 4. Given a set of control values and a time (seconds), set the hardware to the control values for the given number of seconds.
 * 5. Turn the hardware off (Stop).
 *
 */
public class Hardware {
	
	
	//=====================================================================
	//	=>	PROPERTIES
	//=====================================================================
	private int airPressuePSI;			//	air pressure, measured in PSI
	private int currentAmps;			//	current in the system, measured in amps
	private boolean isOnline;			//	boolean indicating whether the machine is on or off
	private String currentLogFileName;	//	the name of the active logfile
	
	
	//=====================================================================
	//	=>	CONSTANTS
	//=====================================================================
	public static final int MAX_AIR_PRESSURE_PSI = 200;
	public static final int MIN_AIR_PRESSURE_PSI = 0;
	public static final int MAX_CURRENT_AMPS = 200;
	public static final int MIN_CURRENT_AMPS = 0;
	public static final String INPUT_KEY_FOR_AIR_PRESSURE = "air pressure";
	public static final String INPUT_KEY_FOR_ELECTRICAL_CURRENT = "current";
	public static final String INPUT_KEY_FOR_SECONDS = "seconds";
	public static final int DEFAULT_SECONDS = 1;
	
	private static final int SECONDS_PER_MILLISECOND = 1000;
	
	
	//=====================================================================
	//	=>	CONSTRUCTOR
	//=====================================================================
	public Hardware() {
		this.isOnline = false;
		this.airPressuePSI = 0;
		this.currentAmps = 0;
		this.currentLogFileName = "";
	}
	
	
	/**
	 * Method that accepts 1 to N control parameters
	 * @param controlParameters - n parameters used to control the machine
	 * @return true if the work finished successfully, false otherwise
	 */
	public boolean work(HashMap<String, Integer> controlParameters) {
		
		if (!this.isOnline) {
			System.out.println("Machine is not online and cannot work");
			return false;
		}
		
		assert (controlParameters.get(INPUT_KEY_FOR_SECONDS)) != null;
		
		int secondsToWork = controlParameters.get(INPUT_KEY_FOR_SECONDS);
		
		for (int i = 0; i < secondsToWork; i++) {
			
			if(!this.performOneSecondOfWork(controlParameters, i)) return false;
		}
			
		return true;
		
	}
	
	
	/**
	 * Run the hardware with the current control settings for an arbitrary number of seconds
	 * @param seconds - number of seconds to run the hardware
	 * @return - true if the work finished successfully, false otherwise
	 */
	public boolean work(int seconds) {
		HashMap<String, Integer> currentControls = new HashMap<String, Integer>();
		currentControls.put(INPUT_KEY_FOR_AIR_PRESSURE, this.airPressuePSI);
		currentControls.put(INPUT_KEY_FOR_ELECTRICAL_CURRENT, this.currentAmps);
		currentControls.put(INPUT_KEY_FOR_SECONDS, seconds);
		
		return this.work(currentControls);
		
	}
	
	
	/**
	 * Run the hardware with the current control settings for DEFAULT_SECONDS
	 * @return - true if the work finished successfully, false otherwise
	 */
	public boolean work() { return this.work(DEFAULT_SECONDS); }
	
	
	/**
	 * Method to perform one second of work, with given paramters
	 * @param controlParameters - HashMap of control parameters
	 * @param currentSecond - the human readable current second to display in a log file
	 * @return - true if the work succeeded, false otherwise
	 */
	public boolean performOneSecondOfWork(HashMap<String, Integer> controlParameters, int currentSecond) {
		
		//		check that the required control parameters are made
		assert (controlParameters.get(INPUT_KEY_FOR_AIR_PRESSURE)) != null;
		assert (controlParameters.get(INPUT_KEY_FOR_ELECTRICAL_CURRENT)) != null;
		
		//		parse the control parameters
		this.setAirPressure(controlParameters.get(INPUT_KEY_FOR_AIR_PRESSURE));
		this.setCurrent(controlParameters.get(INPUT_KEY_FOR_ELECTRICAL_CURRENT));
		
		
		//		simulate machine working
		try { Thread.sleep(1 * SECONDS_PER_MILLISECOND); } 
		catch (InterruptedException e) {
			System.out.println("Hardware failure: machine failed to work for the alloted time");
			return false;
		} 
		
		//		prepare to write to the log file
		boolean success = true;
		File currentLogFile = new File(this.currentLogFileName);
		FileWriter fw = null;
		try { fw = new FileWriter(currentLogFile.getAbsolutePath(), true); } 
		catch (IOException e1) { 
			System.out.println("Hardware failure: machine failed to find log file"); 
			success = false;
		}
		BufferedWriter bw = new BufferedWriter(fw);
		
		//		write control values to the log file
		try {
			
			if (currentSecond != 0) bw.write("\n");
			bw.write(currentSecond+","+this.airPressuePSI+","+this.currentAmps);
			
					
		} catch (IOException e) {
			System.out.println("Hardware failure: machine failed to write to log");
			success = false;
		}
				
		try {
			bw.close();
		} catch (IOException e) {
			System.out.println("Hardware failure: machine failed to close log file");
			success = false;
		}
		
		return success;
	}
	
	//=====================================================================
	//	=>	GETTERS AND SETTERS
	//=====================================================================
	/**
	 * set the air pressure of the hardware, measured in PSI
	 * @param PSI - the desired PSI to set this hardware
	 * by convention, if the PSI requested is greater/less than the maximum/minimum, the hardware sets the psi to the maximum/minimum possible psi
	 */
	public void setAirPressure(int PSI) {
		
		if (PSI > MAX_AIR_PRESSURE_PSI) {
			this.airPressuePSI = MAX_AIR_PRESSURE_PSI;
		}
		else if (PSI < MIN_AIR_PRESSURE_PSI) {
			this.airPressuePSI = MIN_AIR_PRESSURE_PSI;
		}
		else {
			this.airPressuePSI = PSI;
		}
		
	}
	
	/**
	 * @return - the current air pressure of this hardware instance
	 */
	public int getAirPressure() { return this.airPressuePSI; }
	
	
	/**
	 * set the current of the system measured in amps
	 * @param amps - the desired current to set this hardware
	 * by convention, if the amps requested is greater/less than the maximum/minimum, the hardware sets the amps to the maximum/minimum possible amps
	 */
	public void setCurrent(int amps) {
		
		if (amps > MAX_CURRENT_AMPS) {
			this.currentAmps = MAX_CURRENT_AMPS;
		}
		else if (amps < MIN_AIR_PRESSURE_PSI) {
			this.currentAmps = MIN_CURRENT_AMPS;
		}
		else {
			this.currentAmps = amps;
		}
		
	}
	
	
	/**
	 * @return - the current current in this hardware instance
	 */
	public int getCurrent() { return this.currentAmps; }
	
	
	/**
	 * test if the current hardware instance is running
	 * @return - true if the hardware is running, false otherwise
	 */
	public boolean isOnline() { return this.isOnline; }
	
	
	/**
	 * method to "boot up" this instance of hardware
	 * booting up the hardware generates a log file
	 * the log file is stored in the 'dasFiles' directory in the src folder
	 * the log file name is a concatenation of the number of milliseconds since January 1, 1970 and a pseudo-random number
	 * @return - true if the hardware started successfully, false otherwise
	 */
	public boolean startHardware() { 
		
		this.isOnline = true; 
		
		//	generate a log file
		int createFileAttempts = 0;
		boolean logFileSuccessfullyCreated = false;
		
		do {
			
			createFileAttempts++;
			
			//	build a candidate file name
			long timeStamp = System.currentTimeMillis();
			Random r = new Random();
			int random = r.nextInt(999999);
			String fileName = "src/dasFiles/logFile" + timeStamp + random + ".csv";
			
			File logFile = new File(fileName);
			
			if (logFile.exists()) {
				//	file naming conflict, need to create another file
			}
			else {
				//	create the file
				try {
					logFile.createNewFile();
					logFileSuccessfullyCreated = true;
					this.currentLogFileName = fileName;
				} catch (IOException e) {
					System.out.println("Failed to write log file");
				}
			}
			
		} while (createFileAttempts <= 3 && logFileSuccessfullyCreated == false);
		
		return logFileSuccessfullyCreated;
		
	}
	
	
	/**
	 * stop the current hardware
	 * by convention, control values are set to their minimum values
	 */
	public String stopHardware() { 
		
		String logFileName = this.currentLogFileName;
		
		this.isOnline = false; 
		this.currentLogFileName = "";
		this.airPressuePSI = MIN_AIR_PRESSURE_PSI;
		this.currentAmps = MIN_CURRENT_AMPS;
		
		return logFileName;
		
	}
	
	
	public String getLogFileName() { return this.currentLogFileName; }

}