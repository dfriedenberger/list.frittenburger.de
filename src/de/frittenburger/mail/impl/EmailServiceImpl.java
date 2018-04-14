package de.frittenburger.mail.impl;
/*
 *  Copyright notice
 *
 *  (c) 2016 Dirk Friedenberger <projekte@frittenburger.de>
 *
 *  All rights reserved
 *
 *  This script is part of the Email2PDFA project. The Email2PDFA is
 *  free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The GNU General Public License can be found at
 *  http://www.gnu.org/copyleft/gpl.html.
 *
 *  This script is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  This copyright notice MUST APPEAR in all copies of the script!
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.frittenburger.mail.bo.EmailServiceAccountData;
import de.frittenburger.mail.bo.Range;
import de.frittenburger.mail.interfaces.EmailCache;
import de.frittenburger.mail.interfaces.EmailIndexSyncService;
import de.frittenburger.mail.interfaces.EmailService;
import de.frittenburger.mail.interfaces.Message;
import de.frittenburger.mail.interfaces.Sequence;



public class EmailServiceImpl implements EmailService {

	private final EmailCache emailIndex;
	private EmailIndexSyncService emailIndexSyncService = new EmailIndexSyncServiceImpl();
	private EmailBoxReader reader = null;


	private String folder = null;
	private Sequence sequence = null;

	public EmailServiceImpl(EmailCache emailIndex) {
		this.emailIndex = emailIndex;
	}

	@Override
	public void open(EmailServiceAccountData emailServiceAccountData) throws IOException {
		reader = new EmailBoxReader(emailServiceAccountData);
		reader.open();
	}

	@Override
	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		reader = null;
	}

	@Override
	public void openFolder(String folder,Sequence sequence) throws IOException {
		this.folder = folder;
		this.sequence = sequence;
	}

	@Override
	public List<String> getFolders() throws IOException {
		List<String> folderList = new ArrayList<String>();
		
		for(String fd : reader.listFolders())
		{
			//Todo: Filter konfigurierbar
			if(!fd.toLowerCase().startsWith("inbox")) 
			{
				//System.out.println("Filter "+fd);
				continue;
			}
			folderList.add(fd);
		}
		return folderList;
		
	}

	@Override
	public Sequence getUnreadMessages(String folder) throws IOException {
		
		int cnt = reader.cnt(folder);
		Sequence seq =  emailIndexSyncService.sync(emailIndex.getIndex(folder), new FolderWrapper(reader,folder,cnt));
		System.out.println("Folder "+folder+" cnt= "+cnt+ " seq= "+ (seq.hasNext()? seq : " ready"));
		return seq;
	}
	
	
	@Override
	public List<Message> getMessages() {

		if (!sequence.hasNext())
			return null; // ready

		List<Message> messages = new ArrayList<Message>();
		Range range = sequence.next();

		System.out.println("Read Range " + range);

		try {

			String[] msglst = reader.listMessages(folder, range);

			for (int i = 0; i < msglst.length; i++) {
				String msgid = msglst[i];
				int index = range.from + i;

				if(msgid == null) 
				{
					//register error
					emailIndex.getIndex(folder).register(index, null);
					continue;
				}
					
				// Download Message ???
				if (!emailIndex.exists(msgid)) {
					Message mail = reader.read(folder, msgid);
					messages.add(mail);
				}
				emailIndex.getIndex(folder).register(index, msgid);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return messages;

	}

	
}
