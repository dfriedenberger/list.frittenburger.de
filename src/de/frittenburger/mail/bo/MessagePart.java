package de.frittenburger.mail.bo;

import java.nio.charset.Charset;


public class MessagePart {

	private String name;
	private Charset charset;
	private String mimeType;
	private byte[] data;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Charset getCharset() {
		return charset;
	}
	public void setCharset(Charset charset) {
		this.charset = charset;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "name="+name+" charset=" + charset + ", mimeType=" + mimeType + ", length=" + data.length;
	}
	

}
