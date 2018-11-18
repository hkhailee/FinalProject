package finalProject;

import java.io.File;
import java.util.Scanner;

public class GeneBankCreateBTree {

	public static int cacheSize;
	public static int k;

	public static void main(String args[]) {
		try {

			// Checks to see if required size is present
			if (args.length >= 4) {

				/*
				 * if args 0 is 1 then use cache else no cache uses args 4 to get cache size
				 */

				if (Integer.parseInt(args[0]) == 0 || Integer.parseInt(args[0]) == 1) {
					if (Integer.parseInt(args[0]) == 1) {
						// create cache
						cacheSize = Integer.parseInt(args[4]); // cache size should never change
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

				if (args.length > 4) {
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
				 */
				boolean foundStart = false;
				int bitNum = 0;
				String sequence = "";
				Scanner scan = new Scanner(new File(args[2]));

				while (scan.hasNextLine()) {
					Scanner s2 = new Scanner(scan.nextLine());

					while (s2.hasNext()) {

						String s = s2.next();

						if (s.equals("ORIGIN")) {
							foundStart = true;
							System.out.println(foundStart);
						}
						else if (s.equals("//")) {
							foundStart = false;
							sequence = "";
							System.out.println(foundStart);
						}

						else if (foundStart == true) {

							Scanner sScan = new Scanner(s);
							char c = sScan.next().charAt(0);
							bitNum++;

							if (c == 'n' || c == 'N') {
								sequence = "";

							}

							else {
								if (c == 'a' || c == 't' || c == 'c' || c == 'g'|| c == 'A' || c == 'T' || c == 'C' || c == 'G') {
									if (sequence.length() < k) {
										sequence += c;
										if (sequence.length() == k) {
											System.out.println(bitNum + sequence);
											sequence = "";

										}
									}

								}
							}
						}

						// soultion doesnt work because you need to ignore everything before an origin
						// this will get
						// letter that are a t c g before an origin and count them in the gene sequence

						/*
						 * if(s.equals("\\")) { foundStart = false; }
						 * 
						 * 
						 * if (foundStart == false) {
						 * 
						 * if (s.equals("ORIGIN")) { foundStart = true; } } else {
						 * 
						 * Scanner sScan = new Scanner(s); char seq = sScan.next().charAt(0); bitNum++;
						 * 
						 * if(seq=='n') { sequence= "";
						 * 
						 * }
						 * 
						 * 
						 * 
						 * 
						 * else if (seq == 'a' || seq == 't' || seq == 'c' || seq == 'g') { // only adds
						 * valid characters to sequence
						 * 
						 * if (sequence.length() < k) { // while less than the given sequence size
						 * sequence += seq; //add it to the sequence if(sequence.length() == k) //test
						 * 
						 * System.out.println("[" + (bitNum - sequence.length()) + "] :" + sequence); //
						 * will add to the btree class
						 * 
						 * 
						 * } else { sequence= ""; //if the sequence is greater than the limit restart
						 * sequence += seq; //add the letter that has yet to be added }
						 * 
						 * } else if(seq=='n') { sequence= "";
						 * 
						 * }
						 * 
						 * }
						 */
					}

				}
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
