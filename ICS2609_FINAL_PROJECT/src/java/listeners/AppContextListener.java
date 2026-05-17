/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import dao.*;
import java.util.HashSet;
import javax.servlet.ServletContext;

/**
 * Web application lifecycle listener.
 *
 * @author ethan
 */
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // initialize context params here such as key, credentials and others.
       
        
        ServletContext context = sce.getServletContext();
        
        //database DAO objects initializations
        
        DAOFactory factory = new DAOFactory(context);
        
        context.setAttribute("DAOFactory", factory);
        
        /*
        In any servler
        Ex:
          (kung anong DAO need mo) = (DAOFactory)getServletContext().getAttribute("DAOFactory");
        
        */
        
     
        
        
        
        
        
        
        
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
