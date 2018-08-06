package de.frittenburger.crypt;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;

public class FileSecretProvider implements SecretProvider {

	private String secretFile;

	public FileSecretProvider(String secretFile) throws IOException {

		this.secretFile = secretFile;
		if(!new File(secretFile).exists())
		{
			//create
			byte[] b = new byte[500];
			new Random().nextBytes(b);
			saveText(DataConverter.b64encode(b));
		}
	
	}

	private void saveText(String text) throws IOException {

		Writer out = new OutputStreamWriter(new FileOutputStream(secretFile), "UTF-8");
		try {
			out.write(text);
		} finally {
			out.close();
		}
	}
	private String readText() throws IOException {

		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(secretFile),"UTF-8"));
		try {
			return in.readLine();
		} finally {
			in.close();
		}
	}
	
	@Override
	public byte[] get128BitSecret()  {

		try {
			byte[] b = DataConverter.b64decode(readText());
			
			int s = b[0] * b[1] + b[2];
			if(s < 0) s *= -1;
			int i = (s % (b.length - 16));
			
			return Arrays.copyOfRange(b, i, i + 16);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	

}
