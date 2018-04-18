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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public abstract class BaseServlet extends HttpServlet {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	
	protected Authentication authentication = Authentication.getInstance();
	

	
	protected abstract void handleGet(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
	protected abstract void handlePost(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
	
	private void doGetLoginForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//HtmlTemplate page = HtmlTemplate.load("templates/frame.htm");
		HtmlTemplate login = HtmlTemplate.load("templates/loginregister.htm");
		//page.replace("{menu}", "");
		//page.replace("{content}", login.toString());
		login.replace("{action}", request.getContextPath());
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		login.writeTo(response.getWriter());
		
	}
	
	
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		String id = session.getId();
		long last = session.getLastAccessedTime();
		
		//Not authenticated
		String userId = authentication.authenticated(id,last);
		if (userId == null)
		{
			//Send Login Form
			doGetLoginForm(request,response);
			return;
		}
		
	
	    handleGet(userId,request,response);
		

	}

	


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		HttpSession session = request.getSession();
		String id = session.getId();
		long last = session.getLastAccessedTime();

		//Logout
		String function = request.getParameter("function");
		if ("logout".equals(function)) {
			authentication.remove(id);
			
			//Send Login Form
			doGetLoginForm(request,response);
			return;
		}
		
		
		//Login
		String user = request.getParameter("username");
		String pass = request.getParameter("password");
		if (user != null && pass != null) {
			try {
				authentication.authenticate(id,user,pass);
				doGet(request,response);
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		
		String userId = authentication.authenticated(id,last);
		//Not authenticated
		if (userId == null)
		{
			//Send Login Form
			doGetLoginForm(request,response);
			return;
		}

		handlePost(userId,request,response);

	}
	
	

}