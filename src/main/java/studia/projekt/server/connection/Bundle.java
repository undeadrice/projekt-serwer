package studia.projekt.server.connection;

import java.util.HashMap;

public class Bundle {

	private byte header;
	private HashMap<String, String> strings = new HashMap<>();
	private HashMap<String, Long> longs = new HashMap<>();
	private HashMap<String, Double> doubles = new HashMap<>();
	private HashMap<String, Boolean> bools = new HashMap<>();
	private HashMap<String, Byte> bytes = new HashMap<>();
	private HashMap<String, Integer> ints = new HashMap<>();

	public Bundle(byte header) {
		this.header = header;
	}

	public byte getHeader() {
		return header;
	}

	public void putString(String key, String value) {
		strings.put(key, value);
	}

	public void putLong(String key, Long value) {
		longs.put(key, value);
	}

	public void putByte(String key, Byte value) {
		bytes.put(key, value);
	}

	public void putBool(String key, Boolean value) {
		bools.put(key, value);
	}

	public void putDouble(String key, Double value) {
		doubles.put(key, value);
	}

	public void putInt(String key, Integer value) {
		ints.put(key, value);
	}

	public String getString(String key) {
		return strings.get(key);
	}

	public Long getLong(String key) {
		return longs.get(key);
	}

	public Double getDouble(String key) {
		return doubles.get(key);
	}

	public Byte getByte(String key) {
		return bytes.get(key);
	}

	public Boolean getBool(String key) {
		return bools.get(key);
	}
	public Integer getInt(String key) {
		return ints.get(key);
	}
	
}
