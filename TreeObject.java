public class TreeObject {

	private long Stream;
	public static int freq;
	private long Value; 
	
	public TreeObject(long stream) {
		this.Stream = stream;
		this.freq = 0;
		this.Value = stream;
	}
	public TreeObject(long stream, int frequency) {
		this.Stream = stream;
		this.freq = frequency;
		this.Value = stream;
	}
	public long getValue() {
		long value = Value;
		return value;
	}
	public long getSequence() { 
		long strm = Stream;
		return strm;
	}
	
	public int getFrequency() {

		return freq;
	}

	public String getObject() {
		
		//return string value of stream
		return seq;
	}
}


