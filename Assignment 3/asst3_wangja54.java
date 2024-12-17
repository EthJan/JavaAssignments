import java.io.*;
import java.util.ArrayList;

// LU Factor class
class luFactor {
    public static void factor(String inputPath) {
        System.out.println("Starting.");
        input(inputPath); // Call the input method with the file path
    }

    static void input(String inputPath) {
        // Get config file 
        String configPath;
        String [] parallelState; 
        try {
            // Config file read
            BufferedReader configReader= new BufferedReader((new FileReader("config.txt")));
            configPath = configReader.readLine();
            parallelState = configPath.split("=");
            String parallel = parallelState[1];

            configReader.close();
            // Write this to the file
            BufferedWriter configWriter= new BufferedWriter((new FileWriter("output.txt", true)));
            configWriter.write ("\nExecution mode: ");
            if (parallel.equals("true")){
                configWriter.write ("parallel\n");
            } else {
                configWriter.write ("sequential\n ");
            }
            configWriter.close();
            
            // Read matrix input file
            BufferedReader reader = new BufferedReader(new FileReader(inputPath));
            String readLine;
            System.out.println("Contents of the file '" + inputPath + "':");
            int size =0;
            ArrayList<double[]> rows= new ArrayList<>();
            while ((readLine = reader.readLine()) != null) {
                String[] proper =readLine.split(" ");
                size = proper.length;
                double row[] = new double[size]; 
                for (int i = 0; i < size; i++){
                    // Assign to new row 
                    row[i] = Double.parseDouble(proper[i]);
                }   
                rows.add(row);
                
            }
            reader.close(); 

            // Get number of rows
            int rowCount = rows.size();
            // Check square matrix
            readMatrix(size, rowCount);

            double [][] matrix = new double [size][];
            for (int i = 0; i < size; i ++){
                matrix[i] = rows.get(i);
            }
            
            // Update output file
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true));
                // OG matrix
                writer.write("\nMatrix A: \n");
                for (int i = 0; i < size; i++){
                    for (int j = 0; j < size; j++){
                        writer.write(String.format("%.1f ", matrix[i][j]));
                    }
                    writer.write("\n");
                
                }
                writer.close();
    
            } catch (IOException e) {
    
            }
            // Create matrices and perform calculations
            if (parallel.equals("true")){
                System.out.println("Parallel Computing");
                DoLittleP(size, matrix, inputPath);
            } else {
                DoLittle(size, matrix, inputPath);
            }
            
            
            
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + inputPath);
        } catch (IOException e) {
            System.err.println("Error: Unable to read the file - " + inputPath);
        }

    }

    static void readMatrix(int size, int rows){
        try {
            if (rows == size){
                // Proceed to Dolittle Algorithm
            } else{
                throw new IllegalArgumentException ();
            }
        } catch (IllegalArgumentException e) {
            // If number of rows does not equal the size/number of columns, can't perform
            try {
                
                BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true));
                // OG matrix
                writer.write("\nError: Matrix must be square.");
                writer.close();
                System.exit(0);
                //writer.write(String.format("%.4f ", value));
    
            } catch (IOException f) {
    
            }
        }
        
    }
    // To do 
   static void DoLittleP(int size, double [][] matrix, String inputPath){
        double [][] lower = new double[size][size];
        double [][] upper = new double[size][size];
        // L matrix diagonal intialize
        for (int i = 0; i < size; i++){ // row
            lower [i][i] = 1.0; 
        }
        //availble threads
        // create array of threads
        // calcaulte chunk size for each thread
        // Get the number of available processors
        int numThreads = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[numThreads];

        // Calculate the chunk size for each thread
        int chunkSize = size / numThreads;

        // Create and start threads
        for (int a = 0; a < numThreads; a++) {
            final int startRow = a * chunkSize;
            final int endRow = (a == numThreads - 1) ? size : startRow + chunkSize; // Last row handles remain

            threads[a] = new Thread(() -> {
                for (int row = startRow; row < endRow; row++) {
                        for (int j = row; j < size; j++){ // column
                            double sum = 0.0; 
                            for (int k = 0; k < row; k++){
                                sum += lower[row][k] * upper[k][j];
                            }
                            upper [row][j] = matrix[row][j] - sum;
                            if (row == j){
                                decompose(size, upper [row][j]);
                            }
                        }
        
                        for (int j = row + 1; j < size; j++){ // column
                            double sum = 0.0; 
                            for (int k = 0; k < row; k++){
                                sum += lower[j][k] * upper[k][row];
                            }
                            lower [j][row] = (matrix[j][row] - sum)/ (upper [row][row]);
                            
                        }
                    
                }
            });
            threads[a].start();
        }

        // Join threads to wait for all to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true));
            writer.write("\nFinal Matrix L: \n");
            for (int i = 0; i < size; i++){
                for (int j = 0; j < size; j++){
                    writer.write(String.format("%.1f ", lower[i][j]));
                }
                writer.write("\n");
            }

            writer.write("\nFinal Matrix U: \n");
            for (int i = 0; i < size; i++){
                for (int j = 0; j < size; j++){
                    writer.write(String.format("%.1f ", upper[i][j]));
                }
                writer.write("\n");
            
            }

            writer.close();
        } catch (IOException f) {
        }
        difference(size, matrix, lower, upper);
    }
    
    static void DoLittle(int size, double [][] matrix, String inputPath){
        // Initialize matrices L and U
        double [][] lower = new double[size][size];
        double [][] upper = new double[size][size];
        // L matrix diagonal intialize
        for (int i = 0; i < size; i++){ // row
            lower [i][i] = 1.0; 
        }

        try {
            // Calculate
            // Upper triangle matrix calculation
            for (int i = 0; i < size; i++){ // row
                for (int j = i; j < size; j++){ // column
                    
                    double sum = 0.0; 
                    for (int k = 0; k < i; k++){
                        sum += lower[i][k] * upper[k][j];
                    }
                    upper [i][j] = matrix[i][j] - sum;
                    if (i == j){
                        decompose(size, upper [i][j]);
                    }
                    
                }
                
                for (int j = i + 1; j < size; j++){ // column
                    double sum = 0.0; 
                    for (int k = 0; k < i; k++){
                        sum += lower[j][k] * upper[k][i];
                    }
                    lower [j][i] = (matrix[j][i] - sum)/ (upper [i][i]);
                    
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true));
            writer.write("\nFinal Matrix L: \n");
            for (int i = 0; i < size; i++){
                for (int j = 0; j < size; j++){
                    writer.write(String.format("%.1f ", lower[i][j]));
                }
                writer.write("\n");
            }

            writer.write("\nFinal Matrix U: \n");
            for (int i = 0; i < size; i++){
                for (int j = 0; j < size; j++){
                    writer.write(String.format("%.1f ", upper[i][j]));
                }
                writer.write("\n");
            
            }

            writer.close();

            
        } catch (IOException f) {
        }
        
        difference(size, matrix, lower, upper);
    }

    static void decompose(int size, double matrix){
        try {
            if (matrix == 0){
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                writer.write("\nError: Matrix is singular, cannot perform decomposition.\n");
            } catch (IOException f) {
                System.err.println("Error writing.");
            }
            System.exit(0);
        }
           
        
    }
    static void difference(int size, double[][] matrix, double[][] lower, double[][] upper) {
        double[][] differenceMatrix = new double[size][size];
        
        // Calculate the difference matrix
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double sum = 0.0;
                for (int k = 0; k < size; k++) {
                    sum += lower[i][k] * upper[k][j];
                }
                differenceMatrix[i][j] = matrix[i][j] - sum;
            }
        }
    
        // Write the difference matrix to the output file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
            writer.write("\nDifference Matrix (A - LU):\n");
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    writer.write(String.format("%.4f ", differenceMatrix[i][j]));
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing the difference matrix to output file.");
        }
    
        // Proceed to calculate tolerance
        toleranceCalculate(size, matrix, lower, upper,differenceMatrix);
    }
    
    

    static void toleranceCalculate(int size, double[][] matrix, double [][] lower, double[][] upper, double[][] differenceMatrix) {
        double tolerance = 0.0;
        double differenceSquared = 0.0;
    
        // Calculate the sum of squared differences
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                differenceSquared += differenceMatrix[i][j] * differenceMatrix[i][j];
            }
        }
    
        // Compute the tolerance
        tolerance = Math.sqrt(differenceSquared);
    
        // Write the tolerance to the output file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
            writer.write(String.format("\nTolerance (difference between A and LU): %.4f\n", tolerance));
            writer.write(String.format("\nDecomposition complete. Results written to output.txt"));
        } catch (IOException e) {
            System.err.println("Error writing tolerance to output file.");
        }
    }
    
}
// Main class
public class asst3_wangja54 {
    public static void main(String[] args) {
        String inputPath;
        Boolean path = false; 
        // Check if the file path is provided as a command-line argument
        if (args.length > 0) {
            inputPath = args[0]; 
        } else {
            path = true;
            inputPath = "input.txt"; //if empty
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            if (path){
                writer.write("No input file specified. Using default: input.txt\n");
                writer.write("Input file: " + inputPath);
            } else {
                writer.write("Input file: " + inputPath);
            }
            
            writer.write("\nOutput file: output.txt");
            writer.close();
        } catch (IOException e) {

        }

        luFactor.factor(inputPath); // start factorization
    }
}
