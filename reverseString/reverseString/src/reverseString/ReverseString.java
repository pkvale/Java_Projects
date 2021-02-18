package reverseString;

import java.util.Scanner;

public class ReverseString {

	public static void main(String[] args) {
		String UserString = getUserString();
		System.out.println("The user's value is: " + UserString);
		String StringBackwards = reverseString(UserString);
		System.out.println("The backwards string is: " + StringBackwards);
	}
	private static String getUserString () {
		Scanner myScanner = new Scanner (System.in);
		String userString = "";
		System.out.println("Please enter a value for a string");
		userString = myScanner.nextLine();
		return userString;
	}
	private static String reverseString (String stringParam) {
		String stringForward = stringParam;
		char [] arrayForward = stringForward.toCharArray();
		char [] arrayBackward = new char [arrayForward.length];
		String stringBackward = "";
		for (int i=0; i < arrayForward.length; i++) {
			arrayBackward [i] = arrayForward[arrayForward.length-(i+1)];
			stringBackward = stringBackward + arrayBackward[i];
		}
		return stringBackward;
	}
}
