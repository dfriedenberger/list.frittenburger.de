package de.frittenburger.list.interfaces;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.frittenburger.list.bo.Task;

public interface TaskListService {

	TaskList getList(String listId) throws IOException;

	Task createDefaultTask(String listId, String title) throws IOException;

	String taskSearch(String taskId) throws IOException;
	
	Task createTask(String listId, String taskId, String title, Date duedate, String details) throws IOException;

	Task updateTask(String listId, String taskId, String title, Date duedate, String details) throws IOException;

	void moveTask(String taskId, String listIdFrom, String listIdTo) throws IOException;

	List<TaskList> getLists() throws IOException;

	Task copyTask(String taskId, String listIdFrom, String listIdTo, Date duedate) throws IOException;

}
