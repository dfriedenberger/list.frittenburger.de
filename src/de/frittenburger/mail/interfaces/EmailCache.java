package de.frittenburger.mail.interfaces;


public interface EmailCache {


	Index getIndex(String folder);

	void add(String messageKey);
	boolean exists(String messageKey);
		
}
