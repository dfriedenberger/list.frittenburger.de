package de.frittenburger.list.crypt;
/*
 * Copyright (c) 2018 Dirk Friedenberger <projekte@frittenburger.de>
 * 
 * This file is part of list.frittenburger.de project.
 *
 * list.frittenburger.de is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * list.frittenburger.de is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MP3-Album-Art.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
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
