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
import java.io.File;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;

import de.frittenburger.list.impl.EmailPollJob;
import de.frittenburger.list.impl.ResubmissionJob;
import de.frittenburger.list.impl.Scheduler;



public class ListServer {

	public static void main(String[] args) throws Exception {
		
		
		Server server = new Server(8888);

	      

        //data for files to assign with meta data
        ResourceHandler rh1 = new ResourceHandler();
        ContextHandler context1 = new ContextHandler();
        context1.setContextPath("/");
        File dir1 = new File("htdocs");
        context1.setBaseResource(Resource.newResource(dir1));
        context1.setHandler(rh1);

     
        
        ServletContextHandler context2 = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context2.setContextPath("/");
        //context2.setResourceBase("xxxxdataxxxx");
        context2.addServlet(ListServlet.class, "/*");
   
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] {  context1 , context2 });		        
        server.setHandler(contexts);
        
        server.start();
        
        Scheduler scheduler = new Scheduler();
        scheduler.add(new EmailPollJob());
        scheduler.add(new ResubmissionJob());
        scheduler.start();
        
        scheduler.join();
        server.join();
	}


}
