package de.frittenburger.list.bo;
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
import java.util.Date;

public class Task {

	private String id;
	private String title;
	private Date duedate;
	private String details;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getDuedate() {
		return duedate;
	}
	public void setDuedate(Date duedate) {
		this.duedate = duedate;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	
	
}
