Homework 3 : Due May 21, 2013, 5:00 pm
Complete the following problem:

Submitting:

Submit your assignments in a folder called HW3 to the subversion repository according to the directions on the syllabus page. Place all of your Homework 3 work into one folder. You will only be graded on your material within a reasonably labeled folder. Also, please include a README text file that contains the programming language you used. If you want to give the TAs/graders instructions on how to compile, run, or even understand your code, you can place that within the README as well.

From now on, each HW and Lab Deliverable should be submitted as described here and in HW 1. Each assignment (HW or Lab Deliverable) should have its own folder with its own README inside of it. Please ask for assistance if you have any questions

Problem (Layers, Facade, Strategy): For this assignment you will implement the Phoenix Fictitious Manufacturing Machine, or PhFMM. The PhFMM is part of a manufacturing process. It combines mechanical and electrical hardware to complete one step of a manufacturing process. The hardware includes an air pressure system, which is capable of applying 0 to 200 psi of air pressure, and an electrical current system, which is capable of sending 0 to 200 amps into a part. Both of these hardware systems must be precisely controlled over a variable amount of time, in units of seconds, which can range from 0 to 100 seconds.

What you need to implement:

The PhFMM must support a simple UserInterface layer, which should:
1. Allow the user to manually set control values in the hardware system.
2. Allow the user to read the control values. 
3. Start the system using the manually controlled values, let it run for T seconds, and then automatically Stop.
4. Allow the user to select a recipe that is used to manufacture a particular part and execute that recipe.

The PhFMM must also contain a MachineControl layer that accepts user input, and manages and coordinates the hardware. It should:
1. Get control values from the hardware system to the UI.
2. Send control values to the hardware system.
3. Support 3 different machine modes (i.e. types of recipe strategies), in which the hardware is controlled, and varies, in one of 3 particular ways over a given amount of time. The names and algorithms for the three modes are:
3.1. ConstantPressure: T (time, in seconds) = { 0, 1,...10 }, PSI (should remain constant) = part size + 100, AMPS = T * 2
3.2. ConstantCurrent: T = { 0...20 }, PSI = 50 - (T * 2) where PSI can never get below 10, AMPS (constant) = part size + 50
3.3. Ramp: T = { 0...30 }, PSI = ramp up from 0 to 100, in 10 PSI increments each second and then hold at 100,  AMPS = ramp up, starting from part size, in 20 AMP increments each second. Only part sizes of 50 or above can be used. If a recipe has a smaller part size, the MachineControl must generate an error. This part size restriction only applies to the Ramp recipe.
4. Support executing a recipe.
5. Validate that a recipe executed successfully by comparing generated data with reference data. If the data matches, the MachineControl should return a 'good part' result, otherwise return a 'bad part' result.  When the UserInterface manually controls the hardware, no validation is necessary.

The PhFMM must contain a Hardware layer. It should:
1. When requested, send the current values of the hardware subsystem to the client.
2. When requested, apply a given control value to a hardware subsystem. The hardware must limit the control values to within its minimum or maximum. If a control value is above the maximum, use the maximum. If below the minimum, use the minimum.
3. Turn the hardware on (Start).
4. Given a set of control values and a time (seconds), set the hardware to the control values for the given number of seconds.
5. Turn the hardware off (Stop).

Of course, we won't use actual hardware. Instead, we'll simulate what happens to the hardware over time by creating a csv text file, in which you will record the resulting control data of a recipe applied to a specific part over time between Start and Stop. This is akin to a Data Acquisition System. The DAS simply stores the control values, by time, in a csv file. Each time the hardware Starts, you create a file, and for every second until the Stop, you write the set of control values to the file.

You must implement the Layers pattern for the 3 layers. You must implement the Facade pattern in each layer such that the layer above it is the one and only one that uses the facade. The UserInterface layer facade should only be used by your test program. You must implement the Strategy pattern in the MachineControl layer in order to support the 3 different types of recipe algorithms.

The UserInterface should be simplified and not present any kind of actual UI. Instead, user interaction should be modeled by implementing at least these methods: SetControlValue, GetControlValue, ManualRun, and ExecuteRecipe.

The MachineControl layer should present a simple facade to the UserInterface layer. Regarding recipes, the UI should only need to specify a recipe file and an execute call, for which the MC layer should return a good or bad part result.

