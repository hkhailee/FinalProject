import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class tester {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File file = new File("dump2");
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		raf.seek(0);
		for (int j = 0; j < 3; j++) {
			System.out.print(raf.readInt()); System.out.print(" "); System.out.print(raf.readInt()); System.out.print(" "); System.out.print(raf.readInt()); System.out.print(" "); System.out.print(raf.readInt()); System.out.print(" ");
			for (int i = 0; i < 6; i++) {
				System.out.print(raf.readInt());
				System.out.print(" ");
			}
			for (int i = 0; i < 5; i++) {
				System.out.print(raf.readLong());
				System.out.print(" ");
				System.out.print(raf.readInt());
				System.out.print(" ");
			}
			System.out.println();
		}
		raf.close();
	}

}
