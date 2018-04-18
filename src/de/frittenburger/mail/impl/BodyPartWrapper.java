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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParameterList;



public class BodyPartWrapper {

	
	private class ParamHeader {
		public String value;
		public Map<String,String> params = new HashMap<String,String>();
	}
	
	private String filename = null;
	private String contentId = null;
	private String disposition = null;
	private ContentType contentType = null;

	
	public BodyPartWrapper(BodyPart bodyPart) throws MessagingException, UnsupportedEncodingException {
		
		
		ParamHeader contentTypeHeader = parseHeader(bodyPart.getHeader("Content-Type"));
		String p[] = contentTypeHeader.value.split("/");
		ParameterList params = new ParameterList();
		for(String k : contentTypeHeader.params.keySet())
		{
			params.set(k, contentTypeHeader.params.get(k));
		}
		contentType = new ContentType(p[0],p[1],params);

		
		
		
		
		ParamHeader dispositionHeader = parseHeader(bodyPart.getHeader("Content-Disposition"));
		if(dispositionHeader != null)
		{
			disposition = dispositionHeader.value;
			// has Filename?
			if(dispositionHeader.params.containsKey("filename"))
			{
				filename = dispositionHeader.params.get("filename"); //bodyPart.getFileName();
				String decodedFilename = MimeUtility.decodeText(filename);
				filename = parseValidFilename(decodedFilename,contentType);	
			}
		}
				
		ParamHeader contentIdHeader = parseHeader(bodyPart.getHeader("Content-ID"));
		if (contentIdHeader != null) {
			contentId = contentIdHeader.value.trim();
			contentId = contentId.substring(1, contentId.length()-1);
		}
		
	
	}

	public String getContentId() {
		return contentId;
	}

	public String getFilename() {
		return filename;
	}

	public String getDisposition() {
		return disposition;
	}

	public ContentType getContentType() {
		return contentType;
	}

	private static final String allowedfilenamechar = "[]#+-_=.~";
	
	private static String MIMETypes = "res/extensions.txt";
	static Map<String,String> extensions = new HashMap<String,String>();
	
	
	static {
		BufferedReader in = null;
		try {
			File fileDir = new File(MIMETypes);
			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));

			String str;

			while ((str = in.readLine()) != null) {
				str = str.trim();
				if (str.isEmpty())
					continue;
				if (str.startsWith("#"))
					continue;
				int i = str.indexOf("=");
				if (i < 0)
					continue;

				String ext = str.substring(0, i).trim();
				String contentType = str.substring(i + 1).trim();
				if (extensions.containsKey(contentType))
					throw new IOException("Contenttype also registered " + contentType);
				extensions.put(contentType, ext);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally
		{
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	
	
	private String parseValidFilename(String decodedFilename,ContentType contentType) {
		
		char c[] = decodedFilename.toCharArray();
		
		for(int i = 0;i < c.length;i++)
		{
			
			if('a' <= c[i] && c[i] <= 'z') continue;
			if('A' <= c[i] && c[i] <= 'Z') continue;
			if('0' <= c[i] && c[i] <= '9') continue;
			if(allowedfilenamechar.indexOf(c[i]) >= 0) continue;
			
			//replace
			c[i] = '_';
		}
		
		//check Extension
        String filename = new String(c);
        
        int i = filename.lastIndexOf(".");
        if(i < 0) //no extension found
        {
    		String 	baseType =  contentType.getBaseType();
    		if(extensions.containsKey(baseType))
    			filename += extensions.get(baseType);
    	}	
		return filename;
	}
	
	private ParamHeader parseHeader(String header[]) throws MessagingException {
		// 	attachment; filename=Herbstfeuerfest 2016

		
		if(header == null) return null; //no such header
		
		if(header.length < 1)
		{
			throw new MessagingException("no header value");
		}
		
		
		if(header.length > 1)
		{
			System.err.println("more than one header value found, ignore others");
			for(String h : header)
				System.err.println("header found "+h);

		}
		
		String text = header[0];
		
		String p[] = text.split(";");
		if(p.length < 1) throw new MessagingException(text);
		
		
		String value = p[0].trim();
		if(value.matches("[\";]"))
			throw new MessagingException(text);
		
		
		ParamHeader pheader = new ParamHeader();
		pheader.value = value;
		
		
		for(int i = 1;i < p.length;i++)
		{
			int ix = p[i].indexOf("=");
			if(ix <= 0) 
				throw new MessagingException(text);
			
			String key = p[i].substring(0, ix).trim();
			String val = p[i].substring(ix + 1).trim().replaceAll("^\"|\"$", "");
			if(key.matches("[;\"]"))
				throw new MessagingException(text);		
			if(val.matches("[;\"]"))
				throw new MessagingException(text);		
			
			
			if(key.equals("filename*"))
				key = "filename";
			if(key.equals("filename*0"))
				key = "filename";
			
			if(pheader.params.containsKey(key))
				throw new MessagingException(text);		
			
			pheader.params.put(key, val);
		}
		
		
		return pheader;
	}

	
}
