package de.frittenburger.list.app;
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
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.frittenburger.crypt.HashCalculator;
import de.frittenburger.list.bo.UserData;
import de.frittenburger.list.impl.UserManagerImpl;
import de.frittenburger.list.interfaces.UserManager;




public class Authentication  {

	private class UserSession {
		
		private String userId;
		private boolean remember;
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public boolean isRemember() {
			return remember;
		}
		public void setRemember(boolean remember) {
			this.remember = remember;
		}

		
	}
	
	private static final long ONEHOUR = 1000 * 60 * 60;
	private final static HashCalculator hashCalculator = new HashCalculator();
	private final Map<String,UserSession> sessions = new HashMap<String, UserSession>();
	
	public void authenticate(String sessionId, String username, String password, boolean remember) throws IOException {
	
		UserManager userManager = UserManagerImpl.getInstance();
		
		if(!userManager.userExists(username))
			throw new IOException("PasswordRequired");

		UserData config = userManager.getUser(username);
	

		String hash;
		try {
			hash = hashCalculator.hash(config.getPasswordSalt(),password); 
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			throw new IOException("NoSuchAlgorithm");
		}
				
		if (!hash.equals(config.getPasswordHash()))
			throw new IOException("PasswordRequired");
		
		
		UserSession usersession = new UserSession();
		usersession.setUserId(config.getUserId());
		usersession.setRemember(remember);
		
		sessions.put(sessionId, usersession); //directory
	}

	public String authenticated(String sessionId, long last) {
		
		if(!sessions.containsKey(sessionId)) return null;
		
		UserSession us = sessions.get(sessionId);
		
		if(!us.isRemember())
		{
			if( (last + ONEHOUR) <= new Date().getTime()) //zu lang her
				return null;
		}
		//weitere Pruefungen
		
		return us.getUserId();
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

	public String create(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
