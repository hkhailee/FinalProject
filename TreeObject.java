public class TreeObject {

	private long Stream;
	private int Frequency;
	private long Value; 
	
	public TreeObject(long stream) {
		this.Stream = stream;
		this.Frequency = 0;
		this.Value = stream;
	}
	public TreeObject(long stream, int frequency) {
		this.Stream = stream;
		this.Frequency = frequency;
		this.Value = stream;
	}
	public long getValue() {
		long value = Value;
		return value;
	}
	public long getStream() { 
		long strm = Stream;
		return strm;
	}
	
	public int getFrequency() {
		int freq = Frequency;
		return freq;
	}
}
