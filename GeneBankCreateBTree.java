package cs321_final;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class GeneBankCreateBTree {

	static Cache<TreeObject> thisCache = null;
	static BTree tree;
	static int cacheSize;
	static int subSize = 0; // sequence length
	static int degree = 0; // degree of tree
	static boolean cacheInitialized = false;
	static File file;
	static File dump;
	static boolean fileNot = false;
	static boolean error = false;

	/*
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
					if (Integer.parseInt(args[4]) <= 1) {
						error = true;
						printUsage();
					} else {
						cacheInitialized = true;
						cacheSize = Integer.parseInt(args[4]); // cache size should never change
						thisCache = new Cache<TreeObject>(cacheSize); // uses optional value of cache size
					}
				}
			} else {
				System.out.println(Integer.parseInt(args[0]));
				error = true;
				printUsage();
				// throw new Exception(); // invalid cache value given
			}

			// if degree is 0 then find optimal degree and use that
			if (Integer.parseInt(args[1]) == 0) {

				//4096 >= (2t-1)12+(2t+1)4+12
				degree= 127;
				
				

			} else {
				degree = Integer.parseInt(args[1]); // takes in degree t
			}
			file = new File("xyz.gbk.btree.data" + degree + "." + subSize); // takes in file name

			/*
			 * creating btree
			 */

			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			tree = new BTree(degree, raf);

			/*
			 * checks to see if k is within range
			 */

			if (Integer.parseInt(args[3]) >= 1 && Integer.parseInt(args[3]) <= 31) {

				subSize = Integer.parseInt(args[3]); // Substring length

			} else {
				error = true;
				printUsage();

			}

			/**************************************************************************************
			 * scan a given gbk file and then send the corresponding sequence string to the
			 * btree class
			 * 
			 * also will potentially add the value to cache
			 **************************************************************************************/
			boolean foundStart = false;
			File file1 = new File(args[2]);

			TreeObject obj;
			System.out.println(args[2]);

			StringBuilder sb = new StringBuilder();

			BufferedReader input = new BufferedReader(new FileReader(file1));
			String lineToken;

			while ((lineToken = input.readLine()) != null) {

				Scanner lineScan = new Scanner(lineToken);

				String str2 = lineToken.replaceAll("\\s", "");
				String str = str2.replaceAll("\\d", "");
				if (str.equals("ORIGIN")) {

					foundStart = true;
					System.out.println("foundStart: " + foundStart);

				} else if (lineToken.equals("//")) {
					foundStart = false;

					sb = new StringBuilder();
					System.out.println("foundStart: " + foundStart);

				} else if (foundStart == true) {
				
					for (int i = 0; i < str.length(); i++) {
						char token = str.charAt(i);

						if (token == 'n' || token == 'N') {

							sb = new StringBuilder();

						} else if (token == 'a' || token == 't' || token == 'c' || token == 'g' || token == 'A'
								|| token == 'T' || token == 'C' || token == 'G') {

							sb.append(Character.toLowerCase(token));
							

						}
						if (sb.length() > subSize) {
							String st = sb.toString();
							sb = new StringBuilder();
							sb.append(st.substring(1, subSize + 1));
						}
						if (subSize == sb.length()) {
							long stream = toLong(sb.toString());
							obj = new TreeObject(stream);

							/*
							 * if the cache is initialized then it will add the object to the cache
							 */
							if (cacheInitialized) {

								if (thisCache.getObject(obj) == false) {

									thisCache.addObject(obj);
								} else {
									thisCache.moveToTop(obj);
								}

								// pass the object to the btree class
								tree.insert(obj);
								obj = new TreeObject(stream);

							} else {
								tree.insert(obj);
								obj = new TreeObject(stream);
							}

						}

					}

				}
				lineScan.close();

			}
			input.close();

			/*****************************************************************************************
			 * debugging level
			 *****************************************************************************************/

			if (args.length == 5 && (Integer.parseInt(args[4]) == 0 || Integer.parseInt(args[4]) == 1)) {
				if (Integer.parseInt(args[4]) == 0) {
					// Any diagnostic messages, help and status messages must be be printed on
					// standard error stream

					printUsage();

				} else if (Integer.parseInt(args[4]) == 1) {
					// The program writes a text file named dump, that has the following line
					if (cacheInitialized) {
						System.out.println(thisCache.toString());
					} else {
						tree.traverseTree(args[2], subSize, subSize);
					}

				}
			} else if (args.length == 6) {
				if (Integer.parseInt(args[5]) == 0) {
					// Any diagnostic messages, help and status messages must be be printed on
					// standard error stream
					printUsage();
				} else if (Integer.parseInt(args[5]) == 1) {
					// The program writes a text file named dump, that has the following line
					if (cacheInitialized) {
						System.out.println(thisCache.toString());
					} else {
						tree.traverseTree(args[2], subSize, subSize);
					}

				} else {
					printUsage(); // invalid debug value given
				}

			}
		}

		catch (FileNotFoundException x) {
			fileNot = true;
		} catch (Exception e) {
			e.printStackTrace();
			error = true;
			System.out.println(
					"java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
		}

	}

	/***************************
	 * creating long value
	 ****************************/
	private static long toLong(String subString) {

		String bineString = "";
		for (int i = 0; i < subSize; i++) {

			if (subString.charAt(i) == 'a' || subString.charAt(i) == 'A') {
				bineString += "00";
				continue;
			} else if (subString.charAt(i) == 't' || subString.charAt(i) == 'T') {
				bineString += "11";
				continue;
			} else if (subString.charAt(i) == 'c' || subString.charAt(i) == 'C') {
				bineString += "01";
				continue;
			} else if (subString.charAt(i) == 'g' || subString.charAt(i) == 'G') {
				bineString += "10";
				continue;
			}
		}
		long stream = 0;
		int factor = 1;
		for (int i = bineString.length() - 1; i >= 0; i--) {
			stream += ((int) bineString.charAt(i) - 48) * factor;
			factor = factor * 2;
		}

		return stream;
	}

	private static void printUsage() {

		if (fileNot == true) {
			System.err.println("file does not exist");
		}
		if (error = true) {
			System.err.println("Your input does not fit the parameters domain");
			System.err.println(
					"java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
		} else {
			System.err.println("program ran smoothly");
		}

	}

}
