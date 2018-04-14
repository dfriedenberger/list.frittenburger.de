package de.frittenburger.list.crypt;

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
