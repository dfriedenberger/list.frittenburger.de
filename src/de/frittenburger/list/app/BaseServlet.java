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
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.frittenburger.list.impl.NotificationServiceImpl;
import de.frittenburger.list.interfaces.NotificationService;


public abstract class BaseServlet extends HttpServlet {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	
	protected Authentication authentication = Authentication.getInstance();
	

	
	protected abstract void handleGet(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
	protected abstract void handlePost(String userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
	
	private void doGetLoginForm(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws IOException {
		
		//HtmlTemplate page = HtmlTemplate.load("templates/frame.htm");
		HtmlTemplate login = HtmlTemplate.load("templates/loginregister.htm");
		//page.replace("{menu}", "");
		//page.replace("{content}", login.toString());
		login.replace("{action}", request.getContextPath());
		
		
		
		
		if(errorMessage != null)
		{
			login.replace("{messageClass}", "error");
			login.replace("{message}", errorMessage);
		}
		else
		{
			login.replace("{messageClass}", "hidden");
			login.replace("{message}", "");
		}
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
			doGetLoginForm(request,response,null);
			return;
		}
		
	
	    handleGet(userId,request,response);
		

	}

	


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String error = null;
		
		HttpSession session = request.getSession();
		String id = session.getId();
		long last = session.getLastAccessedTime();
		
		//dump - Todo never dump passwords
		dump(request.getParameterMap());

		
		//Logout
		String function = request.getParameter("function");
		if ("logout".equals(function)) {
			authentication.remove(id);
			
			//Send Login Form
			doGetLoginForm(request,response,null);
			return;
		}
		
		
		//Login
		if ("login".equals(function)) {
			String user = request.getParameter("username");
			String pass = request.getParameter("password");
			boolean remember = "on".equals(request.getParameter("remember"));
			
			if (user != null && pass != null) {
				try {
					authentication.authenticate(id,user,pass,remember);
					doGet(request,response);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					error = "Unknown user / password combination";
				}
			}	
		}
		
		if ("register".equals(function)) {
			String email = request.getParameter("email");
			String pass = request.getParameter("password");
			String pass2 = request.getParameter("confirm-password");
			
			if(email.length() < 3 || !email.contains("@")) error = "invalid email";
			else if(!pass.equals(pass2)) error = "passwords different";
			else if(pass.length() < 8) error = "password to short use minimal 8 characters";
			else 
			{
				//create Account 
				String token = authentication.create(email,pass);
				
				NotificationService service = new NotificationServiceImpl();
				
				//send Token
				service.sendToken(email,token);
				
				//send Notification
				//maybe
			}
		}
		
		
		
		
		String userId = authentication.authenticated(id,last);
		//Not authenticated
		if (userId != null)
		{
			handlePost(userId,request,response);			
			return;
		}

		//Send Login Form
		doGetLoginForm(request,response,error);
		
		
		

	}
	
	private void dump(Map<String, String[]> parameterMap) {

		String dump = "";
		
		
		if(parameterMap.size() == 0)
			dump += "no parameters";
		
		for(String key : parameterMap.keySet())
		{
			dump += key+"=";
			
			//do not show passwords in dump
			if(key.toLowerCase().contains("pass"))
			{
				dump += "***** ";
				continue;
			}
			
			String[] values = parameterMap.get(key);
			if(values == null)
			{
				dump += "null ";
				continue;
			}
			if(values.length == 0)
			{
				dump += "[] ";
				continue;
			}
			
			
			for(int i = 0;i< values.length;i++)
			{
				dump += values[i] + (i+1 < values.length?",":" "); 
			}
			
		}
		System.out.println(dump);
		
		
	}
	
	

}