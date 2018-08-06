package de.frittenburger.list.app;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.list.interfaces.Configuration;
import de.frittenburger.smtp.bo.Sender;
import de.frittenburger.smtp.bo.SmtpParameter;

public class CreateDefaults {

	public static void main(String[] args) throws IOException {

		
		File smtp = new File(Configuration.smtpParameterFilePath);
		File send = new File(Configuration.smtpSenderFilePath);
		
		ObjectMapper mapper = new ObjectMapper();
		
		
		if(!smtp.exists())
		{
			SmtpParameter param = new SmtpParameter();
			mapper.writerWithDefaultPrettyPrinter().writeValue(smtp, param);
		}
		
		if(!send.exists())
		{
		    Sender sender = new Sender();
		    mapper.writerWithDefaultPrettyPrinter().writeValue(send, sender);
		}

	}

}
