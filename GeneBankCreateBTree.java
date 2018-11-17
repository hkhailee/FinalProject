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

						if (foundStart == false) {
							String s = s2.next();
							if (s.equals("ORIGIN")) {
								foundStart = true;
							}
						} else {

							char seq = s2.next().charAt(0);
							bitNum++;

							if (seq == 'a' || seq == 't' || seq == 'c' || seq == 'g') { // only adds valid characters to sequence
								

								if (sequence.length() < k) { // while less than the given sequence size
									sequence += seq;		//add it to the sequence
									if(sequence.length() == k)
									System.out.println(sequence);
									
								}
								else {
									sequence= "";	//if the sequence is greater than the limit restart
									sequence += seq; //add the letter that has yet to be added
								}

							}

						}
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
