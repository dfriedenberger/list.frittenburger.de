package de.frittenburger.smtp.interfaces;


import de.frittenburger.smtp.bo.Email;
import de.frittenburger.smtp.bo.Sender;

public interface SmtpService {

	void send(Sender sender, Email email);

}
