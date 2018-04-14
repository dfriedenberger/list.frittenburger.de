package de.frittenburger.list.interfaces;

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
