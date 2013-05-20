package phfmm;

import java.util.HashMap;

public class Driver {
	
	public static void main (String[] args) {
		
		MachineControl mc = new MachineControl();
		
		System.out.println(mc.getControlValues());
		mc.setControlValues(75, 75);
		System.out.println(mc.runMachineForTsecondsAtCurrentSettings(5));
		
		
		System.out.println(mc.constantPressueMode(3, 20));
		System.out.println(mc.constantCurrentMode(4, 10));
		System.out.println(mc.rampMode(30, 51));
	}

}
