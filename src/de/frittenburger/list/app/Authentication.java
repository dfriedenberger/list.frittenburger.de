package de.frittenburger.list.app;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.frittenburger.list.bo.UserData;
import de.frittenburger.list.impl.UserManagerImpl;
import de.frittenburger.list.interfaces.UserManager;




public class Authentication  {

	private static final long ONEHOUR = 1000 * 60 * 60;

	private final Map<String,String> sessions = new HashMap<String, String>();
	
	public void authenticate(String sessionId, String username, String password) throws IOException {
	
		UserManager userManager = UserManagerImpl.getInstance();
		
		if(!userManager.userExists(username))
			throw new IOException("PasswordRequired");

		UserData config = userManager.getUser(username);
	

		String hash;
		try {
			hash = MungPass(config.getPasswordSalt(),password); 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IOException("NoSuchAlgorithm");
		}
		
		//Todo
		System.out.println("create Hash "+hash);
		
		if (!hash.equals(config.getPasswordHash()))
			throw new IOException("PasswordRequired");
		
		sessions.put(sessionId, config.getUserId()); //directory
	}

	public String authenticated(String sessionId, long last) {
		
		if(!sessions.containsKey(sessionId)) return null;
		if( (last + ONEHOUR) <= new Date().getTime()) //zu lang her
			return null;
			
		//weitere Pruefungen
		
		return sessions.get(sessionId);
	}
	
	public void remove(String sessionId) {
		sessions.remove(sessionId);		
	}

	
	private static Authentication authentication = null;
	public synchronized static Authentication getInstance() {
		
		if(authentication == null)
		{
			authentication = new Authentication();
		}
		
		return authentication;
	}



	public static String MungPass(String salt,String pass) throws NoSuchAlgorithmException {
		MessageDigest m = MessageDigest.getInstance("SHA-256");
		// m.update(salt); 16Byte salt
		byte[] data = (salt + pass).getBytes();
		m.update(data, 0, data.length);
		BigInteger i = new BigInteger(1, m.digest());
		return i.toString(16);
	}

	
	
}
