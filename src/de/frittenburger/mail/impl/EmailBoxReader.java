package de.frittenburger.mail.impl;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import de.frittenburger.mail.bo.EmailServiceAccountData;
import de.frittenburger.mail.bo.Range;



public class EmailBoxReader  {

	Store store = null;
	private final EmailServiceAccountData config;
	private Map<String, Integer> cache = null;

	public EmailBoxReader(EmailServiceAccountData config) {
		this.config = config;
	}

	public void open() throws IOException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			store = session.getStore(config.getProvider());
			cache = new HashMap<String, Integer>();
		} catch (NoSuchProviderException e) {
			throw new RuntimeException(e);
		}
		try {
			store.connect(config.getMailserver(), config.getUsername(), config.getPassword());
		} catch (MessagingException e) {
			store = null;
			throw new IOException(e);
		}

	}

	public void close() throws IOException {
		try {
			store.close();
		} catch (MessagingException e) {
			throw new IOException(e);
		} finally {
			store = null;
			cache = null;
		}
	}

	public int cnt(String folder) throws IOException {
		try {
			Folder inbox = store.getFolder(folder);

			inbox.open(Folder.READ_ONLY);
			int cnt = inbox.getMessageCount();
			inbox.close(false);
			return cnt;
		} catch (MessagingException e) {
			throw new IOException(e);
		}

	}

	
	public List<String> listFolders() throws IOException {
		try {
			List<String> folder = new ArrayList<String>();
			list(folder, store.getDefaultFolder());
			return folder;
		} catch (MessagingException e) {
			throw new IOException(e);
		}

	}

	private void list(List<String> folderList, Folder folder) throws MessagingException {
		
		for (Folder fd : folder.list()) {
			folderList.add(fd.getFullName());
			if(config.getProvider().equals("pop3")) continue;
			list(folderList, fd);
		}
	}
	
	public String listMessage(String folder, int ix) throws IOException {
		
		try {
			Folder inbox = store.getFolder(folder);
	
			inbox.open(Folder.READ_ONLY);
			
			MessageWrapper wrapper = new MessageWrapper(inbox.getMessage(ix));

			inbox.close(false);

			return wrapper.getKey();
		
		} catch (Exception e) {
			
			if(e instanceof MessagingException)
			{
				System.err.printf("Invalid email (%s) in folder=%s index=%d\n",e.getMessage(),folder ,ix);
				return null;
			}
			else
			{
				e.printStackTrace();
			}
			throw new IOException(e);

		}
		
	}

	public String[] listMessages(String folder,Range range) throws IOException {
	
		String[] mesgids = new String[range.to - range.from + 1];
		try {

			Folder inbox = store.getFolder(folder);

			inbox.open(Folder.READ_ONLY);
			
			Message[] message = inbox.getMessages(range.from,range.to);
			for (int i = 0; i < message.length; i++) {
				mesgids[i] = null;
				try {

					MessageWrapper wrapper = new MessageWrapper(message[i]);
					cache.put(wrapper.getKey(), range.from + i);
					mesgids[i] = wrapper.getKey();
					
				} catch (Exception e) {
					
					if(e instanceof MessagingException)
					{
						message[i].writeTo(new FileOutputStream(new File("c:/temp/mailerror.eml")));
						System.err.printf("Invalid email (%s) in folder=%s index=%d\n",e.getMessage(),folder ,range.from + i);
					}
					else
						e.printStackTrace();
					
				} 
			}

			inbox.close(false);

		} catch (MessagingException e) {
			throw new IOException(e);
		}
		return mesgids;
	}

	public MessageWrapper read(String folder,String id) throws IOException {
		
		try {

			Folder inbox = store.getFolder(folder);

			inbox.open(Folder.READ_ONLY);
			
			int ix = cache.get(id).intValue();
			
			//Content lesen bevor inbox geschlossen wird
			MessageWrapper mail = new MessageWrapper(inbox.getMessage(ix));
			mail.parse();

			inbox.close(false);
			

			return mail;
			
		} catch (MessagingException | ParseException e) {
			throw new IOException(e);
		}
	}

	public void delete(int ix) throws IOException {
		// TODO Auto-generated method stub
		
	}

	
	

	
}
