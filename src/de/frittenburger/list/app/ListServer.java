package de.frittenburger.list.app;

import java.io.File;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;



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
        
        
      
		
        server.join();
	}

}