For the Hardware layer, you must present a simple facade to the MachineControl layer. For example, it's common for a hardware layer to support 0 to n controllable parameters. In this problem, you are asked to control only air pressure and electrical current. However, your design and implementation must accommodate the notion that in the future, the hardware layer may be changed to incorporate many other types of hardware, but these changes should not affect (or minimally affect) the MachineControl layer. This also applies to Hardware layer requirement #4 above. Your Hardware layer must accept 0 to n control values while executing a recipe.

Notes:

Recipe File:
A recipe is a csv file, with one line of text, with this format:
Recipe name (text, no commas), machine mode, and part size (an integer between 0 and 100)..
The machine mode must be one of the following 3 strings: ConstantPressure, ConstantCurrent, Ramp
Example:
Widget,ConstantPressure,50

DAS File:
The name of the DAS file should be the name of the recipe file, appended with ".DAS.csv".  If the user manually starts the machine, the file should be named Manual.DAS.csv
The first line of a DAS file should be the same as the first line of a recipe, if used, or the text "Manual" if the user manually starts.
The format for the rest of the file is repeating lines of text, with 3 values per line:
Time in seconds (int), Air Pressure in psi (int), Current in amps (int).
Example 1:
Manual
0,15,25
1,15,25
2,15,25

Example 2:
Widget,ConstantPressure,50
0,150,0
1,150,2
2,150,4
etc.

Reference File:
The name of the reference file should be the name of the recipe file, appended with ".reference.csv". 
The format of the file is exactly the same as the DAS file.

A sample ConstantPressure recipe file can be found here
The corresponding reference DAS file can be found here

A sample ConstantCurrent recipe file can be found here
The corresponding reference DAS file can be found here

A sample Ramp recipe file can be found here
The corresponding reference DAS file can be found here

An Excel spreadsheet to generate recipe files and reference data can be found here
As a requirement, your PhFMM must be able to read and execute a recipe file, resulting in a DAS csv file. The recipe file must be read at run time (the parameters cannot be hard coded) and the csv file must also be produced at run time. Your MachineControl must verify that indeed, the produced DAS csv file data matches what was expected. This is done by taking the generated DAS data file and comparing it to the reference file. Your MachineControl must confirm that either the correct data was produced (good part), or there was an error (bad part).

As part of grading, your PhFMM will be given a few other recipe files, along with corresponding DAS reference csv files. Your PhFMM must be able to execute the recipe, generate a DAS csv file, and compare it to the reference file to determine if a good or bad part was manufactured. Also during grading, we will use a recipe with a corresponding bad reference file, in which, after executing the recipe, we will expect your MachineControl to claim that the part is 'bad'.

Therefore, if the reference file matches a good/correct data file (i.e. the code works properly) it will always claim that the part is 'good'.


Notes and Hints:

You must compare the values within the file, not a character by character comparison. You must read each line of data in the file and compare the values with the values that your program generates. The reference files are not binary and might have extra white space characters, but that should not matter. The file size should be meaningless.

No multi-threading is required. While this would be cool, it's not necessary. The user should be able to get the control values after the hardware is done processing. Another optional possibility is to provide an option to have the output printed to the command line for viewing (instead of stored in a file). Furthermore, if you want to watch the output of a file from a unix command line as the hardware is writing to it (for debugging purposes or otherwise), you can use the unix command
tail -f filename
Implementing the Strategy pattern at the Hardware layer was considered, but we chose to require that these types of algorithms be implemented at the MachineControl.
There could certainly be a set of algorithms that would need to be implemented using the strategy pattern in the Hardware layer. But they would be more hardware specific, as opposed to machine 'process' specific. For example, a more realistic Hardware layer might use the strategy pattern to implement:
Closed loop hardware control: PID loops,
Value scaling: while the MachineControl may ask for an electrial current in AMPS, the actual way current is generated might require a much more complicated mechanism/algorithm in the hardware layer.
A 'control value' is a value that eventually gets to the Air Pressure system, or the Electric Current system. For example, the value that 'controls' the amps delivered by the hardware. Sometimes, the value is called the setpoint.
When the MachineControl is executing a recipe, it controls those values. When not executing a strategy, the UserInterface is free to request whatever PSI or Amps the user desires.