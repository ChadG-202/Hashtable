package ci583.htable.impl;

/**
 * A HashTable with no deletions allowed. Duplicates overwrite the existing value. Values are of
 * type V and keys are strings -- one extension is to adapt this class to use other types as keys.
 * 
 * The underlying data is stored in the array `arr', and the actual values stored are pairs of 
 * (key, value). This is so that we can detect collisions in the hash function and look for the next 
 * location when necessary.
 */

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

public class Hashtable<V> {

	private Object[] arr; //an array of Pair objects, where each pair contains the key and value stored in the hashtable
	private int max; //the size of arr. This should be a prime number
	private int itemCount; //the number of items stored in arr
	private final double maxLoad = 0.6; //the maximum load factor

	public static enum PROBE_TYPE {
		LINEAR_PROBE, QUADRATIC_PROBE, DOUBLE_HASH;
	}

	PROBE_TYPE probeType; //the type of probe to use when dealing with collisions
	private final BigInteger DBL_HASH_K = BigInteger.valueOf(8);

	/**
	 * Create a new Hashtable with a given initial capacity and using a given probe type
	 * @param initialCapacity
	 * @param pt
	 */
	public Hashtable(int initialCapacity, PROBE_TYPE pt) {
		if (!isPrime(initialCapacity)) {//check if 'initialCapacity' is prime
			max = nextPrime(initialCapacity);//if not prime make 'max' the next possible prime
		}else{
			max = initialCapacity;//else set 'max' to initialcapacity
		}

		//setting variables
		arr = new Object[max];
		probeType = pt;
	}
	
	/**
	 * Create a new Hashtable with a given initial capacity and using the default probe type
	 * @param initialCapacity
	 */
	public Hashtable(int initialCapacity) {
		//setting array size
		if (!isPrime(initialCapacity)) {
			max = nextPrime(initialCapacity);
		}else{
			max = initialCapacity;
		}

		//setting variables
		arr = new Object[max];
		probeType = PROBE_TYPE.LINEAR_PROBE;//setting probeType to defult LINEAR_PROBE
	}

	/**
	 * Store the value against the given key. If the loadFactor exceeds maxLoad, call the resize 
	 * method to resize the array. If the key already exists then its value should be overwritten.
	 * Create a new Pair item containing the key and value, then use the findEmpty method to find an unoccupied 
	 * position in the array to store the pair. Call findEmmpty with the hashed value of the key as the starting
	 * position for the search, stepNum of zero and the original key.
	 * containing   
	 * @param key
	 * @param value
	 */
	public void put(String key, V value) {
		try{//try catch testing for empty keys being inputted
			if(getLoadFactor() > maxLoad){
				resize();//call resize() when array is 60% full
			}

			Pair item = new Pair(key, value);
			int pos = findEmpty(hash(key), 0, key);//finding position to store Pair 'item'
			if(arr[pos] == null) itemCount++;//if the position is null then this is a new item
			arr[pos] = item;

		}catch(Exception e){
			throw new IllegalArgumentException("Empty key");
		}
	}

	/**
	 * Get the value associated with key, or return null if key does not exists. Use the find method to search the
	 * array, starting at the hashed value of the key, stepNum of zero and the original key.
	 * @param key
	 * @return
	 */
	public V get(String key) {
		return find(hash(key), key, 0);
	}

	/**
	 * Return true if the Hashtable contains this key, false otherwise 
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key) {//if get(key) returns null that key doesnt exist
		return (get(key) == null) ? false : true;
	}

	/**
	 * Return all the keys in this Hashtable as a collection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> getKeys() {
		Collection<String> keys = new ArrayList<String>();
		for(Object item : arr){//iterate through 'arr' items
			if(item != null) keys.add(((Pair)item).key);//add key to collection if position in 'arr' isn't 'null'
		}
		return keys;
	}

	/**
	 * Return the load factor, which is the ratio of itemCount to max
	 * 
	 * @return
	 */
	public double getLoadFactor() {
		return (itemCount == 0) ? 0 : itemCount / (double)max;
		//if no items added return '0' else return ratio as a 'double'
	}

	/**
	 * return the maximum capacity of the Hashtable
	 * 
	 * @return
	 */
	public int getCapacity() {
		return max;
		
	}

