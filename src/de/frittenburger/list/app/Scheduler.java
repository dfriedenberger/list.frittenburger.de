package de.frittenburger.list.app;

import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import de.frittenburger.list.bo.UserData;
import de.frittenburger.list.crypt.DataProtector;
import de.frittenburger.list.crypt.FileSecretProvider;
import de.frittenburger.list.impl.TaskListServiceImpl;
import de.frittenburger.list.impl.UserManagerImpl;
import de.frittenburger.list.interfaces.Configuration;
import de.frittenburger.list.interfaces.TaskListService;
import de.frittenburger.list.interfaces.UserManager;
import de.frittenburger.mail.bo.EmailServiceAccountData;
import de.frittenburger.mail.bo.MessagePart;
import de.frittenburger.mail.impl.PersistenceEmailCache;
import de.frittenburger.mail.impl.EmailServiceImpl;
import de.frittenburger.mail.interfaces.EmailService;
import de.frittenburger.mail.interfaces.Message;
import de.frittenburger.mail.interfaces.Sequence;

public class Scheduler {

	public static void main(String[] args) throws Exception {
		new Scheduler().step();
	}

	private void step() throws IOException, GeneralSecurityException {
       
		
		DataProtector protector = new DataProtector(new FileSecretProvider("C:/Secrets/list.txt"));
        
        
        
        Properties prop = new Properties();
		InputStream input = new FileInputStream(new File(Configuration.defaultMailPath, "config.properties"));
		EmailServiceAccountData emailServiceAccountData = new EmailServiceAccountData();

		try
		{
			// load a properties file
			prop.load(input);

			emailServiceAccountData.setProvider(prop.getProperty("provider"));
			emailServiceAccountData.setMailserver(prop.getProperty("mailserver"));
			emailServiceAccountData.setUsername(prop.getProperty("username"));
			emailServiceAccountData.setPassword(protector.unprotect(prop.getProperty("password")));


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
     
        
        PersistenceEmailCache index = new PersistenceEmailCache(Configuration.defaultMailPath + "/cache.txt");
		EmailService service = new EmailServiceImpl(index);
		
		try
		{
			service.open(emailServiceAccountData);
			List<String> folder = service.getFolders();
			for(String fd : folder)
			{
				if(!fd.equals("INBOX/ZTodo")) continue;
				Sequence seq = service.getUnreadMessages(fd);
				System.out.println(seq);
				service.openFolder(fd, seq);
				
				while (true) {
					List<Message> messages = service.getMessages();

					if (messages == null) break;
					for(Message mail : messages)
					{
						System.out.println(mail);
						
						String text = null;
						String html = "";
						for(MessagePart mp : mail.getContent())
						{
							if(mp.getName().equals("body"))
							{
								if(mp.getMimeType().equals("text/plain"))
									text = new String(mp.getData(),mp.getCharset());
								if(mp.getMimeType().equals("text/html"))
									html = new String(mp.getData(),mp.getCharset());
							}	
							else
							{
								//Attachment
							}
						}
						
						UserManager userManager = UserManagerImpl.getInstance();
						UserData userdata = userManager.findUserByAlias(mail.getFrom());
						if(userdata != null)
						{
							TaskListService tls = TaskListServiceImpl.getTaskListService(userdata.getUserId());
							String taskId = UUID.nameUUIDFromBytes(mail.toString().getBytes()).toString();   
							String list = tls.taskSearch(taskId);
							if(list != null) continue; //exists in list
							
							String details = html != null? html : 
								text != null ? text : "";
							
							tls.createTask("email", taskId, mail.getSubject(), mail.getDate(), details);
							
						}
					}
				}
				index.commit();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			service.close();
		}
	
	
	
	}

}
