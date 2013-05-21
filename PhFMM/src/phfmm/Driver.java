package phfmm;

public class Driver {
	
	public static void main (String[] args) {
		
		//MachineControl mc = new MachineControl();
		
		/*
		System.out.println(mc.getControlValues());
		mc.setControlValues(75, 75);
		System.out.println(mc.runMachineForTsecondsAtCurrentSettings(5));
		
		
		System.out.println(mc.constantPressueMode(3, 20));
		System.out.println(mc.constantCurrentMode(4, 10));
		System.out.println(mc.rampMode(30, 51));
		*/
		/*
		System.out.println(mc.runMachineFromRecipe("src/recipeFiles/hw3_recipe1.csv"));
		System.out.println(mc.runMachineFromRecipe("src/recipeFiles/hw3_recipe2.csv"));
		System.out.println(mc.runMachineFromRecipe("src/recipeFiles/hw3_recipe3.csv"));
		*/
		
		UserInterface ui = new UserInterface();
		System.out.println(ui.setControlValues(100, 100));
		System.out.println(ui.manualRun(10));
		System.out.println(ui.executeRecipe(UserInterface.RECIPE_1_PATH));
		System.out.println(ui.executeRecipe(UserInterface.RECIPE_2_PATH));
		System.out.println(ui.executeRecipe(UserInterface.RECIPE_3_PATH));
		
	}
}
