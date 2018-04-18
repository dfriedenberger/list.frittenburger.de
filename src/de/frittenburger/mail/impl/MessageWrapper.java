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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.MimeMessage;

import de.frittenburger.mail.bo.MessagePart;


public class MessageWrapper implements de.frittenburger.mail.interfaces.Message {

	
	private final static DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");


	
	private final Message message;
	private final List<MessagePart> content = new ArrayList<MessagePart>();

	private String from = null;
	private String subject = null;
	private Date date = null;
	private String key = null;

	public MessageWrapper(Message message) throws MessagingException, ParseException {
		this.message = message;
		parseHeader();
	}

	
	public String getKey() {
		return key;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public String getFrom() {
		return from;
	}
	
	@Override
	public String toString()
	{
		return from + " "+ subject + " " + dateformat.format(date) +  " " + content;
	}
	
	public void parseHeader() throws MessagingException, ParseException 
	{
		//sentdate = message.getSentDate();
		//messageId = message.getHeader("Message-ID")[0];
		
		String[] dateheader = message.getHeader("Date");
		if(dateheader == null || dateheader.length != 1)
			throw new MessagingException("Invalid Date-Header");
	    date = parseDate(dateheader[0]);
		
		String[] fromheader = message.getHeader("From");
		if(fromheader == null || fromheader.length != 1)
			throw new MessagingException("Invalid From-Header");
		
		from = parseFromAddress(fromheader[0]);
		
		subject = message.getSubject();
		if(subject == null) subject = "";
	
		key = from+"_"+dateformat.format(date).replaceAll("[^0-9_]+","")+"_"+subject.replaceAll("[^a-zA-Z0-9_]+","_");
		
	}

	public String parseFromAddress(String email) throws AddressException {
		 if(email.trim().equals("")) throw new AddressException("Empty Address");
		 InternetAddress emailAddr = new InternetAddress(email);
	     emailAddr.validate();		
	     return emailAddr.getAddress();
	}


	private static  DateFormat[] dateformats = new DateFormat[]{
			new MailDateFormat() , 
			new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH) //Sun Feb 28 13:17:24 GMT+01:00 2016
   };

	public Date parseDate(String date) throws ParseException   {
	   Date result = null;
	   for(DateFormat df : dateformats)
	   {
		   result =  df.parse(date); 
		   if(result != null) break;
	   }
	   if(result == null)
			throw new ParseException("could not parse "+date,0);
		return result;
	}


	public void parse() throws IOException, MessagingException
	{
		Object msgContent = message.getContent();
		parseContent(msgContent,null,new ContentType(message.getContentType()));
	}
	
	private void parseContent(Object msgContent, String filename , ContentType contentType)
			throws IOException, MessagingException {

		if(filename == null)
			filename = "body";
		
		Charset charset = null;
		try
		{
			String charsetStr = contentType.getParameter("charset");
			if(charsetStr != null)
				charset = Charset.forName(charsetStr);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Could not convert: " + contentType.toString()+ " ex="+ e.getMessage());
		}
		
		/*
			System.out.println("Part " + msgContent.getClass() + " type=" + contentType.getBaseType());
			System.out.println("charset " + charset);
			System.out.println("name " + filename);
		 */
		if (msgContent instanceof Multipart) {
			Multipart multipart = (Multipart) msgContent;
			// System.out.println("MultiPartContentType
			// "+multipart.getContentType());
	
			
			for (int j = 0; j < multipart.getCount(); j++) {
				BodyPart bodyPart = multipart.getBodyPart(j);
				BodyPartWrapper wrapper = new BodyPartWrapper(bodyPart);
				parseContent( bodyPart.getContent(), wrapper.getFilename(), wrapper.getContentType());
			}
			
			
		} else {

			if(charset == null)
				charset = StandardCharsets.UTF_8;
			
			if (msgContent instanceof String) {
				String str = (String) msgContent;
				parseInputStream(new ByteArrayInputStream(str.getBytes(charset)), filename , contentType, charset);
			} else if (msgContent instanceof InputStream) {
				// BASE64DecoderStream
				// QPDecoderStream
				// SharedByteArrayInputStream
				InputStream is = (InputStream) msgContent;
				parseInputStream( is, filename , contentType, charset);
			} else if (msgContent instanceof MimeMessage) {
				MimeMessage mimeMessage = (MimeMessage) msgContent;
				parseInputStream(mimeMessage.getRawInputStream(), filename , new ContentType("message/rfc822"), charset);
			} else {
				throw new RuntimeException("Not implemented " + msgContent);
			}
		}

	}


	private void parseInputStream(InputStream is, String filename, ContentType contentType, Charset charset) throws IOException
	{
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}
		buffer.flush();
		
		MessagePart part = new MessagePart();
		part.setName(filename);
		part.setCharset(charset);
		part.setMimeType(contentType.getBaseType());
		part.setData(buffer.toByteArray());
		
		content.add(part);
		
	}


	@Override
	public List<MessagePart> getContent() {
		return content;
	}


	



	


	



}
