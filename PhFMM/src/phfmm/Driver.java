package phfmm;

import java.util.HashMap;

public class Driver {
	
	public static void main (String[] args) {
		
		Hardware h = new Hardware();
		h.startHardware();
		HashMap<String, Integer> inputs = new HashMap<String, Integer>();
		inputs.put(Hardware.INPUT_KEY_FOR_AIR_PRESSURE, 100);
		inputs.put(Hardware.INPUT_KEY_FOR_ELECTRICAL_CURRENT, 100);
		inputs.put(Hardware.INPUT_KEY_FOR_SECONDS, 3);
		
		h.work(inputs);
		
	}

}
