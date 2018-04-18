package de.frittenburger.list.app;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

public class HtmlTemplate {





	private String text;


	public HtmlTemplate(String text) {
		this.text = text;
	}
	
	public void replace(String token,String content) {
		
		for(int d = 0;d < 100;d++)
		{
			int i = text.indexOf(token);
			if(i < 0) 
			{
				if(d == 0) throw new RuntimeException("Token "+token+ " not found");
				return;
			}
			text = text.substring(0,i) +  content + text.substring(i+ token.length());
		}
	}
	
	public String toString()
	{
		return text;
	}
	
	

	public static HtmlTemplate load(String filename) throws IOException {

		InputStream in = null;
		try
		{
			String text = readStream(new FileInputStream(filename));
			return new HtmlTemplate(text);
		}
		finally
		{
			if(in != null)
				in.close();
		}
		
	}
	
	
	public static String readStream(InputStream is) throws IOException {
	    StringBuilder sb = new StringBuilder(512);
	  
		        Reader r = new InputStreamReader(is, "UTF-8");
		        int c = 0;
		        while ((c = r.read()) != -1) {
		            sb.append((char) c);
		        }
	 
	    return sb.toString();
	}

	public void writeTo(PrintWriter writer) {
		writer.print(text);
		writer.flush();
	}


	

}
