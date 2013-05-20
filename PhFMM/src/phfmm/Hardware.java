package phfmm;

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
	private int airPressuePSI;	//	air pressure, measured in PSI
	private int currentAmps;	//	current in the system, measured in amps
	private boolean isRunning;	//	boolean indicating whether the machine is on or off
	
	
	//=====================================================================
	//	=>	CONSTANTS
	//=====================================================================
	public static final int MAX_AIR_PRESSURE_PSI = 200;
	public static final int MIN_AIR_PRESSURE_PSI = 0;
	public static final int MAX_CURRENT_AMPS = 200;
	public static final int MIN_CURRENT_AMPS = 0;
	
	private static final int SECONDS_PER_MILLISECOND = 1000;
	
	
	//=====================================================================
	//	=>	CONSTRUCTOR
	//=====================================================================
	public Hardware() {
		this.isRunning = false;
		this.airPressuePSI = 0;
		this.currentAmps = 0;
	}
	
	//=====================================================================
	//	=>	PUBLIC API
	//=====================================================================
	public void work(int airPressure, int current, int seconds) {
		
		if (!this.isRunning) {
			throw new IllegalStateException("This machine is not running: cannot perform work");
		}
		
		this.setAirPressure(airPressure);
		this.setCurrent(current);
		
		try {
			Thread.sleep(seconds * SECONDS_PER_MILLISECOND);
		} catch (InterruptedException e) {
			System.out.println("System failure: failed to run for indicated time");
			e.printStackTrace();
			throw new IllegalStateException("Machine failed to run for the indicated time");
		}
		
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
	public boolean isRunning() { return this.isRunning; }
	
	
	//	methods to start and stop the hardware
	public void startHardware() { this.isRunning = true; }
	public void stopHardware() { this.isRunning = false; }
	
}