	/**
	 * Find the value stored for this key, starting the search at position startPos
	 * in the array. If the item at position startPos is null, the Hashtable does
	 * not contain the value, so return null. If the key stored in the pair at
	 * position startPos matches the key we're looking for, return the associated
	 * value. If the key stored in the pair at position startPos does not match the
	 * key we're looking for, this is a hash collision so use the getNextLocation
	 * method with an incremented value of stepNum to find the next location to
	 * search (the way that this is calculated will differ depending on the probe
	 * type being used). Then use the value of the next location in a recursive call
	 * to find.
	 * 
	 * @param startPos
	 * @param key
	 * @param stepNum
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private V find(int startPos, String key, int stepNum) {
		Pair item = (Pair)arr[startPos];
		if (arr[startPos] == null) {
			return null;
		}else if (item.key.equals(key)) {
			//if position in 'arr key' equals the 'key' we are looking for return the 'value'
			return item.value;
		}
		stepNum++;//else increase 'stepNum' and recursively call 'find' with next 'startPos'
		return find(getNextLocation(startPos, stepNum, key), key, stepNum);
	}

	/**
	 * Find the first unoccupied location where a value associated with key can be
	 * stored, starting the search at position startPos. If startPos is unoccupied,
	 * return startPos. Otherwise use the getNextLocation method with an incremented
	 * value of stepNum to find the appropriate next position to check (which will
	 * differ depending on the probe type being used) and use this in a recursive
	 * call to findEmpty.
	 * 
	 * @param startPos
	 * @param stepNum
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int findEmpty(int startPos, int stepNum, String key) {
		if(arr[startPos] == null || ((Pair)arr[startPos]).key.equals(key)){
			//if position given equals 'null' then return that position
			//or if given position 'arr key' equals 'key' given return position to be overwritten
			return startPos;
		}
		stepNum++;//else increase 'stepNum' and recursively call 'findEmpty' with next 'startPos'
		return findEmpty(getNextLocation(startPos, stepNum, key), stepNum, key);
	}

	/**
	 * Finds the next position in the Hashtable array starting at position startPos.
	 * If the linear probe is being used, we just increment startPos. If the double
	 * hash probe type is being used, add the double hashed value of the key to
	 * startPos. If the quadratic probe is being used, add the square of the step
	 * number to startPos.
	 * 
	 * @param i
	 * @param stepNum
	 * @param key
	 * @return
	 */
	private int getNextLocation(int startPos, int stepNum, String key) {
		int step = startPos;
		switch (probeType) {
			case LINEAR_PROBE:
				step++;
				break;
			case DOUBLE_HASH:
				step += doubleHash(key);
				break;
			case QUADRATIC_PROBE:
				step += stepNum * stepNum;
				break;
			default:
				break;
		}
		return step % max;
	}

	/**
	 * A secondary hash function which returns a small value (less than or equal to
	 * DBL_HASH_K) to probe the next location if the double hash probe type is being
	 * used
	 * 
	 * @param key
	 * @return
	 */
	private int doubleHash(String key) {
		BigInteger hashVal = BigInteger.valueOf(key.charAt(0) - 96);
		for (int i = 0; i < key.length(); i++) {
			BigInteger c = BigInteger.valueOf(key.charAt(i) - 96);
			hashVal = hashVal.multiply(BigInteger.valueOf(27)).add(c);
		}
		return DBL_HASH_K.subtract(hashVal.mod(DBL_HASH_K)).intValue();
	}

	/**
	 * Return an int value calculated by hashing the key. See the lecture slides for
	 * information on creating hash functions. The return value should be less than
	 * max, the maximum capacity of the array
	 * 
	 * @param key
	 * @return
	 */
	private int hash(String key) {
		int hash = 0;
		for(int i = 0; i < key.length(); i++){//iterate through the 'key' characters
			hash = (hash << 5) | (hash >>> 27);//left shift bit or shift a zero into the leftmost position (Tang, 2011)
			hash = (31 * hash + key.charAt(i) - 47);//'-47' because keys can include numbers
		}
		return Math.abs(hash%max);//use of 'Math.abs' to return absolute value inverting negative values(Tutorials Point, 2020)
		//'%max' to compress hash value to fit the array size
	}

	/**
	 * Return true if n is prime
	 * 
	 * @param n
	 * @return
	 */
	private boolean isPrime(int n) {
		if (n <= 2){//if array size is between 0-2
			if(n == 0) return false;
			return true;
		} 
		if (n % 2 == 0) return false;//if divisible by 2 then not prime
		for (int i = 3; i * i < n; i += 2) {//find any other divisible numbers
			if (n % i == 0) return false;
		}
		return true;//if not divisible then is prime
	}

	/**
	 * Get the smallest prime number which is larger than n
	 * 
	 * @param n
	 * @return
	 */
	private int nextPrime(int n) {
		while (!isPrime(n)) {//keep adding 1 till the value is prime
			++n;
		}
		return n;
	}

	/**
	 * Resize the hashtable, to be used when the load factor exceeds maxLoad. The
	 * new size of the underlying array should be the smallest prime number which is
	 * at least twice the size of the old array.
	 */
	@SuppressWarnings("unchecked")
	private void resize() {
		Object[] oldarr = arr;
		max = nextPrime(max*10);//set 'max' to a prime number 10 times bigger to prevent constant resizing
		arr = new Object[max];
		itemCount = 0;//reset itemCount to zero as we need to put the items in again
		for (int i = 0; i < oldarr.length; i++) {//iterate through the 'oldarr'
			if (oldarr[i] != null) {//if position in 'oldarr' holds data then put values into resized 'arr'
				Pair p = (Pair)oldarr[i];
				put(p.key, p.value);
			}
		}
	}

	/**
	 * Instances of Pair are stored in the underlying array. We can't just store the
	 * value because we need to check the original key in the case of collisions.
	 * 
	 * @author jb259
	 *
	 */
	private class Pair {
		private String key;
		private V value;

		public Pair(String key, V value) {
			this.key = key;
			this.value = value;
		}
	}
}