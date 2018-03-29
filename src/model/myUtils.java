package model;

public class myUtils {
	public static void pprint(String message, int indent) {
		for (int i=0; i<indent;i++) {
			System.out.print("\t\t");
		}
		System.out.println(message);
	}
}
