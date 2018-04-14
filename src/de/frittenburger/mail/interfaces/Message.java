package de.frittenburger.mail.interfaces;

import java.util.Date;
import java.util.List;

import de.frittenburger.mail.bo.MessagePart;

public interface Message {

	
	public String getSubject();
	public Date getDate();
	public String getFrom();
	public List<MessagePart> getContent();

}
