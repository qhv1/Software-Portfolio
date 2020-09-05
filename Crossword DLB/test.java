import java.io.*;
import java.util.*;

public class test
{
	public static void main(String[] args) throws IOException
	{
		DLB test = new DLB();
		test.add("abc");
		test.add("abe");
		test.add("abet");

		StringBuilder stringTest = new StringBuilder("abet");
		System.out.println(test.searchPrefix(stringTest));
	}
}