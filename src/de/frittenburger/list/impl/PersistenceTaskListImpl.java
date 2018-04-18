package de.frittenburger.list.impl;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.list.bo.Task;
import de.frittenburger.list.bo.TaskCollection;
import de.frittenburger.list.interfaces.TaskList;

public class PersistenceTaskListImpl implements TaskList {

	private File list;
	private TaskCollection collection;

	public PersistenceTaskListImpl(File list) throws IOException {

		
		ObjectMapper mapper = new ObjectMapper();
		this.list = list;
		if(list.exists())
			this.collection = mapper.readValue(list, TaskCollection.class);
		else
		{
			this.collection = new TaskCollection();
			this.collection.setTasks(new ArrayList<Task>());
			
		}
	}

	@Override
	public String getKey() {
		String name = list.getName();
		return name.substring(0, name.lastIndexOf(".json"));
	}

	@Override
	public int getCount() {
		return collection.getTasks().size();
	}

	
	
	private void commit() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writerWithDefaultPrettyPrinter().writeValue(list, collection);
	}
	

	@Override
	public void create(Task task) throws IOException {
		collection.getTasks().add(task);
		commit();
	}

	
	@Override
	public Task read(String id) throws IOException {
		
		for(Task task : collection.getTasks())
		{
			if(task.getId().equals(id))
				return task;
		}
		return null;
	}
	
	@Override
	public List<Task> read() throws IOException {
		return collection.getTasks();
	}
	
	@Override
	public void update(Task task) throws IOException {
		delete(task.getId());
		create(task);
	}

	@Override
	public void delete(String id) throws IOException {
		for(Task task : collection.getTasks())
		{
			if(task.getId().equals(id))
			{
				collection.getTasks().remove(task);
				commit();
				return;
			}
		}
		throw new IOException(id+" not found");
	}

	




	

}
