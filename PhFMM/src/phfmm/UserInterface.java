package phfmm;

/**
 * @author Joseph Malandruccolo
 *	User interface layer of a Phoenix Fictitious Manufacturing Machine
 *	This layer allows the user to accomplish four tasks 
 *	1) 	Allow the user to manually set control values in the hardware system.
 *	2)	Allow the user to read the control values. 
 *	3)	Start the system using the manually controlled values, let it run for T seconds, and then automatically Stop.
 *	4)	Allow the user to select a recipe that is used to manufacture a particular part and execute that recipe.
 *
 */
public class UserInterface {
	
	//=====================================================================
	//	=>	CONSTANTS
	//=====================================================================
	public static final String RECIPE_1_PATH = "src/recipeFiles/hw3_recipe1.csv";
	public static final String RECIPE_2_PATH = "src/recipeFiles/hw3_recipe2.csv";
	public static final String RECIPE_3_PATH = "src/recipeFiles/hw3_recipe3.csv";
	
	
	
	//=====================================================================
	//	=>	PROPERTIES
	//=====================================================================
	private MachineControl mcLayer;
	
	
	//=====================================================================
	//	=>	CONSTRUCTOR
	//=====================================================================
	public UserInterface() {
		this.mcLayer = new MachineControl();
	}
	
	

	//=====================================================================
	//	=>	PUBLIC API
	//=====================================================================
	public String getControlValues() {
		
		StringBuffer sb = new StringBuffer();
		sb.append(new String("Current control values:\n"));
		sb.append(this.mcLayer.getControlValues());
		
		return sb.toString();
		
	}
	
	
	public String setControlValues(int airPressure, int current) {
		
		this.mcLayer.setControlValues(airPressure, current);
		return this.getControlValues();
		
	}
	
	
	public String manualRun(int secondsToRunAtCurrentSettings) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("Running in manual mode\n");
		sb.append(this.getControlValues());
		sb.append(new String("\nWith result:\n"));
		sb.append(this.mcLayer.runMachineForTsecondsAtCurrentSettings(secondsToRunAtCurrentSettings));
		
		return sb.toString();
		
	}
	
	
	public String executeRecipe(String recipeFilePath) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("Running recipe saved in file: "+recipeFilePath);
		sb.append("\nWith result:\n");
		sb.append(this.mcLayer.runMachineFromRecipe(recipeFilePath));
		
		return sb.toString();
		
	}
}
