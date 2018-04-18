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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.frittenburger.list.bo.Task;
import de.frittenburger.list.bo.UserData;
import de.frittenburger.list.interfaces.Constants;
import de.frittenburger.list.interfaces.Job;
import de.frittenburger.list.interfaces.TaskList;
import de.frittenburger.list.interfaces.TaskListService;
import de.frittenburger.list.interfaces.UserManager;

public class ResubmissionJob implements Job {

	@Override
	public boolean shouldRun(long current, long last) {
		return getDayOfMonth(current) != getDayOfMonth(last);
	}

	private int getDayOfMonth(long d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(d));
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public void step() {
		//For all Users get resubmission folder
		UserManager user = UserManagerImpl.getInstance(); 
		
		Calendar cal = Calendar.getInstance(); // locale-specific
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long endofday = cal.getTimeInMillis();
		
		
		
		for(UserData userdata : user.getUsers())
		{
			try {
				TaskListService tls = TaskListServiceImpl.getTaskListService(userdata.getUserId());
				TaskList tl = tls.getList(Constants.ResubmissionList);
				List<String> resubmit = new ArrayList<String>();
				for(Task t : tl.read())
				{
					if(t.getDuedate().getTime() < endofday)
					{
						resubmit.add(t.getId());
						
					}
				}
				for(String taskId : resubmit)
					tls.moveTask(taskId, Constants.ResubmissionList, Constants.TodoList);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		
		
		
		
	}

}
