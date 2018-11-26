package finalProject;

import java.io.File;
import java.util.Scanner;

public class GeneBankCreateBTree {

	public static void main(String args[]) {
		Cache thisCache = null;
		int cacheSize;
		int k;

		try {

			// Checks to see if required size is present
			if (args.length >= 4) {

				/*
				 * if args 0 is 1 then use cache else no cache uses args 4 to get cache size
				 */

				if (Integer.parseInt(args[0]) == 0 || Integer.parseInt(args[0]) == 1) {
					if (Integer.parseInt(args[0]) == 1) {

						cacheSize = Integer.parseInt(args[4]); // cache size should never change
						thisCache = new Cache(cacheSize); // uses optional value of cache size

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
					int degree = Integer.parseInt(args[1]); // takes in degree t
				}
				File file = new File(args[2]); // takes in file name

				/*
				 * checks to see if k is within range
				 */

				if (Integer.parseInt(args[3]) > 1 || Integer.parseInt(args[3]) < 31) {

					k = Integer.parseInt(args[3]); // Sequence length
				} else {

					System.out.println(Integer.parseInt(args[3]));
					throw new Exception(
							"sequnce length k must be greater than or equal to 1 and less than or equal to 31");

				}

				/*
				 * debugging level
				 */

				if (args.length > 5) {
					if (Integer.parseInt(args[4]) == 0 || Integer.parseInt(args[4]) == 1) {
						if (Integer.parseInt(args[4]) == 0) {
							// Any diagnostic messages, help and status messages must be be printed on
							// standard error stream
						}
						if (Integer.parseInt(args[4]) == 1) {
							// The program writes a text file named dump, that has the following line
							// format:
							// DNA string: frequency. The dump file contains DNA string (corresponding to
							// the key stored) and frequency in an inorder traversal. You can find a dump
							// file
						}
						/*
						 * debug level 0 or 1 (optional)
						 */
					} else {
						throw new Exception(); // invalid debug value given

					}
				}

				/*
				 * scan a given kgb file and then send the corresponding sequence string to the
				 * btree class
				 * 
				 * also will potentially add the value to cache
				 */
				boolean foundStart = false;
				int bitNum = 1;
				String sequence = "";
				Scanner scan = new Scanner(new File(args[2]));
				String s;

				while (scan.hasNextLine()) {
					Scanner s2 = new Scanner(scan.nextLine());

					while (s2.hasNext()) {
						s = s2.next();

						if (s.equals("ORIGIN")) {
							foundStart = true;
							System.out.println(foundStart);
						} else if (s.equals("//")) {
							foundStart = false;
							System.out.println(sequence.length());
							bitNum = bitNum - sequence.length();
							sequence = "";
							System.out.println(foundStart);
						}

						else if (foundStart == true) {

							for (int i = 0; i < s.length(); i++) {

								char c = s.charAt(i);

								if (c == 'n' || c == 'N') {
									bitNum = bitNum - sequence.length();
									sequence = "";

								}

								else {
									if (c == 'a' || c == 't' || c == 'c' || c == 'g' || c == 'A' || c == 'T' || c == 'C'
											|| c == 'G') {
										if (sequence.length() < k) {

											sequence += c;
											bitNum++;

											if (sequence.length() == k) {
												System.out.println((bitNum - sequence.length()) + sequence);

												/*
												 * if the cache is initalized then it will add the object to the cache
												 */
												if (Integer.parseInt(args[0]) == 1) {

													if (thisCache.getObject(sequence) == false) {
														thisCache.addObject(sequence);
													} else {
														thisCache.moveToTop(sequence);
													}
												}
												
												//pass the object to the btree class

												sequence = "";

											}
										}

									}

								}
							}

						}

					}

				}
				scan.close();
				/*
				 * next step is to assign each of these sequences with bit locations
				 */
				System.out.println(bitNum);
			} else {
				throw new Exception(); // required arguments do not exist

			}
		}

		catch (Exception e) {

			System.out.println(
					"java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");

		}
	}

}