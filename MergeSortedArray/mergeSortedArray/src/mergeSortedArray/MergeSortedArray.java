package mergeSortedArray;
import java.util.Arrays;
public class MergeSortedArray {

	public static void main(String[] args) {
		//merges 2 arrays of varying length that are sorted and returns a new sorted array
		int [] myArray = combineTwoArrays(new int [] {1, 2, 4, 7}, new int [] {0, -1, 2, 4});
		Arrays.sort(myArray);
		System.out.println(Arrays.toString(myArray));
	}
	private static int [] combineTwoArrays(int [] array1, int[] array2) {
		int [] combinedArray = new int [array1.length + array2.length];
		for (int i = 0; i < combinedArray.length; i++) {
			if (i <= array1.length-1) {
				combinedArray[i] = array1[i];
				System.out.println("The item added was: " + combinedArray[i]);
			}
			else if (array1.length - i <= array2.length-1){
				combinedArray[i] = array2[i-array1.length];
				System.out.println("The item added was: " + combinedArray[i]);
			}
			else {
				
			}
		}
		return combinedArray;
	}
	
}
