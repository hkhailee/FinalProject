public class TreeObject {

	private long Stream;
	private int Frequency;
	private int Position; //The byte position of the object within the the node in the file. May need to divide into two methods; the other method being getValue(). Make sure the right most object is larger
//	private int Value;
	public TreeObject() {
		
	}
	
	public TreeObject(long stream, int position) {
		this.Stream = stream;
		Frequency = 0;
		this.Position = position;
	}
	public int getPosition() {
		int position = Position;
		return position;
	}
	public long getSequence() { 
		long strm = Stream;
		return strm;
	}
	
	public int getFrequency() {
		int freq = Frequency;
		return freq;
	}
}
