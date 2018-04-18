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
import java.io.IOException;


public class FolderWrapper {

	private final EmailBoxReader reader;
	private final String folder;
	private int cnt;

	public FolderWrapper(EmailBoxReader reader, String folder,int cnt) throws IOException {
		this.reader = reader;
		this.folder = folder;
		this.cnt = cnt;
	}

	public int getCount() {
		return cnt;
	}
	
	public String listMessage(int u) throws IOException {
		if(u > cnt) 
			return null;
		if(u < 1)
			throw new IOException("Index "+u+" not allowed");
		
		return reader.listMessage(folder,u);
	}

	

}
