package de.frittenburger.smtp.bo;

public class Sender {

	private String from;
	private boolean sign;
	private String keystore;
	private String keystorePassword;
	private String privkeyPasswort;
	private String privkeyAlias;
	private String certAlias;
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public boolean isSign() {
		return sign;
	}
	public void setSign(boolean sign) {
		this.sign = sign;
	}
	public String getKeystore() {
		return keystore;
	}
	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}
	public String getKeystorePassword() {
		return keystorePassword;
	}
	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}
	public String getPrivkeyPasswort() {
		return privkeyPasswort;
	}
	public void setPrivkeyPasswort(String privkeyPasswort) {
		this.privkeyPasswort = privkeyPasswort;
	}
	public String getPrivkeyAlias() {
		return privkeyAlias;
	}
	public void setPrivkeyAlias(String privkeyAlias) {
		this.privkeyAlias = privkeyAlias;
	}
	public String getCertAlias() {
		return certAlias;
	}
	public void setCertAlias(String certAlias) {
		this.certAlias = certAlias;
	}

	
}
