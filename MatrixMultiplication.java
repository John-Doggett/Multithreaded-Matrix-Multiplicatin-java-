

import java.util.Date;

/** 
 * Simple class showing single-threaded matrix multiplication. Includes methods that are easily split up
 * to allow easy integration with threading.
 * 
 * @author John Doggett
 * @date Thu 27-Feb-2020
 *
 */
public class MatrixMultiplication {

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
		for(int a = 0; a < arrayTwo.length; a++) {
			output += arrayOne[a] * arrayTwo[a];
		}
		return output;
	}

	/** 
	 * converts standard matrix from [column][row] to [row][column]
	 * 
	 * @author John Doggett
	 * @date Thu 27-Feb-2020
	 * 
	 * @param matrix 
	 * @return converted matrix
	 */
	static double[][] convertSecondMatrixFormat(double[][] matrix){
		double[][] output = new double[matrix[0].length][matrix.length];
		for(int a = 0; a < matrix.length; a++) {
			for(int b = 0; b < matrix[0].length; b++) {
				output[b][a] = matrix[a][b];
			}
		}
		return output;
	}

	/** 
	 * creates an output array based on multiply a column of matrix one by matrix two
	 * 
	 * @author John Doggett 
	 * @date Thu 27-Feb-2020
	 *  
	 * @param matrixOneLine
	 * @param matrixTwo
	 * @return row of the sum of the matrix with respect to a single matrix one row
	 */
	static double[] multiplyOneArrayInMatrix(double[] matrixOneLine, double[][] matrixTwo) {
		double[] output = new double[matrixTwo.length];
		for(int a = 0; a < matrixTwo.length; a++) {
			output[a] = multiplyTwoArrays(matrixOneLine, matrixTwo[a]);
		}
		return output;
	}

	/** 
	 * generates a new empty matrix based on [matrix one's column length][matrix two's row length]
	 * if the two were multiplied
	 * 
	 * @author John Doggett
	 * @date Thu 27-Feb-2020
	 * 
	 * @param matrixOne
	 * @param matrixTwo
	 * @return output empty matrix
	 */
	static double[][] generateEmptyOutputMatrix(double[][] matrixOne, double[][] matrixTwo){
		return new double[matrixOne.length][matrixTwo[0].length];
	}

	/** 
	 * multiplies two matrices together by running threw each row of matrix one and multiplying it to matrix two
	 * 
	 * @author John Doggett
	 * @date Thu 27-Feb-2020
	 * 
	 * @param matrixOne
	 * @param matrixTwo
	 * @return the output sum matrix
	 */
	static double[][] multiplyMatrix(double[][] matrixOne, double[][] matrixTwo) {
		double[][] output = MatrixMultiplication.generateEmptyOutputMatrix(matrixOne, matrixTwo);
		double[][] fixedMatrixTwo = MatrixMultiplication.convertSecondMatrixFormat(matrixTwo);
		for(int a = 0; a < matrixOne.length; a++) {
			output[a] = MatrixMultiplication.multiplyOneArrayInMatrix(matrixOne[a], fixedMatrixTwo);
		}
		return output;
	}
	
	// MAIN METHOD, may require extra allocated ram to run depending on size of calculation
	public static void main(String[] args) {
		//Create input matrices. Both matrices must call the same size createAnArray. 
		double[][] one = Generate2DArray.createAnArray(2000); //create matrix one
		double[][] two = Generate2DArray.createAnArray(2000); //create matrix two
		
		long t1 = java.lang.System.nanoTime();
		
		double[][] sum = multiplyMatrix(one, two); //multiply matrix one x matrix two
		
		long t2 = java.lang.System.nanoTime();	
		System.out.println(t2-t1); //print out time it took to find sum, in nano seconds
	}

}