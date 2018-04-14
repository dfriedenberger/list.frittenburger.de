package de.frittenburger.list.impl;

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
