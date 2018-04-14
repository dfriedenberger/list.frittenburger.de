package de.frittenburger.list.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.frittenburger.list.bo.Task;
import de.frittenburger.list.interfaces.Configuration;
import de.frittenburger.list.interfaces.TaskList;
import de.frittenburger.list.interfaces.TaskListService;

public class TaskListServiceImpl implements TaskListService {

	private final File data;
	public TaskListServiceImpl(File data) {
		this.data = data;
	}

	public static TaskListService getTaskListService(String userId) throws IOException {

		File data = new File(Configuration.userPath,userId);
		if(!data.exists()) throw new IOException(userId+" not exists");
		if(!data.isDirectory()) throw new IOException(userId+" not a directory");
        return new TaskListServiceImpl(data);
	}

	@Override
	public TaskList getList(String listId) throws IOException {

		File list = new File(data,listId+".json");
		return new PersistenceTaskListImpl(list);
		
	}

	@Override
	public List<TaskList> getLists() throws IOException {
		List<TaskList> l = new ArrayList<TaskList>();
		for(File f : data.listFiles())
		{
			if(f.getName().endsWith(".json"))
				l.add(new PersistenceTaskListImpl(f));
			
			
		}
		return l;
	}
	
	@Override
	public String taskSearch(String taskId) throws IOException {
		 
		for(TaskList taskList : getLists())
		{
			if(taskList.read(taskId) != null)
				return taskList.getKey();
		}
		return null;
	}
	
	
	@Override
	public Task createDefaultTask(String listId, String title) throws IOException {
		
		TaskList list = new PersistenceTaskListImpl(new File(data,listId+".json"));
		
		Task task = new Task();
		task.setId(UUID.randomUUID().toString());
		task.setTitle(title);
		task.setDuedate(new Date());
		task.setDetails("");
		list.create(task);
		
		return task;
	}

	
	@Override
	public Task updateTask(String listId, String taskId, String title, Date duedate, String details) throws IOException {
		
		TaskList list = new PersistenceTaskListImpl(new File(data,listId+".json"));
		
		Task task = list.read(taskId);
		
		task.setTitle(title);
		task.setDuedate(duedate);
		task.setDetails(details);
		list.update(task);
		
		return task;
	}

	@Override
	public Task createTask(String listId, String taskId, String title, Date duedate, String details)
			throws IOException {
		TaskList list = new PersistenceTaskListImpl(new File(data,listId+".json"));
	
		Task task = new Task();
		task.setId(taskId);
		task.setTitle(title);
		task.setDuedate(duedate);
		task.setDetails(details);
		list.create(task);
		
		return task;
	}

	
	@Override
	public void moveTask(String taskId, String listIdFrom, String listIdTo) throws IOException {
		
		TaskList listFrom = new PersistenceTaskListImpl(new File(data,listIdFrom+".json"));
		TaskList listTo = new PersistenceTaskListImpl(new File(data,listIdTo+".json"));

		Task task = listFrom.read(taskId);
		
		listTo.create(task);
		listFrom.delete(task.getId());
		
		
	}

	@Override
	public Task copyTask(String taskId, String listIdFrom, String listIdTo, Date duedate) throws IOException {
		TaskList listFrom = new PersistenceTaskListImpl(new File(data,listIdFrom+".json"));
		TaskList listTo = new PersistenceTaskListImpl(new File(data,listIdTo+".json"));

		Task orgTask = listFrom.read(taskId);
		
		Task task = new Task();
		task.setId(UUID.randomUUID().toString());
		task.setTitle(orgTask.getTitle());
		task.setDuedate(duedate);
		task.setDetails(orgTask.getDetails());
		listTo.create(task);
		return task;
	}

	

	
	


}
