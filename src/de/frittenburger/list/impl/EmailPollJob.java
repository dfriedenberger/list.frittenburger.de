package de.frittenburger.list.impl;
/*
 * Copyright (c) 2018 Dirk Friedenberger <projekte@frittenburger.de>
 * 
 * This file is part of list.frittenburger.de project.
 *
 * list.frittenburger.de is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * list.frittenburger.de is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MP3-Album-Art.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import de.frittenburger.crypt.DataProtector;
import de.frittenburger.crypt.FileSecretProvider;
import de.frittenburger.list.bo.UserData;
import de.frittenburger.list.interfaces.Configuration;
import de.frittenburger.list.interfaces.Constants;
import de.frittenburger.list.interfaces.Job;
import de.frittenburger.list.interfaces.TaskListService;
import de.frittenburger.list.interfaces.UserManager;
import de.frittenburger.mail.bo.EmailServiceAccountData;
import de.frittenburger.mail.bo.MessagePart;
import de.frittenburger.mail.impl.EmailServiceImpl;
import de.frittenburger.mail.impl.PersistenceEmailCache;
import de.frittenburger.mail.interfaces.EmailService;
import de.frittenburger.mail.interfaces.Message;
import de.frittenburger.mail.interfaces.Sequence;

public class EmailPollJob implements Job {

	@Override
	public boolean shouldRun(long current, long last) {
		return (current - last) > (10 * 60 * 1000); //every 10 minutes
	}
	
	@Override
	public void step() {

		DataProtector protector;
		try {
			protector = new DataProtector(new FileSecretProvider(Configuration.secret));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		for (File d : new File(Configuration.mailPath).listFiles()) {
			if (!d.isDirectory())
				continue;
			Properties prop = new Properties();
			InputStream input = null;
			try {
				input = new FileInputStream(new File(d, "config.properties"));
				EmailServiceAccountData emailServiceAccountData = new EmailServiceAccountData();

				// load a properties file
				prop.load(input);

				emailServiceAccountData.setProvider(prop.getProperty("provider"));
				emailServiceAccountData.setMailserver(prop.getProperty("mailserver"));
				emailServiceAccountData.setUsername(prop.getProperty("username"));
				emailServiceAccountData.setPassword(protector.unprotect(prop.getProperty("password")));

				Set<String> folders = new HashSet<String>();
				
				// defining variable for assignment in loop condition part
			    String value;
			    // next value loading defined in condition part
			    for(int i = 0; (value = prop.getProperty("folder" + "." + i)) != null; i++) {
			    	folders.add(value);
			    }
			    emailServiceAccountData.setFolders(folders);
			
				
				poll(d.getName(), emailServiceAccountData);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
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
		}

	}

	private void poll(String id, EmailServiceAccountData emailServiceAccountData) throws IOException {

		PersistenceEmailCache index = new PersistenceEmailCache(Configuration.mailPath + "/" + id + "/cache.txt");
		EmailService service = new EmailServiceImpl(index);
		try {

			service.open(emailServiceAccountData);
			List<String> folder = service.getFolders();
			for (String fd : folder) {
				if (!emailServiceAccountData.getFolders().contains(fd)) continue;
				
			
				Sequence seq = service.getUnreadMessages(fd);
				System.out.println(seq);
				service.openFolder(fd, seq);

				while (true) {
					List<Message> messages = service.getMessages();

					if (messages == null)
						break;
					for (Message mail : messages) {
						System.out.println(mail);

						String text = null;
						String html = "";
						for (MessagePart mp : mail.getContent()) {
							if (mp.getName().equals("body")) {
								if (mp.getMimeType().equals("text/plain"))
									text = new String(mp.getData(), mp.getCharset());
								if (mp.getMimeType().equals("text/html"))
								{
									html = new String(mp.getData(), mp.getCharset());
									html = Jsoup.clean(html, Whitelist.basic());
								}
							} else {
								// Attachment
							}
						}

						UserManager userManager = UserManagerImpl.getInstance();
						UserData userdata = userManager.findUserByAlias(mail.getFrom());
						if (userdata != null) {
							TaskListService tls = TaskListServiceImpl.getTaskListService(userdata.getUserId());
							String taskId = UUID.nameUUIDFromBytes(mail.toString().getBytes()).toString();
							String list = tls.taskSearch(taskId);
							if (list != null)
								continue; // exists in list

							String details = html != null ? html : text != null ? text : "";
							tls.createTask(Constants.TodoList, taskId, mail.getSubject(), mail.getDate(), details);

						}
					}
				}
				index.commit();
			}

		} finally {
			service.close();
		}
	}

	

}
