package de.frittenburger.smtp.bo;

public class SmtpParameter {

	private boolean useSSL;
	private String host;
	private boolean authenticate;
	private String username;
	private String password;
	public boolean isUseSSL() {
		return useSSL;
	}
	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public boolean isAuthenticate() {
		return authenticate;
	}
	public void setAuthenticate(boolean authenticate) {
		this.authenticate = authenticate;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
