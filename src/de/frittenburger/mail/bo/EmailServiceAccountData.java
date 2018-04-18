package de.frittenburger.mail.bo;
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

import java.util.Set;

public class EmailServiceAccountData {

	private String provider;
	private String mailserver;
	private String username;
	private String password;
	private Set<String> folders;
	
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getMailserver() {
		return mailserver;
	}
	public void setMailserver(String mailserver) {
		this.mailserver = mailserver;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Set<String> getFolders() {
		return folders;
	}
	public void setFolders(Set<String> folders) {
		this.folders = folders;
	}
	

}
