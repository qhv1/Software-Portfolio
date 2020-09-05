public class test
{
	public static void main(String[] args)
	{
		SymCipher cipher = new Add128();
		String testString = "test";
		byte[] byteTest = cipher.encode(testString);
		String testDecode = cipher.decode(byteTest);
		System.out.println(testString);
		System.out.println(testDecode);

	}
}