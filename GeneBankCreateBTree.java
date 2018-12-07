
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class GeneBankCreateBTree {

	static Cache<TreeObject> thisCache = null;
	static BTree tree;
	static int cacheSize;
	static int subSize = 0; // sequence length
	static int degree = 0; // degree of tree
	static boolean cacheInitialized = true;
	static File file;
	static File dump;
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String args[]) {
		
		
		

		try {

			// Checks to see if the correct amount of parameters were presented 
			if (args.length < 4) {
				printUsage();
			}	

				/*
				 * if args 0 is 1 then use cache else no cache uses args 4 to get cache size
				 */

				if (Integer.parseInt(args[0]) == 0 || Integer.parseInt(args[0]) == 1) {
					if (Integer.parseInt(args[0]) == 1) {

						cacheSize = Integer.parseInt(args[4]); // cache size should never change
						thisCache = new Cache<TreeObject>(cacheSize); // uses optional value of cache size

					}
				} else {
					System.out.println(Integer.parseInt(args[0]));
					throw new Exception(); // invalid cache value given
				}

				// if degree is 0 then find optimal degree and use that
				if (Integer.parseInt(args[1]) == 0) {
					
					// ((2t-1)application object size) + ((2t+1)pointer object size) + (BtreeNode
					// metadata size) <= 4096

				} else {
					degree = Integer.parseInt(args[1]); // takes in degree t
				}
				
				
				if (Integer.parseInt(args[0]) == 1) {
					cacheInitialized = true;
				}

				/*
				 * checks to see if k is within range
				 */

				if (Integer.parseInt(args[3]) >= 1 || Integer.parseInt(args[3]) <= 31) {
					
					subSize = Integer.parseInt(args[3]); // Substring length
					System.out.println(subSize);
				} else {

					System.out.println(Integer.parseInt(args[3]));
					printUsage();

				}
				
				file = new File(args[2]+".btree.data."+subSize+"."+degree); // takes in file name
				
				RandomAccessFile raf = new RandomAccessFile(file, "rw");
				tree = new BTree(degree, raf);

				/*
				 * scan a given kgb file and then send the corresponding sequence string to the
				 * btree class
				 * 
				 * also will potentially add the value to cache
				 */
				boolean foundStart = false;
				int bitNum = 1;
//				String bineString = ""; 
				int lineCount= 0;
				String subString = "";
				File file1 = new File(args[2]);
//				Scanner scan = new Scanner(file1);
				TreeObject obj;
				System.out.println(args[2]);
				int count = 0;
//				System.out.println(scan.nextLine());
				StringBuilder sb = new StringBuilder();
				
				BufferedReader input = new BufferedReader(new FileReader(file1)); 
				String lineToken;
				
				while ((lineToken = input.readLine()) != null)  {
//					String lineToken = scan.nextLine();
					Scanner lineScan = new Scanner(lineToken);
//					System.out.println(lineToken);

					String str2 = lineToken.replaceAll("\\s", "");
					String str = str2.replaceAll("\\d", "");
					if (str.equals("ORIGIN")) {
//						System.out.println(1);
						foundStart = true;
						System.out.println("foundStart: " + foundStart);

					} else if (lineToken.equals("//")) {
						foundStart = false;

						bitNum = bitNum - subString.length();
						sb = new StringBuilder();
						System.out.println("foundStart: " + foundStart);

					} else if (foundStart == true) {
						lineCount++;
						for (int i =0; i < str.length(); i++) {
							char token = str.charAt(i);
							
							if (token == 'n' || token == 'N') {
//								bitNum = bitNum - subString.length();
								sb = new StringBuilder();
//								count = 0;
							} else if (token == 'a' || token == 't' || token == 'c' || token == 'g' || token == 'A' || token == 'T' || token == 'C' || token == 'G') {
								
								sb.append(Character.toLowerCase(token));
								count++;
								bitNum++;
//								Possibly make a method everytime the length of the sub string is reached
								
							}
							
							if (sb.length() > subSize) {
								String st = sb.toString();
								sb = new StringBuilder();
								sb.append(st.substring(1,subSize +1));
							}
//						index	System.out.println(subString);
							if (subSize == sb.length()) {
								long stream = toLong(sb.toString());
								
								/*
								 * if the cache is initialized then it will add the object to the cache
								 */
								if (cacheInitialized) {
									
									obj = new TreeObject(stream);

									if (thisCache.getObject(obj) == false) {

										thisCache.addObject(obj);
									} else {
										thisCache.moveToTop(obj);
									}
									
									// pass the object to the btree class\
									tree.insert(obj);
									System.out.println(sb.toString());
									System.out.println(tree.search(28));
									obj = new TreeObject(stream);
								}	
							}
														
						}					
					}
					lineScan.close();
					}
//				scan.close();
				System.out.println(lineCount);
				
				/*
				 * debugging level			
				 */
				if (args.length == 6) {
					if (Integer.parseInt(args[5]) == 0) {
						// Any diagnostic messages, help and status messages must be be printed on
						// standard error stream
					} else if (Integer.parseInt(args[5]) == 1) {
						// The program writes a text file named dump, that has the following line
						// format:
						// DNA string: frequency. The dump file contains DNA string (corresponding to
						// the key stored) and frequency in an inorder traversal. You can find a dump
						// file
						//System.out.println(thisCache.toString());

					} else {
						printUsage(); // invalid debug value given
					}
				}
				tree.finish();
				tree.traverseTree(args[2], degree, subSize);

			} catch (Exception e) {
				e.printStackTrace();

				System.out.println(
					"java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
		}
	}

	private static long toLong(String subString) {
		
		String bineString = "";
		for (int i = 0 ; i < subSize ; i++) {
			
			if(subString.charAt(i) == 'a'||subString.charAt(i) == 'A') {
				bineString += "00";
				continue;
			} else if(subString.charAt(i) == 't'|| subString.charAt(i) == 'T') {
				bineString += "11";
				continue;
			} else if(subString.charAt(i) == 'c'|| subString.charAt(i) == 'C') {
				bineString += "01";
				continue;
			} else if(subString.charAt(i) == 'g'|| subString.charAt(i) == 'G') {
				bineString += "10";
				continue;
			}				
		}
		long stream = 0;
		int factor = 1;
		for (int i = bineString.length()-1; i >= 0; i--) {
			stream += ((int) bineString.charAt(i) - 48) * factor;
			factor = factor*2;
		}
		/*
		 * creating long value 
		 */
		
	
		return stream;
	}

	private static void printUsage() {
		System.out.println("Your input does not fit the parameters domain");
		
	}

}
