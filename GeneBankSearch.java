package finalProject;

public class GeneBankSearch {

	/*
	 * java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache
	 * size>] [<debug level>]
	 * 
	 * program will be used to search the btree this can be done faster by using a
	 * cache
	 */

	public static void main(String args[]) {
		Cache bTreeCache = null;
		int cacheSize;
		int DEFAULT = 50;

		boolean exists0 = false;
		boolean exists1 = false;
		boolean exists2 = false;
		boolean exists3 = false;
		boolean exists4 = false;
		/*
		 * checking to see if specific arguments exist
		 */
		for (int i = 0; i <= args.length; i++) {
			if (i == 0) {
				exists0 = true;
			}
			if (i == 1) {
				exists0 = true;
			}
			if (i == 2) {
				exists0 = true;
			}
			if (i == 3) {
				exists0 = true;
			}
			if (i == 4) {
				exists0 = true;
			}
		}

		try {

			if (args.length >= 3) {

				/*
				 * checking to see if useing cache or not
				 */
				if (Integer.parseInt(args[0]) == 0 || Integer.parseInt(args[0]) == 1) {
					if (Integer.parseInt(args[0]) == 1) {
						// using optional input
						if (exists3 == true) {
							cacheSize = Integer.parseInt(args[3]);
							bTreeCache = new Cache(cacheSize);
						}
						// if none given then use default value
						else {
							bTreeCache = new Cache(DEFAULT);
						}
					}
				} else {
					throw new Exception();
				}

				/*
				 * taking in the created b tree file taking in quary file
				 */

				/*
				 * debugging lvl 0 or 1
				 */

				if (exists4 == true) {

					if (Integer.parseInt(args[4]) == 0 || Integer.parseInt(args[4]) == 1) {
						if (Integer.parseInt(args[4]) == 0) {
							// Any diagnostic messages, help and status messages must be be printed on
							// standard error stream

						}
						if (Integer.parseInt(args[4]) == 1) {
							// The program writes a text file named dump, that has the following line
							// format:
							// DNA string: frequency. The dump file contains DNA string (corresponding to
							// the key stored) and frequency in an inorder traversal.

						}

					} else {
						throw new Exception(); // invalid debug value given

					}

				}

			} else {
				throw new Exception();
			}

		}

		catch (Exception e) {

			System.out.println("java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>]\n"
					+ "[<debug level>]");
		}
	}

}
