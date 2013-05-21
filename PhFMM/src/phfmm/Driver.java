package phfmm;

/**
 * 
 * @author Joseph Malandruccolo
 * class to run the user interface
 *
 */
public class Driver {
	
	public static void main (String[] args) {
		
		UserInterface ui = new UserInterface();
		System.out.println(ui.setControlValues(100, 100));
		System.out.println(ui.manualRun(10));
		System.out.println(ui.executeRecipe(UserInterface.RECIPE_1_PATH));
		System.out.println(ui.executeRecipe(UserInterface.RECIPE_2_PATH));
		System.out.println(ui.executeRecipe(UserInterface.RECIPE_3_PATH));
		
	}
}
