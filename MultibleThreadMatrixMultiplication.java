
import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Class that demonstrates Multi-Threaded Matrix Multiplication. The amount of columns of matrix one are proportionally split for each CPU core to do partial
 * matrix multiplication.
 * 
 * @author John Doggett
 * @date Fri 6-Mar-2020
 *
 */
public class MultibleThreadMatrixMultiplication {

	/**
	 * converts standard matrix from [column][row] to [row][column]
	 * 
	 * @author John Doggett
	 * @date Thu 27-Feb-2020
	 * 
	 * @param matrix
	 * @return converted matrix
	 */
	static double[][] convertSecondMatrixFormat(double[][] matrix) {
		double[][] output = new double[matrix[0].length][matrix.length];
		for (int a = 0; a < matrix.length; a++) {
			for (int b = 0; b < matrix[0].length; b++) {
				output[b][a] = matrix[a][b];
			}
		}
		return output;
	}

	/**
	 * generates a new empty matrix based on [matrix one's column length][matrix
	 * two's row length] if the two were multiplied
	 * 
	 * @author John Doggett
	 * @date Thu 27-Feb-2020
	 * 
	 * @param matrixOne
	 * @param matrixTwo
	 * @return output empty matrix
	 */
	static double[][] generateEmptyOutputMatrix(double[][] matrixOne, double[][] matrixTwo) {
		return new double[matrixOne.length][matrixTwo[0].length];
	}

	/**
	 * copies a 2d array
	 * 
	 * @author John Doggett
	 * @date Fri 6-Mar-2020
	 * 
	 * @param input array to be copied
	 * @return copied array
	 * 
	 */
	static double[][] copy2dArray(double[][] input) {
		double[][] output = new double[input.length][];
		for (int a = 0; a < input.length; a++) {
			output[a] = new double[input[a].length];
			for (int b = 0; b < input[a].length; b++) {
				output[a][b] = input[a][b];
			}
		}
		return output;
	}

	/**
	 * Multiplies two matrices together by running threw each row of matrix one and multiplying it to matrix two.
	 * matrixOne is divided into equal proportions with the amount of CPU cores, along with a copy of matrixTwo,
	 * the starting position of which portion of matrixOne, and a pointer to output; all of which are used to
	 * create a PartialMatrixMultiplication. The PartialMatrixMultiplication threads are run once all of the objects have
	 * been constructed. 
	 * 
	 * @author John Doggett
	 * @date Fri 6-Mar-2020
	 * 
	 * @param matrixOne the first matrix to be multiplied
	 * @param matrixTwo the second matrix to be multiplied
	 * @return the product matrix of matrixOne and matrixTwo
	 */
	static double[][] multiplyMatrix(double[][] matrixOne, double[][] matrixTwo) {
		double[][] output = generateEmptyOutputMatrix(matrixOne, matrixTwo);
		double[][] fixedMatrixTwo = convertSecondMatrixFormat(matrixTwo);
		int numberOfCores = Runtime.getRuntime().availableProcessors();

		ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(numberOfCores);
		int currentPosition = 0;

		ArrayList<Runnable> temp = new ArrayList<Runnable>(); //temporary collection to hold the newly constructed PartialMatrixMultiplication objects
		for (int a = 0; a < numberOfCores; a++) { //divide up matrixOne for each CPU core
			int end = currentPosition + (matrixOne.length / numberOfCores); //adjust the end to current position plus amount of matrix one per core
			if (end >= matrixOne.length) { //if the end is more than the actual number of columns in matrix one, set end to the last column in matrix one
				end = matrixOne.length - 1;
			}
			//add thread to a collection once it is constructed
			temp.add(new PartialMatrixMultiplication(currentPosition, getSubArray(matrixOne, currentPosition, end),
					copy2dArray(fixedMatrixTwo), output));
			//accumulate current position to its current value plus the rounded up columns of matrix one per core
			currentPosition += (int) (Math.ceil(Double.valueOf(matrixOne.length) / Double.valueOf(numberOfCores))); 
			//if current position is larger than the amount of columns in matrix one: all the columns have been assigned, end loop. Caused if columns of matrix one < CPU cores
			if (currentPosition >= matrixOne.length) { 
				a = numberOfCores;
			}
		}
		for (int a = 0; a < temp.size(); a++) {
			executor.execute(temp.get(a));	//execute each thread
		}
		executor.shutdown();
		while(!executor.isTerminated()) {
			//wait for threads to end
		}
		return output;
	}

