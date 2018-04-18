package de.frittenburger.list.app;
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
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.frittenburger.list.impl.TaskListServiceImpl;
import de.frittenburger.list.interfaces.Constants;
import de.frittenburger.list.interfaces.TaskList;
import de.frittenburger.list.interfaces.TaskListService;

public class ListServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void handleGet(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
		String listkey = request.getPathInfo().substring(1).trim();
		
		if(listkey.equals("empty"))
			listkey = Constants.TodoList;
			
		HtmlTemplate page = HtmlTemplate.load("templates/frame.htm");
		HtmlTemplate list = HtmlTemplate.load("templates/list.htm");
		HtmlTemplate menu = HtmlTemplate.load("templates/menu.htm");

		
		String links2lists = "";
		
		TaskListService service = TaskListServiceImpl.getTaskListService(userId);

		
		for(TaskList tl : service.getLists())
		{
			HtmlTemplate menuitem = HtmlTemplate.load("templates/menu_item.htm");
			menuitem.replace("{list}", tl.getKey());
			menuitem.replace("{class}",tl.getKey().equals(listkey)?"active":"");
			menuitem.replace("{count}", ""+tl.getCount());
	
			links2lists += menuitem.toString();
		}
		menu.replace("{links2lists}",links2lists );
		
		
		page.replace("{menu}", menu.toString());
		page.replace("{content}", list.toString());
		page.replace("{list}", listkey);

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		page.writeTo(response.getWriter());		
		
	}

	@Override
	protected void handlePost(String userId, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		TaskListService service = TaskListServiceImpl.getTaskListService(userId);
		String function = request.getParameter("function");
		
		Object message = "unknown function "+function;
		int stat = HttpServletResponse.SC_METHOD_NOT_ALLOWED;
		if("create".equals(function))
		{
			String listid = request.getParameter("list");
			String title = request.getParameter("title");
			
			message = service.createDefaultTask(listid,title);
			stat = HttpServletResponse.SC_CREATED;
		} 
		else if("list".equals(function))
		{
			String listid = request.getParameter("list");
			
			message = service.getSortedList(listid);
			stat = HttpServletResponse.SC_OK;

		}
		else if("move".equals(function))
		{
			String listFrom = request.getParameter("list");
			String taskId = request.getParameter("id");
			String listTo = request.getParameter("target");
			service.moveTask(taskId,listFrom,listTo);
			
			message = taskId;
			stat = HttpServletResponse.SC_OK;
		}
		else if("copy".equals(function))
		{
			String listFrom = request.getParameter("list");
			String taskId = request.getParameter("id");
			String listTo = request.getParameter("target");
			Date duedate = new Date(Long.parseLong(request.getParameter("date")));

			message = service.copyTask(taskId,listFrom,listTo,duedate);
			stat = HttpServletResponse.SC_OK;
		}
		else if("update".equals(function))
		{
			System.out.println(request.getParameterMap());
			
			String listid = request.getParameter("list");
			String taskId = request.getParameter("id");
		
			String title = request.getParameter("title");
			String details = request.getParameter("details");
			Date duedate = new Date(Long.parseLong(request.getParameter("date")));

			message = service.updateTask(listid,taskId,title,duedate,details);
			stat = HttpServletResponse.SC_OK;

		
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		response.setStatus(stat);
		response.setContentType("application/json");			
		response.getWriter().print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message));
		return;
				
	}

}
