/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;
import javax.servlet.ServletContext;

/**
 *
 * @author ethan
 */
public class DAOFactory {
    // implemented Factory design pattern - Soliven
    
    
    
    private final DerbyAuthDAO derby;
    private final MySqlBusinessDAO mysqlDAO;
    private final PostgresQLDAO postgresDAO;
    
    
    public DAOFactory(ServletContext context){
        this.derby = new DerbyAuthDAO(context);
        this.mysqlDAO = new MySqlBusinessDAO(context);
        this.postgresDAO = new PostgresQLDAO(context);
    }
    
    public DerbyAuthDAO getDerbyDAO(){return derby;}
    public MySqlBusinessDAO getMySQLDAO(){return mysqlDAO;}
    public PostgresQLDAO getpostgreSQLDAO(){return postgresDAO;}
    
}