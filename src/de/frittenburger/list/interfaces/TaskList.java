package de.frittenburger.list.interfaces;
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
import java.util.List;

import de.frittenburger.list.bo.Task;

public interface TaskList {

	
	String getKey();
	int getCount();
	//CRUD
	
	void create(Task task) throws IOException;
	Task read(String id) throws IOException;
	List<Task> read() throws IOException;

	void update(Task task) throws IOException;
	void delete(String id) throws IOException;
	

	
}