	/**
	 * returns a partial matrix of input inclusive start column to the end column
	 * 
	 * @author John Doggett
	 * @date Fri 6-Mar-2020 
	 * 
	 * @param input master matrix
	 * @param start start column
	 * @param end end column
	 * @return partial matrix
	 */
	static double[][] getSubArray(double[][] input, int start, int end) {
		double[][] output = new double[end - start + 1][];
		int b = 0;
		for (int a = start; a <= end; a++) {
			output[b] = new double[input[a].length];
			for (int c = 0; c < input[a].length; c++) {
				output[b][c] = input[a][c];
			}
			b++;
		}

		return output;
	}

	// MAIN METHOD, may require extra allocated ram to run depending on size of calculation
	public static void main(String[] args) {
		//Create input matrices. Both matrices must call the same size createAnArray. 
		double[][] one = Generate2DArray.createAnArray(2000); //create matrix one
		double[][] two = Generate2DArray.createAnArray(2000); //create matrix two

		long t1 = java.lang.System.nanoTime(); 

		double[][] sum = multiplyMatrix(one, two); // multiply matrix one x matrix two

		long t2 = java.lang.System.nanoTime();
		System.out.println(t2 - t1); //print out time it took to find sum, in nano seconds
		
	}
	/**
	 * Inner class of MultibleThreadMatrixMultiplication. Performs a partial matrix multiplication based on the columns given by matrixOne.
	 * 
	 * Implements Runnable.
	 * 
	 * int beginningIndexOfMasterMatrix, the index at which the thread is assigned in matrixOne
	 * double[][] partialMatrixOne, a partial matrix of matrixOne
	 * double[][] fixedMatrixTwo, a copy of fixedMatrixTwo
	 * double[][] output, pointer to the output array found in the method multiplyMatrix
	 * 
	 * @author John Doggett
	 * @date Fri 6-Mar-2020 
	 *
	 */
	private static class PartialMatrixMultiplication implements Runnable {
		int beginningIndexOfMasterMatrix;
		double[][] partialMatrixOne;
		double[][] fixedMatrixTwo;
		double[][] output;

		/**
		 * Constructor for PartialMatrixMultiplication
		 * 
		 * @author John Doggett
		 * @date Fri 6-Mar-2020 
		 * 
		 * @param beginningIndexOfMasterMatrix the index at which the thread is assigned in matrixOne
		 * @param partialMatrixOne a partial matrix of matrixOne
		 * @param fixedMatrixTwo a copy of fixedMatrixTwo
		 * @param output pointer to the output array found in the method multiplyMatrix
		 */
		public PartialMatrixMultiplication(int beginningIndexOfMasterMatrix, double[][] partialMatrixOne,
				double[][] fixedMatrixTwo, double[][] output) {
			super();
			this.beginningIndexOfMasterMatrix = beginningIndexOfMasterMatrix;
			this.partialMatrixOne = partialMatrixOne;
			this.fixedMatrixTwo = fixedMatrixTwo;
			this.output = output;
		}

		/**
		 * creates a temp matrix output. multiplies partialMatrixOne with the copy of matrixTwo. In synchronization, edits output starting on 
		 * beginning index from calculated columns in temp.
		 * 
		 * @author John Doggett
		 * @date Fri 6-Mar-2020 
		 * 
		 */
		@Override
		public void run() {
			double[][] temp = new double[partialMatrixOne.length][fixedMatrixTwo.length];
			for (int b = 0; b < partialMatrixOne.length; b++) {
				for (int a = 0; a < fixedMatrixTwo.length; a++) {
					temp[b][a] = PartialMatrixMultiplication.multiplyTwoArrays(partialMatrixOne[b], fixedMatrixTwo[a]);
				}
			}
			for (int a = 0; a < temp.length; a++) {
				synchronized (output) {
					output[a + beginningIndexOfMasterMatrix] = temp[a];
				}
			}
		}

		/** 
		 * accumulates the product of each position of two arrays
		 * 
		 * @author John Doggett
		 * @date Thu 27-Feb-2020
		 * 
		 * @param arrayOne
		 * @param arrayTwo
		 * @return value of accumulation
		 */
		static double multiplyTwoArrays(double[] arrayOne, double[] arrayTwo) {
			double output = 0;
			for (int a = 0; a < arrayTwo.length; a++) {
				output += arrayOne[a] * arrayTwo[a];
			}
			return output;
		}

	}
}