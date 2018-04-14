package de.frittenburger.list.bo;

import java.util.Set;

public class UserData {

	private String userId;
	private String username;
	private String passwordSalt;
	private String passwordHash;
	private Set<String> aliases;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPasswordSalt() {
		return passwordSalt;
	}
	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public Set<String> getAliases() {
		return aliases;
	}
	public void setAliases(Set<String> aliases) {
		this.aliases = aliases;
	}
	

	
}
