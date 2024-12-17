
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

// Gradient: Direction of greatest increase. Taking a step in the opp direction will help us go towards the minimum
// Length of gradient vector indicates the steepness.
// Steps to locate the min: Find gradient, take a step downhill and repeat
abstract class ObjectiveFunction {

    // Compute the objective function value
    abstract double compute(double[] variables);

    // Compute the gradient
    abstract double[] computeGradient(double[] variables);

    // Retrieve the bounds
    abstract double[] getBounds();

    // Retrieve the name of the function
    abstract String getName();

    // Compute the tolerance on the given iteration
    abstract double computeTolerance(double[] gradient);
}

class QuadraticFunction extends ObjectiveFunction {

    // Highest degree is 2
    @Override
    double compute(double[] variables) {
        double result = 0.0;
        for (double variable : variables) {
            result += variable * variable;
        }
        return result;
    }

    @Override
    double[] computeGradient(double[] variables) {
        double[] gradient = new double[variables.length];
        int i = 0;
        for (double variable : variables) {
            gradient[i] = 2 * variable;
            i++;
        }
        return gradient;
    }

    @Override
    double[] getBounds() {
        double bounds[] = {-5, 5};
        return bounds;
    }

    @Override
    String getName() {
        return "Quadratic";
    }

    @Override
    double computeTolerance(double[] gradient) {
        double sumOfSquares = 0.0;
        for (double component : gradient) {
            sumOfSquares += component * component;
        }
        return Math.sqrt(sumOfSquares);
    }

}

class RosenbrockFunction extends ObjectiveFunction {

    @Override
    double compute(double[] variables) {
        double result = 0.0;
        for (int i = 0; i < (variables.length - 1); i++) {
            result += 100 * Math.pow(variables[i + 1] - (variables[i] * variables[i]), 2) + (Math.pow((1 - variables[i]), 2));
        }
        return result;
    }

    @Override
    double[] computeGradient(double[] variables) {
        double[] gradient = new double[variables.length];
        for (int i = 0; i < variables.length - 1; i++) {
            gradient[i] = -400 * variables[i] * (variables[i + 1] - Math.pow(variables[i], 2)) - 2 * (1 - variables[i]);
        }
        gradient[variables.length - 1] = 200 * (variables[variables.length - 1] - Math.pow(variables[variables.length - 2], 2));
        return gradient;
    }

    @Override
    double[] getBounds() {
        double bounds[] = {-5, 5};
        return bounds;
    }

    @Override
    String getName() {
        return "Rosenbrock";
    }

    @Override
    double computeTolerance(double[] gradient) {
        double sumOfSquares = 0.0;
        for (double component : gradient) {
            sumOfSquares += component * component;
        }
        return Math.sqrt(sumOfSquares);
    }

}

// Bonus Function for n-dimensionality Rosenbrock function
class RosenbrockBonusFunction extends ObjectiveFunction {

    @Override
    double compute(double[] variables) {
        double result = 0.0;
        for (int i = 0; i < (variables.length - 1); i++) {
            result += 100 * Math.pow(variables[i + 1] - (variables[i] * variables[i]), 2) + (Math.pow((1 - variables[i]), 2));
        }
        return result;
    }

    @Override
    double[] computeGradient(double[] variables) {
        double[] gradient = new double[variables.length];
        // The gradient for each n
        // n = 1: -400xi (xi+1 - xi^2) - 2(1-xi)
        // n between 1 and last element: 200(xi-x^2i-1) - 400xi(xi+1-xi^2)-2(1-xi)
        // n = n: 200(xi-xi-1^2)
        gradient[0] = -400 * variables[0] * (variables[1] - Math.pow(variables[0], 2)) - 2 * (1 - variables[0]);
        for (int i = 1; i < variables.length - 1; i++) {
            gradient[i] = 200 * (variables[i] - Math.pow(variables[i - 1], 2))
                    - 400 * variables[i] * (variables[i + 1] - Math.pow(variables[i], 2))
                    - 2 * (1 - variables[i]);
        }
        gradient[variables.length - 1] = 200 * (variables[variables.length - 1] - Math.pow(variables[variables.length - 2], 2));
        return gradient;
    }

    @Override
    double[] getBounds() {
        double bounds[] = {-5, 5};
        return bounds;
    }

    @Override
    String getName() {
        return "Rosenbrock_Bonus";
    }

    @Override
    double computeTolerance(double[] gradient) {
        double sumOfSquares = 0.0;
        for (double component : gradient) {
            sumOfSquares += component * component;
        }
        return Math.sqrt(sumOfSquares);
    }

}

