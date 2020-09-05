
import java.util.*;
import java.math.*;
public class Substitute implements SymCipher
{
    private byte[] key;
	private byte[] inverse;

	public Substitute()
	{
        ArrayList<Byte> preKey = new ArrayList<>(256);
        for(int i = 0; i < 256; i++)
        {
            preKey.add((byte)i);
        }
        Collections.shuffle(preKey);
        key = new byte[256];
		for(int i = 0; i < key.length; i++)
        {
            key[i] = preKey.get(i).byteValue();
        }
		inverse = new byte[256];
		for(int i = 0; i < key.length; i++)
		{
			int temp;
			temp = key[i] + 128;
			inverse[temp] = (byte)i;
		}
	}
	public Substitute(byte[] key)
	{
		this.key = key;
        inverse = new byte[256];
        for(int i = 0; i < key.length; i++)
        {
            int temp;
            temp = key[i] + 128;
            inverse[temp] = (byte)i;
        }
	}
	public byte[] getKey()
	{
		return key;
	}

	public byte[] encode(String S)
	{
		byte[] byteString = S.getBytes();
		for(int i = 0; i < byteString.length; i++)
		{
			int temp;
            temp = byteString[i];

            byteString[i] = key[temp];
		}
        return byteString;
	}

	public String decode(byte[] b)
	{
        for(int i = 0; i < b.length; i++)
        {
            int temp = b[i] + 128;
            b[i] = inverse[temp];
        }
        return new String(b);
	}
}