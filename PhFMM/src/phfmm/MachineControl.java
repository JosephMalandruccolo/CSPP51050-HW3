package phfmm;

/**
 * @author Joseph Malandruccolo
 * Machine control layer performs the following functions
 * 1. Get control values from the hardware system to the UI.
 * 2. Send control values to the hardware system.
 * 3. Support 3 different machine modes (i.e. types of recipe strategies), in which the hardware is controlled, and varies, in one of 3 particular ways over a given amount of time. 
 * 4. Support executing a recipe.
 * 5. Validate that a recipe executed successfully by comparing generated data with reference data. If the data matches, the MachineControl should return a 'good part' result, otherwise return a 'bad part' result.  When the UserInterface manually controls the hardware, no validation is necessary.
 * 
 * 
 *
 */
public class MachineControl {

}
