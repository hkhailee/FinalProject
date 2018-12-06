import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class tester {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
		BTree tree = new BTree(2, new RandomAccessFile(new File("dump2"), "rw"), 3);
		TreeObject t = new TreeObject(23);
		tree.insert(t);
		Random rand = new Random();
//		for (int i = 0; i < 1000; i++) {
//			tree.insert(new TreeObject(rand.nextLong()));
//		}
		tree.insert(new TreeObject(24));
//		for (int i = 0; i < 1000; i++) {
//			tree.insert(new TreeObject(rand.nextLong()));
//		}
		tree.insert(new TreeObject(25));
		tree.insert(new TreeObject(5));
		tree.insert(new TreeObject(4));
		tree.insert(new TreeObject(76));
		tree.insert(new TreeObject(25));
		tree.insert(new TreeObject(25));
		tree.insert(new TreeObject(25));
		tree.insert(new TreeObject(3));
		tree.insert(new TreeObject(252));
		tree.insert(new TreeObject(53));
		tree.insert(new TreeObject(44));
		tree.insert(new TreeObject(761));
		tree.insert(new TreeObject(32));
		tree.insert(new TreeObject(26));
		tree.insert(new TreeObject(78));
		tree.insert(new TreeObject(91));
		tree.insert(new TreeObject(322));
		tree.insert(new TreeObject(25));
	
		System.out.println(tree.search(25));
//		
//		File file = new File("dump2");
		RandomAccessFile raf = new RandomAccessFile(new File("dump2"), "rw");
		raf.seek(0);
		for (int j = 0; j < 1; j++) {
			System.out.print(raf.readInt()); System.out.print(" "); System.out.print(raf.readInt()); System.out.print(" "); System.out.print(raf.readInt()); System.out.print(" "); System.out.print(raf.readInt()); System.out.print(" ");
			for (int i = 0; i < 4; i++) {
				System.out.print(raf.readInt());
				System.out.print(" ");
			}
			for (int i = 0; i < 3; i++) {
				System.out.print(raf.readLong());
				System.out.print(" ");
				System.out.print(raf.readInt());
				System.out.print(" ");
			}
			System.out.println();
		}
//	}
//
//}
	}
}
