package de.frittenburger.list.impl;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import de.frittenburger.list.bo.UserData;
import de.frittenburger.list.interfaces.Configuration;
import de.frittenburger.list.interfaces.UserManager;

public class UserManagerImpl implements UserManager {

	private final String path;
	private final List<UserData> users = new ArrayList<UserData>();

	public UserManagerImpl(String path) {
		this.path = path;
	}
	
	

	@Override
	public boolean userExists(String username) {
		for(UserData userData : users)
		{
			if(userData.getUsername().equals(username)) return true;
		}
		return false;
	}
	
	@Override
	public UserData getUser(String username) throws IOException {
		
		for(UserData userData : users)
		{
			if(userData.getUsername().toLowerCase().equals(username.toLowerCase())) return userData;
		}
		throw new IOException("User not exists");
	}

	@Override
	public UserData findUserByAlias(String email) {
		for(UserData userData : users)
		{
			if(userData.getAliases().contains(email.toLowerCase())) return userData;
		}
		return null;
	}

	@Override
	public List<UserData> getUsers() {
		return users;
	}
	
	private void load() {

		users.clear();

		for (File d : new File(path).listFiles()) {
			if (!d.isDirectory())
				continue;
			Properties prop = new Properties();
			InputStream input = null;
			try {

				input = new FileInputStream(new File(d, "config.properties"));

				// load a properties file
				prop.load(input);

				UserData userData = new UserData();
				userData.setUserId(d.getName());
				userData.setUsername(prop.getProperty("username"));
				userData.setPasswordSalt(prop.getProperty("passwordsalt"));
				userData.setPasswordHash(prop.getProperty("passwordhash"));

				Set<String> aliases = new HashSet<String>();
				
				// defining variable for assignment in loop condition part
			    String value;
		    	
			    // next value loading defined in condition part
			    for(int i = 0; (value = prop.getProperty("alias" + "." + i)) != null; i++) {
			    	aliases.add(value.toLowerCase());
			    }
				
				
				
				userData.setAliases(aliases);
				users.add(userData);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public synchronized static UserManager getInstance() {

		// Todo check for changes on files
		UserManagerImpl userManager = new UserManagerImpl(Configuration.userPath);
		userManager.load();
		return userManager;
	}







	
	
	

}
