/**
 * Class that contains a simple method for creating the same square 2d array based on a size
 *
 * @author John Doggett
 * @date Fri 6-Mar-2020
 *
 */
public class Generate2DArray {
	
	/**
	 * returns a square 2d array that will always be the same based on size, each value in the 2d array is unique
	 * 
	 * @author John Doggett
	 * @date Fri 6-Mar-2020
	 * 
	 * @param size of 2d array
	 * @return generated 2d array
	 */
	public static double[][] createAnArray(int size){
		int c = 0;
		double[][] output = new double[size][size];
		for(int a = 0; a < size; a++) {
			for(int b = 0; b < size; b++) {
				output[a][b] = c;
				c++;
			}
		}
		return output;
	}
}