class SteepestDescentOptimizer {

    static int dim, itr;
    static int num, in, out;
    static double tol, step;
    static double[] bounds = {-5, 5};
    static String outputPath = null;

    // Optimize Function after creation
    static void optimizeSteepestDescent(ObjectiveFunction objectiveFunction, double[] variables,
            int iterations, double tolerance, double stepSize, int dimensionality) {
        // For itr iterations
        if (out == 1) { // Console output
            double currentTolerance = 0;
            for (int i = 0; i < iterations; i++) {
                // Calculate gradient
                double gradient[] = objectiveFunction.computeGradient(variables);
                // Output Results
                System.out.println("\nIteration " + (i + 1) + ":");
                System.out.printf("Objective Function Value: %.5f\n", floorTo5Decimals(objectiveFunction.compute(variables)));
                System.out.print("x-values: ");
                for (double variable : variables) {
                    System.out.printf("%.5f ", variable);
                }
                // Calcualte tolerance after calculation actually made
                if (i > 0) {
                    System.out.printf("\nCurrent Tolerance: %.5f", floorTo5Decimals(currentTolerance));
                    // Check if tolernace limit reached
                    if (currentTolerance < tolerance) {
                        System.out.println("\n\nConvergence reached after " + (i + 1) + " iterations.");
                        break;
                    }
                    System.out.println("");
                } else {
                    System.out.println("");
                }
                currentTolerance = objectiveFunction.computeTolerance(gradient);
                // Update Values
                for (int j = 0; j < dim; j++) {
                    variables[j] += (stepSize * -gradient[j]);
                    variables[j] = floorTo5Decimals(variables[j]);
                }

                // If succesfully reached the end of the process
                if (i + 1 == iterations) {
                    System.out.println("\nMaximum iterations reached without satisfying the tolerance.");
                }
            }
            System.out.println("\nOptimization process completed.");
        } else if (out == 0) { // File output
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true));
                // Grab/Create output file
                try {
                    double currentTolerance = 0;
                    for (int i = 0; i < iterations; i++) {
                        // Calculate gradient
                        double gradient[] = objectiveFunction.computeGradient(variables);
                        // Output Results
                        writer.write("\nIteration " + (i + 1) + ":");
                        writer.write(String.format("\nObjective Function Value: %.5f\n", floorTo5Decimals(objectiveFunction.compute(variables))));
                        writer.write("x-values: ");
                        for (double variable : variables) {
                            writer.write(String.format("%.5f ", variable)); // check this out later tomorrow . i mean today technically. 
                        }
                        // Calcualte tolerance after calculation actually made
                        if (i > 0) {
                            writer.write(String.format("\nCurrent Tolerance: %.5f", floorTo5Decimals(currentTolerance)));
                            // Check if tolernace limit reached
                            if (currentTolerance < tolerance) {
                                writer.newLine();
                                writer.newLine();
                                writer.write("Convergence reached after " + (i + 1) + " iterations.");
                                break;
                            }
                            writer.newLine();
                        } else {
                            writer.newLine();
                        }
                        currentTolerance = objectiveFunction.computeTolerance(gradient);
                        // Update Values
                        for (int j = 0; j < dim; j++) {
                            variables[j] += (stepSize * -gradient[j]);
                            variables[j] = floorTo5Decimals(variables[j]);
                        }

                        // If succesfully reached the end of the process
                        if (i + 1 == iterations) {
                            writer.newLine();
                            writer.write("Maximum iterations reached without satisfying the tolerance.");
                        }
                    }
                    writer.newLine();
                    writer.newLine();
                    writer.write("Optimization process completed.");
                } catch (IOException e) {
                    // Catch error in writing file
                    System.out.println("Error: Failed to create output file.");
                    System.exit(0);
                }
                writer.close();
            } catch (IOException e) {

            }

        }
    }

    // Floor values to 5 decimal points
    private static double floorTo5Decimals(double value) {
        BigDecimal bd = new BigDecimal(value).setScale(5, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

    // Handle manual input
    static void getManualInput(Scanner input) {
        String function;
        System.out.println("Enter the choice of objective function (quadratic or rosenbrock): ");
        function = input.nextLine();
        System.out.println("Enter the dimensionality of the problem: ");
        dim = Integer.parseInt(input.nextLine());
        System.out.println("Enter the number of iterations:");
        itr = Integer.parseInt(input.nextLine());
        System.out.println("Enter the tolerance:");
        tol = Double.parseDouble(input.nextLine());
        System.out.println("Enter the step Size: ");
        step = Double.parseDouble(input.nextLine());
        double[] variables = new double[dim];
        String line;
        System.out.println("Enter the initial point as " + dim + " space seperated values: ");
        // Accept single string with commas (need to convert after)
        line = input.nextLine();
        String[] proper = line.split(" ");
        // Check that the right number of variables were entered
        if (dim == proper.length) {
            for (int i = 0; i < dim; i++) {
                variables[i] = Double.parseDouble(proper[i]);
            }
        } else {
            System.out.println("Error: Initial point dimensionality mismatch.");
            System.exit(0);
        }
        // Check bounds 
        checkBounds(variables, bounds);
        // If console output selected (manual input, console output)
        if (out == 0) {
            System.out.println("Please provide the path for the output file:");
            outputPath = input.nextLine();
            fileOutput(variables, function);
        } else if (out == 1) {
            consoleOutput(variables, function);
        }

    }

    // Output the starting variabales and inputs
    static void ManualInputTemplate(double[] variables) {
        // Display initial variables 
        System.out.println("Dimensionality: " + dim);
        System.out.print("Initial Point: ");
        for (double variable : variables) {
            System.out.print(variable + " ");
        }
        System.out.printf("\nIterations: %d", itr);
        System.out.printf("\nTolerance: %.5f ", floorTo5Decimals(tol));
        System.out.printf("\nStep Size: %.5f", floorTo5Decimals(step));
        // Call optimize function
        System.out.print("\n\nOptimization Process:");
    }

    // Handle file input
    static void getFileInput(Scanner input) {
        // Ask for file input and check if file output as well
        System.out.println("Please provide the path to the config file:");
        String inputPath = input.nextLine();

        // Create new buffered reader to read the input file
        try { // Use try catch to error check if the file is found
            // Try to read file
            BufferedReader reader = new BufferedReader(new FileReader(inputPath));
            // If console output selected (console input, console output)
            String function = null;
            String readLine;
            if ((readLine = reader.readLine()) != null && (readLine.equals("quadratic") || readLine.equals("rosenbrock") || readLine.equals("rosenbrock_bonus"))) {
                function = readLine;
            } else {
                System.out.println("Error: Unknown objective function.");
                System.exit(0);
            }
            // Ask for output file is requested (after reading input file)
            dim = Integer.parseInt(reader.readLine());
            itr = Integer.parseInt(reader.readLine());
            tol = Double.parseDouble(reader.readLine());
            step = Double.parseDouble(reader.readLine());
            double[] variables = new double[dim];
            String line = reader.readLine();
            String[] proper = line.split(" ");
            // Check that the right number of variables were entered
            if (dim == proper.length) {
                for (int i = 0; i < dim; i++) {
                    variables[i] = Double.parseDouble(proper[i]);
                }
            } else {
                System.out.println("Error: Initial point dimensionality mismatch.");
                System.exit(0);
            }
            // check bounds
            checkBounds(variables, bounds);
            reader.close();
            if (out == 0) {
                System.out.println("Please provide the path for the output file:");
                outputPath = input.nextLine();
                fileOutput(variables, function);
            } else {
                consoleOutput(variables, function);
            }
        } catch (IOException e) {
            System.out.println("Error reading the file.");
        }

    }

    static void consoleOutput(double[] variables, String function) {

        // Check valid function entered
        // If quadratic function selected
        if (function.equals("quadratic")) {
            // Create new function
            QuadraticFunction quadratic = new QuadraticFunction();
            // Print Initial Information
            System.out.println("Objective Function: " + quadratic.getName());
            ManualInputTemplate(variables);
            // Run loop within optimize funciton
            optimizeSteepestDescent(quadratic, variables, itr, tol, step, dim);

// If rosenbrock function selected
        } else if (function.equals("rosenbrock")) {

            // Create new function and optimize
            RosenbrockFunction rosenbrock = new RosenbrockFunction();
            // Print Initial Information
            System.out.println("Objective Function: " + rosenbrock.getName());
            ManualInputTemplate(variables);
            // Optimize
            optimizeSteepestDescent(rosenbrock, variables, itr, tol, step, dim);

            // Rosenbrock Bonus
        } else if (function.equals("rosenbrock_bonus")) {
            // Create new function and optimize
            RosenbrockBonusFunction rosenbrock_bonus = new RosenbrockBonusFunction();
            // Print Initial Information
            System.out.println("Objective Function: " + rosenbrock_bonus.getName());
            ManualInputTemplate(variables);
            // Optimize
            optimizeSteepestDescent(rosenbrock_bonus, variables, itr, tol, step, dim);

        } else {
            System.out.println("Error: Unknown objective function.");
        }
    }

    static void fileOutput(double[] variables, String function) {
        // Check valid function entered 
        if (function.equals("quadratic")) {
            // Create new function
            QuadraticFunction quadratic = new QuadraticFunction();
            // State Given Variables
            // FileInputTemplate(quadratic, variables);
            try {
                // Writing the initial vairables and starting stuff
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));

                writer.write("Objective Function: " + quadratic.getName());
                writer.newLine();
                writer.close();
                // Print initial template/information
                FileInputTemplate(variables);
                // Run loop within optimize funciton
                optimizeSteepestDescent(quadratic, variables, itr, tol, step, dim);
            } catch (IOException e) {
                System.out.println("Error writing file.");
            }
        } else if (function.equals("rosenbrock_bonus")) {
            // Create new function
            RosenbrockBonusFunction rosenbrock_bonus = new RosenbrockBonusFunction();
            // State Given Variables
            try { // Do try catch for each statement
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));

                writer.write("Objective Function: " + rosenbrock_bonus.getName());
                writer.newLine();
                writer.close();
                FileInputTemplate(variables);
                // Run loop within optimize funciton
                optimizeSteepestDescent(rosenbrock_bonus, variables, itr, tol, step, dim);

                writer.close();
            } catch (IOException e) {
                System.out.println("Error writing the file.");
            }
        } else if (function.equals("rosenbrock")) {
            // Create new function
            RosenbrockFunction rosenbrock = new RosenbrockFunction();
            // State Given Variables
            try { // Do try catch for each statement
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));

                writer.write("Objective Function: " + rosenbrock.getName());
                writer.newLine();
                writer.close();
                FileInputTemplate(variables);
                // Run loop within optimize funciton
                optimizeSteepestDescent(rosenbrock, variables, itr, tol, step, dim);
            } catch (IOException e) {
                System.out.println("Error writing file.");
            }
        } else {
            System.out.println("Error: Unknown objective function.");
            System.exit(0);
        }
    }

    // Output the starting variabales and inputs
    static void FileInputTemplate(double[] variables) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true));

            writer.write("Dimensionality: " + dim);
            writer.newLine();
            writer.write("Initial Point: ");
            for (double variable : variables) {
                writer.write(variable + " ");
            }
            writer.newLine();
            writer.write("Iterations: " + itr);
            writer.newLine();
            writer.write(String.format("Tolerance: %.5f", floorTo5Decimals(tol)));
            writer.newLine();
            writer.write(String.format("Step Size: %.5f", floorTo5Decimals(step)));
            writer.newLine();
            writer.newLine();
            // Call optimize function
            writer.write("Optimization Process:");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing file.");
        }
    }

    // Check intial points are in bounds
    static void checkBounds(double[] variables, double[] bounds) {
        for (double var : variables) {
            if (var < bounds[0] || var > bounds[1]) {
                System.out.println("Error: Initial point " + var + " is outside the bounds [-5.0, 5.0].");
                System.exit(0);
            }
        }
    }

    // Check valid input
    static int getValidatedInput(Scanner input, String message) {
        System.out.println(message);
        try {
            num = Integer.parseInt(input.nextLine());
            if (num != 1 && num != 0) {
                System.out.println("Please enter a valid input (0 or 1).");
                return 1000;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid input (0 or 1).");
            return 1000;
        }
        return num;
    }

    public static void mainCode() {
        String prompt;
        Scanner input = new Scanner(System.in);
        // Initial user settings (grab preferences)
        while (true) {
            prompt = "Press 0 to exit or 1 to enter the program: ";
            num = getValidatedInput(input, prompt);
            if (num == 1) {
                break;
            } else if (num == 0) {
                System.out.println("Exiting program...");
                break;
            }
        }
        if (num == 1) {
            while (true) {
                prompt = "Press 0 for .txt input or 1 for manual input: ";
                in = getValidatedInput(input, prompt);
                if (in == 1 || in == 0) {
                    break;
                }
            }

            while (true) {
                prompt = "Press 0 for .txt output or 1 for console output: ";
                out = getValidatedInput(input, prompt);
                if (out == 1 || out == 0) {
                    break;
                }
            }
            // Get input
            if (in == 0) {
                getFileInput(input);
            } else if (in == 1) {
                getManualInput(input);
            }
        }
        input.close();
    }
}

public class asst2_wangja54 {

    public static void main(String[] args) {
        // Call Steepest descenet optimizer
        SteepestDescentOptimizer.mainCode();
    }

}
