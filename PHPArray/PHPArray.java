import java.util.*;

public class PHPArray<V> implements Iterable<V>
{
	private int inputs;
	private int tableSize;
	private Node<V>[] table;
	private Node<V> head;
	private Node<V> tail;
	private Node<V> eachNode;
	private boolean isEachEnd;
	private V tailSortedValue;
	private int sortCount;

	public PHPArray(int cap)
	{
		tableSize = cap;
		@SuppressWarnings("unchecked")
		Node<V>[] temp = (Node<V>[]) new Node<?>[tableSize];
		table = temp;
		inputs = 0;
		tailSortedValue = null;
		sortCount = 0;
		head = null;
		tail = null;
		eachNode = null;
		isEachEnd = false;
	}
	public void put(Object key, V value)
	{
		if(value == null)
		{
			unset(value);
		}
		if(inputs >= tableSize/2)
		{
			System.out.println("\t\tSize: " + tableSize + " -- resizing from " + tableSize + " to " + (tableSize*2));
			resize(tableSize*2);
		}

		String keyString;
		if(key instanceof Integer)
		{
			Integer temp = (Integer)key;
			keyString = temp.toString();
		}
		else
		{
			keyString = (String)key;
		}
		int i;
		for(i = hash(keyString); table[i] != null; i = (i + 1) % tableSize)
		{
			if(table[i].key == keyString)
			{
				table[i].val = value;
				return;
			}
		}

		table[i] = new Node<V>(keyString, value);

		if(head == null)
    	{
      		head = table[i];
      		tail = table[i];
    	}
    	else
    	{	
      		Node<V> temp = tail;
      		tail = table[i];
      		temp.next = tail;
      		tail.prev = temp;
    	}
    	inputs++;
	}
	public void unset(Object keyObject) 
	{
		String key;
		if(keyObject instanceof Integer)
		{
			Integer temp = (Integer)keyObject;
			key = temp.toString();
		}
		else
		{
			key = (String)keyObject;
		}
    	if (get(key) == null) return;

    	// find position i of key
    	int i = hash(key);
    	while (!key.equals(table[i].key))
    	{
      		i = (i + 1) % tableSize;
    	}

    	// delete node from hash table
    	Node<V> toDelete = table[i];
    	table[i] = null;
    	// TODO: delete the node from the linked list in O(1)
    	if(toDelete == head)
    	{
      		Node<V> temp;
      		temp = toDelete.next;
	      	if(temp == null)
	      	{
	        	head = null;
	        	tail = null;
	      	}
	      	else
	      	{
        		head = temp;
        		head.prev = null;
      		}
    	}
    	else if(toDelete == tail)
    	{
      		tail = toDelete.prev;
      		tail.next = null;
    	}
    	else
    	{
      	toDelete.prev.next = toDelete.next;
      	toDelete.next.prev = toDelete.prev;
    	}

    	// rehash all keys in same cluster
    	i = (i + 1) % tableSize;
    	while (table[i] != null)
    	{
      		// delete and reinsert
      		Node<V> nodeToRehash = table[i];
      		table[i] = null;
	      rehash(nodeToRehash);
	      i = (i + 1) % tableSize;
	    }

	    inputs--;

    	// halves size of array if it's 12.5% full or less
    	if (inputs > 0 && inputs <= tableSize/8) 
    	{
    		System.out.println("\t\tSize: " + tableSize + " -- resizing from " + tableSize + " to " + (tableSize/2));
    		resize(tableSize/2);
    	}
  	}
	private int hash(String key) 
	{
    	return (key.hashCode() & 0x7fffffff) % tableSize;
  	}
  	private void rehash(Node<V> node)
  	{  
  		System.out.print("\t\tKey " + node.key + " rehashed...\n");
    	int i = hash(node.key);
    	table[i] = node;
  	}
  	//Method to flip array into new array. Uses ArrayList to store the String values
  	//and iterates through the array to see if any values repeat. If they do,
  	//the first encountered key value pair is used
  	public PHPArray<String> array_flip()
  	{
  		if(head.val instanceof String)
  		{
  			PHPArray<String> flipped = new PHPArray<String>(tableSize);
  			ArrayList<String> theValues = new ArrayList<String>(tableSize);
  			Node<V> curr = head;
  			while(curr != null)
  			{
  				boolean isRepeated = false;
  				for(int i = 0; i < theValues.size(); i++)
  				{
  					if(theValues.get(i).equals(curr.val))
  					{
  						isRepeated = true;
  						break;
  					}
  				}
  				if(!isRepeated)
  				{
  					flipped.put(curr.val.toString(), curr.key);
  					theValues.add(curr.val.toString());
  				}
  				curr = curr.next;
  			}
  			return flipped;
  		}
  		else
  		{
  			throw new ClassCastException("Cannot convert class java.lang.Integer to String");
  		}
  	}
  	//same functionality of the rehash except it doesn't display a message
  	private void rehashSort(Node<V> node)
  	{
    	int i = hash(node.key);
    	table[i] = node;	
  	}
  	//asort and sort are impleneted almost exactly the same way, uses a merge sort
  	//of the linked list to find the sorted order in nlogn time.
  	public void asort()
  	{
  		if(head.val instanceof Comparable)
  		{
  			Node<V> tempNode = head;
  			tailSortedValue = tail.val;
  			head = mergeSort(tempNode);
  			tail.next = null;
		}
		else
  		{
  			throw new ClassCastException("PHPArray<V> is not instance of Comparable");
  		}
  	}
  	//Functionally same as asort as it uses merge sort, the only
  	//difference being at the end we need to rehash all the values
  	//in the table so we can index them 0 -> length() - 1
  	public void sort()
  	{
  		if(head.val instanceof Comparable)
  		{
  			Node<V> tempNode = head;
  			tailSortedValue = tail.val;
  			head = mergeSort(tempNode);
  			tail.next = null;
  			Node<V> curr = head;
  			table = (Node<V>[]) new Node<?>[tableSize];
			int i = 0;

  			while(curr != null)
  			{
  				curr.key = "" + i;
  				rehashSort(curr);
  				i++;
  				curr = curr.next;	
  			}
  		}
  		else
  		{
  			throw new ClassCastException("PHPArray<V> is not instance of Comparable");
  		}
  	}
  	private Node<V> mergeSort(Node<V> theHead)
  	{
  		if(theHead == null || theHead.next == null)
  		{
  			return theHead;
  		}
  		Node<V> second = splitList(theHead);
  		theHead = mergeSort(theHead);
  		second = mergeSort(second);

  		return theHead = merge(theHead, second);
  	}
  	private Node<V> splitList(Node<V> theHead)
  	{
  		Node<V> middle = theHead;
  		Node<V> search = theHead.next;

  		while(search.next != null && search.next.next != null)
  		{
  			search = search.next.next;
  			middle = middle.next;
  		}
  		Node<V> newList = middle.next;
  		middle.next = null;
  		return newList;
  	}
  	private Node<V> merge(Node<V> first, Node<V> second)
  	{
  		if(first == null)
  		{
  			return second;
  		}
  		if(second == null)
  		{
  			return first;
  		}
  		Comparable theValue = (Comparable)first.val;
  		if(theValue.compareTo(second.val) <= 0)
  		{
  			//tailSortedValue simply keeps track of whatever the value of
  			//the tail is. If the tailSortedValue is smaller than that of
  			//the node we are comparing it to then the node's value becomes
  			//the new tailSortedValue
  			if(theValue.compareTo(tailSortedValue) > 0)
  			{
  				tail = first;
  				tailSortedValue = tail.val;
  			}
  			first.next = merge(first.next, second);
  			first.next.prev = first;
  			first.prev = null;
  			return first;
  		}
  		else
  		{
  			if(theValue.compareTo(tailSortedValue) > 0)
  			{
  				tail = first;
  				tailSortedValue = tail.val;
  			}
  			second.next = merge(first, second.next);
  			second.next.prev = second;
  			second.prev = null;
  			return second;
  		}
  	}
  	private void resize(int capacity)
  	{
	    PHPArray<V> temp = new PHPArray<V>(capacity);

	    //rehash the entries in the order of insertion
	    Node<V> current = head;
	    while(current != null){
	        temp.put(current.key, current.val);
	        current = current.next;
	    }
	    table     = temp.table;
	    head      = temp.head;
	    tail      = temp.tail;
	    tableSize = temp.tableSize;
  	}
  	public V get(Object keyObject)
  	{
  		String key;
  		if(keyObject instanceof Integer)
  		{
  			Integer temp = (Integer)keyObject;
  			key = temp.toString();
  		}
  		else
  		{
  			key = (String)keyObject;
  		}
    	for (int i = hash(key); table[i] != null; i = (i + 1) % tableSize)
      	if (table[i].key.equals(key))
        	return table[i].val;
    	return null;
  	}
  	public static <V> void showData(PHPArray<V> array)
  	{
    	for (V i : array) 
    	{
        	System.out.print(i + " ");
    	}
    	System.out.println();
  	}
	public Iterator<V> iterator()
	{
		return new MyIterator<V>(head);
	}
	public ArrayList<String> keys()
	{
		Node<V> current = head;
		ArrayList<String> keys = new ArrayList<String>(inputs);
		while(current != null)
		{
			keys.add(current.key);
			current = current.next;
		}
		return keys;
	}
	public ArrayList<V> values()
	{
		Node<V> current = head;
		ArrayList<V> values = new ArrayList<V>(inputs);
		while(current != null)
		{
			values.add(current.val);
			current = current.next;
		}
		return values;
	}
	public Pair each()
	{
		//isEachEnd determines if we have already reached the
		//end of the linked list. This value is reset by the
		//reset() method.
		if(isEachEnd == true)
		{
			return null;
		}
		else if(isEachEnd == false && eachNode == null)
		{
			eachNode = head;
		}
		else
		{
			eachNode = eachNode.next;
		}
		//As we are at the end of the list, we can set isEachNode
		//to be true so we don't recieve pairs from each.
		if(eachNode == null)
		{
			isEachEnd = true;
			return null;
		}
		return new Pair<V>(eachNode.key, eachNode.val);
	}
	public void showTable()
	{
		System.out.println("\tRaw Hash Table Contents:");
		for(int i = 0; i < tableSize; i++)
		{
			if(table[i] != null)
				System.out.println(i + ": " + table[i].toString());
			else
				System.out.println(i + ": null");
		}
	}
	public int length()
	{
		return inputs;
	}
	public void reset()
	{
		eachNode = null;
		isEachEnd = false;
	}
	public class MyIterator<V> implements Iterator<V>
	{
		Node<V> current;

		public MyIterator(Node<V> theHead)
		{
			current = theHead;
		}
		public boolean hasNext()
		{
			return current != null;
		}
		public V next()
		{
			V temp = current.val;
			current = current.next;
			return temp;
		}
	}
	private static class Node<V>
	{
		public String key;
		public V val;
		public Node next;
		public Node prev;

		public Node(String key, V val)
		{
			this.key = key;
			this.val = val;
			next = null;
		}
		public Node(String key, V val, Node next, Node prev)
		{
			this.key = key;
			this.val = val;
			this.next = next;
			this.prev = prev;
		}
		public boolean hasNext()
		{
			if(next != null)
			{
				return true;
			}
			return false;
		}
		public boolean hasPrev()
		{
			if(prev != null)
			{
				return true;
			}
			return false;
		}
		public String toString()
		{
				return "key: " + key + " value: " + val.toString();
		}
	}
	public static class Pair<V>
	{
		public String key;
		public V value;

		public Pair(String key, V value)
		{
			this.key = key;
			this.value = value;
		}
	}
}