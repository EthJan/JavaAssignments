import java.util.Scanner;
public class asst1_wangja54{
    public static double getValidInput(int t){
        // Intialize new variables
        Scanner input = new Scanner(System.in);
        boolean valid;
        double user = 0;
        //Receive user coil wire diameter input
        if (t == 1){
            do{
                // Reset valid boolean
                valid = true;
                try {
                    // Fetch user input
                    System.out.print("Enter coil diameter D (m): ");
                    user = Double.parseDouble(input.nextLine());
                    // Go through input check function 
                    if (!CheckError(1, user, 0,0)){
                        // If test case triggered, invalid input so set valid to false
                        valid = false;
                    }
                //Catch invalid user inputs (character)
                } catch (NumberFormatException e) {
                    System.out.println("ENTER A VALID INPUT");
                    valid = false;
            }}while (!valid);
        }
        //Receive user wire diameter input
        if (t==2){
            do{
                // Reset valid boolean
                valid = true;
                try {
                    // fetch user inputs
                    System.out.print("Enter wire diameter d (m): ");
                    user = Double.parseDouble(input.nextLine());
                    if (!CheckError(2, 0, user,0)){
                        // if test case triggered, invalid input so set valid to false
                        valid = false;
                    }
                    // Catch user character input
                } catch (NumberFormatException e) {
                    System.out.println("ENTER A VALID INPUT");
                    valid = false;
            }}while (!valid);
        }
        //Receive user turns input
        if (t ==3){
            do{
                // Reset valid boolean
                valid = true;
                try {
                    // Fetch user input
                    System.out.print("Enter number of turns N: ");
                    user= Double.parseDouble(input.nextLine());
                    // Check if n is an integer
                    if (user%1 != 0){
                        System.out.println("N SHOULD BE AN INTEGER");
                        valid = false;
                    }
                    if (!CheckError(3, 0, 0, user)){
                        // if test case triggered valid becomes false
                        valid = false;
                    }
                    // catch character input
                } catch (NumberFormatException e) {
                    System.out.println("ENTER A VALID INPUT");
                    valid = false;
                }  
            }while (!valid);
            input.close();
        }
        return user;
    }
    //Valid Input Check
    public static boolean CheckError(int t, double D, double d, double n){
        // Check inputs D and d are positive
        if (D < 0|| d < 0 ){
            System.out.println("ENTER A POSITIVE INPUT");
            // Return false to indicate invalid input
            return false;
        }
        // Check input n is positive
        if (n < 0){
            System.out.println("N SHOULD BE A POSITIVE INTEGER");
            return false;
        }
        // For test cases 1 and 2 (Coil and wire) check inputs are within bounds
        if (((t == 1) && (D < 0.25 || D > 1.3)) || ((t == 2)  && (d < 0.05 || d > 2))){
                System.out.println("INPUT MUST BE WITHIN BOUNDS");
                return false;
        } 
        // For test case 3 check within bounds and valid integer
        if (t == 3){
            if (n < 1 || n > 15){
                System.out.println("INPUT MUST BE WITHIN BOUNDS");
                return false;
            }
        }
        // Return true if no test cases triggered
        return true;
    }
    //Calculator
    public static double calculate(double coil_diameter, double wire_coil_diameter, double turns){
        double n = (turns +2)*coil_diameter * (wire_coil_diameter*wire_coil_diameter) * 9.81;
        // After making calculation, return output
        return n;
    }
    //Exit Message
    public static void Exit(){
        // Print goodbye and close system
        System.out.println("GOODBYE!");
        System.exit(0);
    }
    public static void main (String[] args){
        //Declare variables
        double coil_diameter = 0.0 , wire_coil_diameter = 0.0, turns = 0.0;
        int entry_code;
        Scanner input = new Scanner(System.in);
    //Prompt user for welcome choice untill exit or continue input recieved
        do{
            System.out.print("WELCOME TO THE SPRING WEIGHT CALCULATOR (0 TO QUIT, 1 TO PROCEED) ");
            entry_code = Integer.parseInt(input.nextLine());
        // Check if user wants to exit
            if (entry_code == 0){
                Exit();
            }
        }while ((entry_code != 1));

    //Grab user inputs through getValidInput function
        coil_diameter = getValidInput(1);
        wire_coil_diameter = getValidInput(2);
        turns = (int)getValidInput(3);

    //Calculate and print to terminal
        double weight = calculate(coil_diameter, wire_coil_diameter, turns);

    //Truncate until 2 decimal places
        int newWeight = (int)(weight * 100);
        weight = ((double)(newWeight))/100;
        System.out.println("Weight: " + String.format("%.2f", weight) + " kgm/s^2");

    //Close all programs and tools
        input.close();
        Exit();
    }
}
