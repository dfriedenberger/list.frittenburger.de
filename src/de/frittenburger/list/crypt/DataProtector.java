package de.frittenburger.list.crypt;

import java.security.GeneralSecurityException;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DataProtector {


	private SecretProvider secretprovider;


	public DataProtector(SecretProvider secretprovider)
	{
		this.secretprovider = secretprovider;
	}
	public String protect(String text) throws GeneralSecurityException {
		

		 byte[] key = secretprovider.get128BitSecret();
		
         // Create key and cipher
         Key aesKey = new SecretKeySpec(key, "AES");
         Cipher cipher = Cipher.getInstance("AES");
         // encrypt the text
         cipher.init(Cipher.ENCRYPT_MODE, aesKey);
         byte[] encrypted = cipher.doFinal(text.getBytes());
         return DataConverter.b64encode(encrypted);
       
	}
	
	
	public String unprotect(String text)  throws GeneralSecurityException {
		
		 byte[] key = secretprovider.get128BitSecret();
			
         // Create key and cipher
         Key aesKey = new SecretKeySpec(key, "AES");
         Cipher cipher = Cipher.getInstance("AES");
		 // decrypt the text
         cipher.init(Cipher.DECRYPT_MODE, aesKey);
         byte[] encrypted = DataConverter.b64decode(text);
        return new String(cipher.doFinal(encrypted));
      
	}
	
}
