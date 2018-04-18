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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.frittenburger.list.bo.Task;
import de.frittenburger.list.interfaces.Configuration;
import de.frittenburger.list.interfaces.Constants;
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
	public List<Task> getSortedList(final String listId) throws IOException {
	
		Task[] list = getList(listId).read().toArray(new Task[0]);
		
		Arrays.sort(list, new Comparator<Task>(){

			@Override
			public int compare(Task task0, Task task1) {
				
				if(listId.equals(Constants.ArchivList) || listId.equals(Constants.TrashList))
					return Long.compare(task0.getDuedate().getTime(),task1.getDuedate().getTime());

				return Long.compare(task1.getDuedate().getTime(),task0.getDuedate().getTime());
			}});
		
		return new ArrayList<Task>(Arrays.asList(list));
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
