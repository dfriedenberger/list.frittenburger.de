package de.frittenburger.list.interfaces;

import java.io.IOException;

import de.frittenburger.list.bo.UserData;

public interface UserManager {

	boolean userExists(String username);
	UserData getUser(String username) throws IOException;
	UserData findUserByAlias(String email);
	
}
