
import java.io.*;
import java.util.*;

public class Main {
	public static void main(String[] args) {
		LilSearchEngine engine = new LilSearchEngine();
		Scanner scanner = new Scanner(System.in);
		String docs = "docs.txt";
		String noiseWords = "noisewords.txt";

		try {
			engine.makeIndex(docs,noiseWords);
		} catch(FileNotFoundException e) {};
		
		System.out.println("Please enter two words to search the books for:");
		System.out.println("Enter word 1: ");
		String word1 = scanner.nextLine();		
		System.out.println("Enter word 2: ");
		String word2 = scanner.nextLine();		

		System.out.println(engine.top5search(word1, word2));
		scanner.close();
	}
}