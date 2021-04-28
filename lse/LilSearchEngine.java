

import java.io.*;
import java.util.*;

//Builds index of keywords. Each keyword maps to a set of pages in which it occurs, with frequency of occurrence in each page.
public class LilSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LilSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		if (docFile == null) {
			throw new FileNotFoundException();
		}
		HashMap<String,Occurrence> keywords = new HashMap<String,Occurrence>();
		
		Scanner scan = new Scanner(new File(docFile));
		while (scan.hasNext()) {
			String word = getKeyword(scan.next());
			if (word == null) {
				continue;
			}
			if (!keywords.containsKey(word)) {
				Occurrence occurrence = new Occurrence(docFile,1);
				keywords.put(word,occurrence);
			} else {
				Occurrence occurrence = keywords.get(word);
				occurrence.frequency++;
			}
		}
		//System.out.println(keywords.toString());
		return keywords;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		for (String i : kws.keySet()) {
			ArrayList<Occurrence> occurrence = new ArrayList<Occurrence>();
			if (keywordsIndex.containsKey(i)) {
				occurrence = keywordsIndex.get(i);
			}
			occurrence.add(kws.get(i));
			insertLastOccurrence(occurrence);
			keywordsIndex.put(i,occurrence);
		}
		//System.out.println(keywordsIndex.toString());
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER COUNTS AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		if (word == null) {
			return null;
		}
		word = word.toLowerCase();
		boolean letterPassed = false;
		for (int i = word.length()-1; i >= 0; i--) {
			if (!Character.isLetter(word.charAt(i))) {
				if (letterPassed) {
					return null;
				} else {
					word = word.substring(0,i);
				}
			} else {
				letterPassed = true;
			}
		}
		if (noiseWords.contains(word)) {
			return null;
		}
		if (word.length() <= 0) {
			return null;
		}
		return word;
	}
	
	/**
	 * Inserts last occurrence in the parameter list in the right position in the
	 * list based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only for testing the code.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		if (occs.size() <= 1) {
			return null;
		}
		ArrayList<Integer> midPoints = new ArrayList<Integer>();
		int low = 0;
		int high = occs.size() - 2;
		int mid = 0;
		int target = occs.get(occs.size()-1).frequency;
		
		while (low <= high) {
			mid = (low + high) / 2;
			midPoints.add(mid);
			if (occs.get(mid).frequency == target) {
				break;
			} else if (occs.get(mid).frequency > target) {
				low = mid + 1;
			} else {
				high = mid - 1;
			}
		}
		
		//System.out.println(midPoints.toString());
		occs.add(midPoints.get(midPoints.size()-1),occs.remove(occs.size()-1));
		return midPoints;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Occurrence> occurrences1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> occurrences2 = new ArrayList<Occurrence>();
		
		if (keywordsIndex.containsKey(kw1)) {
			occurrences1 = keywordsIndex.get(kw1);
		}
		if (keywordsIndex.containsKey(kw2)) {
			occurrences2 = keywordsIndex.get(kw2);
		}
		
		ArrayList<Occurrence> bothOccurrences = new ArrayList<Occurrence>();
		bothOccurrences.addAll(occurrences1);
		bothOccurrences.addAll(occurrences2);
		
		if (occurrences1.size() > 0 && occurrences2.size() > 0) {
			for (int i = 0; i < bothOccurrences.size()-1; i++) {
				for (int j = 1; j < bothOccurrences.size() - i; j++) {
					if (bothOccurrences.get(j).frequency > bothOccurrences.get(j-1).frequency) {
						Occurrence oc = bothOccurrences.get(j-1);
						bothOccurrences.set(j-1, bothOccurrences.get(j));
						bothOccurrences.set(j,oc);
					}
				}
			}
			for (int i = 0; i < bothOccurrences.size()-1; i++) {
				for (int j = i + 1; j < bothOccurrences.size(); j++) {
					if (bothOccurrences.get(i).document == bothOccurrences.get(j).document) {
						bothOccurrences.remove(j);
					}
				}
			}
		}
		
		while (bothOccurrences.size() > 5) {
			bothOccurrences.remove(bothOccurrences.size()-1);
		}
		//System.out.println(bothOccurrences.toString());
		
		ArrayList<String> list = new ArrayList<String>();
		for (Occurrence i : bothOccurrences) {
			list.add(i.document);
		}
		return list;
	}
}
