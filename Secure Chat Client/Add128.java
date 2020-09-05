import java.util.*;
import java.math.*;

public class Add128 implements SymCipher
{
    private byte[] key;
    public Add128()
    {
        key = new byte[128];
        new Random().nextBytes(key);
    }
    public Add128(byte[] key)
    {
        this.key = key;
    }
	public byte[] getKey()
	{
        return key;   
	}

	public byte[] encode(String S)
	{
        byte[] byteString = S.getBytes();
        int j = 0;
        for(int i = 0; i < byteString.length; i++)
        {
            byteString[i] += key[j];
            j++;
            if(j >= 128)
            {
                j = 0;
            }
        }
        return byteString;
	}

	public String decode(byte[] b)
	{
        int j = 0;
        for(int i = 0; i < b.length; i++)
        {
            b[i] -= key[j];
            j++;
            if(j >= 128)
            {
                j = 0;
            }
        }
        return new String(b);
	}
}	