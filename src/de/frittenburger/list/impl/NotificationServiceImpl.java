package de.frittenburger.list.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.list.app.HtmlTemplate;
import de.frittenburger.list.interfaces.Configuration;
import de.frittenburger.list.interfaces.NotificationService;
import de.frittenburger.smtp.bo.Email;
import de.frittenburger.smtp.bo.Sender;
import de.frittenburger.smtp.bo.SmtpParameter;
import de.frittenburger.smtp.impl.SmtpServiceImpl;
import de.frittenburger.smtp.interfaces.SmtpService;

public class NotificationServiceImpl implements NotificationService {

		
	@Override
	public void sendToken(String addr,String token) throws IOException 
	{
		
	
		ObjectMapper mapper = new ObjectMapper();
		SmtpParameter param = mapper.readValue(new File(Configuration.smtpParameterFilePath), SmtpParameter.class);
		SmtpService emailservice = new SmtpServiceImpl(param);
		Sender sender = mapper.readValue(new File(Configuration.smtpSenderFilePath), Sender.class);


		Email email = new Email();
		email.setTo(new ArrayList<String>());
		email.getTo().add(addr);
		
		//todo
		email.setSubject("Welcome");
		 
		HtmlTemplate register_txt = HtmlTemplate.load("templates/emailregister.txt");
		register_txt.replace("{token}", token);
		email.setTextBody(register_txt.toString());
			
	    HtmlTemplate register_html = HtmlTemplate.load("templates/emailregister.htm");
	    register_html.replace("{token}", token);
		email.setHtmlBody(register_html.toString());
		
		//no attachments
		email.setAttachments(new ArrayList<String>());
			
		
		emailservice.send(sender, email);
		
		
		
	}
}

